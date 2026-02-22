package CoffeeShop.Discounts;

import java.util.LinkedList;

import CoffeeShop.Order;
import CoffeeShop.Items.IItem;

public class DiscountPercentage implements IDiscount {
	IItem _item;
	float _percentage;

	public DiscountPercentage(IItem item, float percentage) throws InvalidDiscountException {
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
	public float DiscountEval(LinkedList<Order> orders) {
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
