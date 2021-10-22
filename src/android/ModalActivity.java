package com.ahmedayachi.webview;

import com.ahmedayachi.webview.WebViewActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.ViewGroup;


public class ModalActivity extends WebViewActivity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        this.isModel=true;
        super.onCreate(savedInstanceState);

        final Window window=getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
