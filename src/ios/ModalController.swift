import AVFAudio;


class ModalController:WebViewController {

    override var isModal:Bool { get { return true; } };
    lazy var style:[String:Any]=[:];
    let bgview=UIView();

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
        self.setSilent();
        self.setDismissible();
    }
    
    
    override func viewDidLayoutSubviews(){
        super.viewDidLayoutSubviews();
        self.setViewBounds();
        if let lastView=self.view.subviews.last {
            lastView.backgroundColor = .clear;
        }
    }
    
    override func show(){
        ShowAnimation.fadeIn(view:self.view);
        ShowAnimation.modal(view:self.webView!);
    }

    override func hide(_ onHidden:((Bool)->Void)?){
        let duration=0.15;
        HideAnimation.modal(
            view:self.webView!,
            options:["duration":duration],
            onFinish:onHidden
        );
        HideAnimation.fadeOut(
            view:self.bgview,
            options:["duration":duration]
        );
    }

    var audioPlayer:AVAudioPlayer?=nil;
    private func setSilent(){
        let silent:Bool=style["silent"] as? Bool ?? true;
        if(!silent){
            guard let audioURL=Bundle.main.url(forResource:"modal_shown",withExtension:"mp3") else { return };
            do{
                try AVAudioSession.sharedInstance().setCategory(.playback,options:.mixWithOthers);
                audioPlayer=try AVAudioPlayer(contentsOf:audioURL);
                audioPlayer?.volume=0.1;
                audioPlayer?.play();
            }
            catch{}
        }
    }
    
    private func setDismissible(){
        let dismissible=options["dismissible"] as? Bool ?? (style["dismissible"] as? Bool ?? true);
        if(dismissible){
            self.setNotch();
            bgview.addGestureRecognizer(UITapGestureRecognizer(
                target:self,
                action:#selector(self.onBgViewTap)
            ));
            setupGestureRecognizer();
        }
    }
    
    @objc
    func onBgViewTap(sender:UITapGestureRecognizer){
        self.remove();
    }
    
    private func setupGestureRecognizer(){
        let panGesture=UIPanGestureRecognizer(target:self,action:#selector(self.onPanGesture));
        self.webView?.addGestureRecognizer(panGesture);
    }
    
    private func setNotch(){
        guard let webview=self.webView else { return };
        let notch=UIView();
        let notchColor=style["notchColor"] as? String;
        let width=webview.frame.width;
        let fraction=0.1;
        notch.frame=CGRect(
            x:(1-fraction)*width/2.0,y:7.5,
            width:fraction*width,
            height:4
        );
        notch.layer.cornerRadius=3;
        notch.backgroundColor=notchColor==nil ? UIColor(displayP3Red:0,green:0,blue:0,alpha:0.1) : getUIColorFromHex(notchColor!);
        webview.addSubview(notch);
    }
    
    var startY=CGFloat(),originY=CGFloat();
    var dragging=false;
    @objc func onPanGesture(gesture:UIPanGestureRecognizer){
        let state=gesture.state;
        let y=gesture.location(in:self.view).y;
        if(state==UIGestureRecognizer.State.began){
            let distance=gesture.location(in:self.webView).y;
            if(distance<=100){
                dragging=true;
                startY=y;
                originY=self.webView?.frame.origin.y ?? 0;
            }
        }
        else if(dragging){
            guard let webview=self.webView else { return };
            let dy=y-startY;
            if(state==UIGestureRecognizer.State.ended){
                dragging=false;
                let threshold=0.6*webview.frame.height;
                let speedY=gesture.velocity(in:self.view).y;
                if((speedY>1000)||(dy>threshold)){self.remove()}
                else{
                    UIView.animate(
                        withDuration:0.25,
                        delay:0,
                        options:.curveEaseOut,
                        animations:{
                            webview.frame.origin.y=self.originY;
                        }
                    );
                };
            }
            else if(dy>0){
                webview.frame.origin.y=originY+dy;
            }
        }
    }
    
    private func setViewBounds(){
        guard let webview=self.webView else { return };
        self.setCorners(webview);
        webview.layer.masksToBounds=true;
        webview.layer.isOpaque=true;
        let availableSize=webview.superview!.frame;
        let screenWidth=availableSize.width;
        let screenHeight=availableSize.height;
        let width=self.getDimension("width")*screenWidth;
        let height=self.getDimension("height",0.85)*(screenHeight-(isStatusBarTranslucent() ? 0 : UIApplication.shared.statusBarFrame.height));
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
    private func setCorners(_ view:UIView){
        let roundedTopLeftCorner=style["roundedTopLeftCorner"] as? Bool ?? true;
        let roundedTopRightCorner=style["roundedTopRightCorner"] as? Bool ?? true;
        let roundedBottomLeftCorner=style["roundedBottomLeftCorner"] as? Bool ?? false;
        let roundedBottomRightCorner=style["roundedBottomRightCorner"] as? Bool ?? false;
        let layer=view.layer;
        layer.cornerRadius=12;
        layer.maskedCorners=[
            roundedTopLeftCorner ? .layerMinXMinYCorner : [],
            roundedTopRightCorner ? .layerMaxXMinYCorner : [],
            roundedBottomLeftCorner ? .layerMinXMaxYCorner : [],
            roundedBottomRightCorner ? .layerMaxXMaxYCorner : [],
        ];
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
        guard let mainview=self.view else { return };
        self.bgview.frame=mainview.frame;
        bgview.backgroundColor=UIColor(red:0,green:0,blue:0,alpha:0.35);
        mainview.insertSubview(bgview,belowSubview:self.webView!);
    }
}
