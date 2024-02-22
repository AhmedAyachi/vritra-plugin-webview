

func fadeIn(_ view:UIView,_ duration:Double?=0.3){
    view.alpha=0;
    UIView.animate(
        withDuration:duration ?? 0.3,
        delay:0,
        options:.curveEaseIn,
        animations:{
            view.alpha=1;
        }
    );
}

func slideUp(_ view:UIView,_ duration:Double?=0.3){
    view.transform=CGAffineTransform(translationX:0,y:UIScreen.main.bounds.size.height);
    view.alpha=0;
    UIView.animate(
        withDuration:duration ?? 0.3,
        delay:0,
        options:.curveEaseOut,
        animations:{
            view.transform=CGAffineTransform(translationX:0,y:0);
            view.alpha=1;
        }
    );
}

func slideLeft(_ view:UIView,_ duration:Double?=0.3){
    view.transform=CGAffineTransform(translationX:UIScreen.main.bounds.size.width,y:0);
    view.alpha=0;
    UIView.animate(
        withDuration:duration ?? 0.3,
        delay:0,
        options:.curveEaseInOut,
        animations:{
            view.transform=CGAffineTransform(translationX:0,y:0);
            view.alpha=1;
        }
    );
}
