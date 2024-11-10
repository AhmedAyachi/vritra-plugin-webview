package com.vritra.webview;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.StringTokenizer;
import java.util.ArrayList;
import android.util.Log;


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

    public ArrayList<Object> get(String path) throws Exception {
        final ArrayList<String> tokens=getTokens(path);
        final String lastToken=tokens.remove(tokens.size()-1);
        final ArrayList<Object> properties=getTargetObjects(tokens);
        final ArrayList<Object> values=new ArrayList<Object>();
        final int size=properties.size();
        for(int i=0;i<size;i++){
            values.add(getProperty(properties.get(i),lastToken));
        }
        return values;
    }

    public void set(String path,Object value) throws Exception {
        final ArrayList<String> tokens=getTokens(path);
        final String lastToken=tokens.remove(tokens.size()-1);
        final ArrayList<Object> properties=getTargetObjects(tokens);
        final int size=properties.size();
        for(int i=0;i<size;i++){
            setProperty(properties.get(i),lastToken,value);
        }
    }

    public void delete(String path) throws Exception {
        final ArrayList<String> tokens=getTokens(path);
        final String lastToken=tokens.remove(tokens.size()-1);
        final ArrayList<Object> properties=getTargetObjects(tokens);
        final int size=properties.size();
        for(int i=0;i<size;i++){
            deleteProperty(properties.get(i),lastToken);
        }
    }

    private ArrayList<Object> getTargetObjects(ArrayList<String> tokens) throws Exception {
        ArrayList<Object> properties=new ArrayList<Object>();
        properties.add(this.store);
        final int length=tokens.size();
        for(int i=0;i<length;i++){
            final String token=tokens.get(i);
            for(int j=0;j<properties.size();j++){
                final Object property=properties.get(j);
                final ArrayList<Object> objects=getTokenProperties(token,property);
                flatMap(properties,property,objects);
                j+=objects.size();
            }
        }
        return properties;
    }

    static private ArrayList<String> getTokens(String path){
        final ArrayList<String> tokens=new ArrayList<String>();
        final StringTokenizer tokenizer=new StringTokenizer(path.trim(),".[");
        while(tokenizer.hasMoreTokens()){
            tokens.add(tokenizer.nextToken());
        }
        return tokens;
    }

    static private ArrayList<Object> getTokenProperties(String token,Object source) throws Exception {
        ArrayList<Object> properties=new ArrayList<Object>();
        final Object object=getProperty(source,token);
        if(token.indexOf("*")>-1){
            final JSONArray array=(JSONArray)object;
            final int length=array.length();
            for(int i=0;i<length;i++){
                properties.add(array.get(i));
            }
        }
        else{
            properties.add(object);
        }
        return properties;
    }

    static private Object getProperty(Object source,String token) throws Exception {
        if(source!=null){
            int bracketindex=token.indexOf("]");
            if(bracketindex>0){
                final String indexStr=token.subSequence(0,bracketindex).toString().trim();
                if(indexStr.equals("*")) return source;
                else{
                    final JSONArray array=(JSONArray)source;
                    if(indexStr.equals("pop")) return array.remove(array.length()-1);
                    else if(indexStr.equals("shift")) return array.remove(0);
                    else if(indexStr.equals("last")){
                        final int length=array.length();
                        return array.opt(length-1);
                    }
                    else{
                        int index=Integer.parseInt(indexStr);
                        return array.opt(index);
                    }
                }
            }
            else{
                final JSONObject object=(JSONObject)source;
                return object.opt(token);
            }
        }
        else throw new Exception("cannot read properties of null (reading \""+token+"\")");
    }

    static private void setProperty(Object source,String token,Object value) throws Exception {
        if(source!=null){
            int bracketindex=token.indexOf("]");
            if(bracketindex>0){
                final JSONArray array=(JSONArray)source;
                String indexStr=token.subSequence(0,bracketindex).toString().trim();
                if(indexStr.equals("*")){
                    final int length=array.length();
                    for(int i=0;i<length;i++){
                        array.put(i,value);
                    }
                }
                else if(indexStr.equals("push")){
                    array.put(value);
                }
                else if(indexStr.equals("unshift")){
                    for(int i=array.length()-1;i>=0;i--){
                        array.put(i+1,array.get(i));
                    }
                    array.put(0,value);
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
                else{
                    int index=Integer.parseInt(indexStr);
                    array.put(index,value);
                }
            }
            else{
                final JSONObject object=(JSONObject)source;
                object.put(token,value);
            }
        }
        else throw new Exception("cannot set properties of null (setting \""+token+"\")");
    }

    static private void deleteProperty(Object source,String token) throws Exception {
        if(source!=null){
            int bracketindex=token.indexOf("]");
            if(bracketindex>0){
                final JSONArray array=(JSONArray)source;
                String indexStr=token.subSequence(0,bracketindex).toString().trim();
                if(indexStr.equals("*")){
                    final int length=array.length();
                    for(int i=0;i<length;i++){
                        array.remove(0);
                    }
                }
                else if(indexStr.equals("push")){
                    array.put(null);
                }
                else if(indexStr.equals("unshift")){
                    for(int i=array.length()-1;i>=0;i--){
                        array.put(i+1,array.get(i));
                    }
                    array.put(0,null);
                }
                else if(indexStr.equals("pop")){
                    array.remove(array.length()-1);
                }
                else if(indexStr.equals("shift")){
                    array.remove(0);
                }
                else if(indexStr.equals("last")){
                    final int length=array.length();
                    if(length>0){
                        array.remove(length-1);
                    }
                }
                else{
                    int index=Integer.parseInt(indexStr);
                    array.remove(index);
                }
            }
            else{
                final JSONObject object=(JSONObject)source;
                object.remove(token);
            }
        }
        else throw new Exception("cannot delete properties of null (deleting \""+token+"\")");
    }

    static private void flatMap(ArrayList<Object> array,Object target,ArrayList<Object> items){
        int index=array.indexOf(target);
        array.remove(target);
        final int length=items.size();
        for(int i=0;i<length;i++){
            final Object item=items.get(i);
            array.add(index+i,item);
        }
    }
}
