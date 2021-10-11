const exec=require("cordova/exec");


module.exports={
    show:(options)=>{
        const {onClose}=options;
        exec(onClose,null,"WebView","show",[options]);
    },
    useMessage:(onFullfilled)=>{
        exec(onFullfilled,null,"WebView","useMessage",[onFullfilled]);
    },
    setMessage:(message)=>{
        exec(null,null,"WebView","setMessage",[message]);
    },
    close:()=>{
        exec(null,null,"WebView","close",null);
    }
}

