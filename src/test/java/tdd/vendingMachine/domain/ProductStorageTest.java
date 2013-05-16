package tdd.vendingMachine.domain;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ProductStorageTest {

    private ProductStorage storage;
    
    @Before
    public void setUp() {

        storage = new ProductStorage();
    }

    @Test
    public void shouldBeEmptyJustAfterCreation() throws Exception {

        // given:
        // when:
        Product product = storage.productOnShelf(1);

        // then:
        assertThat(product).isEqualTo(Product.NO_PRODUCT);
    }
    
    @Test
    public void shouldNotBeEmptyAfterLoadingProductOnShelf() throws Exception {

        // given:
        storage.loadOnShelf(1, aProduct("Chocolate Bar"));
        
        // when:
        Product product = storage.productOnShelf(1);

        // then:
        assertThat(product).isEqualTo(aProduct("Chocolate Bar"));
    }

    @Test
    public void shouldBeEmptyAfterLoadingProductOnOtherShelf() throws Exception {

        // given:
        storage.loadOnShelf(1, aProduct("Chocolate Bar"));
        
        // when:
        Product product = storage.productOnShelf(2);

        // then:
        assertThat(product).isEqualTo(Product.NO_PRODUCT);
    }

    @Test
    public void shouldTakeProductFromShelf() throws Exception {

        // given:
        storage.loadOnShelf(1, aProduct("Chocolate Bar"));
        
        // when:
        Product product = storage.takeFromShelf(1);
        
        // then:
        assertThat(product).isEqualTo(aProduct("Chocolate Bar"));
    }
    
    @Test
    public void shouldRemoveProductFromShelfAfterTake() throws Exception {
        
        // given:
        storage.loadOnShelf(1, aProduct("Chocolate Bar"));
        
        // when:
        storage.takeFromShelf(1);
        
        // then:
        assertThat(storage.productOnShelf(1)).isEqualTo(Product.NO_PRODUCT);
    }
    
    @Test
    public void shouldLoadMultipleProductsOnShelf() throws Exception {
        
        // given:
        storage.loadOnShelf(1, aProduct("Chocolate Bar"));
        storage.loadOnShelf(1, aProduct("Chocolate Bar"));
        
        // when:
        storage.takeFromShelf(1);
        
        // then:
        assertThat(storage.productOnShelf(1)).isEqualTo(aProduct("Chocolate Bar"));
    }
    
    // --
    
    private Product aProduct(String name) {
        return new Product(name);
    }
}

