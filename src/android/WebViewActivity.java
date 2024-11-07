package com.vritra.webview;

import org.apache.cordova.*;
import android.os.Bundle;
import android.view.Window;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import java.lang.Runnable;


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
        this.overridePendingTransition(getShowAnimation(),0);
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
        self.setStyle();
    }

    @Override
    public void onBackPressed(){
        if(this.dismissible){
            super.onBackPressed();
        }
    }

    protected int getShowAnimation(){
        String animationId=intent.getStringExtra("showAnimation");
        String name=null;
        switch(animationId){
            case "slideUp": name="slide_up";break;
            case "fadeIn": name="fade_in";break;
            case "slideLeft":
            default: name="slide_left";break; 
        }
        return WebView.getResourceId("animator","showanim_"+name);
    }

    protected int getCloseAnimation(){
        String animationId=intent.getStringExtra("closeAnimation");
        String name=null;
        switch(animationId){
            case "slideDown": name="slide_down";break;
            case "slideRight": name="slide_right";break;
            case "fadeOut":
            default: name="fade_out";break; 
        }
        return WebView.getResourceId("animator","hideanim_"+name);
    }

    protected void setStyle(){
        final View webView=this.appView.getView();
        final String backgroundColor=intent.getStringExtra("backgroundColor");
        webView.setBackgroundColor(WebView.getColor(backgroundColor));
        final Boolean statusBarTranslucent=this.isStatusBarTranslucent();
        final int statusBarColor=WebView.getColor(statusBarTranslucent?"transparent":intent.getStringExtra("statusBarColor"));
        final Window window=getWindow();
        if(statusBarTranslucent){
            final View decorview=window.getDecorView();
            decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        window.setStatusBarColor(statusBarColor);
    }

    protected Boolean isStatusBarTranslucent(){
        Boolean statusBarTranslucent=intent.getBooleanExtra("statusBarTranslucent",false);
        return statusBarTranslucent;
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
        this.overridePendingTransition(0,getCloseAnimation());
    }

    public String getMessage(){
        return this.message;
    }
    
    public void setMessage(String str){
        this.message=str;
        intent.putExtra("message",str);
    }
}
