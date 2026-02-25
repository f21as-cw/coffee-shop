package CoffeeShop;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import CoffeeShop.SaveLoader.ISaveLoader;
import CoffeeShop.SaveLoader.SaveLoaderOrders;
import CoffeeShop.SaveLoader.SaveLoaderItems;
import CoffeeShop.SaveLoader.SaveLoaderCustomers;
import CoffeeShop.SaveLoader.SaveLoaderException;

public class App {

	public static void main(String[] args) {
		Path dataDir = Paths.get("data");

		String customersPath = dataDir.resolve("customers.csv").toString();
		String itemsPath = dataDir.resolve("items.csv").toString();
		String ordersPath = dataDir.resolve("orders.csv").toString();

		ISaveLoader<Customer> customerSL = new SaveLoaderCustomers(customersPath, customersPath);
		ISaveLoader<Item> itemsSL = new SaveLoaderItems(itemsPath, itemsPath);

		List<Customer> customers = customerSL.LoadData();
		List<Item> items = itemsSL.LoadData();

		ISaveLoader<Order> ordersSL = new SaveLoaderOrders(ordersPath, ordersPath, items, customers);

		List<Order> orders = ordersSL.LoadData();

		CoffeeShopManager coffeeShopManager = new CoffeeShopManager(customers, items, orders);
		coffeeShopManager.CreateCustomer("GonzaloPro");


		for (Map.Entry<Customer, Bill> entry: coffeeShopManager.CustomerData.entrySet()) {
			Customer customer = entry.getKey();
			Bill bill = entry.getValue();

			customers.add(customer);

			for (Order order : bill.Orders) {
				orders.add(order);
			}
		}

		try {
			customerSL.SaveData(customers);
		} catch (SaveLoaderException e) {
			System.err.println("Failed to save customers: " + e.getMessage());
		}

		try {
			itemsSL.SaveData(coffeeShopManager.getAvaliableItems());
		} catch (Exception e) {
			System.err.println("Failed to save items: " + e.getMessage());
		}

		try {
			ordersSL.SaveData(orders);
		} catch (Exception e) {
			System.err.println("Failed to save orders: " + e.getMessage());
		}
	}
}
