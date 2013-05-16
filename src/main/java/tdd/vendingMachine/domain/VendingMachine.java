package tdd.vendingMachine.domain;

import java.util.ArrayList;
import java.util.List;

import tdd.vendingMachine.domain.CoinDispenser.ChangeCannotBeReturnedException;

public class VendingMachine {

    private final ProductStorage storage;
    private final CoinDispenser coinDispenser;
    private final ProductFeeder productFeeder;
    private final PriceList priceList;

    private List<Coin> inserted = new ArrayList<Coin>();
    private Product selectedProduct = Product.NO_PRODUCT;

    public VendingMachine(ProductStorage storage, CoinDispenser coinDispenser, ProductFeeder productFeeder, PriceList priceList) {
        this.storage = storage;
        this.coinDispenser = coinDispenser;
        this.productFeeder = productFeeder;
        this.priceList = priceList;
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

        selectedProduct = storage.productOnShelf(shelfNumber);
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
            
            inserted.clear();
            selectedProduct = Product.NO_PRODUCT;
        }
    }

    public void cancel() {
        coinDispenser.accept(inserted.toArray(new Coin[0]));
        coinDispenser.giveBack(insertedAmmount());
        inserted.clear();
        selectedProduct = Product.NO_PRODUCT;
    }
}
