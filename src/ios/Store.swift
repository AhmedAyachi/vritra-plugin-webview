

class Store {
    private var store:[String:Any]=[:];
    
    init(){
        
    }
    
    func initiate(_ state:[String:Any]?){
        if(state != nil){
            self.store=state!;
        }
    }
    
    func get(_ path:String)throws->[Any]{
        let keys=path.replacingOccurrences(of:" ",with:"").components(separatedBy:[".","["]);
        var data:[Any]=[store];
        try keys.forEach({key in
            data=try data.flatMap({item in try Store.getProperties(key,item)});
        });
        try self.mutate(Store.getKeysPath(keys),data);
        return data;
    }
    
    func set(_ path:String,_ value:Any?=nil) throws {
        if(path.isEmpty){
            throw Webview.Error("invalid key");
        }
        else{
            try self.mutate(path,[value ?? false]);
        }
    }
    
    func delete(_ path:String) throws {
        var keys=path.replacingOccurrences(of:" ",with:"").components(separatedBy:[".","["]);
        let target=keys.popLast()!;
        var data:[Any]=[store];
        try keys.forEach({key in
            data=try data.flatMap({item in try Store.getProperties(key,item)});
        });
        
        let length=data.count;
        for i in 0..<length {
            try Store.deleteProperty(target,&data[i]);
        }
        try self.mutate(Store.getKeysPath(keys),data);
    }
    
    private func mutate(_ path:String,_ values:[Any]=[false]) throws {
        var keys=path.replacingOccurrences(of:" ",with:"").components(separatedBy:[".","["]);
        let target=keys.popLast()!;
        var data:[Any]=[store];
        try keys.forEach({key in
            data=try data.flatMap({item in try Store.getProperties(key,item)});
        });
        
        let length=data.count;
        for i in 0..<length {
            try Store.setProperty(target,target.contains("*") ? values:values[0],&data[i]);
        }
        if(keys.isEmpty){
            store=data[0] as? [String:Any] ?? [:];
        }
        else{
            try self.mutate(Store.getKeysPath(keys),data);
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
    
    static func getProperties(_ key:String,_ data:Any)throws->[Any]{
        if let value=data as? Bool,value==false {
            throw Webview.Error("cannot get properties of falsy (getting \""+key+"\")");
        }
        var props:[Any]=[];
        let index=key.firstIndex(of:"]");
        if let bracketIndex=index {
            if var array=data as? [Any?] {
                let symbol=key.prefix(upTo:bracketIndex);
                switch(symbol){
                    case "*":
                        array.forEach({item in
                            props.append(item ?? false);
                        });
                        break;
                    case "pop":
                        props.append(array.removeLast() ?? false);
                        break;
                    case "shift":
                        if(array.isEmpty){
                            props.append(false);
                        }
                        else{
                            props.append(array.removeFirst() ?? false);
                        }
                        break;
                    case "last":
                        let index=array.count-1;
                        if(index > -1){
                            props.append(array[index] ?? false);
                        }
                        break;
                    default:
                        let index=Int(symbol) ?? -1;
                        if((0..<array.count).contains(index)){
                            props.append(array[index] ?? false);
                        }
                        else{
                            props.append(false);
                        }
                        break;
                }
            }
            else{
                throw Webview.Error("object cannot be cast to array");
            }
        }
        else if let dictionary=data as? [String:Any] {
            props.append(dictionary[key] ?? false);
        }
        else{
            throw ObjectCastError;
        }
        return props;
    }
    
    static func setProperty(_ key:String,_ value:Any?,_ data: inout Any) throws {
        if let value=data as? Bool,value==false {
            throw Webview.Error("cannot set properties of falsy (setting \""+key+"\")");
        }
        if let bracketIndex=key.firstIndex(of:"]"){
            if var array=data as? [Any?] {
                let symbol=key.prefix(upTo:bracketIndex);
                switch(symbol){
                    case "push":array.append(value ?? false);break;
                    case "unshift":
                        array.insert(value ?? false,at:0);break;
                    case "pop":
                        _=array.popLast();break;
                    case "shift":
                        if(!array.isEmpty){
                            array.remove(at:0);
                        }
                        break;
                    case "last":
                        array[array.count-1]=value ?? false;break;
                    case "*":
                        let length=array.count;
                        let values=value as! [Any];
                        for i in 0..<length {
                            array[i]=values[values.count>1 ?i:0];
                        }
                        break;
                    default:
                        let index=Int(symbol)!;
                        array[index]=value ?? false;
                        break;
                }
                data=array;
            }
            else{
                throw Webview.Error("Object caanot be cast to array");
            }
        }
        else if var dictionary=data as? [String:Any]{
            dictionary[key]=value;
            data=dictionary;
        }
        else{
            throw ObjectCastError;
        }
    }
    
    static func deleteProperty(_ key:String,_ data: inout Any) throws {
        if let value=data as? Bool,value==false {
            throw Webview.Error("cannot delete properties of falsy (deleting \""+key+"\")");
        }
        if let bracketIndex=key.firstIndex(of:"]"),
           var array=data as? [Any?]{
            let symbol=key.prefix(upTo:bracketIndex);
            switch(symbol){
                case "pop","last":array.removeLast();break;
                case "shift":
                    if(!array.isEmpty){
                        array.remove(at:0);
                    }
                    break;
                case "push":array.append(nil);break;
                case "unshift":array.insert(nil,at:0);break;
                case "*":array.removeAll();break;
                default:
                    if let index=Int(symbol) {
                        array.remove(at:index);
                    };
                    break;
            }
            data=array;
        }
        else if var dictionary=data as? [String:Any] {
            dictionary.removeValue(forKey:key);
            data=dictionary;
        }
        else{
            throw ObjectCastError;
        }
    }
    
    func toObject()->[String:Any]{
        return store;
    }
}

let ObjectCastError=Webview.Error("cast to object failed");
