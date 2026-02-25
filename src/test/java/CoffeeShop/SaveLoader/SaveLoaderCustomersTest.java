package CoffeeShop.SaveLoader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import CoffeeShop.Customer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SaveLoaderCustomersTest {

    @TempDir
    Path tempDir;

    @Test
    void LoadData_ValidCustomersFile_ReturnsCustomers() throws IOException {
        UUID uuid1 = UUID.fromString("12345678-1234-1234-1234-123456789012");
        UUID uuid2 = UUID.fromString("87654321-4321-4321-4321-210987654321");
        
        Path readPath = tempDir.resolve("customers.txt");
        Files.writeString(readPath, uuid1 + ",John Doe\n" + uuid2 + ",Jane Smith\n");

        SaveLoaderCustomers loader = new SaveLoaderCustomers(readPath.toString(), tempDir.resolve("output.txt").toString());
        List<Customer> customers = loader.LoadData();

        assertEquals(2, customers.size());
        assertEquals(uuid1, customers.get(0).id);
        assertEquals("John Doe", customers.get(0).name);
        assertEquals(uuid2, customers.get(1).id);
        assertEquals("Jane Smith", customers.get(1).name);
    }

    @Test
    void LoadData_InsufficientFields_ReturnsNull() throws IOException {
        Path readPath = tempDir.resolve("customers.txt");
        Files.writeString(readPath, "12345678-1234-1234-1234-123456789012\n");

        SaveLoaderCustomers loader = new SaveLoaderCustomers(readPath.toString(), tempDir.resolve("output.txt").toString());
        List<Customer> customers = loader.LoadData();

        assertEquals(0, customers.size());
    }

    @Test
    void LoadData_InvalidUUID_ThrowsException() throws IOException {
        Path readPath = tempDir.resolve("customers.txt");
        Files.writeString(readPath, "invalid-uuid,John Doe\n");

        SaveLoaderCustomers loader = new SaveLoaderCustomers(readPath.toString(), tempDir.resolve("output.txt").toString());
        
        assertThrows(IllegalArgumentException.class, () -> loader.LoadData());
    }

    @Test
    void LoadData_EmptyFile_ReturnsEmptyList() throws IOException {
        Path readPath = tempDir.resolve("empty.txt");
        Files.writeString(readPath, "");

        SaveLoaderCustomers loader = new SaveLoaderCustomers(readPath.toString(), tempDir.resolve("output.txt").toString());
        List<Customer> customers = loader.LoadData();

        assertTrue(customers.isEmpty());
    }

    @Test
    void SaveData_ValidCustomers_SavesToFile() throws IOException, SaveLoaderException {
        Path writePath = tempDir.resolve("output.txt");
        SaveLoaderCustomers loader = new SaveLoaderCustomers(tempDir.resolve("input.txt").toString(), writePath.toString());

        UUID uuid1 = UUID.fromString("12345678-1234-1234-1234-123456789012");
        UUID uuid2 = UUID.fromString("87654321-4321-4321-4321-210987654321");
        
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("John Doe", uuid1));
        customers.add(new Customer("Jane Smith", uuid2));

        loader.SaveData(customers);

        List<String> lines = Files.readAllLines(writePath);
        assertEquals(2, lines.size());
        assertEquals("12345678-1234-1234-1234-123456789012,John Doe", lines.get(0));
        assertEquals("87654321-4321-4321-4321-210987654321,Jane Smith", lines.get(1));
    }

    @Test
    void LoadData_NonExistentFile_ThrowsRuntimeException() {
        SaveLoaderCustomers loader = new SaveLoaderCustomers("/nonexistent/path/customers.txt", tempDir.resolve("output.txt").toString());
        
        assertThrows(SaveLoaderRuntimeException.class, () -> loader.LoadData());
    }
}
