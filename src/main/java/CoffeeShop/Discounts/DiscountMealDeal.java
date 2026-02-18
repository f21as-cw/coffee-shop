package CoffeeShop.Discounts;

import java.util.ArrayList;
import java.util.LinkedList;

import CoffeeShop.Order;
import CoffeeShop.Items.IItem;

public class DiscountMealDeal implements IDiscount {
	ArrayList<IItem> _items;
	float _cost;

	public DiscountMealDeal(ArrayList<IItem> items, float cost) throws InvalidDiscountException {
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
	public float DiscountEval(LinkedList<Order> orders) {
		ArrayList<Integer> counts = new ArrayList<Integer>(_items.size());
		for (int i = 0; i < _items.size(); i++) {
			counts.add(0);
		}

		for (int i = 0; i < _items.size(); i++) {
			IItem item = _items.get(i);
			for (Order o : orders) {
				if (!o.getItem().getClass().isInstance(item))
					continue;
				counts.set(i, counts.get(i) + 1);
			}
		}

		int count = counts.get(0);
		for (Integer c : counts) {
			count = c < count ? c : count;
		}

		float initalCost = 0.0f;
		for (IItem i : _items) {
			initalCost += i.getCost();
		}

		float discount = (initalCost - _cost) * count;
		return discount;
	}
}
