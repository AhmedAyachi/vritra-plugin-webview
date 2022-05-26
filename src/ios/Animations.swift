

func showWebView(_ viewcontroller:UIViewController){
    let view=viewcontroller.view!;
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

func hideWebView(_ viewcontroller:UIViewController,_ onHidden:((Bool)->Void)?){
    UIView.animate(
        withDuration:0.1,
        delay:0,
        options:.curveLinear,
        animations:{
            viewcontroller.view.alpha=0;
        },
        completion:onHidden
    );
}

func showModal(_ viewcontroller:UIViewController){
    let view=viewcontroller.view!;
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

func hideModal(_ viewcontroller:UIViewController,_ onHidden:((Bool)->Void)?){
    let view=viewcontroller.view!;
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
