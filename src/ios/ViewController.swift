

class ViewController:CDVViewController{

    var plugin:Webview?=nil;
    var options:[AnyHashable:Any]=[:];
    var message:String?=nil;
    public var isModal:Bool=true;

    override public var isBeingPresented:Bool{
        get{
            return self.isModal;
        }
    }
    

    override public var title:String?{
        get{
            return message;
        }
        set(value){
            self.message=value;
        }
    };

    override func viewDidDisappear(_ animated:Bool){
        super.viewDidDisappear(animated);
        let showCommand:CDVInvokedUrlCommand?=plugin!.showCommand;
        if(!(showCommand==nil)){
            let data:[AnyHashable:Any]=[
                "message":self.title as Any,
                "store":Webview.store,
            ];
            plugin!.success(showCommand!,data);
        }
    }

    func setOptions(_ options:[AnyHashable:Any],_ plugin:Webview){
        self.options=options;
        self.plugin=plugin;
        self.title=options["message"] as? String;
        self.isModal=(options["asModal"] as? Bool) ?? false;
        self.setUrl();
        self.setView();
    }

    func setUrl(){
        var url=options["file"] as? String;
        if(url==nil){
            self.wwwFolderName=nil;
            url=options["url"] as? String;
        }
        else{
            self.wwwFolderName="www";
        }
        self.startPage=url;
    }

    func setView(){
        let view=self.view!;
        view.frame=UIScreen().bounds;
        view.clipsToBounds=false;
        view.backgroundColor=UIColor.white;
        view.isOpaque=true;
        //var statusHeight=CGFloat(20);
        let webview=self.webView!;
        /* if#available(iOS 13,*){
            let value=view.window?.windowScene?.statusBarManager?.statusBarFrame.height;
            if(!(value==nil)){
                statusHeight=value!;
            }
        } */
        webview.frame.origin.y=(-100);
    }

    static func getInstance(_ options:[AnyHashable:Any],_ plugin:Webview)->ViewController{
        let viewcontroller=ViewController();
        viewcontroller.setOptions(options,plugin);
        return viewcontroller;
    }
}
