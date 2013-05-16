package tdd.vendingMachine.domain;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
public class ProductStorage {

    private Map<Integer, Product> shelfs = new HashMap<Integer, Product>();

    public Product productOnShelf(int shelfNumber) {
    
        Product productFromShelf = shelfs.get(shelfNumber);
    
        return productFromShelf == null ? Product.NO_PRODUCT : productFromShelf;
    }

    public void loadOnShelf(int shelfNumber, Product productToLoad) {
        shelfs.put(shelfNumber, productToLoad);
    }

    public void clear() {
        shelfs.clear();
    }
}