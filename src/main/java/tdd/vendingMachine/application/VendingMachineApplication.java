package tdd.vendingMachine.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tdd.vendingMachine.domain.CoinDispenser;
import tdd.vendingMachine.domain.Price;
import tdd.vendingMachine.domain.PriceList;
import tdd.vendingMachine.domain.Product;
import tdd.vendingMachine.domain.ProductFeeder;
import tdd.vendingMachine.domain.ProductStorage;
import tdd.vendingMachine.domain.VendingMachine;

@Configuration
public class VendingMachineApplication {

    @Bean
    public ProductStorage productStorage(){
        return new ProductStorage();
    }

    @Bean
    @Autowired
    public VendingMachine vendingMachine(ProductStorage productStorage){
        return new VendingMachine(productStorage, new CoinDispenser(), new ProductFeeder(), new StaticPriceList());
    }
    
    public class StaticPriceList extends PriceList {
        
        @Override
        public Price priceOf(Product product) {
            return Price.of("2,50");
        }
        
    }
}

