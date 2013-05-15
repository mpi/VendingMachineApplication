package tdd.vendingMachine.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tdd.vendingMachine.domain.CoinDispenser.ChangeCannotBeReturnedException;

public class VendingMachine {

    private final CoinDispenser coinDispenser;
    private final PriceList priceList;
    private final ProductFeeder productFeeder;

    private Map<Integer, Product> shelfs = new HashMap<Integer, Product>();

    private List<Coin> inserted = new ArrayList<Coin>();
    private Product selectedProduct = Product.NO_PRODUCT;

    public VendingMachine(CoinDispenser coinDispenser, ProductFeeder productFeeder, PriceList priceList) {
        this.coinDispenser = coinDispenser;
        this.productFeeder = productFeeder;
        this.priceList = priceList;
    }

    public Product productOnShelf(int shelfNumber) {

        Product productFromShelf = shelfs.get(shelfNumber);

        return productFromShelf == null ? Product.NO_PRODUCT : productFromShelf;
    }

    public void loadOnShelf(int shelfNumber, Product productToLoad) {
        shelfs.put(shelfNumber, productToLoad);
    }

    public String getDisplay() {

        if (selectedProduct == Product.NO_PRODUCT) {
            return "";
        }

        return String.format("%s: %s PLN", selectedProduct.toString(), remainingAmmount());
    }

    private Price remainingAmmount() {

        Price remainingPrice = priceList.priceOf(selectedProduct);
        return remainingPrice.minus(insertedAmmount());
    }

    private Price insertedAmmount() {

        Price instertedAmmount = Price.of("0,00");

        for (Coin coin : inserted) {
            instertedAmmount = instertedAmmount.plus(coin.asPrice());
        }

        return instertedAmmount;
    }

    public void select(int shelfNumber) {

        selectedProduct = productOnShelf(shelfNumber);
    }

    public void insert(Coin money) {

        inserted.add(money);

        long remainingCents = remainingAmmount().asCents();
        if (remainingCents <= 0) {
            
            coinDispenser.accept(inserted.toArray(new Coin[0]));

            try {

                if (remainingCents < 0) {
                    coinDispenser.giveBack(Price.fromCents(-remainingCents));
                }
                productFeeder.release(selectedProduct);
                
            } catch (ChangeCannotBeReturnedException e) {
                coinDispenser.giveBack(insertedAmmount());
            }
            
            selectedProduct = Product.NO_PRODUCT;
        }
    }
}
