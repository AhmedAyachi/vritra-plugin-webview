declare var WebView:WebView;

interface WebView{
    show:(options:{
        url:string,
        file:string,
        message:string,
        onClose:(data:{message:string,store:object})=>void,
    })=>void,
    close:(message:string)=>void,
    useMessage:(handler:(message:string)=>void)=>void,
    setMessage:(message:string)=>void,
    useStore:(handler:(store:object)=>void)=>void,
    setStore:(key:string,value:any)=>void,
}
