package CoffeeShop.Exceptions;

public class InvalidItemFormatException extends RuntimeException {
    public InvalidItemFormatException(String message) {
        super(message);
    }

    public InvalidItemFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
