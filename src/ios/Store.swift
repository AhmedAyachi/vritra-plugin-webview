

class Store {
    private var store:[String:Any]=[:];

    init(){

    } 

    func initiate(state:[String:Any]?){
        if(!(state==nil)){
            self.store=state!;
        }
    }

    func toObject()->[String:Any]{
        return store;
    }
}

