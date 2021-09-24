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
            JSONObject options=args.getJSONObject(0);
            this.create(options,callbackContext);
            return true;
        }
        else if(action.equals("useMessage")){
            WebViewActivity wvact=(WebViewActivity)this.cordova.getActivity();
            callbackContext.error(wvact.getMessage());
            return true;
        }
        return false;
    }

    private void create(JSONObject options,CallbackContext callbackContext){
        final Activity activity=this.cordova.getActivity();
        this.cordova.getThreadPool().execute(new Runnable(){
            public void run(){
                try{
                    Intent intent=new Intent(activity,WebViewActivity.class);
                    final String url=options.getString("url");
                    String message="";
                    try{
                        message=options.getString("message"); 
                    }
                    catch(JSONException exception){};
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("url",url);
                    intent.putExtra("message",message);
                    plugin.cordova.startActivityForResult(plugin,intent,resultCode);
                }
                catch(JSONException exception){};
            }
        });
    }

    /*public void onActivityResult(int requestCode,int code,Intent intent){
        super.onActivityResult(requestCode,code,intent);
        callback.error("message");
        //if(requestCode==WebView.resultCode){}
    }*/
}
