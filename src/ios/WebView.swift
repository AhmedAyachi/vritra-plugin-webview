import Foundation;


class Webview:VritraPlugin {

    static let store=Store();
    public var showCommand:CDVInvokedUrlCommand?=nil;
    private static var webviews:[String:[String:Any]]=[:];

    @objc(defineWebViews:)
    func defineWebViews(command:CDVInvokedUrlCommand){
        do{
            if let webviews=command.arguments[0] as? [[String:Any]] {
                for webview in webviews {
                    if let id=webview["id"] as? String,!id.isEmpty {
                        let file=webview["file"] as? String;
                        let url=webview["url"] as? String;
                        if((file != nil)||(url != nil)){
                            Webview.webviews[id]=webview;
                        }
                        else{throw Webview.Error("webview's path or file prop is required")};
                    }
                    else{throw Webview.Error("webview's id is required")};
                }
            }
            else{throw Webview.Error("invalid webviews definition")};
        }
        catch let error as Webview.Error {
            self.error(command,error.toObject());
        }
        catch{};
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
        do{
            let path=command.arguments[0] as? String ?? "";
            if(path.isEmpty){
                success(command,Webview.store.toObject());
            }
            else{
                success(command,try Webview.store.get(path));
            }
        }
        catch let error as Webview.Error {
            self.error(command,error.toObject());
        }catch{};
    }

    @objc(setStore:)
    func setStore(command:CDVInvokedUrlCommand){
        do{
            let deletables=command.arguments[3] as! [String];
            let deletableCount=deletables.count;
            try deletables.forEach({ path in
                try Webview.store.delete(path);
            });
            let multiSetting=command.arguments[2] as! Bool;
            if(multiSetting){
                if let pairs=command.arguments[0] as? [Any?] {
                    let length=pairs.count;
                    if(length%2==0){
                        for i in stride(from:0,to:length,by:2){
                            if let key=pairs[i] as? String {
                                try Webview.store.set(key,pairs[i+1]);
                            }
                        }
                    }
                    else{throw Error("array length should be even")};
                }
                else{throw Error("param is not of type string|array")};
            }
            else if(deletableCount<1){
                if let key=command.arguments[0] as? String {
                    let value=command.arguments[1];
                    try Webview.store.set(key,value);
                };
            }
            success(command,Webview.store.toObject());
        }
        catch let error as Webview.Error {
            self.error(command,error.toObject());
        }catch{};
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
