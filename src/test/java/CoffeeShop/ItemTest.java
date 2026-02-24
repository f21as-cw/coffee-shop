package CoffeeShop;

import CoffeeShop.Exceptions.InvalidItemFormatException;
import org.junit.jupiter.api.Test;

public class ItemTest {

    @Test
    void createItem(){

        Item item = new Item("DRINK-001", 0.5f);
        assert item._ID == "DRINK-001";
        assert item._category == Category.DRINK;
        assert item._cost == 0.5f;
        assert item.equals(item);
        assert item.equals(new Item("DRINK-001", 0.2f));

        try {
            Item item_error = new Item("ASFSAFAF", 0);
        }
        catch (InvalidItemFormatException e){
            assert true;
        }

    }
}
