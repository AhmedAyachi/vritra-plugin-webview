

class ViewController:CDVViewController {
    
    var options:[AnyHashable:Any]=["":""];

    init?(_ options:[AnyHashable:Any]?){
        super.init(/* coder:NSCoder() */);
        self.options=options!;
        setURL();
    }

    required init?(coder:NSCoder){
        super.init(coder:coder);
    }

    func setURL(){
        var url:String?=options["file"] as? String;
        if((url==nil)||(url!.isEmpty)){
            url=options["url"] as? String;
            self.wwwFolderName="";
        }
        else{
            self.wwwFolderName="www";
        }
        self.startPage=url;
    }

}
