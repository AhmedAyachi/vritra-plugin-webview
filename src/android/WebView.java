package com.ahmedayachi.webview;

import androidx.appcompat.app.AppCompatActivity;
import com.ahmedayachi.webview.WebViewActivity;
import android.content.Intent;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.lang.Runnable;
import java.util.ArrayList;


public class WebView extends CordovaPlugin{

    private static final ArrayList<CallbackContext> wvcallbacks=new ArrayList<CallbackContext>();
    private static int index=-1;
    private static JSONObject store=new JSONObject();
    private final CordovaPlugin plugin=this;
   

    @Override
    public boolean execute(String action,JSONArray args,CallbackContext callbackContext) throws JSONException{
        if(action.equals("show")){
            JSONObject options=args.getJSONObject(0);
            this.show(options,callbackContext);
            return true;
        }
        else if(action.equals("useStore")){
            this.useStore(callbackContext);
            return true;
        }
        else if(action.equals("setStore")){
            this.setStore(args);
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
        return false;
    }

    private void show(JSONObject options,CallbackContext callbackContext){
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
                    WebView.wvcallbacks.add(callbackContext);
                    WebView.index++;
                    plugin.cordova.startActivityForResult(plugin,intent,WebView.index);
                }
                catch(JSONException exception){};
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent){
        if(requestCode==WebView.index){
            final CallbackContext callback=WebView.wvcallbacks.get(WebView.index);
            WebView.wvcallbacks.remove(WebView.index);
            WebView.index--; 
            String message=Integer.toString(WebViewActivity.RESULT_OK);
            if(resultCode==WebViewActivity.RESULT_OK){
                if(intent!=null){
                    message=intent.getStringExtra("message");
                }
            }
            JSONObject data=new JSONObject();
            try{
                data.put("message",message);
                data.put("store",store);
            }
            catch(JSONException exception){};
            callback.success(data);
        }
    }

    private void useStore(CallbackContext callbackContext){
        callbackContext.success(store);
    }

    private void setStore(JSONArray args) throws JSONException{
        final String key=args.getString(0);
        final Object value=args.get(1);
        store.put(key,value);
    }

    private void close(String message){
        final WebViewActivity wvactivity=(WebViewActivity)this.cordova.getActivity();
        if(!message.isEmpty()){
            wvactivity.setMessage(message);
        }
        wvactivity.finish();
    }

    private void setMessage(String message){
        final WebViewActivity wvactivity=(WebViewActivity)this.cordova.getActivity();
        wvactivity.setMessage(message);
    }
}
