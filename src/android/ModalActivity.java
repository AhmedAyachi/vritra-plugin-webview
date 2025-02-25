package com.vritra.webview;

import com.vritra.webview.WebViewActivity;
import android.os.Bundle;
import org.json.JSONObject;
import org.json.JSONException;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.util.DisplayMetrics;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.animation.ObjectAnimator;
import android.util.Log;


public class ModalActivity extends WebViewActivity {
    
    private JSONObject style=null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        try{
            String stylejson=intent.getStringExtra("modalStyle");
            this.style=(stylejson==null)?new JSONObject():new JSONObject(stylejson);
            if(this.dismissible){
                this.dismissible=this.style.optBoolean("dismissible",true);
            }
        }
        catch(JSONException exception){}
    }

    @Override
    protected void onStart(){
        super.onStart();
        this.setSilent();
    }

    private void setSilent(){
        final Boolean silent=style.optBoolean("silent",true);
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
    protected Boolean isNavigationBarTranslucent(){
        return true;
    }

    @Override
    protected int getShowAnimation(){
        return WebView.getResourceId("anim","showanim_translate_up");
    }
    @Override
    protected int getPreActivityCloseAnimation(){
        return 0;
    }

    @Override
    protected int getCloseAnimation(){
        return WebView.getResourceId("anim","hideanim_translate_down");
    }
    @Override
    protected int getPreActivityShowAnimation(){
        return 0;
    }

    @Override
    protected void setStyle(){
        super.setStyle();
        this.setLayout();
        final int transparentColor=WebView.getColor("transparent");
        final Window window=getWindow();
        window.setBackgroundDrawable(new ColorDrawable(transparentColor));
        final View decorView=window.getDecorView();
        decorView.setBackgroundColor(transparentColor);
        decorView.setClipToOutline(true);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        this.setCorners();
        if(this.dismissible){
            this.setDismissible();
        }
    }

    private void setDismissible(){
        final View webView=this.appView.getView();
        this.setNotch(webView);
        this.setOutSlideClick(webView);
        this.setupGestureRecognizer(webView);
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
        final GradientDrawable gradientDrawable=new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(WebView.getColor(notchColor));
        gradientDrawable.setCornerRadius(35); 
        notch.setBackground(gradientDrawable);
        final double fraction=0.1;
        final int width=this.getWebViewWidth();
        final int height=this.getWebViewHeight();
        final ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams((int)(fraction*width),10);
        ((ViewGroup)parentView).addView(notch,layoutParams);
        notch.setX((int)((1-fraction)*width/2));
        notch.setY(20);
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
                        final float velocity=duration>0?(dy/duration):10;
                        if(dy>threshold||(velocity>=2)){self.finish();}
                        else{
                            dragEnabled=false;
                            ObjectAnimator animator=ObjectAnimator.ofFloat(view,"translationY",viewY);
                            animator.setDuration(250);
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
        WebView.context.getDisplay().getMetrics(this.metrics);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        if(this.style!=null){
            final View webView=this.appView.getView();
            final ViewGroup.LayoutParams layoutParams=webView.getLayoutParams();
            layoutParams.width=this.getWebViewWidth();
            layoutParams.height=this.getWebViewHeight();
            webView.setX(this.getX());
            webView.setY(this.getY());
            webView.setAlpha(this.getAlpha());
            setVerticalAlign(webView);
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
        y+=getNavigationBarHeight();
        return (int)y;
    }
    private void setVerticalAlign(View view){
        String verticalAlign=style.optString("verticalAlign","bottom");
        if(!verticalAlign.equals("top")){
            final float viewY=view.getY();
            final float offset=metrics.heightPixels-this.getWebViewHeight();
            if(verticalAlign.equals("bottom")){
                view.setY(viewY+offset);
            }
            else{
                view.setY(viewY+offset/2);
            }
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
            int statusbarHeight=0;
            if(!this.isStatusBarTranslucent()){
                final int resourceId=WebView.resources.getIdentifier("status_bar_height","dimen","android");
                if(resourceId>0){
                    statusbarHeight=WebView.resources.getDimensionPixelSize(resourceId);
                }
            }
            
            height=height*(metrics.heightPixels+getNavigationBarHeight()-statusbarHeight);
            this.webViewHeight=(int)height;
        }
        return this.webViewHeight;
    }

    private int navigationBarHeight=0;
    private int getNavigationBarHeight(){
        if(navigationBarHeight<=0){
            final int resourceId=WebView.resources.getIdentifier("navigation_bar_height","dimen","android");
            if(resourceId>0){
                this.navigationBarHeight=WebView.resources.getDimensionPixelSize(resourceId);
            }
        }
        return this.navigationBarHeight;
    }

    private void setCorners(){
        final Boolean roundedTopLeftCorner=style.optBoolean("roundedTopLeftCorner",true);
        final Boolean roundedTopRightCorner=style.optBoolean("roundedTopRightCorner",true);
        final Boolean roundedBottomLeftCorner=style.optBoolean("roundedBottomLeftCorner",false);
        final Boolean roundedBottomRightCorner=style.optBoolean("roundedBottomRightCorner",false);
        final View webView=this.appView.getView();
        setViewRoundedCorners(webView,roundedTopLeftCorner,roundedTopRightCorner,roundedBottomLeftCorner,roundedBottomRightCorner);
        webView.setClipToOutline(true);
    }

    private static void setViewRoundedCorners(View view,Boolean roundedTopLeftCorner,Boolean roundedTopRightCorner,Boolean roundedBottomLeftCorner,Boolean roundedBottomRightCorner){
        if(roundedTopLeftCorner||roundedTopRightCorner||roundedBottomLeftCorner||roundedBottomRightCorner){
            view.setOutlineProvider(new ViewOutlineProvider(){
                @Override
                public void getOutline(View view,Outline outline){
                    final int radius=35;
                    final Boolean roundedTop=roundedTopLeftCorner&&roundedTopRightCorner;
                    final Boolean roundedBottom=roundedBottomLeftCorner&&roundedBottomRightCorner;
                    if(roundedTop&&roundedBottom){
                        outline.setRoundRect(0,0,view.getWidth(),view.getHeight(),radius);
                    }
                    else{
                        outline.setRoundRect(
                            ((roundedBottomRightCorner&&!roundedBottom)||(roundedTopRightCorner&&(!roundedTopLeftCorner)))?-radius:0,
                            ((roundedBottomLeftCorner&&!roundedTopLeftCorner)||(roundedBottomRightCorner&&!roundedTopRightCorner))?-radius:0,
                            view.getWidth()+(((roundedBottomLeftCorner&&!roundedBottom)||(roundedTopLeftCorner&&!roundedTop))?radius:0),
                            view.getHeight()+((roundedBottomLeftCorner||roundedBottomRightCorner)?0:radius),
                            radius
                        );
                    }
                }
            });
        }
    }
}
