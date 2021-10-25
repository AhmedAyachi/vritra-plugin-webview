package com.ahmedayachi.webview;

import android.os.Bundle;
import com.ahmedayachi.webview.WebView;
import org.apache.cordova.*;
import android.view.Window;
import android.view.View;
import android.graphics.Color;
import android.content.Intent;


public class WebViewActivity extends CordovaActivity{
    
    protected String url=null;
    protected String message="";
    protected Intent intent=null;
    protected Boolean isModel=false;
    protected Boolean isLoaded=false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.init();
        
        this.intent=this.getIntent();
        url=intent.getStringExtra("file");
        if(url==null||url.isEmpty()){
            url=intent.getStringExtra("url");
        }
        message=intent.getStringExtra("message");

        this.setResult(WebViewActivity.RESULT_OK,intent);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(!isLoaded){
            this.loadWebPage();
            isLoaded=true;
        }
    }

    protected void loadWebPage(){
        appView.loadUrl(url);
        final Window window=getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        View decorview=getWindow().getDecorView();
        decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public String getMessage(){
        return this.message;
    }
    public void setMessage(String str){
        this.message=str;
        intent.putExtra("message",str);
    }

}
