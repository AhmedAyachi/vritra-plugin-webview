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
    static ArrayList<Intent> intents=new ArrayList<Intent>();
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

    private void show(CallbackContext callbackContext){
        final Activity activity=this.cordova.getActivity();
        this.cordova.getThreadPool().execute(new Runnable(){
            public void run(){
                try{
                    final int id=options.getInt("id");
                    Intent intent=this.findById(id);
                    //Intent intent=null;
                    if(intent!=null){
                        //intent=(Intent)webview.get("intent");
                        /*final Activity wvact=(Activity)webview.get("activity");
                        intent=wvact.getIntent();*/
                        //plugin.cordova.startActivityForResult(plugin,intent,id);
                    }
                    else{
                        //webview=new JSONObject();
                        intent=new Intent(activity,WebViewActivity.class);
                        /*webview.put("id",id);
                        webview.put("intent",intent);*/
                        WebView.intents.add(webview);
                        final String url=options.getString("url");
                        String message="";
                        try{
                            message=options.getString("message"); 
                        }
                        catch(JSONException exception){};
                        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        intent.putExtra("url",url);
                        intent.putExtra("message",message);
                        intent.putExtra("id",id);
                        //plugin.cordova.startActivityForResult(plugin,intent,id);
                        //webview.put("activity",plugin.cordova.getActivity());
                    }

                    plugin.cordova.startActivityForResult(plugin,intent,id);
                    
                }
                catch(JSONException exception){};
            }

            private Intent findById(int id) throws JSONException{
                JSONObject intent=null;
                final int length=WebView.intents.size();
                int i=0;
                while((intent==null)&&(i<length)){
                    final Intent item=WebView.intents.get(i);
                    if(item.getExtras().getInt("id")==id){
                        intent=item;
                    }
                    i++;
                }
                return intent;
            }
        });
    }

    private void back(){
        final Activity activity=this.cordova.getActivity();
        activity.moveTaskToBack(true);
    }

    /*public void onActivityResult(int id,int code,Intent intent){
        super.onActivityResult(id,code,intent);
        final Activity activity=this.cordova.getActivity();
        //if(requestCode==WebView.resultCode){}
    }*/
}
