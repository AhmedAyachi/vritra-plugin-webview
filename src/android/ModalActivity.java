package com.ahmedayachi.webview;

import com.ahmedayachi.webview.WebViewActivity;
import android.os.Bundle;


public class ModalActivity extends WebViewActivity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        this.isModel=true;
        super.onCreate(savedInstanceState);
    }
}
