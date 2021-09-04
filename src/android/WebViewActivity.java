package com.ahmedayachi.webviewactivity;

import android.os.Bundle;
import org.apache.cordova.*;


public class WebViewActivity extends CordovaActivity{
    static Dialog dialog;
    static Activity activity2;

    @Override
    public void onCreate(Bundle savedInstanceState,String url){
        super.onCreate(savedInstanceState);
        super.init();
        
        loadUrl("file:///android_asset/www/".concat(url));
    }
}