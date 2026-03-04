package CoffeeShop.SaveLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import CoffeeShop.Exceptions.SaveLoaderException;
import CoffeeShop.Exceptions.SaveLoaderRuntimeException;

public class SaveLoaderExceptionClassesTest {

    @Test
    void SaveLoaderException_TestMessage() {
        String message = "Test error message";
        SaveLoaderException exception = new SaveLoaderException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof Exception);
    }

    @Test
    void SaveLoaderRuntimeException_TestMessage() {
        String message = "Test runtime error message";
        SaveLoaderRuntimeException exception = new SaveLoaderRuntimeException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }
}
