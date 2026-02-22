package CoffeeShop.Discounts;

import java.util.ArrayList;
import java.util.List;

import CoffeeShop.Items.Item;
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
	// items = orders
	// .filter(o -> o.getItem() == item))
	// .size();
	// discount = items * item.getCost() * percentage
	@Override
	public DiscountsData DiscountEval(List<Order> orders) {
		List<Order> DiscountedOrders = new ArrayList<>();
		float currentDiscount = 0;
		for (Order order : orders) {
			if (!order.getItem().equals(_item))
				continue;
			DiscountedOrders.add(order);
			currentDiscount += order.getItem().getCost() * _percentage;
		}
		return new DiscountsData(DiscountedOrders, currentDiscount);
	}

	@Override
	public String toString() {
		return _item.toString() + " -" + _percentage*100 + "% OFF";
	}
}
