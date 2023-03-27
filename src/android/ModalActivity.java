package com.ahmedayachi.webview;

import com.ahmedayachi.webview.WebViewActivity;
import android.os.Bundle;
import org.json.JSONObject;
import org.json.JSONException;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.content.pm.ActivityInfo.WindowLayout;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.Toast;


public class ModalActivity extends WebViewActivity {

    private JSONObject style=null;
    static private DisplayMetrics metrics=null;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        final Window window=getWindow();
        try{
            String stylejson=intent.getStringExtra("modalStyle");
            if(stylejson!=null){
                style=new JSONObject(stylejson);
            }
            else{
                style=new JSONObject();
            }
            final LayoutParams layoutparams=window.getAttributes();
            if(metrics==null){
                metrics=new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
            }
            layoutparams.width=this.getWidth();
            layoutparams.height=this.getHeight();
            layoutparams.gravity=this.getGravity();
            layoutparams.alpha=this.getAlpha();
            layoutparams.x=this.getX();
            layoutparams.y=this.getY();
            window.setAttributes(layoutparams);
        }
        catch(JSONException exception){
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
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

}
