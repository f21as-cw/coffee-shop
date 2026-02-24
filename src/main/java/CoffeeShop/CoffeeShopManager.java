package CoffeeShop;

import CoffeeShop.Discounts.IDiscount;
import CoffeeShop.Exceptions.CustomerNotFoundException;
import CoffeeShop.SaveLoader.ISaveLoader;
import CoffeeShop.SaveLoader.SaveLoaderItems;
import CoffeeShop.SaveLoader.SaveLoaderOrders;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CoffeeShopManager {
	public Map<Customer, Bill> CustomerData;
	private List<Item> AvaliableItems;
	private List<IDiscount> AvaliableDiscounts;

	private SaveLoaderOrders saveLoaderOrders;
	private SaveLoaderItems saveLoaderItems;



	public static void main(String[] args) {

	}

	//TODO once the save loader is complete
	public void Startup(){
//		try {
//			Properties prop = new Properties();
//			try (InputStream input = new FileInputStream("Config.properties")){
//				prop.load(input);
//
//				String OrderPath = prop.getProperty("OrderPath");
//				String ItemPath = prop.getProperty("ItemPath");
//
//				saveLoaderOrders = new SaveLoaderOrders(OrderPath, OrderPath, );
//				saveLoaderItems = new SaveLoaderItems();
//			}
//
//		}catch (IOException e){
//			throw new RuntimeException("Unable to open file");
//		}
	}

	public List<Order> GetCustomerOrder(Customer customer) {
		if (!CustomerData.containsKey(customer))
			throw new CustomerNotFoundException("Customer not found");

		return CustomerData.get(customer).Orders;

	}

	public void CreateNewOrder(Item item, Customer customer) {

		if (!CustomerData.containsKey(customer))
			throw new CustomerNotFoundException("Customer not found");

		Bill customerBill = CustomerData.get(customer);

		Order newOrder = new Order(item, customer);
		customerBill.addOrder(newOrder);

	}

	//TODO
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

	public void CloseoutCustomer(Customer customer, boolean Remove) throws Exception {
		if (!CustomerData.containsKey(customer))
			throw new Exception("Customer does not exist");

		Bill bill = CustomerData.get(customer);

		bill.GetTotalCost(AvaliableDiscounts);

		if (Remove)
			RemoveCustomer(customer);

	}

}
