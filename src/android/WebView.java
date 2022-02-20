package com.ahmedayachi.webview;

import com.ahmedayachi.webview.WebViewActivity;
import com.ahmedayachi.webview.ModalActivity;
import com.ahmedayachi.webview.Store;
import com.ahmedayachi.webview.Fetcher;
import org.apache.cordova.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.lang.Runnable;
import java.util.ArrayList;
import java.util.Random;
import androidx.work.WorkRequest;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Data;


public class WebView extends CordovaPlugin{

    protected static final JSONObject callbacks=new JSONObject();
    private static int index=-1;
    private static Store store=new Store();

    static Context context;
    static CordovaInterface cordova;

    @Override
    public void initialize(CordovaInterface cordova,CordovaWebView webView){
        WebView.cordova=cordova;
        WebView.context=cordova.getContext();
    }

    @Override
    public boolean execute(String action,JSONArray args,CallbackContext callbackContext) throws JSONException{
        if(action.equals("show")){
            JSONObject options=args.getJSONObject(0);
            this.show(options,callbackContext);
            return true;
        }
        else if(action.equals("initiateStore")){
            JSONObject state=args.getJSONObject(0);
            this.initiateStore(state,callbackContext);
            return true;
        }
        else if(action.equals("useStore")){
            this.useStore(callbackContext);
            return true;
        }
        else if(action.equals("setStore")){
            this.setStore(args,callbackContext);
            return true;
        }
        else if(action.equals("useMessage")){
            this.useMessage(callbackContext);
            return true;
        }
        else if(action.equals("setMessage")){
            String message=args.getString(0);
            this.setMessage(message);
            return true;
        }
        else if(action.equals("close")){
            String message=args.getString(0);
            this.close(message);
            return true;
        }
        else if(action.equals("download")){
            String url=args.getString(0);
            JSONObject params=args.getJSONObject(1);
            this.download(url,params,callbackContext);
            return true;
        }
        return false;
    }

    private void show(JSONObject options,CallbackContext callbackContext){
        final AppCompatActivity activity=WebView.cordova.getActivity();
        final CordovaPlugin plugin=this;
        WebView.cordova.getThreadPool().execute(new Runnable(){
            public void run(){
                final int ref=new Random().nextInt();
                Boolean asModal=options.optBoolean("asModal");
                final Intent intent=new Intent(activity,asModal?ModalActivity.class:WebViewActivity.class);
                WebView.setIntentExtras(options,intent);
                try{
                    WebView.callbacks.put(Integer.toString(ref),callbackContext);
                }
                catch(JSONException exception){}
                plugin.cordova.startActivityForResult(plugin,intent,ref);
            }
        });
    }

    @Override
    public void onActivityResult(int ref,int resultCode,Intent intent){
        final String key=Integer.toString(ref);
        final CallbackContext callback=(CallbackContext)WebView.callbacks.opt(key);
        if(callback!=null){
            WebView.callbacks.remove(key);
            String message=Integer.toString(WebViewActivity.RESULT_OK);
            if(resultCode==WebViewActivity.RESULT_OK){
                if(intent!=null){
                    message=intent.getStringExtra("message");
                }
            }
            JSONObject data=new JSONObject();
            try{
                data.put("message",message);
                data.put("store",store.toJSONObject());
            }
            catch(JSONException exception){};
            callback.success(data);
        }
    }

    private void initiateStore(JSONObject state,CallbackContext callbackContext){
        store.initiate(state);
        callbackContext.success(store.toJSONObject());
    }
    private void useStore(CallbackContext callbackContext){
        callbackContext.success(store.toJSONObject());
    }

    private void setStore(JSONArray args,CallbackContext callbackContext) throws JSONException{
        final String key=args.getString(0);
        final Object value=args.get(1);
        store.set(key,value);
        callbackContext.success(store.toJSONObject());
    }
    
    private void useMessage(CallbackContext callbackContext){
        final WebViewActivity wvactivity=(WebViewActivity)WebView.cordova.getActivity();
        callbackContext.success(wvactivity.getMessage());
    }
    private void setMessage(String message){
        final WebViewActivity wvactivity=(WebViewActivity)WebView.cordova.getActivity();
        wvactivity.setMessage(message);
    }

    private void close(String message){
        final WebViewActivity wvactivity=(WebViewActivity)WebView.cordova.getActivity();
        if(!message.isEmpty()){
            wvactivity.setMessage(message);
        }
        wvactivity.finish();
    }
    private void download(String url,JSONObject params,CallbackContext callbackContext){
        final String ref=Integer.toString(new Random().nextInt());
        final Data.Builder data=new Data.Builder();
        data.putString("downloadRef",ref);
        data.putString("url",url);
        data.putString("params",params.toString());
        try{
            WebView.callbacks.put(ref,callbackContext);
        }
        catch(JSONException exception){}
        final WorkRequest request=new OneTimeWorkRequest.Builder(Fetcher.class).setInputData(data.build()).build();
        WorkManager.getInstance(WebView.context).enqueue(request);
    }

    private static void setIntentExtras(JSONObject options,Intent intent){
        String file=options.optString("file");
        if((file!=null)&&(!file.isEmpty())){
            intent.putExtra("file","file:///android_asset/www/"+file);
        }
        else{
            String url=options.optString("url");
            intent.putExtra("url",url);
        }
        String message=options.optString("message");
        if(message!=null){
            intent.putExtra("message",message);
        }
        
        Boolean asModal=options.optBoolean("asModal");
        if(asModal){
            JSONObject style=options.optJSONObject("style");
            if(style!=null){
                intent.putExtra("style",style.toString());
            }
        }

        Boolean statusBarTranslucent=options.optBoolean("statusBarTranslucent",true);
        if(statusBarTranslucent!=null){
            intent.putExtra("statusBarTranslucent",statusBarTranslucent);
        }
    }
}
