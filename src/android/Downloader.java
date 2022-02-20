package com.ahmedayachi.webview;

import com.ahmedayachi.webview.WebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.ListenableWorker.Result;
import androidx.work.Data;
import android.content.Context;
import android.content.Intent;
import android.app.DownloadManager;
import android.net.Uri;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Environment;


public class Downloader extends Worker{

    public Downloader(Context context,WorkerParameters params){
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
                this.download(params,callback);
                isFulfilled=true;
                WebView.callbacks.remove(fetchRef);
            }
            catch(Exception exception){}
        }

        return isFulfilled?Result.success():Result.failure();
    }

    private void download(JSONObject params,CallbackContext callback){
        try{
            final String url="https://images.hdqwalls.com/wallpapers/windows-11-4k-k5.jpg";
            final DownloadManager downloader=(DownloadManager)WebView.cordova.getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            final Uri uri=Uri.parse(url.replaceAll(" ","%20"));
            final DownloadManager.Request request=new DownloadManager.Request(uri);
            request.setTitle("My File");
            request.setMimeType("image/jpg");
            request.setDescription("Downloding");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(false);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,"/img.jpg");
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE);
            downloader.enqueue(request);
            WebView.context.registerReceiver(new BroadcastReceiver(){
                @Override
                public void onReceive(Context context,Intent intent){
                    try{
                        callback.success(); 
                    }
                    catch(Exception exception){}
                }
            },new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
        catch(Exception exception){
            callback.error(exception.getMessage());
        }
    }
}
