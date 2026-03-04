package CoffeeShop.Discounts;

import java.util.List;
import java.util.UUID;

import CoffeeShop.Item;
import CoffeeShop.Order;

public interface IDiscount {
	DiscountsData DiscountEval(List<Order> orders);
	IDiscount linkToRealItems(List<Item> availableItems);
	public UUID discountID = UUID.randomUUID();

	String StringToEntity();
	String EntityToString();
}
