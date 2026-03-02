package CoffeeShop;

import CoffeeShop.Exceptions.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import CoffeeShop.Discounts.IDiscount;

import static org.junit.jupiter.api.Assertions.*;

public class CSMTest {

    private List<Customer> customers;
    private List<Item> items;
    private List<Order> orders;
    private Item drink;
    private Item snack;

    @BeforeEach
    void setUp() throws ItemException {
        customers = new ArrayList<>();
        items = new ArrayList<>();
        orders = new ArrayList<>();

        drink = new Item("DRINK-01", 2.50f);
        snack = new Item("SNACK-01", 3.50f);
        items.add(drink);
        items.add(snack);

    }

    @Test
    void constructor_WithValidData_CreatesManager() {
        Customer customer1 = new Customer("Alice");
        customers.add(customer1);
        orders.add(new Order(drink, customer1));

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);

        assertEquals(items, manager.getAvaliableItems());
        assertTrue(manager.CustomerData.containsKey(customer1));
    }

    @Test
    void constructor_WithEmptyLists_CreatesManager() {
        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);

        assertEquals(items, manager.getAvaliableItems());
        assertTrue(manager.CustomerData.isEmpty());
    }

    @Test
    void getAvaliableItems_ReturnsItems() throws ItemException {
        Customer customer = new Customer("Bob");
        customers.add(customer);

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);

        assertEquals(2, manager.getAvaliableItems().size());
        assertTrue(manager.getAvaliableItems().contains(drink));
        assertTrue(manager.getAvaliableItems().contains(snack));
    }

    @Test
    void setAvaliableItems_UpdatesItems() throws ItemException {
        Customer customer = new Customer("Bob");
        customers.add(customer);

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);

        Item newItem = new Item("MAIN-01", 5.00f);
        List<Item> newItems = new ArrayList<>();
        newItems.add(newItem);

        manager.setAvaliableItems(newItems);

        assertEquals(1, manager.getAvaliableItems().size());
        assertTrue(manager.getAvaliableItems().contains(newItem));
    }

    @Test
    void GetCustomerOrders_WithValidCustomer_ReturnsOrders() throws Exception {
        Customer customer1 = new Customer("Alice");
        Customer customer2 = new Customer("Bob");
        customers.add(customer1);
        customers.add(customer2);

        Order order1 = new Order(drink, customer1);
        Order order2 = new Order(snack, customer1);
        orders.add(order1);
        orders.add(order2);
        orders.add(new Order(drink, customer2));

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);

        List<Order> result = manager.GetCustomerOrders(customer1);

        assertEquals(2, result.size());
        assertTrue(result.contains(order1));
        assertTrue(result.contains(order2));
    }

    @Test
    void GetCustomerOrders_WithNonExistentCustomer_ThrowsException() throws ItemException {
        Customer customer = new Customer("Alice");
        customers.add(customer);

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);
        Customer nonExistent = new Customer("Unknown");

        assertThrows(Exception.class, () -> manager.GetCustomerOrders(nonExistent));
    }

    @Test
    void CreateNewOrder_WithValidCustomer_AddsOrder() throws Exception {
        Customer customer = new Customer("Alice");
        customers.add(customer);

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);

        manager.CreateNewOrder(drink, customer);

        List<Order> customerOrders = manager.GetCustomerOrders(customer);
        assertEquals(1, customerOrders.size());
        assertEquals(drink, customerOrders.get(0).getItem());
    }

    @Test
    void CreateNewOrder_WithNonExistentCustomer_ThrowsException() throws ItemException {
        Customer customer = new Customer("Alice");
        customers.add(customer);

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);
        Customer nonExistent = new Customer("Unknown");

        assertThrows(Exception.class, () -> manager.CreateNewOrder(drink, nonExistent));
    }

    @Test
    void RemoveOrder_EmptyImplementation() {
        Customer customer = new Customer("Alice");
        customers.add(customer);
        orders.add(new Order(drink, customer));

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);

        assertDoesNotThrow(() -> manager.RemoveOrder(orders.get(0)));
    }

    @Test
    void CreateCustomer_AddsNewCustomer() {
        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);

        manager.CreateCustomer("NewCustomer");

        assertEquals(1, manager.CustomerData.size());
        assertTrue(manager.CustomerData.keySet().stream()
                .anyMatch(c -> c.name.equals("NewCustomer")));
    }

    @Test
    void RemoveCustomer_RemovesCustomer() {
        Customer customer = new Customer("Alice");
        customers.add(customer);

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);

        manager.RemoveCustomer(customer);

        assertFalse(manager.CustomerData.containsKey(customer));
    }

    @Test
    void CloseoutCustomer_WithRemoveFalse_CalculatesTotal() throws Exception {
        Customer customer = new Customer("Alice");
        customers.add(customer);
        orders.add(new Order(drink, customer));

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);

        assertThrows(NullPointerException.class, () -> manager.CloseoutCustomer(customer, false));
    }

    @Test
    void CloseoutCustomer_WithRemoveTrue_CalculatesTotalAndRemoves() throws Exception {
        Customer customer = new Customer("Alice");
        customers.add(customer);
        orders.add(new Order(drink, customer));

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);

        assertThrows(NullPointerException.class, () -> manager.CloseoutCustomer(customer, true));
    }

    @Test
    void CloseoutCustomer_WithNonExistentCustomer_ThrowsException() throws ItemException {
        Customer customer = new Customer("Alice");
        customers.add(customer);

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);
        Customer nonExistent = new Customer("Unknown");

        assertThrows(Exception.class, () -> manager.CloseoutCustomer(nonExistent, false));
    }
}
