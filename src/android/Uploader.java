package com.ahmedayachi.webview;

import com.ahmedayachi.webview.WebView;
import com.ahmedayachi.webview.UploadAPI;
import com.ahmedayachi.webview.UploaderClient;
import com.ahmedayachi.webview.FileUtils;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import android.content.Intent;
import android.content.Context;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.ListenableWorker.Result;
import javafx.scene.media.Media;
import androidx.work.Data;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.NotificationCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.widget.Toast;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.os.Build;
import android.net.Uri;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Uploader extends Worker{

    static final String channelId="UploaderChannel";
    static Boolean channelCreated=false;
    protected static final NotificationManagerCompat manager=NotificationManagerCompat.from(WebView.context);

    public Uploader(Context context,WorkerParameters params){
        super(context,params);
    }
    
   @Override
    public Result doWork(){
        final Data data=this.getInputData();
        final String callbackRef=data.getString("callbackRef");
        if(callbackRef!=null){
            try{
                final CallbackContext callback=(CallbackContext)WebView.callbacks.opt(callbackRef);
                final JSONObject params=new JSONObject(data.getString("params"));
                this.upload(params,callback);
                WebView.callbacks.remove(callbackRef);
            }
            catch(Exception exception){}
        }

        return Result.success();
    }

    private void upload(JSONObject params,CallbackContext callback){
        final JSONArray files=params.optJSONArray("files");
        final ArrayList<MultipartBody.Part> fileParts=new ArrayList<MultipartBody.Part>(1);
        
        try{
            final int length=files.length();
            for(int i=0;i<length;i++){
                final JSONObject props=files.optJSONObject(i);
                final File file=new File(FileUtils.getPath(WebView.context,Uri.parse(props.optString("path"))));
                final RequestBody fileRequest=RequestBody.create(MediaType.parse(props.optString("type","*")),file);
                final MultipartBody.Part filePart=MultipartBody.Part.createFormData("filename",props.optString("newName",file.getName()),fileRequest);
                fileParts.add(filePart);
            }
            
            final RequestBody dataRequest=RequestBody.create(MediaType.parse("text/plain"),params.optString("body","body"));
            final Retrofit client=UploaderClient.getClient(params.optString("url"));
            final UploadAPI api=client.create(UploadAPI.class);
            final Call call=api.uploadFile(fileParts,dataRequest);
            call.enqueue(new Callback(){
                @Override
                public void onResponse(Call call,Response response){
                    final String message=params.optString("toast",null);
                    if(message!=null){
                        WebView.cordova.getActivity().runOnUiThread(new Runnable(){
                            public void run(){
                                Toast.makeText(WebView.context,message,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    callback.success();
                }
                @Override
                public void onFailure(Call call,Throwable throwable){
                    callback.error(throwable.getMessage());
                }
            });
        }
        catch(Exception exception){
            callback.error(exception.getMessage());
        }
    }

    private void setNotification(CallbackContext callback){
        Uploader.createNotificationChannel();
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