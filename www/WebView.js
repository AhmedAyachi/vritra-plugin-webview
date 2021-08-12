const exec=require("cordova/exec");


module.exports={
    show:(url,onFulfilled,onRejected,loading)=>{
        if(loading){
            exec(onFulfilled,onRejected,"WebView","show",[url,loading]);
        }
        else{
            exec(onFulfilled,onRejected,"WebView","show",[url]);
        }
    },
    hide:(onFulfilled,onRejected,params)=>{
        exec(onFulfilled,onRejected,"WebView","hide",params || []);
    },
    hideLoading:(onFulfilled,onFulfilled)=>{
        exec(onFulfilled,onFulfilled,"WebView","hideLoading",[]);
    },
    exitApp:()=>{
        exec(null,null,"WebView","exitApp",[]);
    },
}
/*var _show = function(url, successCallback, errorCallback, loading) {
    if(loading){
      exec(successCallback, errorCallback, 'WebViewPlugin', 'show', [url, loading]);
    }
    else{
      exec(successCallback, errorCallback, 'WebViewPlugin', 'show', [url]);
    }
  };

  var _hide = function(successCallback, errorCallback, params) {
    exec(successCallback, errorCallback, 'WebViewPlugin', 'hide', params ? params : []);
  };

  var _hideLoading = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'WebViewPlugin', 'hideLoading', []);
  };

  var _subscribeCallback = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'WebViewPlugin', 'subscribeCallback', []);
  };

  var _subscribeExitCallback = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'WebViewPlugin', 'subscribeExitCallback', []);
  };

  var _exitApp = function() {
    exec(function(){},function(){}, 'WebViewPlugin', 'exitApp', []);
  };

  var _setWebViewBehavior = function() {
    exec(function(){},function(){}, 'WebViewPlugin', 'webViewAdjustmenBehavior', []);
  };
*/
