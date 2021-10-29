declare var WebView:WebView;

interface WebView{
    show(options:{
        url:string,
        file:string,
        message:string,
        statusBarTranslucent:boolean,
        asModal:boolean,
        style:{
            width:number,
            height:number,
            marginVertical:number,
            marginHorizontal:number,
            verticalAlign:"bottom"|"top"|"middle",
            opacity:number,
        },
        onClose(data:{message:string,store:object}):void,
    }):void,
    close(message:string):void,
    useMessage(handler:(message:string)=>void):void,
    setMessage(message:string):void,
    initiateStore(store:object,onFullfilled:(store:object)=>void):void,
    useStore(handler:(store:object)=>void):void,
    setStore(key:string,value:any,onFullfilled:(store:object)=>void):void,
}
