const exec=require("cordova/exec");


module.exports={
    show:(options)=>{
        const {onCreated}=options;
        exec(onCreated,null,"WebView","show",[options]);
    },
    back:(onFullfilled)=>{
        exec(onFullfilled,null,"WebView","back",null);
    },
    useMessage:(onFullfilled)=>{
        exec(null,onFullfilled,"WebView","useMessage",null);
    }
}

