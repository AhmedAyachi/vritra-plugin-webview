const exec=require("cordova/exec");


module.exports={
    defineWebViews:(webviews,fallback)=>{
        exec(null,fallback,"WebView","defineWebViews",[webviews]);
    },
    show:(options)=>{
        const {onClose,message}=options;
        if((message!==undefined)&&(typeof(message)!=="string")){
            options.message=JSON.stringify(message);
        }
        exec(onClose,null,"WebView","show",[options]);
    },
    initiateStore:(state,callback)=>{
        exec(callback,null,"WebView","initiateStore",[state]);
    },
    useStore:(path,callback,fallback)=>{
        if(typeof(path)==="function"){
            fallback=callback;
            callback=path;
            path="";
        }
        exec(callback&&((values)=>{
            if(Array.isArray(values)&&(values.length<2)){
                callback(values[0]);
            }
            else{callback(values)}
        }),fallback,"WebView","useStore",[path]);
    },
    setStore:(path,value,callback,fallback)=>{
        const multiSetting=Array.isArray(path);
        const deletables=[];
        if(multiSetting){
            fallback=callback;
            callback=value;
            value=undefined;
            let {length}=path;
            for(let i=1;i<length;i+=2){
                const value=path[i];
                if(value===undefined){
                    const keyIndex=i-1,key=path[keyIndex];
                    deletables.push(key);
                    path.splice(keyIndex,2);
                    length-=2;
                    i-=2;
                }
            }
        }
        else if(typeof(value)==="function"){
            fallback=callback;
            callback=value;
            deletables.push(path);
        }
        else if(value===undefined){
            deletables.push(path);
        }
        exec(callback,fallback,"WebView","setStore",[path,value,multiSetting,deletables]);
    },
    useMessage:(callback)=>{
        exec(callback,null,"WebView","useMessage",[]);
    },
    setMessage:(message)=>{
        exec(null,null,"WebView","setMessage",[stringify(message)]);
    },
    close:(message)=>{
        exec(null,null,"WebView","close",[stringify(message),message===undefined]);
    },
}

const stringify=(message)=>message?((typeof(message)==="string")?message:JSON.stringify(message)):"";
