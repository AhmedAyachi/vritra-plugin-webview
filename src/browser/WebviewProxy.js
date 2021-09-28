const proxy=require("cordova/exec/proxy");


module.exports={
    create:alert,
}

proxy.add("WebView",module.exports);
