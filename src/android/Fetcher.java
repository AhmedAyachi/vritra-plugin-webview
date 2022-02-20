package com.ahmedayachi.webview;

import com.ahmedayachi.webview.WebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import android.content.Intent;
import android.content.Context;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.ListenableWorker.Result;
import androidx.work.Data;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.NotificationCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.widget.Toast;
import org.json.JSONObject;
import java.util.Random;
import android.os.Build;


public class Fetcher extends Worker{

    static final String channelId="FetcherChannel";
    static Boolean channelCreated=false;
    protected static final NotificationManagerCompat manager=NotificationManagerCompat.from(WebView.context);

    public Fetcher(Context context,WorkerParameters params){
        super(context,params);
    }
    
   @Override
    public Result doWork(){
        Boolean isFulfilled=false;
        final Data data=this.getInputData();
        final String fetchRef=data.getString("fetchRef");
        if(fetchRef!=null){
            try{
                final CallbackContext callback=(CallbackContext)WebView.callbacks.opt(fetchRef);
                final String url=data.getString("url");
                final JSONObject params=new JSONObject(data.getString("params"));
                this.setNotification(callback);
                isFulfilled=true;
                WebView.callbacks.remove(fetchRef);
            }
            catch(Exception exception){}
        }

        return isFulfilled?Result.success():Result.failure();
    }

    private void setNotification(CallbackContext callback){
        Fetcher.createNotificationChannel();
        final int id=new Random().nextInt();
        final NotificationCompat.Builder builder=new NotificationCompat.Builder(WebView.context,channelId);
        builder.setContentTitle("File Name");
        builder.setContentText("0%");
        builder.setSmallIcon(WebView.context.getApplicationInfo().icon);
        builder.setOngoing(true);
        builder.setProgress(100,0,false);
        manager.notify(id,builder.build());
        this.fetchData(id,builder,callback);
    }

    private void fetchData(int id,NotificationCompat.Builder builder,CallbackContext callback){
        new Thread(new Runnable(){
            public void run(){
                Boolean isFinished=false;
                int progress=0;
                try{
                    final JSONObject params=new JSONObject();
                    while(progress<100){
                        Thread.sleep(1500);
                        progress+=new Random().nextInt(25);
                        isFinished=progress>=100;
                        params.put("progress",progress);
                        params.put("isFinished",isFinished);
                        builder.setProgress(100,progress,false);
                        builder.setContentText(progress+"%");
                        final PluginResult result=new PluginResult(PluginResult.Status.OK,params);
                        result.setKeepCallback(!isFinished);
                        manager.notify(id,builder.build());
                        callback.sendPluginResult(result);
                    }
                    builder.setContentTitle("File Downloaded");
                    builder.setContentText(null);
                    builder.setProgress(0,0,false);
                    builder.setOngoing(false);
                    manager.notify(id,builder.build());
                } 
                catch(Exception exception){
                    callback.error(exception.getMessage());
                }
            }
        }).start();
    }   

    private void upload(String path,JSONObject params){

    }

    static private void createNotificationChannel(){
        if((!channelCreated)&&(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)){
            int importance=NotificationManager.IMPORTANCE_HIGH;
            final NotificationChannel channel=new NotificationChannel(channelId,channelId,importance);
            channel.setDescription(channelId);
            NotificationManager notificationManager=WebView.cordova.getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            channelCreated=true;
        }
    }
}

/* WebView.cordova.getActivity().runOnUiThread(new Runnable(){
    public void run(){
        Toast.makeText(WebView.context,"downloading...",Toast.LENGTH_SHORT).show();
    }
}); */