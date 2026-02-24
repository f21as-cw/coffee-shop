package CoffeeShop;

import CoffeeShop.Discounts.IDiscount;

import java.util.List;
import java.util.Map;
import java.util.UUID;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class CoffeeShopManager {
	public Map<Customer, Bill> CustomerData;
	private SaveLoader Saveloader;
	private List<Item> AvaliableItems;
	private List<IDiscount> AvaliableDiscounts;

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
