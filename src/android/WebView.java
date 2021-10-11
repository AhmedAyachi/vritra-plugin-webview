package com.ahmedayachi.webview;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.ahmedayachi.webview.WebViewActivity;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.lang.Runnable;

import javax.management.JMException;


public class WebView extends CordovaPlugin{

    public static CallbackContext callbackContext;
    private final CordovaPlugin plugin=this;
    private int index=-1;

    @Override
    public boolean execute(String action,JSONArray args,CallbackContext callbackContext) throws JSONException{
        WebView.callbackContext=callbackContext;
        if(action.equals("show")){
            JSONObject options=args.getJSONObject(0);
            this.show(options);
            return true;
        }
        else if(action.equals("useMessage")){
            this.useMessage();
            return true;
        }
        else if(action.equals("setMessage")){
            String message=args.getString(0);
            this.setMessage(message);
            return true;
        }
        else if(action.equals("close")){
            this.close();
            return true;
        }
        return false;
    }

    private void show(JSONObject options){
        final AppCompatActivity activity=this.cordova.getActivity();
        this.cordova.getThreadPool().execute(new Runnable(){
            public void run(){
                try{
                    String file="",url="";
                    try{
                        file="file:///android_asset/www/"+options.getString("file");
                    }
                    catch(JSONException exception){
                        url=options.getString("url");
                    };
                    String message="";
                    try{
                        message=options.getString("message"); 
                    }
                    catch(JSONException exception){};
                    final Intent intent=new Intent(activity,WebViewActivity.class);
                    if(!file.isEmpty()){
                        intent.putExtra("file",file);
                    }
                    else{
                        intent.putExtra("url",url);
                    }
                    intent.putExtra("message",message);
                    plugin.index++;
                    plugin.cordova.startActivityForResult(plugin,intent,plugin.index);
                }
                catch(JSONException exception){};
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent){
        if(requestCode==plugin.index){
            Bundle bundle=intent.getExtras();
            String message=bundle.getString("message");
            plugin.index--;
            callbackContext.success(message);
        }
    }

    private void close(){
        final AppCompatActivity activity=this.cordova.getActivity();
        activity.finish();
    }

    private void setMessage(String message){
        final WebViewActivity wvactivity=(WebViewActivity)this.cordova.getActivity();
        wvactivity.setMessage(message);
    }

    private void useMessage(){
        final WebViewActivity wvactivity=(WebViewActivity)this.cordova.getActivity();
        callbackContext.succes(wvactivity.getMessage());
    }
}
