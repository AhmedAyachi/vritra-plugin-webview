package com.wurm.webview;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.StringTokenizer;
import java.lang.NumberFormatException;
import java.util.ArrayList;


public class Store {
    private JSONObject store=null;

    public Store(){
        store=new JSONObject();
    }
    public void initiate(JSONObject state){
        store=state==null?new JSONObject():state;
    }

    public JSONObject toJSONObject(){
        return this.store;
    }
    public void set(String path,Object value) throws JSONException{
        final ArrayList<String> tokens=new ArrayList<String>();
        final StringTokenizer tokenizer=new StringTokenizer(path.trim(),".");
        while(tokenizer.hasMoreTokens()){
            tokens.add(tokenizer.nextToken());
        }
        int last=tokens.size()-1;
        ArrayList<JSONObject> properties=new ArrayList<JSONObject>();
        properties.add(store);
        for(int i=0;i<last;i++){
            final String token=tokens.get(i);
            final int length=properties.size();
            for(int j=0;j<length;j++){
                final JSONObject property=properties.get(j);
                flatMap(properties,property,getProperties(token,property));
            }
        }

        final String lasttoken=tokens.get(last);
        properties.forEach(property->{
            try{
                setProperty(lasttoken,value,property);
            }
            catch(JSONException exception){}
        });
    }

    static private ArrayList<JSONObject> getProperties(String token,JSONObject source) throws JSONException,NumberFormatException{
        ArrayList<JSONObject> properties=new ArrayList<JSONObject>();
        int bracketindex=token.indexOf("[");
        if(token.endsWith("]")&&(bracketindex>0)){
            String arraytoken=token.substring(0,bracketindex);
            String indexStr=token.subSequence(bracketindex+1,token.length()-1).toString();
            JSONArray array=source.getJSONArray(arraytoken);
            if(indexStr.equals("*")){
                final int length=array.length();
                for(int i=0;i<length;i++){
                    properties.add(array.getJSONObject(i));
                }
            }
            else{
                int index=Integer.parseInt(indexStr);
                properties.add(array.getJSONObject(index));
            }
        }
        else{
            properties.add(source.getJSONObject(token));
        }
        return properties;
    }

    static private void setProperty(String token,Object value,JSONObject source) throws JSONException{
        int bracketindex=token.indexOf("[");
        if(token.endsWith("]")&&(bracketindex>0)){
            String arraytoken=token.substring(0,bracketindex);
            String indexStr=token.subSequence(bracketindex+1,token.length()-1).toString();
            JSONArray array=source.getJSONArray(arraytoken);

            try{
                int index=Integer.parseInt(indexStr);
                array.put(index,value);
            }
            catch(NumberFormatException exception){
                if(indexStr.equals("push")){
                    array.put(value);
                }
                else if(indexStr.equals("unshift")){
                    final JSONArray newarray=new JSONArray();
                    final int length=array.length();
                    newarray.put(value);
                    for(int i=0;i<length;i++){
                        newarray.put(array.get(i));
                    }
                    source.put(arraytoken,newarray);
                }
                else if(indexStr.equals("pop")){
                    array.remove(array.length()-1);
                }
                else if(indexStr.equals("shift")){
                    array.remove(0);
                }
                else if(indexStr.equals("last")){
                    final int length=array.length();
                    array.put(length-1,value);
                }
            }
        }
        else{
            source.put(token,value);
        }
    }

    static private void flatMap(ArrayList<JSONObject> array,JSONObject target,ArrayList<JSONObject> items){
        int index=array.indexOf(target);
        array.remove(target);
        final int length=items.size(); 
        for(int i=0;i<length;i++){
            final JSONObject item=items.get(i);
            array.add(index+i,item);
        }
    }
}
