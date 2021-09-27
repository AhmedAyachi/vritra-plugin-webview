package com.ahmedayachi.webview;

import android.os.Bundle;
import com.ahmedayachi.webview.WebView;
import org.apache.cordova.*;
import android.app.ActionBar;
import android.view.Window;
import android.view.View;
import android.graphics.Color;



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
        final Window window=getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        View decorview=getWindow().getDecorView();
        decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void onStart(){
        super.onStart();
        WebView.callback.success();
    }

    public String getMessage(){
        return message;
    }
}
