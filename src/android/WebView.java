package com.ahmedayachi.webview;

import android.app.Activity;
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
    public static final int resultCode=47;
    private final CordovaPlugin plugin=this;
    static ArrayList<JSONObject> webviews=new ArrayList<JSONObject>();
    JSONObject options=null;

    @Override
    public boolean execute(String action,JSONArray args,CallbackContext callbackContext) throws JSONException{
        final WebView plugin=this;
        WebView.callback=callbackContext;
        if(action.equals("create")){
            JSONObject options=args.getJSONObject(0);
            this.options=options;
            this.create(callbackContext);
            return true;
        }
        else if(action.equals("useMessage")){
            WebViewActivity wvact=(WebViewActivity)this.cordova.getActivity();
            callbackContext.error(wvact.getMessage());
            return true;
        }
        return false;
    }

    private void create(CallbackContext callbackContext){
        final Activity activity=this.cordova.getActivity();
        this.cordova.getThreadPool().execute(new Runnable(){
            public void run(){
                try{
                    final int id=options.getInt("id");
                    JSONObject webview=this.findById(id);
                    Intent intent=null;
                    if(webview!=null){
                        intent=(Intent)webview.get("intent");
                    }
                    else{
                        webview=new JSONObject();
                        intent=new Intent(activity,WebViewActivity.class);
                        webview.put("id",id);
                        webview.put("intent",intent);
                        WebView.webviews.add(webview);
                        final String url=options.getString("url");
                        String message="";
                        try{
                            message=options.getString("message"); 
                        }
                        catch(JSONException exception){};
                        intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK|
                            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        );
                        intent.putExtra("url",url);
                        intent.putExtra("message",message);
                    }
                    plugin.cordova.startActivityForResult(plugin,intent,id);
                }
                catch(JSONException exception){};
            }

            private JSONObject findById(int id) throws JSONException{
                JSONObject webview=null;
                final int length=WebView.webviews.size();
                int i=0;
                while((webview==null)&&(i<length)){
                    final JSONObject item=WebView.webviews.get(i);
                    if(item.getInt("id")==id){
                        webview=item;
                    }
                    i++;
                }
                return webview;
            }
        });
    }

    /*public void onActivityResult(int id,int code,Intent intent){
        super.onActivityResult(id,code,intent);
        final Activity activity=this.cordova.getActivity();
        //if(requestCode==WebView.resultCode){}
    }*/
}
