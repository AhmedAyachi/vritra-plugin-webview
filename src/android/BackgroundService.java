package com.ahmedayachi.webview;

import com.ahmedayachi.webview.WebView;
import android.app.Service;
import android.content.Intent;
import org.apache.cordova.CallbackContext;
import java.lang.Exception;
import java.lang.Runnable;
import android.os.IBinder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Notification;
import android.R;


public class BackgroundService extends Service{

    private static final String channelId="BackgroundServiceChannel";
    //private final NotificationCompat.Builder builder=new NotificationCompat.Builder(this,channelId);
    private static final NotificationChannel channel=new NotificationChannel(channelId,channelId,NotificationManager.IMPORTANCE_DEFAULT);

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
  
    public int onStartCommand(Intent intent,int flags,int startId){
        final String callbackRef=intent.getStringExtra("callbackRef");
        new Thread(new Runnable(){
            public void run(){
                if(callbackRef!=null){
                    final CallbackContext callback=(CallbackContext)WebView.backgroundCalls.opt(callbackRef);
                    try{
                        callback.success();
                    }
                    catch(Exception exception){
                        callback.error(exception.getMessage());
                    }
                    WebView.backgroundCalls.remove(callbackRef);
                } 
            }
        }).start();
        /* this.getSystemService(NotificationManager.class).createNotificationChannel(channel);
        final Notification.Builder builder=new Notification.Builder(this,channelId);
        builder.setContentText("using BackgroundService");
        builder.setContentTitle("BackgroundService");
        builder.setSmallIcon(R.drawable.alert_dark_frame);

        this.startForeground(Integer.parseInt(callbackRef),builder.build()); */
        return super.onStartCommand(intent,flags,startId);
    }

    
}
