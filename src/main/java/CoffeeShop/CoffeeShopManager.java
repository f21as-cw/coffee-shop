package CoffeeShop;

import CoffeeShop.Discounts.IDiscount;
import CoffeeShop.SaveLoader.ISaveLoader;
import CoffeeShop.SaveLoader.SaveLoaderCustomers;
import CoffeeShop.SaveLoader.SaveLoaderException;
import CoffeeShop.SaveLoader.SaveLoaderItems;
import CoffeeShop.SaveLoader.SaveLoaderOrders;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class CoffeeShopManager {
	private ISaveLoader<Customer> CustomerSaveLoader;
	private ISaveLoader<Item> ItemSaveLoader;
	private ISaveLoader<Order> OrderSaveLoader;

	public Map<Customer, Bill> CustomerData;
	private List<Item> AvaliableItems;
	private List<IDiscount> AvaliableDiscounts;

	public CoffeeShopManager() {
		Path dataDir = Paths.get("data");

		String customersPath = dataDir.resolve("customers.csv").toString();
		String itemsPath = dataDir.resolve("items.csv").toString();
		String ordersPath = dataDir.resolve("orders.csv").toString();

		this.CustomerSaveLoader = new SaveLoaderCustomers(customersPath, customersPath);
		this.ItemSaveLoader = new SaveLoaderItems(itemsPath, itemsPath);

		List<Customer> customers = this.CustomerSaveLoader.LoadData();
		List<Item> items = this.ItemSaveLoader.LoadData();

		this.OrderSaveLoader = new SaveLoaderOrders(ordersPath, ordersPath, items, customers);

		Map<Customer, Bill> customerData = new HashMap<Customer, Bill>();
		List<Order> orders = this.OrderSaveLoader.LoadData();

		for (Customer customer : customers) {
			List<Order> customerOrders = new ArrayList<Order>();
			for (Order order : orders) {
				if (order.getCustomer().equals(customer)) {
					customerOrders.add(order);
				}
			}
			Bill bill = new Bill(customer, customerOrders);
			customerData.put(customer, bill);
		}

		this.AvaliableItems = items;
		this.CustomerData = customerData;
	}

	public List<Order> GetCustomerOrders(Customer customer) throws Exception {
		if (!CustomerData.containsKey(customer))
			throw new Exception("Customer does not Exist");

		return CustomerData.get(customer).Orders;

	}

	public void CreateNewOrder(Item item, Customer customer) throws Exception {

		if (!CustomerData.containsKey(customer))
			throw new Exception("Customer does not Exist");

		Bill customerBill = CustomerData.get(customer);

		Order newOrder = new Order(item, customer);
		customerBill.addOrder(newOrder);

	}

	//TODO
	public void RemoveOrder(Order order) {

	}

	public void CreateCustomer(String name) {
		Customer newCustomer = new Customer(name);
		Bill newBill = new Bill(newCustomer);
		CustomerData.put(newCustomer, newBill);
	}

	public void RemoveCustomer(Customer customer) {
		CustomerData.remove(customer);
	}

	public void CloseoutCustomer(Customer customer, boolean Remove) throws Exception {
		if (!CustomerData.containsKey(customer))
			throw new Exception("Customer does not exist");

		Bill bill = CustomerData.get(customer);

		bill.GetTotalCost(AvaliableDiscounts);

		if (Remove)
			RemoveCustomer(customer);

	}

	// Should be called by the last function called in this class
	public void SaveData() {
		List<Customer> customers = new ArrayList<Customer>();
		List<Order> orders = new ArrayList<Order>();

		for (Map.Entry<Customer, Bill> entry: this.CustomerData.entrySet()) {
			Customer customer = entry.getKey();
			Bill bill = entry.getValue();

			customers.add(customer);

			for (Order order : bill.Orders) {
				orders.add(order);
			}
		}

		try {
			this.CustomerSaveLoader.SaveData(customers);
		} catch (SaveLoaderException e) {
			System.err.println("Failed to save customers: " + e.getMessage());
		}

		try {
			this.ItemSaveLoader.SaveData(this.AvaliableItems);
		} catch (Exception e) {
			System.err.println("Failed to save items: " + e.getMessage());
		}

		try {
			this.OrderSaveLoader.SaveData(orders);
		} catch (Exception e) {
			System.err.println("Failed to save orders: " + e.getMessage());
		}
	}
}
