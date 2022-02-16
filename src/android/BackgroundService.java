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
        //final String callbackRef=intent.getStringExtra("callbackRef");
        //new Thread(new Runnable(){
            //public void run(){
                Toast.makeText(WebView.context,"callbackRef",Toast.LENGTH_SHORT).show();
                /* final String callbackRef=getInputData().getString("callbackRef");
                
                if(callbackRef!=null){
                    final CallbackContext callback=(CallbackContext)WebView.backgroundCalls.opt(callbackRef);
                    try{
                        callback.success();
                    }
                    catch(Exception exception){
                        callback.error(exception.getMessage());
                        return Result.failure();
                    }
                    WebView.backgroundCalls.remove(callbackRef);
                } */ 
            //}
        //}).start();
        return Result.success();
    }
    
}
