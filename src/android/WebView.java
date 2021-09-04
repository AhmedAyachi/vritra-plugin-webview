package com.ahmedayachi.webview;

import android.content.Intent;
import com.ahmedayachi.webviewactivity.WebViewActivity;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebView extends CordovaPlugin{

    @Override
    public boolean execute(String action,JSONArray args,CallbackContext callbackContext) throws JSONException{
        if(action.equals("say")) {
            String message=args.getString(0);
            this.coolAlert(message,callbackContext);
            return true;
        }
        return false;
    }

    private void coolAlert(String message,CallbackContext callbackContext){
        callbackContext.success(message);
    }

}