

const views=[];
const styles={
    iframe:`
        width:100%;
        height:100%;
        border:none;
    `,
}
let message;

module.exports={
    show:(options)=>{
        const {url}=options;
        if(url){
            const view=document.createElement("div");
            views.push(view);
            message=options.message;
            view.innerHTML=`
                <iframe style="${styles.iframe}" src="${url}"/>
            `;
            Object.assign(view.style,{
                position:"fixed",
                width:"100%",
                height:"100%",
                inset:0,
                margin:"auto",
            });
            document.body.appendChild(view);
        }
    },
    useMessage:(onFullfilled)=>{
        onFullfilled&&onFullfilled(message);
    },
    close:()=>{
        views.pop().remove();
    },
    //close
}
