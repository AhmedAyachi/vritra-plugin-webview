

module.exports={
    get:function(path){
        const store=JSON.parse(localStorage.getItem("store"))||{};
        const value=(()=>{
            if(path===undefined) return store;
            else if(typeof(path)==="string"){
                const keys=path.split(/\.|\[/g).filter(key=>key);
                let objects=[store];
                for(const key of keys){
                    objects=objects.flatMap(object=>{
                        const value=getProperty(key,object);
                        return key==="*]"?value:[value];
                    });
                }
                if(objects.length>1) return objects;
                else return objects[0];
            }
            else throw new Error("invalid path");
        })();
        if(path.includes("shift")||path.includes("pop")){
            localStorage.setItem("store",JSON.stringify(store));
        }
        return value;
    },
    set:function(path,value){
        let store;
        try{
            store=JSON.parse(localStorage.getItem("store"))||{};
            if(Array.isArray(path)){
                const {length}=path;
                if(length%2===0){
                    for(let i=0;i<length;i+=2){
                        const token=path[i],value=path[i+1];
                        mutate(store,token,value);
                    }
                }
                else throw new Error("array length should be even");
            }
            else{
                mutate(store,path,value);
            }
            localStorage.setItem("store",JSON.stringify(store));
            return store;
        }
        catch(error){
            localStorage.setItem("store",JSON.stringify(store));
            throw error;
        }
    },
};

const mutate=(data,path,value)=>{
    if(path&&typeof(path)==="string"){
        const keys=path.split(/\.|\[/g).filter(key=>key);
        const target=keys.pop();
        let objects=[data];
        if(keys.length){
            for(const key of keys){
                objects=objects.flatMap(object=>{
                    const value=getProperty(key,object);
                    return key==="*]"?value:[value];
                });
            }
        }
        for(const object of objects){
            if(value===undefined){
                deleteProperty(object,target);
            }
            else{
                setProperty(object,target,value);
            }
        }
    }
    else throw new Error("invalid key");
}

const getProperty=(key,data)=>{
    let value;
    const bracketi=key.indexOf("]");
    if(bracketi>-1){
        if(Array.isArray(data)){
            const index=key.substring(0,bracketi).trim();
            switch(index){
                case "*": value=data;break;
                case "last": value=data[data.length-1];break;
                case "pop": value=data.pop();break;
                case "shift": value=data.shift();break;
                default: 
                    const int=parseInt(index);
                    if(int>=0) value=data[int];
                    else throw UnrecognizedSymbol(index);
                break;
            }
        }
        else throw ArrayCastError(key);
    }
    else if(typeof(data)==="object"){
        value=data[key];
    }
    else throw ObjectCastError(key);
    return value;
}

const setProperty=(data,key,value)=>{
    const bracketi=key.indexOf("]");
    if(bracketi>-1){
        if(Array.isArray(data)){
            const index=key.substring(0,bracketi).trim();
            switch(index){
                case "*":
                    const {length}=data;
                    for(let i=0;i<length;i++){
                        data[i]=value;
                    }
                    break;
                case "last": data[data.length-1]=value;break;
                case "pop": data.pop();break;
                case "shift": data.shift();break;
                case "push": data.push(value);break;
                case "unshift": data.unshift(value);break;
                default: 
                    const int=parseInt(index);
                    if(int>=0) data[i]=value;
                    else throw UnrecognizedSymbol(index);
                break;
            }
        }
        else throw ArrayCastError(key);
    }
    else if(typeof(data)==="object"){
        data[key]=value;
    }
    else throw ObjectCastError(key);
}

const deleteProperty=(data,key)=>{
    const bracketi=key.indexOf("]");
    if(bracketi>-1){
        if(Array.isArray(data)){
            const index=key.substring(0,bracketi).trim();
            switch(index){
                case "last":
                case "pop": data.pop();break;
                case "shift": data.shift();break;
                case "push": data.push(null);break;
                case "unshift": data.unshift(null);break;
                case "*":data.splice(0,data.length);break;
                default:
                    const int=parseInt(index);
                    if(int>=0) data.splice(int,1);
                    else throw UnrecognizedSymbol(index);
                break;
            }
        }
        else throw ArrayCastError(key);
    }
    else if(typeof(data)==="object"){
        delete data[key];
    }
    else throw ObjectCastError(key);
}

const ArrayCastError=(key="")=>new Error(`cast to array failed at "${key}"`);
const ObjectCastError=(key="")=>new Error(`cast to object failed at "${key}"`);
const UnrecognizedSymbol=(symbol)=>new Error(`unrecognized symbol "${symbol}"`);
  