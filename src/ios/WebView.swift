import Foundation;


class Webview:VritraPlugin {

    static let store=Store();
    public var showCommand:CDVInvokedUrlCommand?=nil;
    private static var webviews:[String:[String:Any]]=[:];

    @objc(defineWebViews:)
    func defineWebViews(command:CDVInvokedUrlCommand){
        if let webviews=command.arguments[0] as? [[String:Any]] {
            for webview in webviews {
                if let id=webview["id"] as? String,!id.isEmpty {
                    let file=webview["file"] as? String;
                    let url=webview["url"] as? String;
                    if((file != nil)||(url != nil)){
                        Webview.webviews[id]=webview;
                    }
                };
            }
        }
    }

    @objc(show:)
    func show(command:CDVInvokedUrlCommand){
        if var options=command.arguments[0] as? [String:Any] {
            DispatchQueue.main.async(execute:{[self] in
                options=getWebViewProps(options);
                let asModal=(options["asModal"] as? Bool) ?? false;
                let viewcontroller=asModal ? ModalController(options,self) : WebViewController(options,self);
                viewcontroller.addTo(self.viewController!);
                //viewcontroller.present(childvc,animated:true);
                self.showCommand=command;
            });
        }
    }
    func getWebViewProps(_ options:[String:Any])->[String:Any]{
        var props:[String:Any]=options;
        if let id=options["id"] as? String,!id.isEmpty,
           let defaults=Webview.webviews[id] {
            props=Webview.mergeObjects(defaults,options);
        }
        print("log:WebView Props",props);
        return props;
    }

    @objc(useMessage:)
    func useMessage(command:CDVInvokedUrlCommand){
        if(!(self.viewController.parent==nil)){
            if let viewcontroller=self.viewController as? WebViewController {
                success(command,viewcontroller.message);
            }
        }
    }

    @objc(setMessage:)
    func setMessage(command:CDVInvokedUrlCommand){
        if let viewcontroller=self.viewController as? WebViewController {
            let message=command.arguments[0] as? String;
            viewcontroller.message=message;
        }
    }

    @objc(initiateStore:)
    func initiateStore(command:CDVInvokedUrlCommand){
        let state=command.arguments[0] as? [String:Any];
        Webview.store.initiate(state);
        success(command,Webview.store.toObject());
    }

    @objc(useStore:)
    func useStore(command:CDVInvokedUrlCommand){
        success(command,Webview.store.toObject());
    }

    @objc(setStore:)
    func setStore(command:CDVInvokedUrlCommand){
        let key=command.arguments[0] as! String;
        let value=command.arguments[1];
        Webview.store.mutate(key,value);
        success(command,Webview.store.toObject());
    }

    @objc(close:)
    func close(command:CDVInvokedUrlCommand){
        if let viewcontroller=self.viewController as? WebViewController,
            viewcontroller.parent != nil {
            let isUndefined=command.arguments[1] as! Bool;
            if(!isUndefined){self.setMessage(command:command)};
            viewcontroller.remove();
        }
        else{
            UIControl().sendAction(#selector(URLSessionTask.suspend),to:UIApplication.shared,for:nil);
        }
    }
    
    static func mergeObjects(_ object1:[String:Any],_ object2:[String:Any])->[String:Any]{
        var merged:[String:Any]=[:];
        merged.merge(object1){(current,_) in current};
        merged.merge(object2){(currentValue,newValue) in
            if(newValue is Dictionary<String,Any>){
                if(currentValue is Dictionary<String,Any>){
                    return mergeObjects(
                        currentValue as! Dictionary<String,Any>,
                        newValue as! Dictionary<String,Any>
                    );
                }
                else{return newValue};
            }
            else{return newValue};
        }
        return merged;
    }
}
