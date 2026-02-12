package CoffeeShop.Discounts;

import java.util.LinkedList;

import CoffeeShop.Item;
import CoffeeShop.Order;

public class DiscountPercentage implements IDiscount {
	Item _item;
	float _percentage;

	public DiscountPercentage(Item item, float percentage) throws InvalidDiscountException {
		if (percentage <= 0) {
			throw new InvalidDiscountException(
					String.format("Expected percentage to be greater than 0, instead got %f",
							percentage));
		}
		if (percentage >= 1) {
			throw new InvalidDiscountException(
					String.format("Expected percentage to be smaller than 1, instead got %f",
							percentage));
		}

		_item = item;
		_percentage = percentage;
	}

	// Approach
	// int items = orders
	// .filter(o -> o.getItem() == item))
	// .size();
	// discount = items * item.getCost() * percentage
	@Override
	public float DiscountEval(LinkedList<Order> orders, float currentPrice) {
		int items = 0;
		for (Order order : orders) {
			if (!order.getItem().equals(_item))
				continue;
			items++;
		}

		float discount = items * _item.getCost() * _percentage;
		return discount;
	}
}
