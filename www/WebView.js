const exec=require("cordova/exec");


module.exports={
    create:(options)=>{
        const {onCreated}=options;
        exec(onCreated,null,"WebView","create",[options]);
    },
    useMessage:(onFullfilled)=>{
        exec(null,onFullfilled,"WebView","useMessage",[]);
    }
}

