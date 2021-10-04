

module.exports={
    show:(options)=>{
        const {url}=options;
        if(url){
            const iframe=document.createElement("iframe");
            iframe.src=url;
            localStorage.setItem("message",options.message);
            Object.assign(iframe.style,{
                position:"fixed",
                width:"100%",
                height:"100%",
                inset:0,
                margin:"auto",
                border:"none",
            });
            document.body.appendChild(iframe);
        }
    },
    useMessage:(onFullfilled)=>{
        const message=localStorage.getItem("message");
        onFullfilled&&onFullfilled(message);
    },
    close:()=>{
        frameElement.parentNode.querySelector("iframe").remove();
    },
}
