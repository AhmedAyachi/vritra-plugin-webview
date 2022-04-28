declare const WebView:WebView;

interface WebView{
    /**
    * Shows a new webview with a right-to-left animation.
    * The shown webview will have access to all cordova plugins.
    */
    show(options:{
        /**
        * A http/https url to show external sites.
        * Overwrites the file property.
        * @warning
        * Please be careful when using this property because
        * the target url will have access to cordova plugins.
        */
        url:String,
        /**
        * A filename with extension in your www folder.
        * If the url property is used, this value is ignored.
        */
        file:String,
        /**
        * A message to pass to the new webiew.
        * This message is freed when the new webview is closed
        * and the onClose method is called. 
        */
        message:String,
        /** 
        * if True, the statusbar and the keybaord will overlay the webview.
        * Android only. For ios, use cordova-plugin-statusbar instead.
        * @default true
        */
        statusBarTranslucent:Boolean,
        /** 
        * The webview background color before loading html file.
        * @default "white".
        */
        backgroundColor:String,
        /**
        * If true, shows the new webview with a modal animation.
        * @default false.
        */
        asModal:Boolean,
        /**
        * Ignored when  
        */
        modalStyle:{
            width:Number,
            height:Number,
            marginVertical:Number,
            marginHorizontal:Number,
            verticalAlign:"bottom"|"top"|"middle",
            opacity:Number,
        },
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
    */
    setMessage(message:String):void,
    /**
     * Sets the store value.
     * Only objects are accepted.
     * @default empty object.
     */
    initiateStore(
        store:Object,
        /**
        * Called when the store is successfully set. 
        */
        onFullfilled:(store:Object)=>void,
    ):void,
    /**
    * Uses the store object.
    * @note
    * The store object passed to the handler is a value type.
    * Setting it will not affect the store unless initiateStore is used
    * or call setStore instead.  
    */
    useStore(handler:(store:Object)=>void):void,
    /**
    * Sets the store properties. 
    */
    setStore(
        /**
        * the path to the value you want to set.
        * @ForArrays
        * array[*] will target all the array elements.
        * 
        * [index] => sets an array element at index.
        * @UsableOnLastPathComponent
        * [push] => inserts the value at the end of an array.
        * 
        * [unshift] => inserts the value at the start of an array.
        * 
        * [pop] => removes the last element from an array, value property is ignored.
        * 
        * [shift] => removes the first element from an array, value property is ignored.
        * 
        * [last] => sets the last array element.
        * @example
        * "object.array[*][*].object.array[last]"
        * "object.array[*].object.array[*].property"
        * "object.array[0].array[4].array[last].property"
        */
        key:String,
        value:any,
        onFullfilled:(store:Object)=>void
    ):void,
    /**
    * Close the current webview.
    * @param message
    * The message to pass to the previous webview.
    * if message is undefined, the value is ignored.
    * if message is null, it's passed as an empty string. 
    */
    close(message:String):void,
}
