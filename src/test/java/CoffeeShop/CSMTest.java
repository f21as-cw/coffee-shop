package CoffeeShop;

import CoffeeShop.Discounts.DiscountMealDeal;
import CoffeeShop.Discounts.DiscountPercentage;
import CoffeeShop.Discounts.DiscountX4X;
import CoffeeShop.Exceptions.CustomerNotFoundException;
import CoffeeShop.Exceptions.ItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import CoffeeShop.Discounts.IDiscount;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

public class CSMTest {

    private List<Customer> customers;
    private List<Item> items;
    private List<Order> orders;
    private Item drink;
    private Item snack;

    @TempDir
    Path tempDir;

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

        assertThrows(ItemNotFoundException.class, () -> manager.CreateNewOrder("MAIN-999", customer.id.toString()));
        assertThrows(CustomerNotFoundException.class, () -> manager.CreateNewOrder("SNACK-01", UUID.randomUUID().toString()));

    }

    @Test
    void CreateNewOrder_WithIDs_Incorrect(){
        Customer customer = new Customer("Alice");
        customers.add(customer);

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);

        assertThrows(Exception.class, () -> manager.CreateNewOrder("MAIN-999", "randomid"));
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
    void CloseoutCustomer_Error() throws Exception {
        Customer customer = new Customer("Alice");
        customers.add(customer);
        orders.add(new Order(drink, customer));

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);

        assertThrows(CustomerNotFoundException.class, () -> manager.CloseoutCustomer(new Customer("", UUID.randomUUID()), false));
    }
    @Test
    void CloseoutCustomer() throws Exception {
        Customer customer = new Customer("Alice");
        customers.add(customer);
        orders.add(new Order(drink, customer));

        CoffeeShopManager manager = new CoffeeShopManager(customers, items, orders);

        assertDoesNotThrow(() -> manager.CloseoutCustomer(customer, false));
    }


    @Test
    void LoadTest(){
        CoffeeShopManager.DATA_DIR = "src/test/data";
        CoffeeShopManager csm = new CoffeeShopManager();
        csm.LoadData();

        assertEquals(15, csm.getAvaliableItems().size());
        assertEquals(3, csm.getAvailableDiscounts().size());
        assertEquals(3, csm.getCustomers().size());


        Customer customer = new Customer("", UUID.fromString("bed72c83-3a21-456e-9eac-f2cbd1049359"));
        Bill b = csm.GetCustomerBill(customer);
        assertEquals(4, b.Orders.size());
        assertEquals(17.5850887298584, b.GetCost());

        Bill.BillInfo bi = csm.GetCustomerBillInfo(customer);
        assertEquals(2, bi.DiscountsUsed().size());

        assertEquals(2.8f, bi.FinalCost());

    }

    @Test
    void SaveTest(){
        CoffeeShopManager.DATA_DIR = tempDir.toString();
        CoffeeShopManager csm = new CoffeeShopManager();
        Customer customer1 = csm.CreateCustomer("Bill");
        Customer customer2 = csm.CreateCustomer("Bob");
        Customer customer3 = csm.CreateCustomer("Bary");

        List<Item> items = List.of(
                new Item("DRINK-001", 1.5f),
                new Item("DRINK-002", 5.5f),
                new Item("DRINK-003", 3.5f),
                new Item("SNACK-001", 1.5f),
                new Item("SNACK-002", 76.5f),
                new Item("MAIN-001", 31.5f),
                new Item("MAIN-002", 85.5f),
                new Item("MAIN-003", 28.5f)
        );

        csm.setAvaliableItems(items);

        csm.CreateNewOrder("DRINK-001", customer1.id.toString());
        csm.CreateNewOrder("DRINK-003", customer1.id.toString());
        csm.CreateNewOrder("SNACK-001", customer1.id.toString());

        csm.CreateNewOrder("DRINK-001", customer2.id.toString());
        csm.CreateNewOrder("MAIN-002", customer2.id.toString());

        csm.CreateNewOrder("MAIN-003", customer2.id.toString());
        csm.CreateNewOrder("SNACK-001", customer2.id.toString());
        csm.CreateNewOrder("SNACK-001", customer2.id.toString());

        csm.CreateDiscount(new DiscountPercentage(items.get(1), 0.2f));
        ArrayList<Item> items1 = new ArrayList<>();
        items1.add(items.get(6));
        items1.add(items.get(1));
        csm.CreateDiscount(new DiscountMealDeal(items1, 10f));

        csm.CreateDiscount(new DiscountX4X(items.get(3), 2, 1));

        csm.SaveData();

        Path path = Paths.get(tempDir + "/customers.csv");
        assertTrue(Files.exists(path), "The customer CSV file should exist");

        path = Paths.get(tempDir + "/items.csv");
        assertTrue(Files.exists(path), "The items CSV file should exist");

        path = Paths.get(tempDir + "/orders.csv");
        assertTrue(Files.exists(path), "The orders CSV file should exist");

        path = Paths.get(tempDir + "/discounts.csv");
        assertTrue(Files.exists(path), "The discounts CSV file should exist");



    }

}
