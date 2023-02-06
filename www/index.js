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
    useStore:(callback)=>{
        exec(callback,null,"WebView","useStore",[callback]);
    },
    setStore:(key,value,callback)=>{
        exec(callback,null,"WebView","setStore",[key,value]);
    },
    useMessage:(callback)=>{
        exec(callback,null,"WebView","useMessage",[]);
    },
    setMessage:(message)=>{
        exec(null,null,"WebView","setMessage",[stringify(message)]);
    },
    close:(message)=>{
        exec(null,null,"WebView","close",[message===undefined,stringify(message)]);
    },
}

const stringify=(message)=>message?((typeof(message)==="string")?message:JSON.stringify(message)):"";
