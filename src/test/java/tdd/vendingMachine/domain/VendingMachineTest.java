package tdd.vendingMachine.domain;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import tdd.vendingMachine.domain.CoinDispenser.ChangeCannotBeReturnedException;

@RunWith(MockitoJUnitRunner.class)
public class VendingMachineTest {

    @Mock
    private PriceList priceList;
    @Mock
    private CoinDispenser coinDispenser;
    @Mock
    private ProductFeeder productFeeder;

    private VendingMachine vendingMachine;

    @Before
    public void setUp() {

        priceOf(Mockito.any(Product.class)).is(Price.of("1,00"));
        vendingMachine = new VendingMachine(coinDispenser, productFeeder, priceList);
    }
    
    @Test
    public void shouldBeEmptyJustAfterCreation() throws Exception {

        // given:
        // when:
        Product product = vendingMachine.productOnShelf(1);

        // then:
        assertThat(product).isEqualTo(Product.NO_PRODUCT);
    }
    
    @Test
    public void shouldNotBeEmptyAfterLoadingProductOnShelf() throws Exception {

        // given:
        vendingMachine.loadOnShelf(1, aProduct("Chocolate Bar"));
        
        // when:
        Product product = vendingMachine.productOnShelf(1);

        // then:
        assertThat(product).isEqualTo(aProduct("Chocolate Bar"));
    }

    @Test
    public void shouldBeEmptyAfterLoadingProductOnOtherShelf() throws Exception {

        // given:
        vendingMachine.loadOnShelf(1, aProduct("Chocolate Bar"));
        
        // when:
        Product product = vendingMachine.productOnShelf(2);

        // then:
        assertThat(product).isEqualTo(Product.NO_PRODUCT);
    }

    @Test
    public void shouldDisplayProductNameAfterSelectingShelf() throws Exception {

        // given:
        vendingMachine.loadOnShelf(1, aProduct("Lollipop"));
        
        // when:
        vendingMachine.select(1);
        
        // then:
        assertThat(vendingMachine.getDisplay()).contains("Lollipop");
    }

    @Test
    public void shouldDisplayProductPriceAfterSelectingShelf() throws Exception {
        
        // given:
        vendingMachine.loadOnShelf(1, aProduct("Lollipop"));
        priceOf(aProduct("Lollipop")).is(Price.of("12,99"));
        
        // when:
        vendingMachine.select(1);
        
        // then:
        assertThat(vendingMachine.getDisplay()).contains("12,99 PLN");
    }

    @Test
    public void shouldDisplayNothingIfSelectedEmptyShelf() throws Exception {
        
        // given:
        // when:
        vendingMachine.select(1);
        
        // then:
        assertThat(vendingMachine.getDisplay()).isEmpty();
    }

    @Test
    public void shouldDecrementRemainingPriceAfterInsertingCoin() throws Exception {

        // given:
        selectProductThatCost("3,50");
        
        // when:
        vendingMachine.insert(Coin.FIFTY_CENTS);
        
        // then:
        assertThat(vendingMachine.getDisplay()).contains("3,00 PLN");
    }

    @Test
    public void shouldClearDisplayIfWholeAmmountHasBeenPaid() throws Exception {
        
        // given:
        selectProductThatCost("1,50");
        
        // when:
        vendingMachine.insert(Coin.FIFTY_CENTS);
        vendingMachine.insert(Coin.ONE_DOLLAR);
        
        // then:
        assertThat(vendingMachine.getDisplay()).isEmpty();
    }
    
    @Test
    public void shouldAcceptCoinsIfWholeAmmountHasBeenPaid() throws Exception {
        
        // given:
        selectProductThatCost("1,50");
        
        // when:
        vendingMachine.insert(Coin.FIFTY_CENTS);
        vendingMachine.insert(Coin.ONE_DOLLAR);
        
        // then:
        verify(coinDispenser).accept(Coin.FIFTY_CENTS, Coin.ONE_DOLLAR);
    }
    
    @Test
    public void shouldReturnChangeIfAmmountPaidHasBeenLargerThanPrice() throws Exception {
        
        // given:
        selectProductThatCost("1,50");
        
        // when:
        vendingMachine.insert(Coin.ONE_DOLLAR);
        vendingMachine.insert(Coin.ONE_DOLLAR);
        
        // then:
        verify(coinDispenser).accept(Coin.ONE_DOLLAR, Coin.ONE_DOLLAR);
        verify(coinDispenser).giveBack(Price.of("0,50"));
    }
    
    @Test
    public void shouldReturnWholeAmmountIfChangeCannotBeReturned() throws Exception {
        
        // given:
        selectProductThatCost("1,50");
        changeCannotBeReturned("0,50");
        
        // when:
        vendingMachine.insert(Coin.ONE_DOLLAR);
        vendingMachine.insert(Coin.ONE_DOLLAR);
        
        // then:
        verify(coinDispenser).giveBack(Price.of("2,00"));
    }
    
    @Test
    public void shouldGiveProductAfterPurchase() throws Exception {
        
        // given:
        Product product = aProduct("Apple Juice");
        
        // when:
        purchaseProduct(product);
        
        // then:
        verify(productFeeder).release(product);
    }

    // --
    
    private void changeCannotBeReturned(String change) {
        doThrow(new ChangeCannotBeReturnedException(null, null)).when(coinDispenser).giveBack(Price.of(change));
    }

    private PriceListBuilder priceOf(Product product) {
        return new PriceListBuilder(product);
    }

    private Product aProduct(String name) {
        return new Product(name);
    }
    
    public class PriceListBuilder {

        private final Product product;
        
        public PriceListBuilder(Product product) {
            this.product = product;
        }

        public void is(Price price) {
            when(priceList.priceOf(product)).thenReturn(price);
        }
    }

    private void selectProductThatCost(String price) {
        
        Product product = aProduct("Product that costs $" + price);
        priceOf(product).is(Price.of(price));

        vendingMachine.loadOnShelf(1, product);
        vendingMachine.select(1);
    }

    private void purchaseProduct(Product product) {
        priceOf(product).is(Price.of("1,00"));
        vendingMachine.loadOnShelf(1, product);
        vendingMachine.select(1);
        vendingMachine.insert(Coin.ONE_DOLLAR);
    }
}


