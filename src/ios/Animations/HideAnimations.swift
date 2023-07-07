

func slideDown(_ view:UIView,_ onHidden:((Bool)->Void)?){
    UIView.animate(
        withDuration:0.4,
        delay:0,
        options:.curveEaseOut,
        animations:{
            let screenBounds=UIScreen.main.bounds;
            view.layer.transform=CATransform3DMakeTranslation(
                view.bounds.origin.x,
                screenBounds.height-view.frame.origin.y,0
            );
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
