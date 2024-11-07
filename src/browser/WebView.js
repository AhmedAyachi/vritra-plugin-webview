const Store=cordova.require("vritra-plugin-webview.Store");
const timeout=40;
const data=[];
    
module.exports={
    defineWebViews:(webviews=[],fallback)=>{setTimeout(()=>{
        try{
            webviews.forEach(webview=>{
                if(webview.id&&(webview.file||webview.url)){
                    data.push(webview);
                }
                else throw new Error("invalid webview definition");
            });
        }
        catch(error){
            console.error();
            fallback&&fallback(error);
        }
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
    useStore:(path,callback,fallback)=>{setTimeout(()=>{
        try{
            if(typeof(path)==="function"){
                fallback=callback;
                callback=path;
                path="";
            }
           callback&&callback(Store.get(path));
        }
        catch(error){
            console.error(error);
            fallback&&fallback(error);
        }
    },timeout)},
    setStore:(path,value,callback,fallback)=>{setTimeout(()=>{
        try{
            if(Array.isArray(path)){
                fallback=callback;
                callback=value;
                value=undefined;
            }
            else if(typeof(value)==="function"){
                fallback=callback;
                callback=value;
            }
            const store=Store.set(path,value);
            callback&&callback(store);
        }
        catch(error){
            console.error(error);
            fallback&&fallback(error);
        }
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
            alert("Can't close the main WebView. The app will be minimized on android/iOS.");
        }
    },
}

const stringify=(message)=>message?((typeof(message)==="string")?message:JSON.stringify(message)):"";
    