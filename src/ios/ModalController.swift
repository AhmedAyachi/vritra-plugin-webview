

class ModalController:WebViewController {

    override var isModal:Bool {
        get {return true}
    };
    lazy var style:[String:Any]=[:];

    override init(_ options:[AnyHashable:Any],_ plugin:Webview?){
        super.init(options,plugin);
        if let modalStyle=options["modalStyle"] as? [String:Any] {
            self.style=modalStyle;
        }
    }

    required init?(coder:NSCoder) {
        super.init(coder:coder);
    }
    override func viewDidLayoutSubviews(){
        print("viewDidLayoutSubviews called");
        super.viewDidLayoutSubviews();
        self.setViewBounds();
    }

    private func setViewBounds(){
        let view=self.view!;
        let screenSize=UIScreen.main.bounds.size;
        let screenWidth=screenSize.width;
        let screenHeight=screenSize.height;
        let width=self.getWidth()*screenWidth;
        let height=self.getHeight()*screenHeight;
        let marginLeft=self.getMarginLeft()*screenWidth;
        var marginTop=self.getMarginTop()*screenHeight;
        let verticalAlign=self.getVerticalAlign();
        switch(verticalAlign){
            case "bottom":marginTop+=screenHeight-height;break;
            case "center":marginTop+=(screenHeight-height)/2;break;
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
            if((value=="top")||(value=="center")){
                verticalAlign=value;
            }
        }
        return verticalAlign;
    }
}
