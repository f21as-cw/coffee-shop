package CoffeeShop;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import CoffeeShop.Discounts.IDiscount;
import CoffeeShop.Exceptions.CustomerNotFoundException;
import CoffeeShop.Exceptions.ItemNotFoundException;
import CoffeeShop.Exceptions.SaveLoaderException;
import CoffeeShop.SaveLoader.ISaveLoader;
import CoffeeShop.SaveLoader.SaveLoaderCustomers;
import CoffeeShop.SaveLoader.SaveLoaderDiscounts;
import CoffeeShop.SaveLoader.SaveLoaderItems;
import CoffeeShop.SaveLoader.SaveLoaderOrders;

public class CoffeeShopManager {
	public static final String CUSTOMERS_CSV = "customers.csv";
	public static final String ITEMS_CSV = "items.csv";
	public static final String ORDERS_CSV = "orders.csv";
	public static final String DISCOUNTS_CSV = "discounts.csv";
	public static String DATA_DIR = "data";

	public Map<Customer, Bill> CustomerData = new HashMap<>();
	public List<Customer> getCustomers() { return new ArrayList<>(CustomerData.keySet()); }

	private List<Item> AvaliableItems;
	public List<Item> getAvaliableItems() { return AvaliableItems; }
	public void setAvaliableItems(List<Item> avaliableItems) { AvaliableItems = avaliableItems; }

	private List<IDiscount> AvailableDiscounts = new ArrayList<>();
	public List<IDiscount> getAvailableDiscounts() { return AvailableDiscounts; }
	public void setAvailableDiscounts(List<IDiscount> avaliableDiscounts) { AvailableDiscounts = avaliableDiscounts; }

	private ISaveLoader<Order> saveLoaderOrders;
	private ISaveLoader<Item> saveLoaderItems;
	private ISaveLoader<Customer> saveLoaderCustomers;
	private ISaveLoader<IDiscount> saveLoaderDiscounts;

	public CoffeeShopManager() {
		Path dataDir = Paths.get(DATA_DIR);
		System.out.println(dataDir.toAbsolutePath());
		Properties config = ResourceLoader.loadConfig();

		String customersPath = dataDir.resolve(CUSTOMERS_CSV).toString();
		String itemsPath = dataDir.resolve(ITEMS_CSV).toString();
		String ordersPath = dataDir.resolve(ORDERS_CSV).toString();
		String discountPath = dataDir.resolve(DISCOUNTS_CSV).toString();

		saveLoaderCustomers = new SaveLoaderCustomers(customersPath, customersPath);
		saveLoaderItems = new SaveLoaderItems(itemsPath, itemsPath);
		saveLoaderDiscounts = new SaveLoaderDiscounts(discountPath, discountPath);
		saveLoaderOrders = new SaveLoaderOrders(ordersPath, ordersPath);
	}

	public CoffeeShopManager(List<Customer> customers, List<Item> items, List<Order> orders){
		AvaliableItems = items;

		for (Customer customer : customers) {
			Bill newBill = new Bill(customer);
			CustomerData.put(customer, newBill);
		}

		for (Order order : orders) {
			if (!CustomerData.containsKey(order._customer))
				throw new CustomerNotFoundException("Customer not real");

			CustomerData.get(order._customer).addOrder(order);;
		}

		Path dataDir = Paths.get(DATA_DIR);
		System.out.println(dataDir.toAbsolutePath());
		Properties config = ResourceLoader.loadConfig();

		String customersPath = dataDir.resolve(CUSTOMERS_CSV).toString();
		String itemsPath = dataDir.resolve(ITEMS_CSV).toString();
		String ordersPath = dataDir.resolve(ORDERS_CSV).toString();
		String discountPath = dataDir.resolve(DISCOUNTS_CSV).toString();

		saveLoaderCustomers = new SaveLoaderCustomers(customersPath, customersPath);
		saveLoaderItems = new SaveLoaderItems(itemsPath, itemsPath);
		saveLoaderDiscounts = new SaveLoaderDiscounts(discountPath, discountPath);
		saveLoaderOrders = new SaveLoaderOrders(ordersPath, ordersPath);
	}

	public void LoadData(){
		List<Customer> customers = saveLoaderCustomers.LoadData();
		List<Item> items = saveLoaderItems.LoadData();

		List<IDiscount> loadeddiscounts = saveLoaderDiscounts.LoadData();

		List<Order> loadedorders = saveLoaderOrders.LoadData();

		AvaliableItems = items;
		for (Customer customer : customers) {
			Bill newBill = new Bill(customer);
			CustomerData.put(customer, newBill);
		}

		//Link orders
		List<Order> orders = new ArrayList<>();
		for (Order order : loadedorders) {
			if (!CustomerData.containsKey(order.getCustomer())) continue;
			if (!AvaliableItems.contains(order.getItem())) continue;

			Customer customer = CustomerData.keySet().stream()
					.filter(o -> o.equals(order.getCustomer()))
					.findFirst()
					.orElse(null);

			Item item = getAvaliableItems().stream()
					.filter(o -> o.equals(order.getItem()))
					.findFirst()         // Returns an Optional<Order>
					.orElse(null);

			orders.add(new Order(item, customer));
		}

		for (Order order : orders) {
			if (!CustomerData.containsKey(order._customer)){
				throw new CustomerNotFoundException("Customer not real");
			}

			CustomerData.get(order._customer).addOrder(order);;
		}

		//Discount linking
		for (IDiscount loaded : loadeddiscounts) {
			IDiscount linked = loaded.linkToRealItems(getAvaliableItems());

			if (linked != null) {
				CreateDiscount(linked);
			}
		}
    }

	public void SaveData(){
        try {
            saveLoaderCustomers.SaveData(getCustomers());
			saveLoaderItems.SaveData(getAvaliableItems());
			saveLoaderOrders.SaveData(getOrders());
			saveLoaderDiscounts.SaveData(getAvailableDiscounts());
        } catch (SaveLoaderException e) {
            throw new RuntimeException(e);
        }

    }

	private List<Order> getOrders() {
		List<Order> orders = new ArrayList<>();
		for (Bill bill : CustomerData.values()) {
			orders.addAll(bill.Orders);
		}
		return orders;
	}

	public List<Order> GetCustomerOrders(Customer customer) {
		if (!CustomerData.containsKey(customer))
			throw new CustomerNotFoundException("Customer not found");

		return CustomerData.get(customer).Orders;

	}

	public void CreateNewOrder(Item item, Customer customer) {

		if (!CustomerData.containsKey(customer))
			throw new CustomerNotFoundException("Customer not found");

		if (!AvaliableItems.contains(item))
			throw new ItemNotFoundException("Item does not exist or isn't available");

		Bill customerBill = CustomerData.get(customer);

		Order newOrder = new Order(item, customer);
		customerBill.addOrder(newOrder);

	}

	public void CreateNewOrder(String itemid, String customerid){
		Item item = null;
		for (Item avaliableItem : getAvaliableItems()) {
			if (avaliableItem.equals(new Item(itemid))){
				item = avaliableItem;
			}
		}
		Customer customer = null;
		for (Customer c : CustomerData.keySet()) {
			if(c.equals(new Customer("", UUID.fromString(customerid)))){
				customer = c;
			}
		}
		if (item == null)
			throw new ItemNotFoundException("ITEM ID NOT FOUND");

		if (customer == null)
			throw new CustomerNotFoundException("CUSTOMER ID NOT FOUND");

		CreateNewOrder(item, customer);
	}

	public void RemoveOrder(Order order) {
		if (!CustomerData.containsKey(order._customer))
			throw new CustomerNotFoundException("Customer not Found");
		CustomerData.get(order._customer).RemoveOrder(order);

	}

	public Customer CreateCustomer(String name) {
		Customer newCustomer = new Customer(name);
		Bill newBill = new Bill(newCustomer);
		CustomerData.put(newCustomer, newBill);
		return newCustomer;
	}

	public void RemoveCustomer(Customer customer) {
		if (!CustomerData.containsKey(customer))
			throw new CustomerNotFoundException("Customer not Found");
		CustomerData.remove(customer);
	}

	public void CloseoutCustomer(Customer customer, boolean Remove) {
		if (!CustomerData.containsKey(customer))
			throw new CustomerNotFoundException("Customer is not found");

		Bill bill = CustomerData.get(customer);

		bill.GetTotalCost(AvailableDiscounts);

		if (Remove)
			RemoveCustomer(customer);

	}

	public Bill GetCustomerBill(Customer customer){
		if (!CustomerData.containsKey(customer))
			throw new CustomerNotFoundException("Customer not found");

		return CustomerData.get(customer);
	}

	public Bill.BillInfo GetCustomerBillInfo(Customer customer){
		return GetCustomerBill(customer).GetTotalCostInfo(AvailableDiscounts);
	}

	public void AddItem(Item item){
		AvaliableItems.add(item);
	}

	public void RemoveItem(Item item){
		AvaliableItems.remove(item);
	}

	public void CreateDiscount(IDiscount discount){
		AvailableDiscounts.add(discount);
	}

	public void RemoveDiscount(IDiscount discount){
		AvailableDiscounts.remove(discount);
	}


}
