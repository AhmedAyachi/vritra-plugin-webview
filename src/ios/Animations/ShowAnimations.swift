class ShowAnimation {
    
    static let delay=0.15;
    
    static func translateLeft(view:UIView,options:[String:Any]=[:]){
        view.transform = .identity.concatenating(CGAffineTransform(
            translationX:UIScreen.main.bounds.width,
            y:0
        ));
        UIView.animate(
            withDuration:options["duration"] as? Double ?? 0.4,
            delay:options["delay"] as? Double ?? delay,
            options:.curveEaseOut,
            animations:{
                view.transform = .identity;
            }
        );
    }
    
    static func slideLeft(view:UIView,options:[String:Any]){
        var ops=options;
        if(!options.keys.contains("duration")){
            ops["duration"]=0.3;
        }
        ShowAnimation.translateLeft(view:view,options:ops);
    }
    
    static func translateRight(view:UIView,options:[String:Any]=[:]){
        view.transform = .identity.concatenating(CGAffineTransform(
            translationX:-UIScreen.main.bounds.width,
            y:0
        ));
        UIView.animate(
            withDuration:options["duration"] as? Double ?? 0.5,
            delay:options["delay"] as? Double ?? 0,
            options:.curveEaseOut,
            animations:{
                view.transform = .identity;
            }
        );
    }
    
    static func slideRight(view:UIView,options:[String:Any]){
        var ops=options;
        if(!options.keys.contains("duration")){
            ops["duration"]=0.3;
        }
        ShowAnimation.translateRight(view:view,options:ops);
    }
    
    static func translateUp(view:UIView,options:[String:Any]=[:]){
        view.transform = .identity.concatenating(CGAffineTransform(
            translationX:0,
            y:UIScreen.main.bounds.size.height
        ));
        UIView.animate(
            withDuration:options["duration"] as? Double ?? 0.35,
            delay:options["delay"] as? Double ?? delay,
            options:.curveEaseOut,
            animations:{
                view.transform=CGAffineTransform(translationX:0,y:0);
            }
        );
    }
    
    static func slideUp(view:UIView,options:[String:Any]=[:]){
        var ops=options;
        if(!options.keys.contains("duration")){
            ops["duration"]=0.3;
        }
        ShowAnimation.translateUp(view:view,options:ops);
    }
    
    static func translateDown(view:UIView,options:[String:Any]=[:]){
        view.transform = .identity.concatenating(CGAffineTransform(
            translationX:0,
            y:-UIScreen.main.bounds.size.height
        ));
        UIView.animate(
            withDuration:options["duration"] as? Double ?? 0.35,
            delay:options["delay"] as? Double ?? delay,
            options:.curveEaseOut,
            animations:{
                view.transform = .identity;
            }
        );
    }
    
    static func slideDown(view:UIView,options:[String:Any]=[:]){
        var ops=options;
        if(!options.keys.contains("duration")){
            ops["duration"]=0.4;
        }
        ops["delay"]=0.0;
        ShowAnimation.translateDown(view:view,options:ops);
    }
    
    static func fadeIn(view:UIView,options:[String:Any]=[:]){
        view.alpha=0;
        UIView.animate(
            withDuration:options["duration"] as? Double ?? 0.3,
            delay:options["delay"] as? Double ?? delay,
            options:.curveEaseIn,
            animations:{
                view.alpha=1;
            }
        );
    }
    
    static func defaultAnim(view:UIView,options:[String:Any]=[:]){
        let duration=options["duration"] as? Double ?? 0.4;
        CATransaction.begin();
        CATransaction.setAnimationDuration(duration);
        CATransaction.setAnimationTimingFunction(CAMediaTimingFunction(controlPoints:0.65,0.9,0.42,1));
        view.transform = .identity.concatenating(CGAffineTransform(
            translationX:UIScreen.main.bounds.width,
            y:0
        ));
        UIView.animate(
            withDuration:duration,
            delay:delay/2,
            animations:{
                view.transform=CGAffineTransform(translationX:0,y:0);
            }
        );
        CATransaction.commit();
    }
    
    static func prodRight(view:UIView,options:[String:Any]=[:]){
        let duration=options["duration"] as? Double ?? 0.4;
        CATransaction.begin();
        CATransaction.setAnimationDuration(duration);
        CATransaction.setAnimationTimingFunction(CAMediaTimingFunction(controlPoints:0.22,0.64,0.25,0.95));
        view.layer.frame.origin.x=0;
        view.transform = .identity.concatenating(CGAffineTransform(
            translationX:-0.25*UIScreen.main.bounds.width,
            y:0
        ));
        UIView.animate(
            withDuration:duration,
            animations:{
                view.transform = .identity;
            }
        );
        CATransaction.commit();
    }
    
    static func modal(view:UIView,options:[String:Any]=[:]){
        view.transform = .identity.concatenating(CGAffineTransform(
            translationX:0,
            y:UIScreen.main.bounds.size.height
        ));
        UIView.animate(
            withDuration:options["duration"] as? Double ?? 0.35,
            delay:options["delay"] as? Double ?? delay,
            options:.curveEaseOut,
            animations:{
                view.transform=CGAffineTransform(translationX:0,y:0);
            },
            completion:{ _ in
                view.transform = .identity;
            }
        );
    }
}
