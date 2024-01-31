const JsParser=cordova.require("vritra-plugin-webview.WebViewJsParser");
const timeout=40;
const data=[];

module.exports={
    defineWebViews:(webviews=[],fallback)=>{setTimeout(()=>{
        webviews.forEach(webview=>{
            if(webview.id&&(webview.file||webview.url)){
                data.push(webview);
            }
        });
    },timeout)},
    show:(options)=>{setTimeout(()=>{
        let props,{id}=options;
        const webviews=frameElement?frameElement.parentNode.querySelector("iframe").webviews:data;
        if(id){
            props=webviews.find(webview=>webview.id===id);
            Object.assign(props,options);
        }
        else{
            props=options;
        }
        const {file=options.url,message,backgroundColor,onClose}=props;
        if(file){
            const iframe=document.createElement("iframe");
            iframe.webviews=webviews;
            iframe.src=file;
            iframe.onClose=onClose;
            iframe.message=stringify(message);
            Object.assign(iframe.style,{
                position:"fixed",
                width:"100%",
                height:"100%",
                inset:0,
                margin:"auto",
                border:"none",
                backgroundColor:backgroundColor||"white",
                zIndex:2147483647,
            });
            document.body.appendChild(iframe);
        }
    },timeout)},
    initiateStore:function(store,callback){setTimeout(()=>{
        if(store&&(typeof(store)==="object")&&(!Array.isArray(store))){
            localStorage.setItem("store",stringify(store));
        }
        else throw new Error("store needs to be an object");
        callback&&this.useStore(callback);
    },timeout)},
    useStore:(callback)=>{setTimeout(()=>{
        if(typeof(callback)==="function"){
            let store=localStorage.getItem("store");
            if(store){
                store=JSON.parse(localStorage.getItem("store"));
            }
            else{
                localStorage.setItem("store","{}");
                store={};
            } 
            callback(store);
        }
    },timeout)},
    setStore:(key,value,callback)=>{setTimeout(()=>{
        const store=JSON.parse(localStorage.getItem("store"))||{};
        JsParser(store,key,value);
        localStorage.setItem("store",stringify(store));
        callback&&callback(store);
    },timeout)},
    useMessage:(callback)=>{setTimeout(()=>{
        if(typeof(callback)==="function"){
            const iframe=frameElement.parentNode.querySelector("iframe");
            callback(iframe.message);
        }
    },timeout)},
    setMessage:(message="")=>{setTimeout(()=>{
        const iframe=frameElement.parentNode.querySelector("iframe");
        iframe.message=stringify(message);
    },timeout)},
    close:function(message){
        if(frameElement){
            const iframe=frameElement.parentNode.querySelector("iframe");
            new Promise(resolve=>{
                const {onClose}=iframe;
                onClose?this.useStore(store=>{
                    onClose({message:message===undefined?iframe.message:stringify(message),store});
                    resolve();
                }):resolve();
            }).
            then(()=>{
                setTimeout(()=>{iframe.remove()},timeout);
            });
        }
        else{
            alert("Can't close the main WebView. The app will be minimized on android/ios.");
        }
    },
}

const stringify=(message)=>message?((typeof(message)==="string")?message:JSON.stringify(message)):"";
