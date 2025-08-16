package com.vritra.webview;

import org.apache.cordova.*;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.content.Intent;
import android.graphics.Insets;
import android.widget.FrameLayout;
import java.lang.Runnable;
import android.util.Log;


public class WebViewActivity extends CordovaActivity {
    
    protected String url=null;
    protected String message="";
    protected Intent intent=null;
    protected Boolean dismissible=null;
    private final WebViewActivity self=this;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.intent=this.getIntent();
        this.overridePendingTransition(getShowAnimation(),getPreActivityCloseAnimation());
        super.init();

        url=intent.getStringExtra("file");
        if(url==null||url.isEmpty()){
            url=intent.getStringExtra("url");
        }
        message=intent.getStringExtra("message");
        this.dismissible=this.intent.getBooleanExtra("dismissible",true);
        this.cordovaInterface.getThreadPool().execute(new Runnable(){
            public void run(){
                self.appView.loadUrl(url);
            }
        });
        this.setResult(WebViewActivity.RESULT_OK,intent);
    }

    @Override
    protected void onStart(){
        super.onStart();
        this.setStyle();
    }

    @Override
    public void onBackPressed(){
        if(this.dismissible){
            super.onBackPressed();
        }
    }

    protected int getShowAnimation(){
        final String animationId=intent.getStringExtra("showAnimation");
        return WebView.getResourceId("anim","showanim_"+WebViewActivity.camelToSnakeCased(animationId));
    }
    protected int getPreActivityCloseAnimation(){
        final String animationId=intent.getStringExtra("showAnimation");
        String name=null;
        switch(animationId){
            case "slideLeft": name="slide_left";break;
            case "slideUp": name="slide_up";break;
            default: name="idle";break;
        }
        if(name==null) return 0;
        else return WebView.getResourceId("anim","hideanim_"+name);
    }

    protected int getCloseAnimation(){
        final String animationId=intent.getStringExtra("closeAnimation");
        return WebView.getResourceId("anim","hideanim_"+WebViewActivity.camelToSnakeCased(animationId));
    }
    protected int getPreActivityShowAnimation(){
        final String animationId=intent.getStringExtra("closeAnimation");
        String name=null;
        switch(animationId){
            case "slideRight": name="slide_right";break;
            case "slideDown": name="slide_down";break;
            default: name="idle";break; 
        }
        if(name==null) return 0;
        else return WebView.getResourceId("anim","showanim_"+name);
    }

    protected void setStyle(){
        final View webView=this.appView.getView();
        final String backgroundColor=intent.getStringExtra("backgroundColor");
        webView.setBackgroundColor(WebView.getColor(backgroundColor));

        final Boolean statusBarTranslucent=this.isStatusBarTranslucent();
        final Boolean navigationBarTranslucent=this.isNavigationBarTranslucent();
        final int statusBarColor=WebView.getColor(statusBarTranslucent?"transparent":intent.getStringExtra("statusBarColor"));
        final int navigationBarColor=WebView.getColor(navigationBarTranslucent?"transparent":intent.getStringExtra("navigationBarColor"));
        final Window window=getWindow();
        final View decorView=window.getDecorView();
        final Boolean isAPI35AndAbove=Build.VERSION.SDK_INT>=Build.VERSION_CODES.UPSIDE_DOWN_CAKE;
        if(!isAPI35AndAbove){
            if(statusBarTranslucent||navigationBarTranslucent){
                decorView.setOnApplyWindowInsetsListener((view,insets)->{
                    final int paddingTop=statusBarTranslucent?0:insets.getSystemWindowInsetTop();
                    final int paddingBottom=navigationBarTranslucent?0:insets.getSystemWindowInsetBottom();
                    view.setPadding(0,paddingTop,0,paddingBottom);
                    return insets.consumeSystemWindowInsets();
                });
            }
        }
        webView.setOnApplyWindowInsetsListener((view,insets)->{
            if(isAPI35AndAbove){
                FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                );
                if(statusBarTranslucent) params.topMargin=0;
                else{
                    final Insets statusBarInsets=insets.getInsets(WindowInsets.Type.statusBars());
                    params.topMargin=statusBarInsets.top;
                }
                if(!navigationBarTranslucent){
                    final Insets navigationBarInsets=insets.getInsets(WindowInsets.Type.navigationBars());
                    params.leftMargin=navigationBarInsets.left;
                    params.rightMargin=navigationBarInsets.right;
                    params.bottomMargin=navigationBarInsets.bottom;  
                }
                view.setLayoutParams(params);
            } else {
                final int paddingTop=statusBarTranslucent?0:insets.getSystemWindowInsetTop();
                final int paddingBottom=navigationBarTranslucent?0:insets.getSystemWindowInsetBottom();
                view.setPadding(0,paddingTop,0,paddingBottom);
            }
            return insets.consumeSystemWindowInsets();
        });
        if(statusBarTranslucent){
            decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        } else {
            if(navigationBarTranslucent) decorView.setBackgroundColor(statusBarColor);
            else if(isAPI35AndAbove){
                WebViewActivity.renderStatusBar(this,statusBarColor);
            }
        }
        window.setStatusBarColor(statusBarColor);
        if(navigationBarTranslucent){
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            window.setNavigationBarContrastEnforced(false);
        } else {
            if(isAPI35AndAbove){
                decorView.setBackgroundColor(navigationBarColor);
                //WebViewActivity.renderNavigationBar(this,navigationBarColor);
            }
            else if(statusBarTranslucent) decorView.setBackgroundColor(navigationBarColor);
        }
        window.setNavigationBarColor(navigationBarColor);
    }

    protected Boolean isStatusBarTranslucent(){
        Boolean statusBarTranslucent=intent.getBooleanExtra("statusBarTranslucent",false);
        return statusBarTranslucent;
    }

    protected Boolean isNavigationBarTranslucent(){
        Boolean navigationBarTranslucent=intent.getBooleanExtra("navigationBarTranslucent",true);
        return navigationBarTranslucent;
    }

    @Override
    public void onDestroy(){
        final CordovaWebView appView=this.appView;
        appView.loadUrlIntoView("about:blank",false);
        if(appView!=null){
            final View view=appView.getView();
            if(view!=null){
                ViewGroup parent=(ViewGroup)view.getParent();
                if(parent!=null){
                    parent.removeView(view);
                }
            }
        }
        super.onDestroy();
    }

    @Override
    public void finish(){
        super.finish();
        this.overridePendingTransition(getPreActivityShowAnimation(),getCloseAnimation());
    }

    public String getMessage(){
        return this.message;
    }
    
    public void setMessage(String str){
        this.message=str;
        intent.putExtra("message",str);
    }

    public static String camelToSnakeCased(String camelCased){
        String snakeCased="";
        final int charCount=camelCased.length();
        for(int i=0;i<charCount;i++){
            final Character c=camelCased.charAt(i);
            if(Character.isUpperCase(c)){
                snakeCased+="_"+Character.toLowerCase(c);
            }
            else{
                snakeCased+=c;
            }
        }
        return snakeCased;
    }

    private static void renderStatusBar(WebViewActivity activity,int statusBarColor){
        final View view=new View(activity);
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WebViewActivity.getStatusBarHeight()
        );
        layoutParams.topMargin=0;
        layoutParams.leftMargin=0;
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(statusBarColor);
        ((ViewGroup)activity.getWindow().getDecorView()).addView(view);
    }

    private static void renderNavigationBar(WebViewActivity activity,int navigationBarColor){
        final View view=new View(activity);
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WebViewActivity.getNavigationBarHeight(),
            Gravity.BOTTOM
        );
        layoutParams.bottomMargin=0;
        layoutParams.leftMargin=0;
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(navigationBarColor);
        ((ViewGroup)activity.getWindow().getDecorView()).addView(view);
    }

    protected static int getStatusBarHeight(){
        int height=0;
        final int resourceId=WebView.resources.getIdentifier("status_bar_height","dimen","android");
        if(resourceId>0){
            height=WebView.resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }
    protected static int getNavigationBarHeight(){
        int height=0;
        final int resourceId=WebView.resources.getIdentifier("navigation_bar_height","dimen","android");
        if(resourceId>0){
            height=WebView.resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }
}
