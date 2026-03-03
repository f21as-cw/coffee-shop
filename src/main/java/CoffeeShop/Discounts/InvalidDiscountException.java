package CoffeeShop.Discounts;

public class InvalidDiscountException extends RuntimeException {
	public InvalidDiscountException(String errorMessage) {
		super(errorMessage);
	}
}
