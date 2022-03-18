import Foundation;


class WebView:Plugin {

    @objc(show:)
    func show(command:CDVInvokedUrlCommand){
        //let options=command.arguments[0] as? [AnyHashable:Any];
        success(command,"options");
        //let viewcontroller:CDVViewController=CDVViewController();
        //_=ViewController(options);
    }

}