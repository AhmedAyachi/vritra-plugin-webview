const exec=require("cordova/exec");


module.exports={
    show:(options)=>{
        const {onClose}=options;
        exec(onClose,null,"WebView","show",[options]);
    },
    initiateStore:(state,onFullfilled)=>{
        exec(onFullfilled,null,"WebView","initiateStore",[state,onFullfilled]);
    },
    useStore:(onFullfilled)=>{
        exec(onFullfilled,null,"WebView","useStore",[onFullfilled]);
    },
    setStore:(key,value,onFullfilled)=>{
        exec(onFullfilled,null,"WebView","setStore",[key,value,onFullfilled]);
    },
    useMessage:(onFullfilled)=>{
        exec(onFullfilled,null,"WebView","useMessage",[onFullfilled]);
    },
    setMessage:(message)=>{
        exec(null,null,"WebView","setMessage",[message||""]);
    },
    close:(message)=>{
        exec(null,null,"WebView","close",[message||""]);
    },
    fetch:(url,props={})=>{
        const {onProgress,onFail}=props;
        exec(onProgress,onFail,"WebView","fetch",[url,props]);
    }
}
