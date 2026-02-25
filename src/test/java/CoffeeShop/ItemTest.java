package CoffeeShop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    @Test
    void constructor_WithValidIdAndCost_CreatesItem() throws ItemException {
        Item item = new Item("DRINK-01", 2.50f);

        assertEquals("DRINK-01", item.getID());
        assertEquals(2.50f, item.getCost());
        assertEquals(Category.DRINK, item.getCategory());
    }

    @Test
    void constructor_WithValidIdCostAndDescription_CreatesItem() throws ItemException {
        Item item = new Item("SNACK-02", 3.75f, "Cookie");

        assertEquals("SNACK-02", item.getID());
        assertEquals(3.75f, item.getCost());
        assertEquals("Cookie", item.getDescription());
        assertEquals(Category.SNACK, item.getCategory());
    }

    @Test
    void constructor_WithZeroCost_ThrowsItemException() {
        assertThrows(ItemException.class, () -> new Item("DRINK-01", 0f));
    }

    @Test
    void constructor_WithNegativeCost_ThrowsItemException() {
        assertThrows(ItemException.class, () -> new Item("DRINK-01", -1.0f));
    }

    @Test
    void constructor_WithInvalidIdFormat_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Item("INVALID", 2.50f));
    }

    @Test
    void getCategory_WithMainCategory_ReturnsMain() throws ItemException {
        Item item = new Item("MAIN-01", 5.00f);
        assertEquals(Category.MAIN, item.getCategory());
    }

    @Test
    void getCategory_WithTestCategory_ReturnsTest() throws ItemException {
        Item item = new Item("TEST-01", 1.00f);
        assertEquals(Category.TEST, item.getCategory());
    }

    @Test
    void setCost_UpdatesCost() throws ItemException {
        Item item = new Item("DRINK-01", 2.50f);
        item.setCost(3.00f);
        assertEquals(3.00f, item.getCost());
    }

    @Test
    void setDescription_UpdatesDescription() throws ItemException {
        Item item = new Item("DRINK-01", 2.50f);
        item.setDescription("Latte");
        assertEquals("Latte", item.getDescription());
    }

    @Test
    void setCategory_UpdatesCategory() throws ItemException {
        Item item = new Item("DRINK-01", 2.50f);
        item.setCategory(Category.SNACK);
        assertEquals(Category.SNACK, item.getCategory());
    }

    @Test
    void equals_SameId_ReturnsTrue() throws ItemException {
        Item item1 = new Item("DRINK-01", 2.50f);
        Item item2 = new Item("DRINK-01", 3.00f);

        assertEquals(item1, item2);
    }

    @Test
    void equals_DifferentId_ReturnsFalse() throws ItemException {
        Item item1 = new Item("DRINK-01", 2.50f);
        Item item2 = new Item("DRINK-02", 2.50f);

        assertNotEquals(item1, item2);
    }

    @Test
    void equals_Null_ReturnsFalse() throws ItemException {
        Item item = new Item("DRINK-01", 2.50f);

        assertNotEquals(item, null);
    }

    @Test
    void equals_DifferentObject_ReturnsFalse() throws ItemException {
        Item item1 = new Item("DRINK-01", 2.50f);
        Item item2 = new Item("SNACK-01", 2.50f);

        assertNotEquals(item1, item2);
    }

    @Test
    void hashCode_SameId_SameHashCode() throws ItemException {
        Item item1 = new Item("DRINK-01", 2.50f);
        Item item2 = new Item("DRINK-01", 3.00f);

        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void toString_ReturnsId() throws ItemException {
        Item item = new Item("DRINK-01", 2.50f);
        assertEquals("DRINK-01", item.toString());
    }
}
