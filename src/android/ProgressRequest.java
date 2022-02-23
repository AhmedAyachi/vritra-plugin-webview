package com.ahmedayachi.webview;

import okhttp3.RequestBody;
import okhttp3.MediaType;
import java.io.FileInputStream;
import android.os.Handler;
import android.os.Looper;
import okio.BufferedSink;
import java.lang.Runnable;
import java.io.File;
import java.io.IOException;


public class ProgressRequest extends RequestBody{
    private File file;
    private String mPath;
    private UploadCallbacks listener;
    private String type;

    private static final int DEFAULT_BUFFER_SIZE=2048;
    public ProgressRequest(String type,File file,UploadCallbacks listener){
        this.type=type;
        this.file=file;
        this.listener=listener;
    }
    
    @Override
    public MediaType contentType(){
        return MediaType.parse(type+"/*");
    }
    
    @Override
    public long contentLength() throws IOException{
      return file.length();
    }
    
    @Override
    public void writeTo(BufferedSink sink) throws IOException{
        long fileLength=file.length();
        byte[] buffer=new byte[DEFAULT_BUFFER_SIZE];
        final FileInputStream inputstream=new FileInputStream(file);
        long uploaded=0;
        try{
            int read;
            final Handler handler=new Handler(Looper.getMainLooper());
            while((read=inputstream.read(buffer))!=-1){
                handler.post(new ProgressUpdater(uploaded, fileLength));
                uploaded+=read;
                sink.write(buffer,0,read);
            }
        }
        finally{
            inputstream.close();
        }
    }
    
    private class ProgressUpdater implements Runnable{
        private long uploaded;
        private long total;
        public ProgressUpdater(long uploaded, long total){
            this.uploaded=uploaded;
            this.total=total;
        }

        @Override
        public void run() {
            listener.onProgress((int)(100*this.uploaded/this.total));            
        }
    }

    public interface UploadCallbacks{
        void onProgress(int progress);
        void onError();
        void onFinish();
    }
}
