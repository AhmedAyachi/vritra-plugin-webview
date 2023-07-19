

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
        return true;
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
        if(statusBarTranslucent){
            
        }
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
        self.hide({_ in
            self.view.removeFromSuperview();
            self.removeFromParent();
        });
    };
}

func getUIColorFromHex(_ code:String)->UIColor{
    var color=UIColor.white;
    if(code.starts(with:"#")&&(code.count>6)){
        let hex=code[code.index(after:code.startIndex)..<code.index(code.startIndex,offsetBy:7)];
        var parts:[Int16]=[];
        for i in 0..<3 {
            let start=hex.index(hex.startIndex,offsetBy:i*2);
            let pair=String(hex[start...hex.index(start,offsetBy:1)]);
            let decimal=Int16(pair,radix:16)!;
            parts.append(decimal);
        }
        color=UIColor(
            red:CGFloat(parts[0])/255,
            green:CGFloat(parts[1])/255,
            blue:CGFloat(parts[2])/255,
            alpha:1
        );
    }
    else{
        color=({switch(code){
            case "transparent": return UIColor.clear;
            case "black": return UIColor.black;
            case "blue": return UIColor.blue;
            case "brown": return UIColor.brown;
            case "cyan": return UIColor.cyan;
            case "darkgray": return UIColor.darkGray;
            case "gray": return UIColor.gray;
            case "green": return UIColor.green;
            case "lightgray": return UIColor.lightGray;
            case "magenta": return UIColor.magenta;
            case "orange": return UIColor.orange;
            case "purple": return UIColor.purple;
            case "red": return UIColor.red;
            case "yellow": return UIColor.yellow;
            default: return UIColor.white; 
        }})();
    }
    return color;
}
