

func showWebView(_ viewcontroller:UIViewController,_ animationId:String?){
    let view=viewcontroller.view!;
    switch(animationId){
        case "fadeIn": fadeIn(view);break;
        case "slideUp": slideUp(view);break;
        default: slideLeft(view);break;
    }
}

func hideWebView(_ viewcontroller:UIViewController,_ animationId:String?,_ onHidden:((Bool)->Void)?){
    let view=viewcontroller.view!;
    switch(animationId){
        case "slideDown": slideDown(view,onHidden);break;
        default: fadeOut(view,onHidden);break;
    }
}

func showModal(_ viewcontroller:UIViewController){
    let view=viewcontroller.view!;
    slideUp(view);
}

func hideModal(_ viewcontroller:UIViewController,_ onHidden:((Bool)->Void)?){
    let view=viewcontroller.view!;
    slideDown(view,onHidden);
}
