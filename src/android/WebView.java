package com.ahmedayachi.webview;

import android.app.Activity;
import android.content.Intent;
import 	android.content.Context;
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
    static final ArrayList<Activity> activities=new ArrayList<Activity>();

    public void initialize(CordovaInterface cordova,CordovaWebView webView){
        final Activity activity=cordova.getActivity();
        Intent intent=activity.getIntent();
        intent.putExtra("url","index.html");
        intent.putExtra("message","");
        intent.putExtra("id",0);
        activities.add(activity);
    }

    @Override
    public boolean execute(String action,JSONArray args,CallbackContext callbackContext) throws JSONException{
        final WebView plugin=this;
        WebView.callback=callbackContext;
        if(action.equals("show")){
            JSONObject options=args.getJSONObject(0);
            this.show(options);
            return true;
        }
        else if(action.equals("useMessage")){
            WebViewActivity webviewactivity=(WebViewActivity)this.cordova.getActivity();
            callbackContext.error(webviewactivity.getMessage());
            return true;
        }
        else if(action.equals("back")){
            this.back();
            return true;
        }
        return false;
    }

    private void show(JSONObject options){
        final Context context=this.cordova.getContext();
        
        this.cordova.getThreadPool().execute(new Runnable(){
            public void run(){
                try{
                    final int id=options.getInt("id");
                    final Activity activity=this.findActivityById(id);
                    if(activity==null){
                        final Intent intent=new Intent(context,WebViewActivity.class);
                        final String url=options.getString("url");
                        String message="";
                        try{
                            message=options.getString("message"); 
                        }
                        catch(JSONException exception){};
                        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        intent.putExtra("url",url);
                        intent.putExtra("message",message);
                        intent.putExtra("com.ahmedayachi.webview.id",id);
                        plugin.cordova.startActivityForResult(plugin,intent,id);
                        WebView.callback.success(activities.size());
                    }
                    else{
                        activity.finish();
                        //activities.remove(activity);
                        activity.startActivity(activity.getIntent());
                        WebView.callback.success(activities.size());
                    }
                    
                }
                catch(JSONException exception){};
            }

            private Activity findActivityById(int id){
                Activity activity=null;
                final int length=activities.size();
                int i=0;
                while((activity==null)&&(i<length)){
                    final Activity item=activities.get(i);
                    final Intent intent=item.getIntent();
                    if(intent.getIntExtra("com.ahmedayachi.webview.id",-1)==id){
                        activity=item;
                    }
                    i++;
                }
                return activity;
            }
        });
    }

    private void back(){
        final Activity activity=this.cordova.getActivity();
        activity.finish();
    }

    /*public void onActivityResult(int id,int code,Intent intent){
        super.onActivityResult(id,code,intent);
        final Activity activity=this.cordova.getActivity();
        //if(requestCode==WebView.resultCode){}
    }*/
}
