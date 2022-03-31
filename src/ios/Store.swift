

class Store {
    private var store:[String:Any]=[:];

    init(){

    } 

    func initiate(_ state:[String:Any]?){
        if !(state==nil){
            self.store=state!;
        }
    }
    
    func mutate(_ path:String,_ value:Any?=nil){
        self.setStore(path,[value ?? false]);
    }

    func setStore(_ path:String,_ values:[Any]=[false]){
        var keys=path.replacingOccurrences(of:" ",with:"").components(separatedBy:[".","["]);
        let target=keys.popLast()!;
        var data:[Any]=[store];
        keys.forEach({key in
            data=data.flatMap({item in Store.getProperties(key,item)});
        });
        
        let length=data.count;
        for i in 0..<length {
            Store.setProperty(target,target.contains("*") ? values:values[0],&data[i]);
        } 
        if(keys.isEmpty){
            store=data[0] as! [String:Any];
        }
        else{
            let parent=Store.getKeysPath(keys);
            self.setStore(parent,data);
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
    
    static func getProperties(_ key:String,_ data:Any)->[Any]{
        var props:[Any]=[];
        let bracketIndex=key.firstIndex(of:"]");
        if !(bracketIndex==nil){
            let array=data as? [Any?];
            if !(array==nil){
                let symbol=key.prefix(upTo:bracketIndex!);
                switch(symbol){
                    case "*":
                        array!.forEach({item in
                            props.append(item ?? false);
                        });
                        break;
                    default:
                        let index=Int(symbol)!;
                        props.append(array![index] ?? false);
                    break;
                }
            }
        }
        else{
            let dictionary=data as? [String:Any];
            if !(dictionary==nil){
                props.append(dictionary![key] ?? false);  
            }
        }
        return props;
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
                    case "*":
                        let length=array!.count;
                        let values=value as! [Any];
                        for i in 0..<length {
                            array![i]=values[values.count>1 ?i:0];
                        }
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

