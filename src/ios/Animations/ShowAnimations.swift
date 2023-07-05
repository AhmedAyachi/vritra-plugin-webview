

func fadeIn(_ view:UIView){
    view.alpha=0;
    UIView.animate(
        withDuration:0.3,
        delay:0,
        options:.curveEaseIn,
        animations:{
            view.alpha=1;
        }
    );
}

func slideUp(_ view:UIView){
    view.transform=CGAffineTransform(translationX:0,y:UIScreen.main.bounds.size.height);
    view.alpha=0;
    UIView.animate(
        withDuration:0.3,
        delay:0,
        options:.curveEaseIn,
        animations:{
            view.transform=CGAffineTransform(translationX:0,y:0);
            view.alpha=1;
        }
    );
}

func slideLeft(_ view:UIView){
    view.transform=CGAffineTransform(translationX:UIScreen.main.bounds.size.width,y:0);
    view.alpha=0;
    UIView.animate(
        withDuration:0.2,
        delay:0,
        options:.curveLinear,
        animations:{
            view.transform=CGAffineTransform(translationX:0,y:0);
            view.alpha=1;
        }
    );
}
