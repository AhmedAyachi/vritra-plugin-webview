const proxy=require("cordova/exec/proxy");


module.exports={
    show:({url})=>{
        const view=document.createElement("div");
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
    },
}

const styles={
    iframe:`
        width:100%;
        height:100%;
        border:none;
    `,
}

//proxy.add("WebView",module.exports);
