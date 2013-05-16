package tdd.vendingMachine.application;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import tdd.vendingMachine.domain.Coin;
import tdd.vendingMachine.domain.ProductStorage;
import tdd.vendingMachine.domain.VendingMachine;

@Controller
public class VendingMachineController {

    private final VendingMachine vendingMachine;
    private final ProductStorage productStorage;
    
    @Autowired
    public VendingMachineController(VendingMachine vendingMachine, ProductStorage productStorage) {
        this.vendingMachine = vendingMachine;
        this.productStorage = productStorage;
    }

    @RequestMapping(   value = "/vending-machine/push",
                      method = RequestMethod.POST,
                    consumes = "application/json",
                    produces = "application/json")
    public @ResponseBody Response selectProduct(@RequestBody Integer shelfNumber) {
        
        try{
            vendingMachine.select(shelfNumber);
            return Response.success();

        } catch (Exception e) {
            return Response.failure(e.getMessage());
        }
    }

    @RequestMapping(   value = "/vending-machine/insert",
                      method = RequestMethod.POST,
                    consumes = "application/json",
                    produces = "application/json")
    public @ResponseBody Response insertCoin(@RequestBody String coin) {

        try{
            vendingMachine.insert(Coin.valueOf(coin));
            return Response.success();

        } catch (IllegalArgumentException e) {
            return Response.failure(String.format("Invalid argument: '%s'", coin));
        } catch (Exception e) {
            return Response.failure(e.getMessage());
        }
    }

    @RequestMapping(   value = "/vending-machine/state",
                      method = RequestMethod.GET,
                    produces = "application/json")
    public @ResponseBody VendingMachineMemento state(){
        return new VendingMachineMemento(vendingMachine.getDisplay(), productStorage);
    }
    
    @JsonAutoDetect(fieldVisibility=Visibility.ANY)
    public class VendingMachineMemento {
        
        String display;
        ProductStorage storage;
        
        public VendingMachineMemento(String display, ProductStorage storage) {
            this.storage = storage;
            this.display = display;
        }
        
    }
}

