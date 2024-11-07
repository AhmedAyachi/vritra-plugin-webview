

module.exports={
    get:function(path){
        const store=JSON.parse(localStorage.getItem("store"))||{};
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
                default: value=data[parseInt(index)];break;
            }
        }
        else throw ArrayCastError;
    }
    else if(typeof(data)==="object"){
        value=data[key];
    }
    else throw ObjectCastError;
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
                    const index=parseInt(index);
                    if(isNaN(index)) throw new Error("");
                    else data[index]=value;
                    break;
            }
        }
        else throw ArrayCastError;
    }
    else if(typeof(data)==="object"){
        data[key]=value;
    }
    else throw ObjectCastError;
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
                default: data.splice(parseInt(index),1);break;
            }
        }
        else throw ArrayCastError;
    }
    else if(typeof(data)==="object"){
        delete data[key];
    }
    else throw ObjectCastError;
}

const ArrayCastError=new Error("cast to array failed");
const ObjectCastError=new Error("cast to object failed");
  