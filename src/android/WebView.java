package com.vritra.webview;

import com.vritra.common.*;
import com.vritra.webview.*;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Random;
import java.util.Objects;
import java.util.Iterator;
import java.util.ArrayList;
import java.lang.Runnable;
import android.content.Intent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;


public class WebView extends VritraPlugin {

    protected static final JSONObject callbacks=new JSONObject();
    private static final JSONObject webviews=new JSONObject();
    private static Store store=new Store();

    @Override
    public boolean execute(String action,JSONArray args,CallbackContext callbackContext) throws JSONException{
        boolean result=true;
        if(action.equals("defineWebViews")){
            final JSONArray webviews=args.getJSONArray(0);
            this.defineWebViews(webviews,callbackContext);
        }
        else if(action.equals("show")){
            JSONObject options=args.getJSONObject(0);
            this.show(options,callbackContext);
        }
        else if(action.equals("initiateStore")){
            JSONObject state=args.getJSONObject(0);
            this.initiateStore(state,callbackContext);
        }
        else if(action.equals("useStore")){
            this.useStore(args,callbackContext);
        }
        else if(action.equals("setStore")){
            this.setStore(args,callbackContext);
        }
        else if(action.equals("useMessage")){
            this.useMessage(callbackContext);
        }
        else if(action.equals("setMessage")){
            String message=args.getString(0);
            this.setMessage(message);
        }
        else if(action.equals("close")){
            this.close(args);
        }
        else{
            result=false;
        }
        return result;
    }

    private void defineWebViews(JSONArray webviews,CallbackContext callbackContext){
        final int length=webviews.length();
        for(int i=0;i<length;i++){
            final JSONObject webview=webviews.optJSONObject(i);
            final String id=webview.optString("id");
            final boolean validId=!id.isEmpty();
            try{
                if(validId&&(webview.has("file")||webview.has("url"))){
                    WebView.webviews.put(id,webview);
                }
                else{
                    throw new Exception("Invalid WebView props");
                }
            }
            catch(Exception exception){
                callbackContext.error(exception.getMessage());
            }
        }
    }

    private void show(JSONObject options,CallbackContext callbackContext){
        final AppCompatActivity activity=this.cordova.getActivity();
        final CordovaPlugin plugin=this;
        this.cordova.getThreadPool().execute(new Runnable(){
            public void run(){
                final int ref=new Random().nextInt(999);
                final JSONObject props=WebView.getWebViewProps(options);
                Boolean asModal=props.optBoolean("asModal");
                final Intent intent=new Intent(activity,asModal?ModalActivity.class:WebViewActivity.class);
                WebView.setIntentExtras(props,intent);
                try{
                    WebView.callbacks.put(Integer.toString(ref),callbackContext);
                }
                catch(JSONException exception){}
                plugin.cordova.startActivityForResult(plugin,intent,ref);
            }
        });
    }

    private static JSONObject getWebViewProps(JSONObject options){
        JSONObject props=null;
        final String id=options.optString("id");
        if((!id.isEmpty())&&WebView.webviews.has(id)){
            final JSONObject defaults=WebView.webviews.optJSONObject(id);
            props=WebView.mergeJSONObjects(defaults,options);
        }
        else{
            props=options;
        }
        return props;
    }

    @Override
    public void onActivityResult(int ref,int resultCode,Intent intent){
        final String key=Integer.toString(ref);
        final CallbackContext callback=(CallbackContext)WebView.callbacks.opt(key);
        if(callback!=null){
            WebView.callbacks.remove(key);
            String message="";
            if((resultCode==WebViewActivity.RESULT_OK)&&(intent!=null)){
                message=intent.getStringExtra("message");
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
    private void useStore(JSONArray args,CallbackContext callbackContext){
        try{
            final String path=args.optString(0);
            if(path.isEmpty()){
                callbackContext.success(store.toJSONObject());
            }
            else{
                final ArrayList<Object> values=store.get(path);
                final JSONArray array=new JSONArray();
                final int length=values.size();
                for(int i=0;i<length;i++){
                    array.put(values.get(i));
                }
                callbackContext.success(array); 
            }
        }
        catch(Exception exception){
            callbackContext.error(new VritraError(exception.getMessage()));
        }
    }

    private void setStore(JSONArray args,CallbackContext callbackContext){
        try{
            final JSONArray deletables=args.optJSONArray(3);
            final int deletableCount=deletables.length();
            for(int i=0;i<deletableCount;i++){
                this.setStoreValue(deletables.optString(i));
            }
            final Boolean multiSetting=args.optBoolean(2);
            if(multiSetting){
                final JSONArray pairs=args.optJSONArray(0);
                if(pairs!=null){
                    int length=pairs.length();
                    if(length%2==0){
                        for(int i=0;i<length;i+=2){
                            this.setStoreValue(pairs.getString(i),pairs.get(i+1));
                        }
                    }
                    else throw new Exception("array length should be even");
                }
                else throw new Exception("param is not of type string|array");
            }
            else if(deletableCount<1){
                this.setStoreValue(args.getString(0),args.get(1));
            }
            callbackContext.success(store.toJSONObject());
        }
        catch(Exception exception){
            callbackContext.error(new VritraError(exception.getMessage()));
        }
    }
    private void setStoreValue(String path) throws Exception {
        if(path.isEmpty()) throw new Exception("invalid key");
        else{
            store.delete(path);
        }
    }
    private void setStoreValue(String path,Object value) throws Exception {
        if(path.isEmpty()) throw new Exception("invalid key");
        else{
            store.set(path,value);
        }
    }
    
    private void useMessage(CallbackContext callbackContext){
        final WebViewActivity wvactivity=(WebViewActivity)this.cordova.getActivity();
        callbackContext.success(wvactivity.getMessage());
    }
    private void setMessage(String message){
        final WebViewActivity wvactivity=(WebViewActivity)this.cordova.getActivity();
        wvactivity.setMessage(message);
    }

    private void close(JSONArray params){
        final AppCompatActivity activity=this.cordova.getActivity();
        if(activity instanceof WebViewActivity){
            final WebViewActivity wvactivity=(WebViewActivity)activity;
            final Boolean isUndefined=params.optBoolean(1);
            if(!isUndefined){
                wvactivity.setMessage(params.optString(0));
            }
            this.cordova.getThreadPool().execute(new Runnable(){
                public void run(){
                    wvactivity.finish();
                }
            });
        }
        else{
            Intent intent=new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(intent);
        }
    }

    private static JSONObject mergeJSONObjects(JSONObject object1,JSONObject object2){
        return WebView.mergeJSONObjects(object1,object2,true);
    }
    private static JSONObject mergeJSONObjects(JSONObject object1,JSONObject object2,Boolean nestedMerge){
        JSONObject merged=null;
        try{
            final Iterator<String> keys1=object1.keys();
            final  ArrayList<String> names=new ArrayList<String>();
            keys1.forEachRemaining(names::add);
            merged=new JSONObject(object1,names.toArray(new String[names.size()]));
            final Iterator<String> keys2=object2.keys();
            while(keys2.hasNext()){
                final String key=keys2.next();
                final Object value=object2.opt(key);
                if(nestedMerge&&(value instanceof JSONObject)){
                    final JSONObject defaultValue=object1.optJSONObject(key);
                    if(defaultValue!=null){
                        merged.put(key,WebView.mergeJSONObjects(defaultValue,object2.optJSONObject(key),true));
                    }
                    else{
                        merged.put(key,value);
                    }
                }
                else{
                    merged.put(key,value);
                }
            }
        }
        catch(Exception exception){}
        return merged;
    }

    private static void setIntentExtras(JSONObject props,Intent intent){
        String file=props.optString("file");
        if((file!=null)&&(!file.isEmpty())){
            intent.putExtra("file","file:///android_asset/www/"+file);
        }
        else{
            String url=props.optString("url");
            intent.putExtra("url",url);
        }
        String message=props.optString("message",null);
        if(message!=null){
            intent.putExtra("message",message);
        }
        
        Boolean asModal=props.optBoolean("asModal");
        if(asModal){
            final JSONObject modalStyle=props.optJSONObject("modalStyle");
            if(modalStyle!=null){
                intent.putExtra("modalStyle",modalStyle.toString());
            }
        }

        Boolean statusBarTranslucent=props.optBoolean("statusBarTranslucent",false);
        intent.putExtra("statusBarTranslucent",statusBarTranslucent);
        if(!statusBarTranslucent){
            String statusBarColor=props.optString("statusBarColor","black");
            intent.putExtra("statusBarColor",statusBarColor);
        }

        Boolean dismissible=props.optBoolean("dismissible",true);
        intent.putExtra("dismissible",dismissible);

        String backgroundColor=props.optString("backgroundColor","white");
        intent.putExtra("backgroundColor",backgroundColor);

        final String showAnimation=props.optString("showAnimation","slideLeft");
        intent.putExtra("showAnimation",showAnimation);

        final String closeAnimation=props.optString("closeAnimation","fadeOut");
        intent.putExtra("closeAnimation",closeAnimation);
    }
}
