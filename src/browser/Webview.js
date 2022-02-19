

module.exports={
    show:(options)=>{
        const {file=options.url,message,onClose}=options;
        if(file){
            const iframe=document.createElement("iframe");
            iframe.src=file;
            iframe.onClose=onClose;
            iframe.message=message;
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
            const {message}=iframe;
            onFullfilled(typeof(message)==="string"?message:JSON.stringify(message));
        }
    },
    setMessage:(message="")=>{
        const iframe=frameElement.parentNode.querySelector("iframe");
        iframe.message=message;
    },
    close:function(message){
        const iframe=frameElement.parentNode.querySelector("iframe");
        const {onClose}=iframe;
        onClose&&this.useStore(store=>{
            onClose({message:message===undefined?iframe.message:message,store});
        });
        iframe.remove();
    },
    fetch:(url,props={})=>{
        console.log("fetch not supported");
    }
}
