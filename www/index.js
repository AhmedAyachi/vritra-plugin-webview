const exec=require("cordova/exec");


module.exports={
    show:(options)=>{
        const {onClose}=options;
        exec(onClose,null,"WebView","show",[options]);
    },
    useStore:(onFullfilled)=>{
        exec(onFullfilled,null,"WebView","useStore",[onFullfilled]);
    },
    setStore:(key,value)=>{
        exec(null,null,"WebView","setStore",[key,value]);
    },
    useMessage:(onFullfilled)=>{
        exec(onFullfilled,null,"WebView","useMessage",[onFullfilled]);
    },
    setMessage:(message)=>{
        exec(null,null,"WebView","setMessage",[message||""]);
    },
    close:(message="")=>{
        exec(null,null,"WebView","close",[message]);
    }
}

