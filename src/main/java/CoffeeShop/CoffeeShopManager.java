package CoffeeShop;

import CoffeeShop.Discounts.IDiscount;
import CoffeeShop.Exceptions.CustomerNotFoundException;

import java.util.List;
import java.util.Map;

public class CoffeeShopManager {
	public Map<Customer, Bill> CustomerData;
	private SaveLoader Saveloader;
	private List<Item> AvaliableItems;
	private List<IDiscount> AvaliableDiscounts;

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
