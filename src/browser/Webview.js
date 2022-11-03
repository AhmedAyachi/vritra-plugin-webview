
const JsParser=cordova.require("cordova-plugin-webview.WebviewJsParser");
const timeout=40;

module.exports={
    show:(options)=>{setTimeout(()=>{
        const {file=options.url,message,onClose}=options;
        if(file){
            const iframe=document.createElement("iframe");
            iframe.src=file;
            iframe.onClose=onClose;
            iframe.message=stringifyMessage(message);
            Object.assign(iframe.style,{
                position:"fixed",
                width:"100%",
                height:"100%",
                inset:0,
                margin:"auto",
                border:"none",
                backgroundColor:"white",
                zIndex:2147483647,
            });
            document.body.appendChild(iframe);
        }
    },timeout)},
    initiateStore:function(store,onFullfilled){
        localStorage.setItem("store",JSON.stringify(store));
        if(store&&(typeof(store)==="object")&&(!Array.isArray(store))){
        }
        else{
            localStorage.setItem("store","{}");
        }
        this.useStore(store=>{
            onFullfilled&&onFullfilled(store);
        });
    },
    useStore:(onFullfilled)=>{setTimeout(()=>{
        if(typeof(onFullfilled)==="function"){
            let store=localStorage.getItem("store");
            if(store){
                store=JSON.parse(localStorage.getItem("store"));
            }
            else{
                localStorage.setItem("store","{}");
                store={};
            } 
            onFullfilled(store);
        }
    },timeout)},
    setStore:(key,value,onFullfilled)=>{setTimeout(()=>{
        const store=JSON.parse(localStorage.getItem("store"))||{};
        JsParser(store,key,value);
        localStorage.setItem("store",JSON.stringify(store));
        onFullfilled&&onFullfilled(store);
    },timeout)},
    useMessage:(onFullfilled)=>{setTimeout(()=>{
        if(typeof(onFullfilled)==="function"){
            const iframe=frameElement.parentNode.querySelector("iframe");
            onFullfilled(iframe.message);
        }
    },timeout)},
    setMessage:(message="")=>{setTimeout(()=>{
        const iframe=frameElement.parentNode.querySelector("iframe");
        iframe.message=stringifyMessage(message);
    },timeout)},
    close:function(message){
        const iframe=frameElement.parentNode.querySelector("iframe");
        new Promise(resolve=>{
            const {onClose}=iframe;
            onClose?this.useStore(store=>{
                onClose({message:message===undefined?iframe.message:stringifyMessage(message),store});
                resolve();
            }):resolve();
        }).
        then(()=>{
            setTimeout(()=>{iframe.remove()},timeout);
        });
    },
}

const stringifyMessage=(message)=>{
    let str="";
    if(message){
        if(typeof(message)==="string"){
            str=message;
        }
        else{
            str=JSON.stringify(message);
        }
    }
    return str;
}
