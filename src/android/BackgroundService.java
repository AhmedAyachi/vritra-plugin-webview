package com.ahmedayachi.webview;

import com.ahmedayachi.webview.WebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;


public class BackgroundService extends CordovaActivity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.init();
        appView.loadUrl("");
        final Intent intent=this.getIntent();
        final String callbackRef=intent.getStringExtra("callbackRef");
        if(callbackRef!=null){
            final CallbackContext callback=(CallbackContext)WebView.backgroundCalls.opt(callbackRef);
            if(callback!=null){
                this.runOnUiThread(new Runnable(){
                    public void run(){
                        Toast.makeText(WebView.context,callbackRef,Toast.LENGTH_SHORT).show();
                    }
                });
                try{
                    callback.success();
                    //this.finish();
                }
                catch(Exception exception){
                    callback.error(exception.getMessage());
                }
            }
            WebView.backgroundCalls.remove(callbackRef);
        }
    }
}
