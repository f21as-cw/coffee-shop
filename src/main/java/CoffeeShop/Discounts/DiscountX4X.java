package CoffeeShop.Discounts;

import java.util.ArrayList;
import java.util.List;

import CoffeeShop.Items.Item;
import CoffeeShop.Order;

public class DiscountX4X implements IDiscount {
	Item _item;
	int _x;
	int _y;

	public DiscountX4X(Item item, int x, int y) throws InvalidDiscountException {
		if (x <= 0) {
			throw new InvalidDiscountException(
					String.format("Expected x to be greater than 0, instead got %d", x));
		}
		if (y <= 0) {
			throw new InvalidDiscountException(
					String.format("Expected y to be greater than 0, instead got %d", y));
		}
		if (y >= x) {
			throw new InvalidDiscountException(
					String.format("Expected y to be smaller than x, instead got x=%d y=%d", x, y));
		}

		_item = item;
		_x = x;
		_y = y;
	}

	// Approach
	// items = orders
	// .filter(o -> o.getItem() == item))
	// .size();
	// itemsToPay = (items / x) * y + (items % x)
	// discount = items * item.getCost() - itemsToPay * item.getCost()
	@Override
	public DiscountsData DiscountEval(List<Order> orders) {
		List<Order> UsedOrders = new ArrayList<>();

		float DiscountAmount = 0;
		int counter = 0;
		for (Order order : orders) {
			if (!order.getItem().equals(_item))
				continue;
			counter++;
			UsedOrders.add(order);
			if (counter >= _x){
				counter = 0;
				DiscountAmount += _item.getCost() * _y;
			}
		}
		//Removes any orders remaining in the count
		if (counter > 0 && counter <= UsedOrders.size()) {
			UsedOrders.subList(UsedOrders.size() - counter, UsedOrders.size()).clear();
		}


//		int items = 0;
//		for (Order order : orders) {
//			if (!order.getItem().equals(_item))
//				continue;
//			items++;
//		}
//
//		int itemsToPay = (items / _x) * _y + (items % _x);
//		float discount = items * _item.getCost() - itemsToPay * _item.getCost();
		return new DiscountsData(UsedOrders, DiscountAmount);
	}

	@Override
	public String toString() {
		return _item.toString() + " " + _x + " for " + _y;
	}
}
