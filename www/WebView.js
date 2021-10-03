const exec=require("cordova/exec");


module.exports={
    show:(options)=>{
        const {onCreated}=options;
        exec(onCreated,null,"WebView","show",[options]);
    },
    useMessage:(onFullfilled)=>{
        exec(null,onFullfilled,"WebView","useMessage",null);
    },
    close:()=>{
        exec(null,null,"WebView","close",null);
    }
}

