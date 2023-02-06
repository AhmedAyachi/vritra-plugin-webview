
const JsParser=cordova.require("cordova-plugin-webview.WebviewJsParser");
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
            console.log(props,webviews);
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
    initiateStore:function(store,callback){
        localStorage.setItem("store",stringify(store));
        if(store&&(typeof(store)==="object")&&(!Array.isArray(store))){
        }
        else{
            localStorage.setItem("store","{}");
        }
        this.useStore(store=>{
            callback&&callback(store);
        });
    },
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
    },
}

const stringify=(message)=>message?((typeof(message)==="string")?message:JSON.stringify(message)):"";
