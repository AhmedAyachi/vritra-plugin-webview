package com.ahmedayachi.webview;

import android.os.Bundle;
import org.apache.cordova.*;



public class WebViewActivity extends CordovaActivity{
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.init();
        Bundle bundle=this.getIntent().getExtras();
        String url=bundle.getString("url");
        super.loadUrl(url);
    }
}
