

class Store {
    private var store:[String:Any]=[:];

    init(){

    } 

    func initiate(_ state:[String:Any]?){
        if !(state==nil){
            self.store=state!;
        }
    }
    
    func set(_ path:String,_ value:Any?=nil){
        var keys=path.replacingOccurrences(of:" ",with:"").components(separatedBy:[".","["]);
        let target=keys.popLast()!;
        var data:Any=store;
        keys.forEach({key in
            data=Store.getProperty(key,data);
        });
        Store.setProperty(target,value,&data);
        if(keys.isEmpty){
            store=data as! [String:Any];
        }
        else{
            let parent=Store.getKeysPath(keys);
            self.set(parent,data);
        }
    }
    
    static func getKeysPath(_ keys:[String])->String{
        var path="";
        keys.forEach({key in
            path+=(key.hasSuffix("]") ? "[":".")+key;
        });
        path=String(path[path.index(after:path.startIndex)...]);
        return path;
    }
    
    static func getProperty(_ key:String,_ data:Any)->Any{
        var prop:Any=false;
        let bracketIndex=key.firstIndex(of:"]");
        if !(bracketIndex==nil){
            let index=Int(key.prefix(upTo:bracketIndex!))!;
            let array=data as? [Any?];
            if !(array==nil){
                prop=array![index] ?? false;
            }
        }
        else{
            let dictionary=data as? [String:Any];
            if !(dictionary==nil){
                prop=dictionary![key] ?? false;  
            }
        }
        return prop;
    }
    
    static func setProperty(_ key:String,_ value:Any?,_ data: inout Any){
        let bracketIndex=key.firstIndex(of:"]");
        if !(bracketIndex==nil){
            var array=data as? [Any];
            if !(array==nil){
                let symbol=key.prefix(upTo:bracketIndex!);
                switch(symbol){
                    case "push":
                        array!.append(value ?? false);
                        break;
                    case "unshift":
                        array!.insert(value ?? false,at:0);
                        break;
                    case "pop":
                        _=array!.popLast();
                        break;
                    case "shift":
                        array!.remove(at:0);
                        break;
                    case "last":
                        array![array!.count-1]=value ?? false;
                        break;
                    default:
                        let index=Int(symbol)!;
                        array![index]=value ?? false;
                    break;
                }
                data=array!;
            }
        }
        else{
            var dictionary=data as? [String:Any];
            if !(dictionary==nil){
                dictionary![key]=value;
                data=dictionary!;
            }
        }
    }
    
    func toObject()->[String:Any]{
        return store;
    }
}

