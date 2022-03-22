

class ViewController:CDVViewController{

    var plugin:Webview?=nil;
    var message:String?=nil;
    override public var title:String?{
        get{
            return message;
        }
        set(value){
            self.message=value;
        }
    };

    override func willMove(toParent: UIViewController?){
        super.willMove(toParent:toParent);
    }

    override func didMove(toParent:UIViewController?){
        super.didMove(toParent:toParent);
        let showCommand:CDVInvokedUrlCommand?=plugin!.showCommand;
        if(!(showCommand==nil)){
            let data:[AnyHashable:Any]=[
                "message":self.title,
                "store":Webview.store,
            ];
            plugin!.success(showCommand!,data);
        }
    }

    static func getInstance(_ options:[AnyHashable:Any],_ plugin:Webview)->ViewController{
        let viewcontroller=ViewController();
        viewcontroller.wwwFolderName="www";
        viewcontroller.startPage=options["file"] as? String;
        viewcontroller.view.frame=UIScreen().bounds;
        //viewcontroller.view.layoutMargins=UIEdgeInsets(top:25,left:0,bottom:0,right:0);
        viewcontroller.plugin=plugin;
        viewcontroller.title=options["message"] as? String;

        return viewcontroller;
    }
}
