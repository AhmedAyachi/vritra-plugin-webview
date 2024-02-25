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
import android.animation.ObjectAnimator;
//import android.widget.Toast;


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
        this.setSilent();
    }

    private void setSilent(){
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
        this.setLayout();
        final View decorView=window.getDecorView();
        final View rootView=decorView.getRootView();
        final int transparentColor=WebView.getColor("transparent");
        window.setBackgroundDrawable(new ColorDrawable(transparentColor));
        rootView.setBackgroundColor(transparentColor);
        decorView.setBackgroundColor(transparentColor);
        rootView.setClipToOutline(true);
        decorView.setClipToOutline(true);
        this.setCorners();
        this.setDismissible();
    }

    private void setDismissible(){
        final Boolean dismissible=this.style.optBoolean("dismissible",true);
        if(dismissible){
            final View webView=this.appView.getView();
            this.setNotch(webView);
            this.setOutSlideClick(webView);
            this.setupGestureRecognizer(webView);
        }
    }
    public void setOutSlideClick(View view){  
        final ModalActivity self=this;
        final View rootView=view.getRootView();
        rootView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view,MotionEvent event){
                final int action=event.getAction();
                if(action==MotionEvent.ACTION_UP){self.finish();}
                return true;
            }
        });
    }
    private void setNotch(View parentView){
        final String notchColor=style.optString("notchColor","#1a000000");
        final View notch=new View(this);
        final double fraction=0.1;
        final int width=this.getWebViewWidth();
        final ViewGroup.LayoutParams LayoutParams=new ViewGroup.LayoutParams((int)(fraction*width),10);
        ((ViewGroup)parentView).addView(notch,LayoutParams);
        notch.setX((int)((1-fraction)*width/2));
        notch.setY(25);
        notch.setBackgroundDrawable(new RoundedDrawable(true,true,true,true,WebView.getColor(notchColor)));
    }
    private void setupGestureRecognizer(View view){
        final ModalActivity self=this;
        final float viewY=view.getY();
        final int threshold=(int)(0.6*this.getWebViewHeight());
        view.setOnTouchListener(new View.OnTouchListener(){
            float startY=0;
            long startTime=0;
            boolean dragEnabled=false;
            @Override
            public boolean onTouch(View view,MotionEvent event){
                final int action=event.getAction();
                final float y=event.getRawY();
                if(action==MotionEvent.ACTION_DOWN){
                    final float distance=y-viewY;
                    if((distance>=0)&&(distance<=200)){
                        dragEnabled=true;
                        startY=y;
                        startTime=event.getEventTime();
                    }
                }
                else if(dragEnabled){
                    final int dy=(int)(y-startY);
                    if((action==MotionEvent.ACTION_UP)||(action==MotionEvent.ACTION_CANCEL)){
                        final long duration=event.getEventTime()-startTime;
                        final float velocity=dy/duration;
                        if(dy>threshold||(velocity>=2)){self.finish();}
                        else{
                            dragEnabled=false;
                            ObjectAnimator animator=ObjectAnimator.ofFloat(view,"translationY",viewY);
                            animator.setDuration(100);
                            animator.start();
                        }
                    }
                    else if(dy>0){
                        view.setTranslationY(viewY+dy);
                    }
                }
                return false;
            }
        });
    }

    private DisplayMetrics metrics=null;
    private void setLayout(){
        final Window window=this.getWindow();
        this.metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(this.metrics);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        if(this.style!=null){
            final View webView=this.appView.getView();
            final ViewGroup.LayoutParams layoutParams=webView.getLayoutParams();
            layoutParams.width=this.getWebViewWidth();
            layoutParams.height=this.getWebViewHeight();
            webView.setX(this.getX());
            webView.setY(this.getY());
            webView.setAlpha(this.getAlpha());
            setverticalAlign(webView);
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
    private void setverticalAlign(View view){
        String verticalAlign=style.optString("verticalAlign","bottom");
        final float viewY=view.getY();
        final float offset=metrics.heightPixels-this.getWebViewHeight();
        switch(verticalAlign){
            case "top":break;
            case "middle":view.setY(viewY+offset/2);break;
            case "bottom":
            default: view.setY(viewY+offset);break;
        }
    }

    int webViewWidth=-1;
    private int getWebViewWidth(){
        if(this.webViewWidth<0){
            double width=style.optDouble("width",1); 
            if((width<0)||(width>1)){
                width=1;
            }
            width=width*metrics.widthPixels;
            this.webViewWidth=(int)width;
        }
        return this.webViewWidth;
    }

    int webViewHeight=-1;
    private int getWebViewHeight(){
        if(this.webViewHeight<0){
            double height=style.optDouble("height",0.85); 
            if((height<0)||(height>1)){
                height=1;
            }
            height=height*metrics.heightPixels;
            this.webViewHeight=(int)height;
        }
        return this.webViewHeight;
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
