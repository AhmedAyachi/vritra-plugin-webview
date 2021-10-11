package com.ahmedayachi.webview;

import android.os.Bundle;
import com.ahmedayachi.webview.WebView;
import org.apache.cordova.*;
import android.view.Window;
import android.view.View;
import android.graphics.Color;
import android.content.Intent;



public class WebViewActivity extends CordovaActivity{
    
    private String message="";
    private Intent intent=null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.init();
        intent=this.getIntent();
        Bundle bundle=intent.getExtras();
        String file,url;
        file=bundle.getString("file");
        if(!file.isEmpty()){
            url=file;
        }
        else{
            url=bundle.getString("url");
        }
        message=bundle.getString("message");
        super.loadUrl(url);
        final Window window=getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        View decorview=getWindow().getDecorView();
        decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    /*public void onStart(){
        super.onStart();
        WebView.callbackContext.success();
    }*/

    public String getMessage(){
        return message;
    }

    public void setMessage(String str){
        this.message=str;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        intent.putExtra("message",this.message);
    }

}
