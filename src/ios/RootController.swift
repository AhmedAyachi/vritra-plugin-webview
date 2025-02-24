

class RootController:UIViewController {
    override func viewDidLoad(){
        super.viewDidLoad();
        self.setNeedsStatusBarAppearanceUpdate();
    }
    
    func addViewController(_ viewController:UIViewController){
        guard let childView=viewController.view else { return };
        self.addChild(viewController);
        self.view.addSubview(childView);
        childView.frame=self.view.bounds;
        if let webviewController=viewController as? WebViewController {
            webviewController.show();
        }        
    }
}
