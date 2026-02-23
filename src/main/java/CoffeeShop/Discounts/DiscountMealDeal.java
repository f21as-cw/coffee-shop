package CoffeeShop.Discounts;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import CoffeeShop.Item;
import CoffeeShop.Order;

public class DiscountMealDeal implements IDiscount {
	List<Item> _items;
	float _cost;

	public DiscountMealDeal(List<Item> items, float cost) throws InvalidDiscountException {
		if (items.size() == 0) {
			throw new InvalidDiscountException(String
					.format("Expected size of list to be greater than 0, got %d", items.size()));
		}
		if (cost <= 0) {
			throw new InvalidDiscountException(String
					.format("Expected cost to be greater than 0, got %f", cost));

		}

		_items = items;
		_cost = cost;
	}

	// Approach
	// count = _items
	// .map(i -> orders
	// NOTE: Is equality the best option? Maybe a subclass
	// .filter(o -> o.getItem() == i)
	// .size()
	// )
	// .min()
	// initialCost = _items
	// .map(i -> i.getCost())
	// .sum()
	// discount = (initialCost - _cost) * count
	@Override
	public DiscountsData DiscountEval(List<Order> orders) {
		Map<Item, List<Order>> item2Orders = new HashMap<Item, List<Order>>();
		for (Item item : _items) {
			item2Orders.put(item, new LinkedList<Order>());
		}

		for (Order order : orders) {
			for (Item item : _items) {
				if (!order.getItem().equals(item))
					continue;
				item2Orders.get(item).add(order);
			}
		}

		int count = item2Orders.get(_items.get(0)).size();
		for (int i = 1; i < _items.size(); i++) {
			int amount = item2Orders.get(_items.get(i)).size();
			count = count < amount ? count : amount;
		}

		List<Order> used = new LinkedList<Order>();
		for (List<Order> order : item2Orders.values()) {
			for (int i = 0; i < count; i++) {
				used.add(order.get(i));
			}
		}

		float initalCost = 0.0f;
		for (Item i : _items) {
			initalCost += i.getCost();
		}

		float discount = (initalCost - _cost) * count;

		return new DiscountsData(used, discount);
	}
}
