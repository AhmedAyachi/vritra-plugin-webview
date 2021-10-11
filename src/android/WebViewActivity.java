package com.ahmedayachi.webview;

import android.os.Bundle;
import com.ahmedayachi.webview.WebView;
import org.apache.cordova.*;
import android.view.Window;
import android.view.View;
import android.graphics.Color;
import android.content.Intent;
import android.app.Activity;



public class WebViewActivity extends CordovaActivity{
    
    private String message="";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.init();
        Bundle bundle=this.getIntent().getExtras();
        String url;
        url=bundle.getString("file");
        if(url==null||url.isEmpty()){
            url=bundle.getString("url");
        }
        if((url!=null)&&(url.length()>0)){
            message=bundle.getString("message");
            super.loadUrl(url);
        }
        final Window window=getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        View decorview=getWindow().getDecorView();
        decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String str){
        this.message=str;
    }

    

}
