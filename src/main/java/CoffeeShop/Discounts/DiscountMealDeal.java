package CoffeeShop.Discounts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import CoffeeShop.Items.Item;
import CoffeeShop.Order;

public class DiscountMealDeal implements IDiscount {
	ArrayList<Item> _items;
	float _cost;

	public DiscountMealDeal(ArrayList<Item> items, float cost) throws InvalidDiscountException {
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
		// 1. Map the orders by item for fast lookup
		Map<Item, List<Order>> ordersByItem = orders.stream().collect(Collectors.groupingBy(Order::getItem));

		int bundleCount = _items.stream()
				.mapToInt(item -> ordersByItem.getOrDefault(item, List.of()).size())
				.min()
				.orElse(0);

		List<Order> usedOrders = new ArrayList<>();
		float priceChange = 0.0f;

		if (bundleCount > 0) {
			for (Item bundleItem : _items) {
				List<Order> matchingOrders = ordersByItem.get(bundleItem);
				usedOrders.addAll(matchingOrders.subList(0, bundleCount));
			}

			float initialCost = (float) _items.stream().mapToDouble(Item::getCost).sum();
			priceChange = (initialCost - _cost) * bundleCount;
		}

		return new DiscountsData(usedOrders, priceChange);


//		ArrayList<Integer> counts = new ArrayList<Integer>(_items.size());
//		for (int i = 0; i < _items.size(); i++) {
//			counts.add(0);
//		}
//
//		for (int i = 0; i < _items.size(); i++) {
//			Item item = _items.get(i);
//			for (Order o : orders) {
//				if (!o.getItem().equals(item))
//					continue;
//				counts.set(i, counts.get(i) + 1);
//			}
//		}
//
//		int count = counts.get(0);
//		for (Integer c : counts) {
//			count = c < count ? c : count;
//		}
//
//		float initalCost = 0.0f;
//		for (Item i : _items) {
//			initalCost += i.getCost();
//		}
//
//		float discount = (initalCost - _cost) * count;
//		return new DiscountsData(new ArrayList<>(), discount);
	}

	@Override
	public String toString() {
		String str = "";
		for (Item item : _items) {
			str += item.toString() + " + ";
		}
		str += " for " + _cost;
		return str;
	}
}
