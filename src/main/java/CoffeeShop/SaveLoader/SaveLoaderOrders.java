package CoffeeShop.SaveLoader;

import java.io.IOException;
import java.util.List;

import CoffeeShop.Customer;
import CoffeeShop.Item;
import CoffeeShop.Order;

public class SaveLoaderOrders extends ASaveLoader<Order> {

	List<Customer> _customers;
	List<Item> _items;

	public SaveLoaderOrders(String readPath, String writePath, List<Item> items, List<Customer> customers)  {
		super(readPath, writePath);

		this._customers = customers;
		this._items = items;
	}


	public Order StringToEntity(String str) {
		Item orderItem = null;
		Customer orderCustomer = null;

		String[] values = str.split(",");

		if (values.length < 2) {
			// There is missing information
			return null;
		}
		String customerID = values[0];
		String itemID = values[1];

		for (Item item : this._items) {
			if (item.getID().equals(itemID)) {
				orderItem = item;
			}
		}

		if (orderItem == null) {
			// Couldn't find item associated to order
			return null;
		}

		for (Customer customer : this._customers) {
			if (customer.id.toString().equals(customerID)) {
				orderCustomer = customer;
			}
		}


		if (orderCustomer == null) {
			// Couldn't find customer associated to order
			return null;
		}

		return new Order(orderItem, orderCustomer);
	}

	public String EntityToString(Order entity) {
		return entity.getCustomer().id.toString() + "," + entity.getItem().getID();
	}

}
