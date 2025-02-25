

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
        self.setBackgroundColor();
    }
    
    override func viewWillAppear(_ animated:Bool){
        self.show();
    }

    override func viewWillDisappear(_ animated:Bool){
        super.viewWillDisappear(animated);
    }

    var statusBarTranslucent:Bool { return options["statusBarTranslucent"] as? Bool ?? false };
    var navigationBarTranslucent:Bool { return options["navigationBarTranslucent"] as? Bool ?? true };

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
        guard let webview=self.webView else { return };
        self.view.clipsToBounds=false;
        if let backgroundColor=options["backgroundColor"] as? String {
            webview.isOpaque=false;
            webview.backgroundColor=getUIColorFromHex(backgroundColor);
        };
    }
    
    override func viewDidLayoutSubviews(){
        super.viewDidLayoutSubviews();
        self.setLayoutEdges();
    }
    
    private var needsToSetEdges=true;
    func setLayoutEdges(){
        guard needsToSetEdges else { return };
        guard let statusbarView=self.view.subviews.last,statusbarView.subviews.count<1 else { return };
        self.needsToSetEdges=false;
        guard let webView=self.webView else { return };
        if(self.statusBarTranslucent){
            self.view.backgroundColor = isModal ? .clear : .black;
            statusbarView.isHidden=true;
            if(self.navigationBarTranslucent){
                webView.frame=self.view.frame;
            }
        }
        else{
            let statusBarColor=options["statusBarColor"] as? String ?? "white";
            statusbarView.backgroundColor=getUIColorFromHex(statusBarColor);
            statusbarView.translatesAutoresizingMaskIntoConstraints=false;
            NSLayoutConstraint.activate([
                statusbarView.topAnchor.constraint(equalTo:view.topAnchor),
                statusbarView.leftAnchor.constraint(equalTo:view.leftAnchor),
                statusbarView.rightAnchor.constraint(equalTo:view.rightAnchor),
                statusbarView.bottomAnchor.constraint(equalTo:view.safeAreaLayoutGuide.topAnchor)
            ]);
        }
        if(!navigationBarTranslucent){
            webView.translatesAutoresizingMaskIntoConstraints=false;
            NSLayoutConstraint.activate([
                webView.topAnchor.constraint(equalTo:statusBarTranslucent ? view.topAnchor : view.safeAreaLayoutGuide.topAnchor),
                webView.leftAnchor.constraint(equalTo:view.safeAreaLayoutGuide.leftAnchor),
                webView.rightAnchor.constraint(equalTo:view.safeAreaLayoutGuide.rightAnchor),
                webView.bottomAnchor.constraint(equalTo:view.safeAreaLayoutGuide.bottomAnchor)
            ]);
            let navigationbarView=UIView();
            navigationbarView.translatesAutoresizingMaskIntoConstraints=false;
            view.addSubview(navigationbarView);
            NSLayoutConstraint.activate([
                navigationbarView.topAnchor.constraint(equalTo:webView.bottomAnchor),
                navigationbarView.leftAnchor.constraint(equalTo:webView.leftAnchor),
                navigationbarView.rightAnchor.constraint(equalTo:webView.rightAnchor),
                navigationbarView.bottomAnchor.constraint(equalTo:view.bottomAnchor),
            ])
            let navigationBarColor=options["navigationBarColor"] as? String ?? "black";
            navigationbarView.backgroundColor=getUIColorFromHex(navigationBarColor);
        }
    }
    
    func show(){
        guard let mainview=self.view else { return };
        let animationId=options["showAnimation"] as? String;
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
