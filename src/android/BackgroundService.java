package com.ahmedayachi.webview;

import com.ahmedayachi.webview.WebView;
import org.apache.cordova.CallbackContext;
import android.content.Intent;
import android.content.Context;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.ListenableWorker.Result;
import android.widget.Toast;


public class BackgroundService extends Worker{

    public BackgroundService(Context context,WorkerParameters params){
        super(context,params);
    }
    
   @Override
    public Result doWork(){
        Boolean isFulfilled=false;
        final String callbackRef=getInputData().getString("callbackRef");
        if(callbackRef!=null){
            final CallbackContext callback=(CallbackContext)WebView.backgroundCalls.opt(callbackRef);
            if(callback!=null){
                try{
                    WebView.cordova.getActivity().runOnUiThread(new Runnable(){
                        public void run(){
                            Toast.makeText(WebView.context,callbackRef,Toast.LENGTH_SHORT).show();
                        }
                    });
                    callback.success();
                    isFulfilled=true;
                }
                catch(Exception exception){
                    callback.error(exception.getMessage());
                }
            }
            WebView.backgroundCalls.remove(callbackRef);
        }

        return isFulfilled?Result.success():Result.failure();
    }
}
