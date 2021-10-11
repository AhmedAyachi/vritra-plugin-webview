

module.exports={
    show:(options)=>{
        const {file=options.url,onClose}=options;
        if(file){
            const iframe=document.createElement("iframe");
            iframe.onClose=onClose;
            iframe.src=file;
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
    setMessage:(message="")=>{
        localStorage.setItem("message",message);
    },
    close:()=>{
        const iframe=frameElement.parentNode.querySelector("iframe");
        const {onClose}=iframe;
        if(onClose){
            const message=localStorage.getItem("message");
            onClose(message);
        }
        iframe.remove();
    },
}
