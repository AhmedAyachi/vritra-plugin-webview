

class HideAnimation {
    
    static func translateRight(
        view:UIView,
        options:[String:Any]=[:],
        onFinish:((Bool)->Void)?=nil
    ){
        view.transform = .identity;
        UIView.animate(
            withDuration:options["duration"] as? Double ?? 0.25,
            delay:options["delay"] as? Double ?? 0,
            options:.curveEaseOut,
            animations:{
                view.transform=CGAffineTransform(
                    translationX:UIScreen.main.bounds.width,
                    y:0
                );
            },
            completion:onFinish
        );
    }
    
    static func slideRight(
        view:UIView,
        options:[String:Any]=[:],
        onFinish:((Bool)->Void)?=nil
    ){
        var ops=options;
        if(!options.keys.contains("duration")){
            ops["duration"]=0.3;
        }
        HideAnimation.translateRight(
            view:view,
            options:ops,
            onFinish:onFinish
        );
    }
    
    static func translateLeft(
        view:UIView,
        options:[String:Any]=[:],
        onFinish:((Bool)->Void)?=nil
    ){
        view.transform = .identity;
        UIView.animate(
            withDuration:options["duration"] as? Double ?? 0.4,
            delay:options["delay"] as? Double ?? 0,
            options:.curveEaseOut,
            animations:{
                view.transform=CGAffineTransform(
                    translationX:-UIScreen.main.bounds.width,
                    y:0
                );
            },
            completion:onFinish
        );
    }
    
    static func slideLeft(
        view:UIView,
        options:[String:Any]=[:],
        onFinish:((Bool)->Void)?=nil
    ){
        var ops=options;
        if(!options.keys.contains("duration")){
            ops["duration"]=0.3;
        }
        HideAnimation.translateLeft(
            view:view,
            options:ops,
            onFinish:onFinish
        );
    }
    
    static func translateDown(
        view:UIView,
        options:[String:Any]=[:],
        onFinish:((Bool)->Void)?=nil
    ){
        UIView.animate(
            withDuration:options["duration"] as? Double ?? 0.15,
            delay:options["delay"] as? Double ?? 0,
            options:.curveEaseOut,
            animations:{
                view.transform=CGAffineTransform(translationX:0,y:UIScreen.main.bounds.height);
            },
            completion:onFinish
        );
    }
    
    static func slideDown(
        view:UIView,
        options:[String:Any]=[:],
        onFinish:((Bool)->Void)?=nil
    ){
        var ops=options;
        if(!options.keys.contains("duration")){
            ops["duration"]=0.4;
        }
        HideAnimation.translateDown(
            view:view,
            options:ops,
            onFinish:onFinish
        );
    }
    
    static func translateUp(
        view:UIView,
        options:[String:Any]=[:],
        onFinish:((Bool)->Void)?=nil
    ){
        view.transform = .identity;
        UIView.animate(
            withDuration:options["duration"] as? Double ?? 0.15,
            delay:options["delay"] as? Double ?? 0,
            options:.curveEaseIn,
            animations:{
                view.transform=CGAffineTransform(translationX:0,y:-UIScreen.main.bounds.height);
            },
            completion:onFinish
        );
    }
    
    static func slideUp(
        view:UIView,
        options:[String:Any]=[:],
        onFinish:((Bool)->Void)?=nil
    ){
        var ops=options;
        if(!options.keys.contains("duration")){
            ops["duration"]=0.3;
        }
        HideAnimation.translateUp(
            view:view,
            options:ops,
            onFinish:onFinish
        );
    }

    static func fadeOut(
        view:UIView,
        options:[String:Any]=[:],
        onFinish:((Bool)->Void)?=nil
    ){
        UIView.animate(
            withDuration:options["duration"] as? Double ?? 0.15,
            delay:options["delay"] as? Double ?? 0,
            options:.curveLinear,
            animations:{
                view.alpha=0;
            },
            completion:onFinish
        );
    }
    
    static func defaultAnim(
        view:UIView,
        options:[String:Any]=[:],
        onFinish:((Bool)->Void)?=nil
    ){
        let duration=options["duration"] as? Double ?? 0.4;
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
    
    static func prodLeft(
        view:UIView,
        options:[String:Any]=[:],
        onFinish:((Bool)->Void)?=nil
    ){
        let duration=options["duration"] as? Double ?? 0.3;
        CATransaction.begin();
        CATransaction.setAnimationDuration(duration);
        CATransaction.setAnimationTimingFunction(CAMediaTimingFunction(controlPoints:0.65,0.9,0.42,1));
        view.transform = .identity;
        UIView.animate(
            withDuration:duration,
            animations:{
                view.transform=CGAffineTransformMakeTranslation(-0.25*UIScreen.main.bounds.width,0);
            },
            completion:onFinish
        );
        CATransaction.commit();
    }
    
    static func modal(
        view:UIView,
        options:[String:Any]=[:],
        onFinish:((Bool)->Void)?=nil
    ){
        UIView.animate(
            withDuration:options["duration"] as? Double ?? 0.35,
            delay:options["delay"] as? Double ?? 0,
            options:.curveEaseIn,
            animations:{
                view.frame.origin.y=UIScreen.main.bounds.height;
            },
            completion:onFinish
        );
    }
}
