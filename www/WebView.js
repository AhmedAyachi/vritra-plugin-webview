const exec=require("cordova/exec");


module.exports = {
    say:(message,onFulfilled,onRejected,)=>{
        exec(onFulfilled,onRejected,"WebView","say",[message]);
    },
}

