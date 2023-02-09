package com.ahmedayachi.webview;

import android.os.Bundle;
import com.ahmedayachi.webview.WebView;
import org.apache.cordova.*;
import android.view.Window;
import android.view.View;
import android.graphics.Color;
import android.content.Intent;
import java.lang.Runnable;
//import android.widget.Toast;


public class WebViewActivity extends CordovaActivity{
    
    protected String url=null;
    protected String message="";
    protected Intent intent=null;
    private final WebViewActivity webviewActivity=this;

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
        
        this.cordovaInterface.getThreadPool().execute(new Runnable(){
            public void run(){
                webviewActivity.loadHTML();
            }
        });
        this.setResult(WebViewActivity.RESULT_OK,intent);
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
        return WebView.getResourceId("animator",name);
    }

    protected int getCloseAnimation(){
        String animationId=intent.getStringExtra("closeAnimation");
        String name=null;
        switch(animationId){
            case "slideDown": name="slide_down";break;
            case "fadeOut":
            default: name="fade_out";break; 
        }
        return WebView.getResourceId("animator",name);
    }

    protected void loadHTML(){
        appView.getView().setBackgroundColor(Color.parseColor(intent.getStringExtra("backgroundColor")));
        appView.loadUrl(url);
        Boolean statusBarTranslucent=intent.getBooleanExtra("statusBarTranslucent",false);
        if(statusBarTranslucent){
            final Window window=getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            View decorview=getWindow().getDecorView();
            decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
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
