declare const WebView:WebView;

interface WebView {
    /**
     * Defines the props of app webviews 
     * @param webviews 
     * @param fallback called when an error occurred
     */
    defineWebViews(webviews:WebViewProps[],fallback:(message:String)=>void):void,
    /**
    * Shows a new webview.
    * The shown webview will have access to all cordova plugins.
    * @notice Overwrites defined webview props
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
    * @notice if message is a falsy value, is passed as an empty string.
    * @notice if message is not a string, JSON.stringify is called.
    */
    setMessage(message:String):void,
    /**
     * Sets the store value.
     * Only objects are accepted.
     * @param store 
     * default: empty object
     * @param callback 
     * Called when the store is successfully set. 
     */
    initiateStore(store:Object,callback:(store:Object)=>void):void,
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
     * @param callback 
     */
    setStore(
        key:String,
        value:any,
        callback:(store:Object)=>void
    ):void,
    /**
    * Close the current webview.
    * @param message The message to pass to the previous webview.
    * @notice if message is undefined, the value is ignored.
    * @notice if message is not a string, JSON.stringify is called.
    */
    close(message:String):void,
}

type WebViewProps={
    /**
     * The target webview's id.
     * @requires file or url props in definition
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
    * 
    * Android only. 
    * 
    * For ios, use cordova-plugin-statusbar instead.
    * @default false
    */
    statusBarTranslucent?:boolean,
    /**
     * Only applied when statusBarTranslucent false
     * @default "white"
     */
    statusBarColor?:WebViewColor,
    /** 
    * The webview background color before loading the html file.
    * @default "white".
    */
    backgroundColor?:WebViewColor,
    /**
     * The new webview animation when shown.
     * @notice Applied only for non-modal webviews.
     * @default "slideLeft"
     */
    showAnimation:"slideLeft"|"slideUp"|"fadeIn",
    /**
     * The new webview animation when closed.
     * @notice Applied only for non-modal webviews.
     * @default "fadeOut"
     */
    closeAnimation:"slideDown"|"fadeOut",
    /**
    * If true, shows the new webview with a modal animation.
    * @default false.
    */
    asModal?:boolean,
    /**
    * Only applied when asModal is true
    */
    modalStyle?:{
        /**
         * width of the modal relative to the window width.
         * 
         * Value between 0 and 1.
         * @default 1
         */
        width:number,
        /**
         * height of the modal relative to the window height.
         * 
         * Value between 0 and 1.
         * @default 0.85
         */
        height:number,
        /**
         * Left margin of the modal relative to the window width.
         * 
         * Value between -1 and 1.
         */
        marginLeft:number,
        /**
         * Top margin of the modal relative to the window height.
         * 
         * Value between -1 and 1.
         */
        marginTop:number,
        /**
         * @default "bottom"
         */
        verticalAlign:"bottom"|"top"|"middle",
        /**
         * @default 1
         */
        opacity:number,
    },
}

type WebViewColor=(
    "black"|"blue"|"brown"|"cyan"|
    "darkgray"|"gray"|"green"|"lightgray"|
    "magenta"|"orange"|"purple"|"red"|
    "yellow"|"white"|"transparent"|"#"
)

