package CoffeeShop.SaveLoader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import CoffeeShop.Customer;
import CoffeeShop.Item;
import CoffeeShop.ItemException;
import CoffeeShop.Order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SaveLoaderOrdersTest {

    @TempDir
    Path tempDir;

    @Test
    void LoadData_ValidOrdersFile_ReturnsOrders() throws IOException, ItemException {
        UUID customerId = UUID.fromString("12345678-1234-1234-1234-123456789012");

        Path readPath = tempDir.resolve("orders.txt");
        Files.writeString(readPath, customerId + ",DRINK-01\n");

        List<Item> items = new ArrayList<>();
        items.add(new Item("DRINK-01", 2.50f, "Coffee"));

        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("John Doe", customerId));

        SaveLoaderOrders loader = new SaveLoaderOrders(readPath.toString(), tempDir.resolve("output.txt").toString(), items, customers);
        List<Order> orders = loader.LoadData();

        assertEquals(1, orders.size());
        assertEquals("DRINK-01", orders.get(0).getItem().getID());
        assertEquals(customerId, orders.get(0).getCustomer().id);
    }

    @Test
    void LoadData_InsufficientFields_ReturnsNull() throws IOException, ItemException {
        UUID customerId = UUID.fromString("12345678-1234-1234-1234-123456789012");

        Path readPath = tempDir.resolve("orders.txt");
        Files.writeString(readPath, customerId + "\n");

        List<Item> items = new ArrayList<>();
        items.add(new Item("DRINK-01", 2.50f, "Coffee"));

        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("John Doe", customerId));

        SaveLoaderOrders loader = new SaveLoaderOrders(readPath.toString(), tempDir.resolve("output.txt").toString(), items, customers);
        List<Order> orders = loader.LoadData();

        assertEquals(0, orders.size());
    }

    @Test
    void LoadData_InvalidCustomerID_ReturnsNull() throws IOException, ItemException {
        UUID customerId = UUID.fromString("12345678-1234-1234-1234-123456789012");

        Path readPath = tempDir.resolve("orders.txt");
        Files.writeString(readPath, "invalid-customer-id,DRINK-01\n");

        List<Item> items = new ArrayList<>();
        items.add(new Item("DRINK-01", 2.50f, "Coffee"));

        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("John Doe", customerId));

        SaveLoaderOrders loader = new SaveLoaderOrders(readPath.toString(), tempDir.resolve("output.txt").toString(), items, customers);
        List<Order> orders = loader.LoadData();

        assertEquals(0, orders.size());
    }

    @Test
    void LoadData_InvalidItemID_ReturnsNull() throws IOException, ItemException {
        UUID customerId = UUID.fromString("12345678-1234-1234-1234-123456789012");

        Path readPath = tempDir.resolve("orders.txt");
        Files.writeString(readPath, customerId + ",INVALID-ITEM\n");

        List<Item> items = new ArrayList<>();
        items.add(new Item("DRINK-01", 2.50f, "Coffee"));

        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("John Doe", customerId));

        SaveLoaderOrders loader = new SaveLoaderOrders(readPath.toString(), tempDir.resolve("output.txt").toString(), items, customers);
        List<Order> orders = loader.LoadData();

        assertEquals(0, orders.size());
    }

    @Test
    void LoadData_EmptyFile_ReturnsEmptyList() throws IOException, ItemException {
        Path readPath = tempDir.resolve("empty.txt");
        Files.writeString(readPath, "");

        List<Item> items = new ArrayList<>();
        List<Customer> customers = new ArrayList<>();

        SaveLoaderOrders loader = new SaveLoaderOrders(readPath.toString(), tempDir.resolve("output.txt").toString(), items, customers);
        List<Order> orders = loader.LoadData();

        assertTrue(orders.isEmpty());
    }

    @Test
    void SaveData_ValidOrders_SavesToFile() throws IOException, ItemException, SaveLoaderException {
        UUID customerId = UUID.fromString("12345678-1234-1234-1234-123456789012");

        Path writePath = tempDir.resolve("output.txt");

        List<Item> items = new ArrayList<>();
        items.add(new Item("DRINK-01", 2.50f, "Coffee"));

        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("John Doe", customerId));

        SaveLoaderOrders loader = new SaveLoaderOrders(tempDir.resolve("input.txt").toString(), writePath.toString(), items, customers);

        List<Order> orders = new ArrayList<>();
        orders.add(new Order(items.get(0), customers.get(0)));

        loader.SaveData(orders);

        List<String> lines = Files.readAllLines(writePath);
        assertEquals(1, lines.size());
        assertEquals(customerId + ",DRINK-01", lines.get(0));
    }

    @Test
    void LoadData_MultipleOrders_ReturnsAllOrders() throws IOException, ItemException {
        UUID customerId1 = UUID.fromString("12345678-1234-1234-1234-123456789012");
        UUID customerId2 = UUID.fromString("87654321-4321-4321-4321-210987654321");

        Path readPath = tempDir.resolve("orders.txt");
        Files.writeString(readPath, customerId1 + ",DRINK-01\n" + customerId2 + ",SNACK-01\n");

        List<Item> items = new ArrayList<>();
        items.add(new Item("DRINK-01", 2.50f, "Coffee"));
        items.add(new Item("SNACK-01", 3.50f, "Tea"));

        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("John Doe", customerId1));
        customers.add(new Customer("Jane Smith", customerId2));

        SaveLoaderOrders loader = new SaveLoaderOrders(readPath.toString(), tempDir.resolve("output.txt").toString(), items, customers);
        List<Order> orders = loader.LoadData();

        assertEquals(2, orders.size());
    }

    @Test
    void LoadData_NonExistentFile_ThrowsRuntimeException() {
        List<Item> items = new ArrayList<>();
        List<Customer> customers = new ArrayList<>();

        SaveLoaderOrders loader = new SaveLoaderOrders("/nonexistent/path/orders.txt", tempDir.resolve("output.txt").toString(), items, customers);

        assertThrows(SaveLoaderRuntimeException.class, () -> loader.LoadData());
    }
}
