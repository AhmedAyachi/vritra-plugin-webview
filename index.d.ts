declare var WebView:WebView;

interface WebView{
    show:(options:{
        url:string,
        file:string,
        message:string,
        onClose:(data:{message:string,store:object})=>void,
    })=>void,
    close:(message:string)=>void,
    setMessage:(message:string)=>void,
    setStore:(key:string,value:any)=>void,
    useStore:(store:object)=>void,
}
