package com.vritra.webview;

import com.vritra.webview.WebViewActivity;
import android.os.Bundle;
import org.json.JSONObject;
import org.json.JSONException;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.view.ViewOutlineProvider;
import android.graphics.Rect;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.animation.ObjectAnimator;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.graphics.Insets;
import androidx.window.layout.WindowMetricsCalculator;
//import android.util.Log;


public class ModalActivity extends WebViewActivity {
    
    private JSONObject style=null;
    private final ModalActivity self=this;

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

    @Override
    public void finish(){
        final Window window=getWindow();
        this.runOnUiThread(new Runnable(){
            @Override
            public void run(){
                window.setStatusBarColor(WebView.getColor("transparent"));
                if(self.statusBarView!=null){
                    ((ViewGroup)self.statusBarView.getParent()).removeView(self.statusBarView);
                }
            }
        });
        super.finish();
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
            try { 
                style.put("silent",true); 
            }
            catch(Exception exception){}
        }
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

    private Rect windowBounds=null;
    private Insets windowInsets=null;

    @Override
    protected void setStyle(){
        super.setStyle();
        final int transparentColor=WebView.getColor("transparent");
        final Window window=getWindow();
        WindowCompat.setDecorFitsSystemWindows(window,false);
        window.setBackgroundDrawable(new ColorDrawable(transparentColor));
        final View decorView=window.getDecorView();
        decorView.setBackgroundColor(transparentColor);
        decorView.setClipToOutline(true);

        final View webView=this.appView.getView();
        ViewCompat.setOnApplyWindowInsetsListener(webView,new OnApplyWindowInsetsListener(){
            boolean once=false;
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view,WindowInsetsCompat insets){
                self.windowBounds=WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(self).getBounds();
                self.windowInsets=ViewCompat.getRootWindowInsets(view).getInsets(WindowInsetsCompat.Type.systemBars());
                self.setLayout();
                if(!once){
                    once=true;
                    self.setCorners();
                    if(self.dismissible) self.setDismissible();
                }
                return insets;
            }
        });
    }
    private void setDismissible(){
        final View webView=this.appView.getView();
        this.setNotch(webView);
        this.setOutSlideClick(webView);
        this.setupGestureRecognizer(webView);
    }
    public void setOutSlideClick(View view){  
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

    private void setLayout(){
        final Window window=this.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        if(this.style!=null){
            final View webView=this.appView.getView();
            ViewGroup.LayoutParams layoutParams=webView.getLayoutParams();
            if(webView.getParent() instanceof FrameLayout){
                ((FrameLayout.LayoutParams)layoutParams).gravity=0;
            }
            else if(webView.getParent() instanceof LinearLayout){
                ((LinearLayout.LayoutParams)layoutParams).weight=0;
            }
            layoutParams.width=this.getWebViewWidth();
            layoutParams.height=this.getWebViewHeight();
            webView.setLayoutParams(layoutParams);
            this.setWebViewX(webView);
            this.setWebViewY(webView);
            this.setVerticalAlign(webView);
            webView.setAlpha(this.getAlpha());
        }
    }
    private float getAlpha(){
        return (float)style.optDouble("opacity",1);
    }
    private void setWebViewX(View view){
        double x=style.optDouble("marginLeft",0);
        if((x>=-1)&&(x<=1)) x=Math.round(x*windowBounds.width());
        if(!this.isNavigationBarTranslucent()) x+=this.windowInsets.left;
        view.setX((int)x);
    }
    private void setWebViewY(View view){
        double y=style.optDouble("marginTop",0);
        if((y>=-1)&&(y<=1)) y=Math.round(y*windowBounds.height());
        if(!this.isStatusBarTranslucent()) y+=this.windowInsets.top;
        view.setY((int)y);
    }
    private void setVerticalAlign(View view){
        String verticalAlign=style.optString("verticalAlign","bottom");
        int offsetY=(int)view.getY();
        if(!verticalAlign.equals("top")){
            int freeSpace=this.getAvailableHeight()-this.getWebViewHeight();
            if(verticalAlign.equals("middle")) freeSpace/=2;
            offsetY+=freeSpace;
        }
        view.setY(offsetY);
    }

    int webViewWidth=-1;
    private int getWebViewWidth(){
        if(this.webViewWidth<0){
            double width=style.optDouble("width",1); 
            if(width<=0) width=1;
            width=width*getAvailableWidth();
            this.webViewWidth=(int)Math.round(width);
        }
        return this.webViewWidth;
    }

    private int webViewHeight=-1;
    private int getWebViewHeight(){
        if(this.webViewHeight<0){
            double height=style.optDouble("height",0.85); 
            if(height<=0) height=1;
            height=height*this.getAvailableHeight();
            this.webViewHeight=(int)Math.round(height);
        }
        return this.webViewHeight;
    }

    private int getAvailableWidth(){
        int maxWidth=windowBounds.width();
        if(!this.isNavigationBarTranslucent()){
            maxWidth-=this.windowInsets.left+this.windowInsets.right;
        }
        return maxWidth;
    }

    private int getAvailableHeight(){
        int maxHeight=windowBounds.height();
        if(!this.isStatusBarTranslucent()){
            maxHeight-=this.windowInsets.top;
        }
        if(!this.isNavigationBarTranslucent()){
            maxHeight-=this.windowInsets.bottom;
        }
        return maxHeight;
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
