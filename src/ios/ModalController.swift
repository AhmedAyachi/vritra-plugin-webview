import AVFAudio;


class ModalController:WebViewController {

    override var isModal:Bool {
        get {return true}
    };
    lazy var style:[String:Any]=[:];
    var audioPlayer:AVAudioPlayer?=nil;
    let bgview:UIView=UIView();

    override init(_ options:[String:Any],_ plugin:Webview?){
        super.init(options,plugin);
        setBGView();
        if let modalStyle=options["modalStyle"] as? [String:Any] {
            self.style=modalStyle;
        }
    }

    required init?(coder:NSCoder) {
        super.init(coder:coder);
    }

    override func viewDidAppear(_ animated:Bool){
        super.viewDidAppear(animated);
        let silent:Bool=style["silent"] as? Bool ?? false;
        if(!silent){
            if let audioURL=Bundle.main.url(forResource:"modal_shown",withExtension:"mp3"){
                do{
                    audioPlayer=try AVAudioPlayer(contentsOf:audioURL);
                    audioPlayer?.volume=0.1;
                    audioPlayer?.play();
                }
                catch{}
            };
        }
        
    }

    override func viewDidLayoutSubviews(){
        super.viewDidLayoutSubviews();
        self.setViewBounds();
    }
    
    override func show(){
        let duration=0.2;
        fadeIn(self.view,duration);
        slideUp(self.webView!,duration);
    }

    override func hide(_ onHidden:((Bool)->Void)?){
        let duration=0.2;
        slideDown(self.webView!,[
            "duration":duration,
            "onFinish":onHidden as Any,
        ]);
        fadeOut(self.bgview,["duration":duration]);
    }

    private func setViewBounds(){
        let webview=self.webView!;
        webview.layer.cornerRadius=10;
        webview.layer.masksToBounds=true;
        webview.layer.isOpaque=true;
        let availableSize=webview.superview!.frame;
        let screenWidth=availableSize.width;
        let screenHeight=availableSize.height;
        let width=self.getDimension("width")*screenWidth;
        let height=self.getDimension("height",0.85)*screenHeight;
        let marginLeft=self.getMargin("Left")*screenWidth;
        var marginTop=self.getMargin("Top")*screenHeight;
        let verticalAlign=self.getVerticalAlign();
        switch(verticalAlign){
            case "bottom":marginTop+=screenHeight-height;break;
            case "middle":marginTop+=(screenHeight-height)/2;break;
            default:break;
        }
        webview.alpha=self.getOpacity();
        webview.frame=CGRect(x:marginLeft,y:marginTop,width:width,height:height);
    }
    
    private func getDimension(_ name:String,_ fallback:Double=1)->Double{
        var dimension=style[name] as? Double ?? fallback;
        if((dimension<0)||(dimension>1)){
            dimension=1;
        }
        return dimension;
    }
    private func getMargin(_ side:String)->Double{
        var margin=style["margin"+side] as? Double ?? 0;
        if((margin < -1)||(margin>1)){
            margin=0;
        }
        return margin;
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
    private func getOpacity()->Double{
        var opacity=style["opacity"] as? Double ?? 1;
        if((opacity>1)||(opacity<0)){
            opacity=1;
        }
        return opacity;
    }
    
    private func setBGView(){
        let mainview=self.view!;
        self.bgview.frame=mainview.frame;
        bgview.backgroundColor=UIColor(red:0,green:0,blue:0,alpha:0.25);
        mainview.insertSubview(bgview,belowSubview:self.webView!);
    }
}
