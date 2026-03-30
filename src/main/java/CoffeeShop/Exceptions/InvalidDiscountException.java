package CoffeeShop.Exceptions;

public class InvalidDiscountException extends RuntimeException {
    public InvalidDiscountException(String errorMessage) {
        super(errorMessage);
    }
}
