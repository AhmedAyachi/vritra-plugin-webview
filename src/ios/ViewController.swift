

class ViewController:CDVViewController{

    var plugin:Webview?=nil;
    var options:[AnyHashable:Any]=[:];
    var message:String?=nil;
    public var isModal:Bool=true;

    override var prefersStatusBarHidden:Bool{
        return true;
    }

    override func viewDidLoad(){
        super.viewDidLoad();
        setNeedsStatusBarAppearanceUpdate();
    }

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
                "store":Webview.store.toObject(),
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
        view.frame=UIScreen.main.bounds;
        view.clipsToBounds=false;
        view.isOpaque=true;

        let backgroundColor=options["backgroundColor"] as? String;
        let color=backgroundColor==nil ? UIColor.white:getUIColorFromHex(backgroundColor!);
        self.view.backgroundColor=color;
        self.launchView.backgroundColor=color;
        self.webView.backgroundColor=color;
    }

    static func getInstance(_ options:[AnyHashable:Any],_ plugin:Webview)->ViewController{
        let viewcontroller=ViewController();
        viewcontroller.setOptions(options,plugin);
        return viewcontroller;
    }
}

func getUIColorFromHex(_ code:String)->UIColor{
    var color=UIColor.white;
    if(code.starts(with:"#")&&code.count>6){
        let hex=code[code.index(after:code.startIndex)..<code.index(code.startIndex,offsetBy:7)];
        var parts:[Int16]=[];
        for i in 0..<3 {
            let start=hex.index(hex.startIndex,offsetBy:i*2);
            let pair=String(hex[start...hex.index(start,offsetBy:1)]);
            let decimal=Int16(pair,radix:16)!;
            parts.append(decimal);
        }
        color=UIColor(
            red:CGFloat(parts[0]),
            green:CGFloat(parts[1]),
            blue:CGFloat(parts[2]),
            alpha:1
        );
    }
    else{
        switch(code.lowercased()){
            case "black":
                color=UIColor.black;
                break;
            case "blue":
                color=UIColor.blue;
                break;
            case "brown":
                color=UIColor.brown;
                break;
            case "cyan":
                color=UIColor.cyan;
                break;
            case "darkgray":
                color=UIColor.darkGray;
                break;
            case "gray":
                color=UIColor.gray;
                break;
            case "green":
                color=UIColor.green;
                break;
            case "lightgray":
                color=UIColor.lightGray;
                break;
            case "magenta":
                color=UIColor.magenta;
                break;
            case "orange":
                color=UIColor.orange;
                break;
            case "purple":
                color=UIColor.purple;
                break;
            case "red":
                color=UIColor.red;
                break;
            case "white":
                color=UIColor.white;
                break;
            case "yellow":
                color=UIColor.yellow;
                break;
            default:
                color=UIColor.white;
                break;
        }
    }
    return color;
}
