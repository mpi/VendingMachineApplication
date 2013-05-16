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

    // --
    
    private Product aProduct(String name) {
        return new Product(name);
    }
}

