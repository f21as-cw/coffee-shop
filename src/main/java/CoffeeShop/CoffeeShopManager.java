package CoffeeShop;

import CoffeeShop.Discounts.IDiscount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class CoffeeShopManager {
	public Map<Customer, Bill> CustomerData;
	private List<Item> AvaliableItems;
	private List<IDiscount> AvaliableDiscounts;

	public CoffeeShopManager(List<Customer> customers, List<Item> items, List<Order> orders) {
		Map<Customer, Bill> customerData = new HashMap<Customer, Bill>();

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

	public List<Item> getAvaliableItems() {
		return AvaliableItems;
	}

	public void setAvaliableItems(List<Item> avaliableItems) {
		AvaliableItems = avaliableItems;
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
}
