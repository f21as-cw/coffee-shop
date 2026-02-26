package CoffeeShop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class App {

	public static void main(String[] args) {
		CoffeeShopManager coffeeShopManager = new CoffeeShopManager();
		coffeeShopManager.LoadData();
		List<String> NAMES = List.of(
				"Alex", "Jordan", "Taylor", "Morgan", "Casey",
				"Riley", "Quinn", "Skyler", "Charlie", "Emerson"
		);
		for (String name : NAMES) {
			coffeeShopManager.CreateCustomer(name);
		}

		List<Item> items = new ArrayList<>();
		String[] categories = {"DRINK", "MAIN", "SNACK"};

		// Let's generate 5 items per category for a total of 15 unique items
		for (String category : categories) {
			for (int i = 1; i <= 5; i++) {
				// String.format("%03d", i) turns 1 into "001"
				String id = String.format("%s-%03d", category, i);

				// Random price between 1.0 and 15.0
				float price = ThreadLocalRandom.current().nextFloat(0.2f, 15.0f);

				String description = "Delicious " + category.toLowerCase() + " option #" + i;

				items.add(new Item(id, price, description));
			}
		}

		// Shuffle so they aren't grouped by category when you use them
		Collections.shuffle(items);
		coffeeShopManager.setAvaliableItems(items);

		for (Customer customer : coffeeShopManager.getCustomers()) {
			int length = ThreadLocalRandom.current().nextInt(100);
			for (int i = 0; i < length; i++) {
				Item randomItem = items.get(ThreadLocalRandom.current().nextInt(items.size()));
				coffeeShopManager.CreateNewOrder(randomItem, customer);
			}
		}

		coffeeShopManager.SaveData();

	}
}
