declare const WebView:WebView;

interface WebView{
    /**
     * Defines the props of app webviews 
     * @param webviews 
     */
    defineWebViews(webviews:WebViewProps[]):void,
    /**
    * Shows a new webview with a right-to-left animation.
    * The shown webview will have access to all cordova plugins.
    */
    show(options:WebViewProps&{
        /**
         * The target webview's id. 
         * 
         * More prioritized then url prop, less prioritized than file prop.
         */
        id:string,
        /**
        * A message to pass to the new webiew.
        * This message is freed when the new webview is closed
        * and the onClose method is called. 
        * @Note
        * If message is not of type string, JSON.stringify is called.
        */
        message:String,
        /**
        * Called when the new webview is closed. 
        */
        onClose(data:{
            /**
            * A message from the new webview.
            * If the new webview did not send a message, this value is the same
            * as the message passed in the message property when showing the new
            * webview. 
            */
            message:String,
            store:Object,
        }):void,
    }):void,
    /**
    * Uses the message from the webview that showed
    * the current webview. 
    */
    useMessage(handler:(message:String)=>void):void,
    /**
    * Sets the message value. 
    * @see if message is a falsy value, is passed as an empty string.
    * @see if message is not a string, JSON.stringify is called.
    */
    setMessage(message:String):void,
    /**
     * Sets the store value.
     * Only objects are accepted.
     * @param store 
     * default: empty object
     * @param onFullfilled 
     * Called when the store is successfully set. 
     */
    initiateStore(store:Object,onFullfilled:(store:Object)=>void):void,
    /**
    * Uses the store object.
    * @note
    * The store object passed to the handler is a value type.
    * Setting it will not affect the store unless initiateStore is used
    * or call setStore instead.  
    */
    useStore(handler:(store:Object)=>void):void,
    /**
     * @param key 
     * the path to the value you want to set.
     * @ForArrays
     * 
     * array[*] will target all the array items.
     * 
     * [index] => sets an array item at index.
     * 
     * [last] => uses the last array item.
     * @UsableOnLastPathComponent
     * 
     * [push] => inserts the value at the end of an array.
     * 
     * [unshift] => inserts the value at the start of an array.
     * 
     * [pop] => removes the last item from an array, value property is ignored.
     * 
     * [shift] => removes the first item from an array, value property is ignored.
     * 
     * @example
     * "object.array[*][*].object.array[last]"
     * "object.array[*].object.array[*].property"
     * "object.array[0].array[4].array[last].property"
     * @param value 
     * @param onFullfilled 
     */
    setStore(
        key:String,
        value:any,
        onFullfilled:(store:Object)=>void
    ):void,
    /**
    * Close the current webview.
    * @param message
    * The message to pass to the previous webview.
    * @see if message is undefined, the value is ignored.
    * @see if message is not a string, JSON.stringify is called.
    */
    close(message:String):void,
}

type WebViewProps={
    /**
     * The target webview's id.
     * @requires file or url props while definition
     */
    id:string,
    /**
    * A http/https url to show external sites.
    * @warning
    * Please be careful when using this property because
    * the target url will have access to cordova plugins.
    */
    url:string,
    /**
    * A filename with extension in your www folder.
    * 
    * More prioritized then the url prop.
    */
    file:string,
    /** 
        * if True, the statusbar and the keybaord will overlay the webview.
        * Android only. For ios, use cordova-plugin-statusbar instead.
        * @default true
        */
    statusBarTranslucent?:boolean,
    /** 
    * The webview background color before loading html file.
    * @default "white".
    */
    backgroundColor?:string,
    /**
    * If true, shows the new webview with a modal animation.
    * @default false.
    */
    asModal?:boolean,
    /**
    * Only applied when asModal is true
    */
    modalStyle?:{
        width:number,
        height:number,
        marginVertical:number,
        marginHorizontal:number,
        verticalAlign:"bottom"|"top"|"middle",
        opacity:number,
    },
}
