declare const WebView:WebView;

interface WebView{
    show(options:{
        url:String,
        file:String,
        message:String,
        statusBarTranslucent:Boolean,
        asModal:Boolean,
        modalStyle:{
            width:Number,
            height:Number,
            marginVertical:Number,
            marginHorizontal:Number,
            verticalAlign:"bottom"|"top"|"middle",
            opacity:Number,
        },
        onClose(data:{message:String,store:Object}):void,
    }):void,
    useMessage(handler:(message:String)=>void):void,
    setMessage(message:String):void,
    initiateStore(store:Object,onFullfilled:(store:Object)=>void):void,
    useStore(handler:(store:Object)=>void):void,
    setStore(key:String,value:any,onFullfilled:(store:Object)=>void):void,
    close(message?:String):void,
}
