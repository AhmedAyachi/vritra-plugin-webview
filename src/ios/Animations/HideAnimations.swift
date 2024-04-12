

class HideAnimation {
    
    static func slideRight(_ view:UIView,_ animOptions:[String:Any]?=nil){
        let options=animOptions ?? [:];
        let duration=options["duration"] as? Double ?? 0.4;
        let onFinish=options["onFinish"] as? ((Bool)->Void);
        CATransaction.begin();
        CATransaction.setAnimationDuration(duration);
        CATransaction.setAnimationTimingFunction(CAMediaTimingFunction(controlPoints:0.22,0.64,0.25,0.95));
        view.transform=CGAffineTransform(translationX:0,y:0);
        UIView.animate(
            withDuration:duration,
            animations:{
                view.transform=CGAffineTransform(translationX:UIScreen.main.bounds.width,y:0);
            },
            completion:onFinish
        );
        CATransaction.commit();
    }
    
    static func slideDown(_ view:UIView,_ animOptions:[String:Any]?=nil){
        let options=animOptions ?? [:];
        let duration=options["duration"] as? Double ?? 0.15;
        let onFinish=options["onFinish"] as? ((Bool)->Void);
        UIView.animate(
            withDuration:duration,
            delay:0,
            options:.curveEaseIn,
            animations:{
                view.frame.origin.y=UIScreen.main.bounds.height;
            },
            completion:onFinish
        );
    }

    static func fadeOut(_ view:UIView,_ animOptions:[String:Any]?){
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
}
