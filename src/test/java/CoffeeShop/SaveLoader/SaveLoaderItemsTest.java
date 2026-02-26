package CoffeeShop.SaveLoader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import CoffeeShop.Item;
import CoffeeShop.ItemException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SaveLoaderItemsTest {

    @TempDir
    Path tempDir;

    @Test
    void LoadData_ValidItemsFile_ReturnsItems() throws IOException {
        Path readPath = tempDir.resolve("items.txt");
        Files.writeString(readPath, "DRINK-01,2.50,Coffee\nSNACK-01,3.75,Tea\n");

        SaveLoaderItems loader = new SaveLoaderItems(readPath.toString(), tempDir.resolve("output.txt").toString());
        List<Item> items = loader.LoadData();

        assertEquals(2, items.size());
        assertEquals("DRINK-01", items.get(0).getID());
        assertEquals(2.50f, items.get(0).getCost());
        assertEquals("Coffee", items.get(0).getDescription());
        assertEquals("SNACK-01", items.get(1).getID());
        assertEquals(3.75f, items.get(1).getCost());
        assertEquals("Tea", items.get(1).getDescription());
    }

    @Test
    void LoadData_InvalidCost_ReturnsNull() throws IOException {
        Path readPath = tempDir.resolve("items.txt");
        Files.writeString(readPath, "DRINK-01,invalid,Coffee\n");

        SaveLoaderItems loader = new SaveLoaderItems(readPath.toString(), tempDir.resolve("output.txt").toString());
        List<Item> items = loader.LoadData();

        assertEquals(0, items.size());
    }

    @Test
    void LoadData_InsufficientFields_ReturnsNull() throws IOException {
        Path readPath = tempDir.resolve("items.txt");
        Files.writeString(readPath, "DRINK-01,2.50\n");

        SaveLoaderItems loader = new SaveLoaderItems(readPath.toString(), tempDir.resolve("output.txt").toString());
        List<Item> items = loader.LoadData();

        assertEquals(0, items.size());
    }

    @Test
    void LoadData_NegativeCost_ReturnsNull() throws IOException {
        Path readPath = tempDir.resolve("items.txt");
        Files.writeString(readPath, "DRINK-01,-1.0,Coffee\n");

        SaveLoaderItems loader = new SaveLoaderItems(readPath.toString(), tempDir.resolve("output.txt").toString());
        List<Item> items = loader.LoadData();

        assertEquals(1, items.size());
    }

    @Test
    void LoadData_EmptyFile_ReturnsEmptyList() throws IOException {
        Path readPath = tempDir.resolve("empty.txt");
        Files.writeString(readPath, "");

        SaveLoaderItems loader = new SaveLoaderItems(readPath.toString(), tempDir.resolve("output.txt").toString());
        List<Item> items = loader.LoadData();

        assertTrue(items.isEmpty());
    }

    @Test
    void SaveData_ValidItems_SavesToFile() throws IOException, ItemException, SaveLoaderException {
        Path writePath = tempDir.resolve("output.txt");
        SaveLoaderItems loader = new SaveLoaderItems(tempDir.resolve("input.txt").toString(), writePath.toString());

        List<Item> items = new ArrayList<>();
        items.add(new Item("DRINK-01", 2.50f, "Coffee"));
        items.add(new Item("SNACK-01", 3.75f, "Tea"));

        loader.SaveData(items);

        List<String> lines = Files.readAllLines(writePath);
        assertEquals(2, lines.size());
        assertEquals("DRINK-01,2.5,Coffee", lines.get(0));
        assertEquals("SNACK-01,3.75,Tea", lines.get(1));
    }

    @Test
    void LoadData_NonExistentFile_ThrowsRuntimeException() {
        SaveLoaderItems loader = new SaveLoaderItems("/nonexistent/path/items.txt", tempDir.resolve("output.txt").toString());

        assertThrows(SaveLoaderRuntimeException.class, () -> loader.LoadData());
    }

    @Test
    void SaveData_InvalidWritePath_ThrowsException() throws IOException, ItemException {
        Path invalidPath = tempDir.resolve("subdir");
        Files.createDirectory(invalidPath);

        SaveLoaderItems loader = new SaveLoaderItems(tempDir.resolve("input.txt").toString(), invalidPath.toString());

        List<Item> items = new ArrayList<>();
        items.add(new Item("DRINK-01", 2.50f, "Coffee"));

        SaveLoaderException exception = assertThrows(SaveLoaderException.class, () -> loader.SaveData(items));
        assertTrue(exception.getMessage().contains("Failed to write file"));
    }
}
