

module.exports={
    show:(options)=>{
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
    },
    initiateStore:function(store,onFullfilled){
        if(store&&(typeof(store)==="object")&&(!Array.isArray(store))){
            localStorage.setItem("store",JSON.stringify(store));
        }
        else{
            localStorage.setItem("store","{}");
        }
        this.useStore(store=>{
            onFullfilled&&onFullfilled(store);
        });
    },
    useStore:(onFullfilled)=>{
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
    },
    setStore:(key,value,onFullfilled)=>{
        const store=JSON.parse(localStorage.getItem("store"))||{};
        store[key]=value;
        localStorage.setItem("store",JSON.stringify(store));
        onFullfilled&&onFullfilled(store);
    },
    useMessage:(onFullfilled)=>{
        if(typeof(onFullfilled)==="function"){
            const iframe=frameElement.parentNode.querySelector("iframe");
            onFullfilled(iframe.message);
        }
    },
    setMessage:(message="")=>{
        const iframe=frameElement.parentNode.querySelector("iframe");
        iframe.message=stringifyMessage(message);
    },
    close:function(message){
        const iframe=frameElement.parentNode.querySelector("iframe");
        const {onClose}=iframe;
        onClose&&this.useStore(store=>{
            onClose({message:message===undefined?iframe.message:stringifyMessage(message),store});
        });
        iframe.remove();
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
