package com.ahmedayachi.webview;

import com.ahmedayachi.webview.WebView;
import org.apache.cordova.CallbackContext;
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
        final String uploadRef=data.getString("uploadRef");
        if(uploadRef!=null){
            final CallbackContext callback=(CallbackContext)WebView.fetchCalls.opt(uploadRef);
            if(callback!=null){
                try{
                    final String url=data.getString("url");
                    final JSONObject params=new JSONObject(data.getString("params"));
                    //callback.success();
                    this.setProgressNotification();
                    isFulfilled=true;
                }
                catch(Exception exception){
                    callback.error(exception.getMessage());
                }
            }
            WebView.fetchCalls.remove(uploadRef);
        }

        return isFulfilled?Result.success():Result.failure();
    }

    private void setProgressNotification(/* JSONObject props */){
        Fetcher.createNotificationChannel();
        final int id=new Random().nextInt();
        final NotificationCompat.Builder builder=new NotificationCompat.Builder(WebView.context,channelId);
        builder.setContentTitle("File Download");
        builder.setContentText("Download in progress");
        builder.setSmallIcon(WebView.context.getApplicationInfo().icon);
        builder.setOngoing(true);
        builder.setProgress(100,0,false);
        manager.notify(id,builder.build());
        new Thread(new Runnable(){
            public void run(){
                try{
                    for(int progress=0;progress<=10;progress++){
                        Thread.sleep(1500);
                        builder.setProgress(100,progress*10,false);
                        manager.notify(id,builder.build());
                    }
                    builder.setContentTitle("File Downloaded");
                    builder.setContentText(null);
                    builder.setProgress(0,0,false);
                    builder.setOngoing(false);
                    manager.notify(id,builder.build());
                } 
                catch(Exception exception){}
            }
        }).start();
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