package com.ahmedayachi.webview;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import netscape.javascript.JSObject;

import com.ahmedayachi.webview.WebViewActivity;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.lang.Runnable;
import java.util.ArrayList;


public class WebView extends CordovaPlugin{
    public static CallbackContext callback;
    private final CordovaPlugin plugin=this;
    JSONObject options=null;

    @Override
    public boolean execute(String action,JSONArray args,CallbackContext callbackContext) throws JSONException{
        final WebView plugin=this;
        WebView.callback=callbackContext;
        if(action.equals("show")){
            JSONObject options=args.getJSONObject(0);
            this.options=options;
            this.show(callbackContext);
            return true;
        }
        else if(action.equals("useMessage")){
            WebViewActivity wvact=(WebViewActivity)this.cordova.getActivity();
            callbackContext.error(wvact.getMessage());
            return true;
        }
        else if(action.equals("close")){
            this.close();
        }
        return false;
    }

    private void show(CallbackContext callbackContext){
        final AppCompatActivity activity=this.cordova.getActivity();
        this.cordova.getThreadPool().execute(new Runnable(){
            public void run(){
                try{
                    final String url=options.getString("url");
                    String message="";
                    try{
                        message=options.getString("message"); 
                    }
                    catch(JSONException exception){};
                    final Intent intent=new Intent(activity,WebView.class);
                    intent.putExtra("url",url);
                    intent.putExtra("message",message);
                    plugin.cordova.startActivityForResult(plugin,intent,0);
                }
                catch(JSONException exception){};
            }
        });
    }

    private void close(){
        final AppCompatActivity activity=this.cordova.getActivity();
        activity.finish();
    }
}
