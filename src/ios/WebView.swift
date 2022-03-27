import Foundation;


class Webview:WebViewPlugin {

    static public var store:[String:Any]=[:];
    public var showCommand:CDVInvokedUrlCommand?=nil;

    override func pluginInitialize(){
        
    }

    @objc(show:)
    func show(command:CDVInvokedUrlCommand){
        let options=command.arguments[0] as? [AnyHashable:Any];
        if(!(options==nil)){
            let child=ViewController.getInstance(options!,self);
            let viewcontroller=self.viewController!;
            viewcontroller.addChild(child);
            viewcontroller.view.addSubview(child.view);
            self.showCommand=command;
        }
    }

    @objc(useMessage:)
    func useMessage(command:CDVInvokedUrlCommand){
        if(!(self.viewController.parent==nil)){
            success(command,self.viewController.title);
        }
    }

    @objc(setMessage:)
    func setMessage(command:CDVInvokedUrlCommand){
        self.viewController.title=command.arguments[0] as? String;
    }

    @objc(initiateStore:)
    func initiateStore(command:CDVInvokedUrlCommand){
        let state=command.arguments[0] as? [String:Any];
        if(!(state==nil)){
            Webview.store=state!;
            success(command,Webview.store);
        }
    }

    @objc(useStore:)
    func useStore(command:CDVInvokedUrlCommand){
        success(command,Webview.store);
    }

    @objc(setStore:)
    func setStore(command:CDVInvokedUrlCommand){
        
    }

    @objc(close:)
    func close(command:CDVInvokedUrlCommand){
        if(!(self.viewController.parent==nil)){
            let message=command.arguments[0] as! String;
            if(!message.isEmpty){
                self.setMessage(command:command);
            }
            self.viewController.view.removeFromSuperview();
            self.viewController.removeFromParent();
        }
    }

}

/* 
let alert=UIAlertController(title:"toast title",message:"toast message",preferredStyle:.actionSheet);
self.viewController.present(alert,animated:true);
DispatchQueue.main.asyncAfter(deadline:DispatchTime.now()+1){
    alert.dismiss(animated:true);
} 
*/