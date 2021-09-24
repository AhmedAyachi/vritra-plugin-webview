package com.ahmedayachi.webview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.ahmedayachi.webview.WebViewActivity;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.lang.Runnable;


public class WebView extends CordovaPlugin{
    public static CallbackContext callback;
    public static final int resultCode=47;
    private final CordovaPlugin plugin=this;

    @Override
    public boolean execute(String action,JSONArray args,CallbackContext callbackContext) throws JSONException{
        final WebView plugin=this;
        WebView.callback=callbackContext;
        if(action.equals("create")){
            String url=args.getString(0);
            this.create(url,callbackContext);
            return true;
        }
        return false;
    }

    private void create(String url,CallbackContext callbackContext){
        final Activity activity=this.cordova.getActivity();
        this.cordova.getThreadPool().execute(new Runnable(){
            public void run(){
                Intent intent=new Intent(activity,WebViewActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("url",url);
                plugin.cordova.startActivityForResult(plugin,intent,resultCode);
                callbackContext.success();
            }
        });
    }

    public void onActivityResult(int requestCode,int code,Intent intent){
        super.onActivityResult(requestCode,code,intent);
        callback.error("");
        //if(requestCode==WebView.resultCode){}
    }

    /*public Boolean shouldAllowBridgeAccess(String url) {
        return true;
    }*/
}
