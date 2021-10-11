declare var WebView:WebView;

interface WebView{
    show:(options:{
        url:string,
        file:string,
        message:string,
        transition:string,
        onClose:()=>void,
    })=>void,
    close:()=>void,
    setMessage:(message:string)=>void,
    useMessage:(message:string)=>void,
}
