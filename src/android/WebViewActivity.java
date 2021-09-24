package com.ahmedayachi.webview;

import android.os.Bundle;
import com.ahmedayachi.webview.WebView;
import org.apache.cordova.*;



public class WebViewActivity extends CordovaActivity{
    
    private String message="";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.init();
        Bundle bundle=this.getIntent().getExtras();
        String url="file:///android_asset/www/"+bundle.getString("url");
        message=bundle.getString("message");
        super.loadUrl(url);
    }

    public void onStart(){
        super.onStart();
        WebView.callback.success();
    }

    public String getMessage(){
        return message;
    }
}
