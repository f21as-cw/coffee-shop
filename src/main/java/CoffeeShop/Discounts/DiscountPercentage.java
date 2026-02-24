package CoffeeShop.Discounts;

import java.util.LinkedList;
import java.util.List;

import CoffeeShop.Order;
import CoffeeShop.Item;

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
	// items = orders
	// .filter(o -> o.getItem() == item))
	// .size();
	// discount = items * item.getCost() * percentage
	@Override
	public DiscountsData DiscountEval(List<Order> orders) {
		List<Order> used = new LinkedList<Order>();
		for (Order order : orders) {
			if (!order.getItem().equals(_item))
				continue;
			used.add(order);
		}

		int items = used.size();
		float discount = items * _item.getCost() * _percentage;

		return new DiscountsData(used, discount);
	}
}
