package com.vritra.webview;

import com.vritra.webview.WebViewActivity;
import android.os.Bundle;
import org.json.JSONObject;
import org.json.JSONException;
import android.view.View;
import android.view.Window;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;
import android.util.DisplayMetrics;
import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import androidx.annotation.NonNull;


public class ModalActivity extends WebViewActivity {
    
    private JSONObject style=null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        try{
            String stylejson=intent.getStringExtra("modalStyle");
            this.style=(stylejson==null)?new JSONObject():new JSONObject(stylejson);
        }
        catch(JSONException exception){}
    }

    @Override
    protected void onStart(){
        super.onStart();
        final Boolean silent=style.optBoolean("silent",false);
        if(!silent){
            final int audioId=WebView.getResourceId("raw","modal_shown");
            MediaPlayer mediaplayer=MediaPlayer.create(this,audioId);
            if(mediaplayer!=null){
                mediaplayer.setLooping(false);
                mediaplayer.setVolume(0.1f,0.1f);
                mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer){
                        mediaplayer.release();
                    }
                });
                mediaplayer.start();
            }
            try{style.put("silent",true);}
            catch(Exception exception){}
        }
    }

    @Override
    protected int getShowAnimation(){
        return WebView.getResourceId("animator","slide_up");
    }

    @Override
    protected int getCloseAnimation(){
        return WebView.getResourceId("animator","slide_down");
    }

    @Override
    protected void setStyle(){
        this.setStatusBar();
        final Window window=getWindow();
        this.setLayout(window);
        final View decorView=window.getDecorView();
        final View rootView=decorView.getRootView();
        final int transparentColor=WebView.getColor("transparent");
        window.setBackgroundDrawable(new ColorDrawable(transparentColor));
        rootView.setBackgroundColor(transparentColor);
        decorView.setBackgroundColor(transparentColor);
        this.setCorners();
        this.setDismissible();
    }

    private void setDismissible(){
        final Boolean dismissible=this.style.optBoolean("dismissible",true);
        if(dismissible){
            Window window=this.getWindow();
            window.setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL,LayoutParams.FLAG_NOT_TOUCH_MODAL);
            window.addFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        }
    }
    public boolean onTouchEvent(MotionEvent event){  
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){   
            this.finish();
            return true;
        }  
        return false;
    }  

    private DisplayMetrics metrics=null;
    private void setLayout(Window window){
        this.metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(this.metrics);
        if(this.style==null){
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        }
        else{
            final LayoutParams layoutParams=window.getAttributes();
            layoutParams.width=this.getWidth();
            layoutParams.height=this.getHeight();
            layoutParams.gravity=this.getGravity();
            layoutParams.alpha=this.getAlpha();
            layoutParams.x=this.getX();
            layoutParams.y=this.getY();
            window.setAttributes(layoutParams);
        }
    }
    private int getWidth(){
        double width=style.optDouble("width",1); 
        if((width<0)||(width>1)){
            width=1;
        }
        width=width*metrics.widthPixels;
        return (int)width;
    }
    private int getHeight(){
        double height=style.optDouble("height",0.85); 
        if((height<0)||(height>1)){
            height=1;
        }
        height=height*metrics.heightPixels;
        return (int)height;
    }
    private int getGravity(){
        String verticalAlign=style.optString("verticalAlign","bottom");
        switch(verticalAlign){
            case "top": return Gravity.TOP;
            case "middle": return Gravity.CENTER;
            case "bottom":
            default: return Gravity.BOTTOM;
        }
    }
    private float getAlpha(){
        return (float)style.optDouble("opacity",1);
    }
    private int getX(){
        double x=style.optDouble("marginLeft",0);
        if((x>=-1)&&(x<=1)){
            x=x*metrics.widthPixels;
        }
        return (int)x;
    }
    private int getY(){
        double y=style.optDouble("marginTop",0);
        if((y>=-1)&&(y<=1)){
            y=y*metrics.heightPixels;
        }
        return (int)y;
    }

    private void setCorners(){
        final Boolean roundedTopLeftCorner=style.optBoolean("roundedTopLeftCorner",true);
        final Boolean roundedTopRightCorner=style.optBoolean("roundedTopRightCorner",true);
        final Boolean roundedBottomLeftCorner=style.optBoolean("roundedBottomLeftCorner",false);
        final Boolean roundedBottomRightCorner=style.optBoolean("roundedBottomRightCorner",false);
        final View webView=this.appView.getView();
        webView.setBackgroundColor(WebView.getColor("transparent"));
        webView.setClipToOutline(true);
        webView.setBackgroundDrawable(new RoundedDrawable(
            roundedTopLeftCorner,roundedTopRightCorner,
            roundedBottomLeftCorner,roundedBottomRightCorner,
            getBackgroundColor()
        ));
    }
    private static class RoundedDrawable extends ColorDrawable {

        static final float cornerRadius=35f;
        
        final private Paint paint=new Paint();
        private float[] radii=new float[]{0,0,0,0,0,0,0,0};

        RoundedDrawable(Boolean roundedTopLeftCorner,Boolean roundedTopRightCorner,Boolean roundedBottomLeftCorner,Boolean roundedBottomRightCorner,int backgroundColor){
            super(WebView.getColor("transparent"));
            if(roundedTopLeftCorner){radii[0]=cornerRadius;radii[1]=cornerRadius;};
            if(roundedTopRightCorner){radii[2]=cornerRadius;radii[3]=cornerRadius;};
            if(roundedBottomRightCorner){radii[4]=cornerRadius;radii[5]=cornerRadius;};
            if(roundedBottomLeftCorner){radii[6]=cornerRadius;radii[7]=cornerRadius;};
            paint.setStrokeWidth(0);
            paint.setColor(backgroundColor);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void draw(@NonNull Canvas canvas){
            final RectF rectF=new RectF();
            rectF.set(getBounds());
            final Path path=new Path();
            path.addRoundRect(rectF,radii,Path.Direction.CW);
            canvas.drawPath(path,paint);
        }

        @Override
        public int getOpacity(){
            return 1;
        }
    }
}
