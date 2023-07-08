

func slideDown(_ view:UIView,_ animOptions:[String:Any]?=nil){
    let options=animOptions ?? [:];
    let duration=options["duration"] as? Double ?? 0.15;
    let onFinish=options["onFinish"] as? ((Bool)->Void);
    UIView.animate(
        withDuration:duration,
        delay:0,
        options:.curveEaseOut,
        animations:{
            let screenBounds=UIScreen.main.bounds;
            view.frame.origin.y=screenBounds.height-view.frame.origin.y;
            view.alpha=0;
        },
        completion:onFinish
    );
}

func fadeOut(_ view:UIView,_ animOptions:[String:Any]?){
    let options=animOptions ?? [:];
    let duration=options["duration"] as? Double ?? 0.15;
    let onFinish=options["onFinish"] as? ((Bool)->Void);
    UIView.animate(
        withDuration:duration,
        delay:0,
        options:.curveLinear,
        animations:{
            view.alpha=0;
        },
        completion:onFinish
    );
}
