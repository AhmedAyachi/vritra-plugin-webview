

function JsParser(data,path="",value){
    const keys=path.split(/\.|\[/g).filter(key=>key);
    const target=keys.pop();
    let objects=[data];
    const {length}=keys;
    if(length){
        for(let i=0;i<length;i++){
            const key=keys[i];
            objects=objects.flatMap(object=>{
                const value=getKeyValue(key,object);
                return key==="*]"?value:[value];
            });
        }
    }
    const olength=objects.length;
    for(let i=0;i<olength;i++){
        setKeyValue(target,value,objects[i]);
    }
}

const getKeyValue=(key,data)=>{
    let value;
    const bracketi=key.indexOf("]");
    if(bracketi>-1){
        const index=key.substring(0,bracketi).trim();
        switch(index){
            case "*": value=data;break;
            case "last": value=data[data.length-1];break;
            default: value=data[parseInt(index)];break;
        }
    }
    else{
        value=data[key];
    }
    return value;
}

const setKeyValue=(key,value,data)=>{
    const bracketi=key.indexOf("]");
    if(bracketi>-1){
        const index=key.substring(0,bracketi).trim();
        switch(index){
            case "*":
                const {length}=data;
                for(let i=0;i<length;i++){
                    data[i]=value;
                }
                break;
            case "last": data[data.length-1]=value;break;
            case "push": data.push(value);break;
            case "unshift": data.unshift(value);break;
            case "pop": data.pop();break;
            case "shift": data.shift();break;
            default: data[parseInt(index)]=value;break;
        }
    }
    else{
        data[key]=value;
    }
}

module.exports=JsParser;
