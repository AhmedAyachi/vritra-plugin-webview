const proxy=require("cordova/exec/proxy");


module.exports={
    say:alert,
}

proxy.add("WebView",module.exports);
