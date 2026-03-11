package CoffeeShop.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import CoffeeShop.Order;

public class ProcessedOrdersHashMap {
	private final HashMap<UUID, ArrayList<Order>> hash_map;


	public ProcessedOrdersHashMap() {
		this.hash_map = new HashMap<UUID, ArrayList<Order>>();
	}

	public HashMap<UUID, ArrayList<Order>> getHashMap() {
		return hash_map;
	}

	public synchronized void addOrder(UUID server_id, Order order) {
		if (hash_map.containsKey(server_id)) {
			ArrayList<Order> existingOrders = hash_map.get(server_id);
			existingOrders.add(order);

		} else {
			ArrayList<Order> list = new ArrayList<Order>(Arrays.asList(order));
			hash_map.put(server_id, list);
		}
	}
}
