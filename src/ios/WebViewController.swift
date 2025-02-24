

class WebViewController:CDVViewController {

    var plugin:Webview?;
    var options:[String:Any]=[:];
    var message:String?;
    var isModal:Bool {
        get { return false }
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

    override func viewWillDisappear(_ animated:Bool){
        super.viewWillDisappear(animated);
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
        self.startPage=url ?? "";
    }

    func setBackgroundColor(){
        let webview=self.webView!;
        self.view.clipsToBounds=false;
        let backgroundColor=options["backgroundColor"] as? String;
        let color=backgroundColor==nil ? UIColor.white : getUIColorFromHex(backgroundColor!);
        webview.isOpaque=false;
        webview.backgroundColor=color;
    }
        
    func isStatusBarTranslucent()->Bool{
        return options["statusBarTranslucent"] as? Bool ?? false;
    }
    
    func show(){
        guard let mainview=self.view else { return };
        let animationId=options["showAnimation"] as? String;
        if(!isStatusBarTranslucent()){
            let statusBarColor=options["statusBarColor"] as? String ?? "white";
            let scrollView=mainview.subviews.first!;
            scrollView.backgroundColor=getUIColorFromHex(statusBarColor);
            scrollView.frame.size.height=UIApplication.shared.statusBarFrame.height;
        }
        let animation=({
            switch(animationId){
                case "fadeIn": return ShowAnimation.fadeIn;
                case "slideUp": return ShowAnimation.slideUp;
                case "slideLeft": return ShowAnimation.slideLeft;
                case "translateUp": return ShowAnimation.translateUp;
                case "translateLeft": return ShowAnimation.translateLeft;
                default: return ShowAnimation.defaultAnim;
            }
        })();
        if let prevControllerAnimation=({
            switch(animationId){
                case "slideUp": return HideAnimation.slideUp;
                case "slideLeft": return HideAnimation.slideLeft;
                default: return animationId==nil ? HideAnimation.prodLeft : nil;
            }
        })(){
            DispatchQueue.main.asyncAfter(deadline:.now()+ShowAnimation.delay,execute:{
                guard let preview=self.plugin!.viewController.view else { return };
                prevControllerAnimation(preview,[:]){ _ in
                    preview.transform = .identity;
                };
            });
            
        };
        animation(mainview,[:]);
    }
    
    func hide(_ onHidden:((Bool)->Void)?){
        let animationId=options["closeAnimation"] as? String;
        let mainview=self.view!;
        let animation=({
            switch(animationId){
                case "fadeOut": return HideAnimation.fadeOut;
                case "slideDown": return HideAnimation.slideDown;
                case "slideRight": return HideAnimation.slideRight;
                case "translateDown": return HideAnimation.translateDown;
                case "translateRight": return HideAnimation.translateRight;
                default: return HideAnimation.defaultAnim;
            }
        })();
        if let prevControllerAnimation=({
            switch(animationId){
                case "slideDown": return ShowAnimation.slideDown;
                case "slideRight": return ShowAnimation.slideRight;
                default: return animationId==nil ? ShowAnimation.prodRight : nil;
            }
        })(){
            prevControllerAnimation(plugin!.viewController.view,[:]);
        };
        animation(mainview,[:],onHidden);
    }

    func remove(){
        DispatchQueue.main.asyncAfter(deadline:.now()+0.025,execute:{
            guard let plugin=self.plugin else { return };
            self.hide({_ in
                self.willMove(toParent:nil);
                self.view.removeFromSuperview();
                self.removeFromParent();
            });
            guard let showCommand=plugin.showCommand else { return };
            let data:[AnyHashable:Any]=[
                "message":self.message ?? "",
                "store":Webview.store.toObject(),
            ];
            plugin.success(showCommand,data);
        });
    };
}
