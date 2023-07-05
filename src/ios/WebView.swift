import Foundation;


class Webview:CordovaPlugin {

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
                let childvc=ViewController(options,self);
                let viewcontroller=self.viewController!;
                viewcontroller.addChild(childvc);
                viewcontroller.view.addSubview(childvc.view);
                childvc.isModal ? showModal(childvc) : 
                showWebView(childvc,options["showAnimation"] as? String);
                self.showCommand=command;
            });
        }
    }
    func getWebViewProps(_ options:[String:Any])->[String:Any]{
        var props:[String:Any]=options;
        if let id=options["id"] as? String,!id.isEmpty,
           let defaults=Webview.webviews[id] {
            props.merge(defaults){(current,_) in current}
        }
        return props;
    }

    @objc(useMessage:)
    func useMessage(command:CDVInvokedUrlCommand){
        if(!(self.viewController.parent==nil)){
            if let viewcontroller=self.viewController as? ViewController {
                success(command,viewcontroller.message);
            }
        }
    }

    @objc(setMessage:)
    func setMessage(command:CDVInvokedUrlCommand){
        if let viewcontroller=self.viewController as? ViewController {
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
        if let viewcontroller=self.viewController as? ViewController {
            if !(viewcontroller.parent==nil){
            DispatchQueue.main.async(execute:{[self] in
                let isUndefined=command.arguments[1] as! Bool;
                if(!isUndefined){
                    self.setMessage(command:command);
                }
                let callback=viewcontroller.isModal ? hideModal : {
                    (_ viewcontroller:ViewController,_ onHidden:((Bool)->Void)?)->() in
                    let options=viewcontroller.options;
                    hideWebView(viewcontroller,options["closeAnimation"] as? String,onHidden);
                };
                callback(viewcontroller,{_ in
                    viewcontroller.view.removeFromSuperview();
                    viewcontroller.removeFromParent();
                });
            });
        }
        }
    }
}

/* override func pluginInitialize(){
        
} */
