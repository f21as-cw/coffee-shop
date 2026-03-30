package CoffeeShop.SaveLoader;

import CoffeeShop.Exceptions.SaveLoaderException;
import CoffeeShop.Exceptions.SaveLoaderRuntimeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SaveLoaderExceptionClassesTest {

    @Test
    void SaveLoaderException_TestMessage() {
        String message = "Test error message";
        SaveLoaderException exception = new SaveLoaderException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(Exception.class, exception);
    }

    @Test
    void SaveLoaderRuntimeException_TestMessage() {
        String message = "Test runtime error message";
        SaveLoaderRuntimeException exception = new SaveLoaderRuntimeException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }
}
