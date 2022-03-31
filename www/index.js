const exec=require("cordova/exec");


module.exports={
    show:(options)=>{
        const {onClose}=options;
        exec(onClose,null,"WebView","show",[options]);
    },
    initiateStore:(state,onFullfilled)=>{
        exec(onFullfilled,null,"WebView","initiateStore",[state]);
    },
    useStore:(onFullfilled)=>{
        exec(onFullfilled,null,"WebView","useStore",[onFullfilled]);
    },
    setStore:(key,value,onFullfilled)=>{
        exec(onFullfilled,null,"WebView","setStore",[key,value]);
    },
    useMessage:(onFullfilled)=>{
        exec(onFullfilled,null,"WebView","useMessage",[]);
    },
    setMessage:(message)=>{
        exec(null,null,"WebView","setMessage",[message||""]);
    },
    close:(message)=>{
        exec(null,null,"WebView","close",[message||""]);
    },
}
