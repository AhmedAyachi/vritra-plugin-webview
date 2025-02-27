declare const WebView:WebView;

interface WebView {
    /**
     * Defines the props of app webviews 
     * @param webviews 
     * @param fallback called when an error occurred
     */
    defineWebViews(webviews:WebViewProps[],fallback?:(error:Error)=>void):void,
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
        id?:string,
        /**
        * A message to pass to the new webiew.
        * This message is freed when the new webview is closed
        * and the onClose method is called. 
        * @Note
        * If message is not of type string, JSON.stringify is called.
        */
        message?:string,
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
            message:string,
            store:object,
        }):void,
    }):void,
    /**
    * Uses the message from the webview that showed
    * the current webview. 
    */
    useMessage(handler:(message:string)=>void):void,
    /**
    * Sets the message value. 
    * @notice if message is a falsy value, is passed as an empty string.
    * @notice if message is not a string, JSON.stringify is called.
    */
    setMessage(message:string):void,
    /**
     * Sets the store value.
     * Only objects are accepted.
     * @param store 
     * default: empty object
     * @param callback 
     * Called when the store is successfully set. 
     */
    initiateStore(store:object,callback?:(store:object)=>void):void,
    /**
    * Uses the store object.
    * @note
    * The store object passed to the handler is a value type.
    * Setting it will not affect the store unless initiateStore is used
    * or call setStore instead.  
    */
    useStore(
        callback?:(store:object)=>void,
        fallback?:(error:Error)=>void,
    ):void,
    /**
     * 
     * @param path value path
     * @param callback 
     * @param fallback 
     */
    useStore(
        path:string,
        callback?:(value:any)=>void,
        fallback?:(error:Error)=>void,
    ):void,
    /**
     * @param path 
     * the path to the value to set.
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
     * @param value if undefined, the property is deleted
     * @param callback 
     * @param fallback
     */
    setStore(
        path:string,
        value:any,
        callback?:(store:object)=>void,
        fallback?:(error:Error)=>void,
    ):void,
    /**
     * Multi sets the store
     * @param entries Should contain string-any successive items
     * @param callback 
     * @param fallback 
     * @example setStore([
     *  "items",null,
     *  "user.age",24,
     * ],callback,fallback);
     */
    setStore(
        entries:[]|[string,any,...([string,any]|[])],
        callback?:(store:object)=>void,
        fallback?:(error:Error)=>void,
    ):void,
    /**
     * Deletes the property, equivalent to setStore(path,undefined,callback,fallback);
     * @param path 
     * @param callback 
     * @param fallback 
     */
    setStore(
        path:string,
        callback?:(store:object)=>void,
        fallback?:(error:Error)=>void,
    ):void,
    /**
    * Closes the current webview.
    * @param message The message to pass to the previous webview.
    * If message is undefined, the value is ignored.
    * If message is not a string, JSON.stringify is called.
    * @notice if called in the main webview, the app is minimized.
    */
    close(message:string):void,
}

type WebViewProps={
    /**
     * The webview's identifier.
     * @requires file or url value in definition
     */
    id:string,
    /**
    * A filename with extension in your www folder.
    * 
    * More prioritized then the url prop.
    */
    file?:string,
    /**
    * A http/https url to show external sites.
    * @warning
    * Please be careful when using this property because
    * the target url will have access to cordova plugins.
    */
    url?:string,
    /** 
    * @default false
    */
    statusBarTranslucent?:boolean,
    /**
     * @default true
     */
    navigationBarTranslucent?:boolean,
    /**
     * Only applied when statusBarTranslucent false
     * @default "white" on ios, "black" on android
     */
    statusBarColor?:WebViewColor,
     /**
     * Only applied when navigationBarTranslucent false
     * @default "black"
     */
    navigationBarColor?:WebViewColor;
    /** 
    * The webview background color before loading the html file.
    * @default "white"
    * @notice "transparent" is only apllied for modals
    */
    backgroundColor?:WebViewColor,
    /**
    * If true, the webview is dismissible via touch interactions or back press.
    * @notice affects only android webview and android/ios modals.
    * @default true
    */
    dismissible?:boolean,
    /**
     * The webview animation when shown.
     * @notice Applied only for non-modal webviews.
     * @default null
     */
    showAnimation?:"translateLeft"|"translateUp"|"fadeIn"|"slideLeft"|"slideUp",
    /**
     * The webview animation when closed.
     * @notice Applied only for non-modal webviews.
     * @default null
     */
    closeAnimation?:"translateRight"|"translateDown"|"fadeOut"|"slideRight"|"slideDown",
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
        width?:number,
        /**
         * height of the modal relative to the window height.
         * 
         * Value between 0 and 1.
         * @default 0.85
         */
        height?:number,
        /**
         * Left margin of the modal relative to the window width.
         * 
         * Value between -1 and 1.
         */
        marginLeft?:number,
        /**
         * Top margin of the modal relative to the window height.
         * 
         * Value between -1 and 1.
         */
        marginTop?:number,
        /**
         * @default "bottom"
         */
        verticalAlign?:"bottom"|"top"|"middle",
        /**
         * @default 1
         */
        opacity?:number,
        /**
         * @default true
         */
        silent?:boolean,
        /**
         * If true, the modal is dismissible via touch interactions
         * @default true
         * @deprecated should be specified directly in the props object
         */
        dismissible?:boolean,
        /**
         * Sets the notch color.
         * The notch is shown only when the modal is dismissible
         */
        notchColor?:WebViewColor,
        /** @default true */
        roundedTopLeftCorner?:boolean,
        /** @default true */
        roundedTopRightCorner?:boolean,
        /** @default false */
        roundedBottomLeftCorner?:boolean,
        /** @default false */
        roundedBottomRightCorner?:boolean,
    },
}

type WebViewColor=(
    "black"|"blue"|"brown"|"cyan"|
    "darkgray"|"gray"|"green"|"lightgray"|
    "magenta"|"orange"|"purple"|"red"|
    "yellow"|"white"|"transparent"|"#"
)
