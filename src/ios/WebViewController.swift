

class WebViewController:CDVViewController {

    var plugin:Webview?=nil;
    var options:[String:Any]=[:];
    var message:String?=nil;
    var isModal:Bool {
        get {return false}
    };

    init(_ options:[String:Any],_ plugin:Webview?){
        super.init(nibName:nil,bundle:nil);
        self.options=options;
        self.plugin=plugin;
        self.message=options["message"] as? String;
        self.setUrl();
    }

    required init?(coder:NSCoder) {
        super.init(coder:coder);
    }

    override var prefersStatusBarHidden:Bool{
        return false;
    }

    override func viewDidLoad(){
        super.viewDidLoad();
        setNeedsStatusBarAppearanceUpdate();
        self.setBackgroundColor();
    }

    override func viewDidDisappear(_ animated:Bool){
        super.viewDidDisappear(animated);
        let showCommand:CDVInvokedUrlCommand?=plugin!.showCommand;
        if(!(showCommand==nil)){
            let data:[AnyHashable:Any]=[
                "message":self.message as Any,
                "store":Webview.store.toObject(),
            ];
            plugin!.success(showCommand!,data);
        }
    }

    func setUrl(){
        var url=options["file"] as? String;
        if(url==nil){
            self.wwwFolderName="";
            url=options["url"] as? String;
        }
        else{
            self.wwwFolderName="www";
        }
        self.startPage=url!;
    }

    func setBackgroundColor(){
        let webview=self.webView!;
        self.view.clipsToBounds=false;
        let backgroundColor=options["backgroundColor"] as? String;
        let color=backgroundColor==nil ? UIColor.white:getUIColorFromHex(backgroundColor!);
        webview.isOpaque=false;
        webview.backgroundColor=color;
    }

    func addTo(_ parentController:UIViewController){
        parentController.addChild(self);
        parentController.view.addSubview(self.view);
        self.show();
    }

    func show(){
        let animation=({
            let animationId=options["showAnimation"] as? String;
            switch(animationId){
                case "fadeIn": return fadeIn;
                case "slideUp": return slideUp;
                default: return slideLeft;
            }
        })();
        let mainview=self.view!;
        let statusBarTranslucent=options["statusBarTranslucent"] as? Bool ?? false;
        if(statusBarTranslucent){}
        else{
            let statusBarColor=options["statusBarColor"] as? String ?? "white";
            let scrollView=mainview.subviews.first!;
            scrollView.backgroundColor=getUIColorFromHex(statusBarColor);
            scrollView.frame.size.height=UIApplication.shared.statusBarFrame.height;
        }
        animation(mainview,0.5);
    }
    
    func hide(_ onHidden:((Bool)->Void)?){
        let closeAnimation=options["closeAnimation"] as? String ?? "fadeOut";
        let mainview=self.view!;
        let options=["onFinish":onHidden as Any];
        switch(closeAnimation){
            case "slideDown": slideDown(mainview,options);break;
            default: fadeOut(mainview,options);break;
        }
    }

    func remove(){
        DispatchQueue.main.asyncAfter(deadline:.now()+0.025,execute:{
            self.hide({_ in
                self.view.removeFromSuperview();
                self.removeFromParent();
            });
        });
    };
}
