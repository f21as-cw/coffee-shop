package CoffeeShop;

import CoffeeShop.Discounts.IDiscount;
import CoffeeShop.Exceptions.CustomerNotFoundException;
import CoffeeShop.Exceptions.ItemNotFoundException;
import CoffeeShop.SaveLoader.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CoffeeShopManager {
	public Map<Customer, Bill> CustomerData = new HashMap<>();
	public List<Customer> getCustomers() { return new ArrayList<>(CustomerData.keySet()); }


	private List<Item> AvaliableItems;
	public List<Item> getAvaliableItems() { return AvaliableItems; }
	public void setAvaliableItems(List<Item> avaliableItems) { AvaliableItems = avaliableItems; }

	private List<IDiscount> AvailableDiscounts;
	public List<IDiscount> getAvailableDiscounts() { return AvailableDiscounts; }
	public void setAvailableDiscounts(List<IDiscount> avaliableDiscounts) { AvailableDiscounts = avaliableDiscounts; }

	private ISaveLoader<Order> saveLoaderOrders;
	private ISaveLoader<Item> saveLoaderItems;
	private ISaveLoader<Customer> saveLoaderCustomers;

	public CoffeeShopManager() {}

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
	}

	public void LoadData(){
        try {
            Path dataDir = Paths.get("data");
			System.out.println(dataDir.toAbsolutePath());
			Properties config = ResourceLoader.loadConfig();

            String customersPath = dataDir.resolve("customers.csv").toString();
            String itemsPath = dataDir.resolve("items.csv").toString();
            String ordersPath = dataDir.resolve("orders.csv").toString();

			saveLoaderCustomers = new SaveLoaderCustomers(customersPath, customersPath);
			saveLoaderItems = new SaveLoaderItems(itemsPath, itemsPath);

            List<Customer> customers = saveLoaderCustomers.LoadData();
            List<Item> items = saveLoaderItems.LoadData();

			saveLoaderOrders = new SaveLoaderOrders(ordersPath, ordersPath, items, customers);

            List<Order> orders = saveLoaderOrders.LoadData();

            AvaliableItems = items;
            for (Customer customer : customers) {
                Bill newBill = new Bill(customer);
                CustomerData.put(customer, newBill);
            }

            for (Order order : orders) {
                if (!CustomerData.containsKey(order._customer)){
                    throw new CustomerNotFoundException("Customer not real");
				}

                CustomerData.get(order._customer).addOrder(order);;
            }
        } catch (CustomerNotFoundException e) {
            throw new CustomerNotFoundException("Customer problem");
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

	public void SaveData(){
        try {
            saveLoaderCustomers.SaveData(getCustomers());
			saveLoaderItems.SaveData(getAvaliableItems());
			saveLoaderOrders.SaveData(getOrders());
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

	public void RemoveOrder(Order order) {
		if (!CustomerData.containsKey(order._customer))
			throw new CustomerNotFoundException("Customer not Found");
		CustomerData.get(order._customer).RemoveOrder(order);

	}

	public void CreateCustomer(String name) {
		Customer newCustomer = new Customer(name);
		Bill newBill = new Bill(newCustomer);
		CustomerData.put(newCustomer, newBill);
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

}
