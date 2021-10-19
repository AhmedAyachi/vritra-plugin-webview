package com.ahmedayachi.webview;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.StringTokenizer;
import java.lang.NumberFormatException;


public class Store{
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
            setProperty(lasttoken,value,property);
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

    static private void setProperty(String token,Object value,JSONObject source){
        int bracketindex=token.indexOf("[");
        if(token.endsWith("]")&&(bracketindex>0)){
            String arraytoken=token.substring(0,bracketindex);
            int index=Integer.parseInt(token.subSequence(bracketindex+1,token.length()-1).toString());//NumberFormatException
            JSONArray array=source.getJSONArray(arraytoken);
            array.put(index,value);
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
