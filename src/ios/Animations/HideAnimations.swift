

func slideDown(_ view:UIView,_ onHidden:((Bool)->Void)?){
    UIView.animate(
        withDuration:0.4,
        delay:0,
        options:.curveEaseOut,
        animations:{
            view.transform=CGAffineTransform(translationX:0,y:UIScreen.main.bounds.size.height);
            view.alpha=0;
        },
        completion:onHidden
    );
}

func fadeOut(_ view:UIView,_ onHidden:((Bool)->Void)?){
    UIView.animate(
        withDuration:0.1,
        delay:0,
        options:.curveLinear,
        animations:{
            view.alpha=0;
        },
        completion:onHidden
    );
}
