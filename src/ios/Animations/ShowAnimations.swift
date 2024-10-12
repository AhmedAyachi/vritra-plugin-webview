class ShowAnimation {
    
    static let delay=0.2;
    
    static func fadeIn(_ view:UIView,_ duration:Double?=0.3){
        view.alpha=0;
        UIView.animate(
            withDuration:duration ?? 0.3,
            delay:delay,
            options:.curveEaseIn,
            animations:{
                view.alpha=1;
            }
        );
    }
    
    static func slideUp(_ view:UIView,_ duration:Double?=0.3){
        view.transform=CGAffineTransform(translationX:0,y:UIScreen.main.bounds.size.height);
        UIView.animate(
            withDuration:duration ?? 0.3,
            delay:delay,
            options:.curveEaseOut,
            animations:{
                view.transform=CGAffineTransform(translationX:0,y:0);
            }
        );
    }
    
    static func slideLeft(_ view:UIView,_ animDuration:Double?){
        //fadeIn(view,0.2);
        let duration=animDuration ?? 0.5;
        CATransaction.begin();
        CATransaction.setAnimationDuration(duration);
        CATransaction.setAnimationTimingFunction(CAMediaTimingFunction(controlPoints:0.19,1,0.22,1));
        view.transform=CGAffineTransform(translationX:UIScreen.main.bounds.size.width,y:0);
        UIView.animate(
            withDuration:duration,
            delay:delay,
            animations:{
                view.transform=CGAffineTransform(translationX:0,y:0);
            }
        );
        CATransaction.commit();
    }
}
