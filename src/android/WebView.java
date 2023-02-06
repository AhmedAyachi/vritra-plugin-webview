package com.ahmedayachi.webview;

import com.ahmedayachi.webview.WebViewActivity;
import com.ahmedayachi.webview.ModalActivity;
import com.ahmedayachi.webview.Store;
import org.apache.cordova.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.Context;
import android.content.res.Resources;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.lang.Runnable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;


public class WebView extends CordovaPlugin{

    static Context context;
    static protected Resources resources;
    static protected String packagename;

    protected static final JSONObject callbacks=new JSONObject();
    private static final JSONObject webviews=new JSONObject();
    private static Store store=new Store();

    @Override
    public void initialize(CordovaInterface cordova,CordovaWebView webview){
        WebView.context=cordova.getContext();
        WebView.resources=WebView.context.getResources();
        WebView.packagename=WebView.context.getPackageName();
    }

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
            this.useStore(callbackContext);
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
                    throw new Exception("Invalid Webview props");
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
        final WebViewActivity wvactivity=(WebViewActivity)this.cordova.getActivity();
        callbackContext.success(wvactivity.getMessage());
    }
    private void setMessage(String message){
        final WebViewActivity wvactivity=(WebViewActivity)this.cordova.getActivity();
        wvactivity.setMessage(message);
    }

    private void close(JSONArray params){
        final WebViewActivity wvactivity=(WebViewActivity)this.cordova.getActivity();
        final Boolean isUndefined=params.optBoolean(0);
        if(!isUndefined){
            wvactivity.setMessage(params.optString(1));
        }
        wvactivity.finish();
    }

    private static JSONObject mergeJSONObjects(JSONObject object1,JSONObject object2){
        JSONObject merged=null;
        try{
            final Iterator<String> keys1=object1.keys();
            final  ArrayList<String> names=new ArrayList<String>();
            keys1.forEachRemaining(names::add);
            merged=new JSONObject(object1,names.toArray(new String[names.size()]));
            final Iterator<String> keys2=object2.keys();
            while(keys2.hasNext()){
                final String key=keys2.next();
                merged.put(key,object2.opt(key));
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
        String message=props.optString("message");
        if(message!=null){
            intent.putExtra("message",message);
        }
        
        Boolean asModal=props.optBoolean("asModal");
        if(asModal){
            JSONObject style=props.optJSONObject("style");
            if(style!=null){
                intent.putExtra("style",style.toString());
            }
        }

        Boolean statusBarTranslucent=props.optBoolean("statusBarTranslucent",true);
        if(statusBarTranslucent!=null){
            intent.putExtra("statusBarTranslucent",statusBarTranslucent);
        }

        String backgroundColor=props.optString("backgroundColor","white");
        if(backgroundColor!=null){
            intent.putExtra("backgroundColor",backgroundColor);
        }

        final String showAnimation=props.optString("showAnimation","slideLeft");
        intent.putExtra("showAnimation",showAnimation);

        final String closeAnimation=props.optString("closeAnimation","fadeOut");
        intent.putExtra("closeAnimation",closeAnimation);
        
        final JSONObject modalStyle=props.optJSONObject("modalStyle");
        if(modalStyle!=null){
            intent.putExtra("modalStyle",modalStyle.toString());
        }
    }

    static protected int getResourceId(String type,String name){
        return resources.getIdentifier(name,type,WebView.packagename);
    }
    
}
