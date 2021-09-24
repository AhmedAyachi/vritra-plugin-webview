const exec=require("cordova/exec");


module.exports={
    create:(url,onFulfilled,onRejected)=>{
        exec(onFulfilled,onRejected,"WebView","create",[url]);
    },
}

