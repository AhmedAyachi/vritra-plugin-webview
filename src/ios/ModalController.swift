

class ModalController:WebViewController {

    override var isModal:Bool {
        get {return true}
    };
    lazy var style:[String:Any]=[:];

    override init(_ options:[String:Any],_ plugin:Webview?){
        super.init(options,plugin);
        let mainview=self.view!;
        mainview.backgroundColor=UIColor(red:0,green:0,blue:0,alpha:0.25);
        if let modalStyle=options["modalStyle"] as? [String:Any] {
            self.style=modalStyle;
        }
    }

    required init?(coder:NSCoder) {
        super.init(coder:coder);
    }

    override func viewDidLayoutSubviews(){
        super.viewDidLayoutSubviews();
        self.setViewBounds();
    }
    
    override func show(){
        let mainview=self.view!;
        let webView=self.webView!;
        fadeIn(mainview,0.3);
        slideUp(webView,0.3);
    }

    override func hide(_ onHidden:((Bool)->Void)?){
        let mainview=self.view!;
        let webView=self.webView!;
        fadeOut(mainview,[
            "duration":0.4,
        ]);
        slideDown(webView,[
            "duration":0.4,
            "onFinish":onHidden as Any,
        ]);
    }

    private func setViewBounds(){
        let view=self.webView!;
        view.layer.cornerRadius=10;
        view.layer.masksToBounds=true;
        view.layer.isOpaque=true;
        let availableSize=view.superview!.frame;
        let screenWidth=availableSize.width;
        let screenHeight=availableSize.height;
        let width=self.getWidth()*screenWidth;
        let height=self.getHeight()*screenHeight;
        let marginLeft=self.getMarginLeft()*screenWidth;
        var marginTop=self.getMarginTop()*screenHeight;
        let verticalAlign=self.getVerticalAlign();
        switch(verticalAlign){
            case "bottom":marginTop+=screenHeight-height;break;
            case "middle":marginTop+=(screenHeight-height)/2;break;
            default:break;
        }
        view.frame=CGRect(x:marginLeft,y:marginTop,width:width,height:height);
    }
    
    private func getWidth()->Double{
        var width=style["width"] as? Double ?? 1;
        if((width<0)||(width>1)){
            width=1;
        }
        return width;
    }
    private func getHeight()->Double{
        var height=style["height"] as? Double ?? 0.85;
        if((height<0)||(height>1)){
            height=0.85;
        }
        return height;
    }
    private func getMarginLeft()->Double{
        var marginLeft=style["marginLeft"] as? Double ?? 0;
        if((marginLeft < -1)||(marginLeft>1)){
            marginLeft=0;
        }
        return marginLeft;
    }
    private func getMarginTop()->Double{
        var marginTop=style["marginTop"] as? Double ?? 0;
        if((marginTop < -1)||(marginTop>1)){
            marginTop=0;
        }
        return marginTop;
    }
    private func getVerticalAlign()->String{
        var verticalAlign="bottom";
        if let value=style["verticalAlign"] as? String {
            if((value=="top")||(value=="middle")){
                verticalAlign=value;
            }
        }
        return verticalAlign;
    }
}
