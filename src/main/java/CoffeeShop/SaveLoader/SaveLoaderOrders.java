package CoffeeShop.SaveLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import CoffeeShop.Customer;
import CoffeeShop.Order;
import CoffeeShop.Items.IItem;
import CoffeeShop.Items.Item;

public class SaveLoaderOrders extends ASaveLoader<Order> {

	List<Customer> _customers;
	List<Item> _items;

	SaveLoaderOrders(String readPath, String writePath, List<Item> items, List<Customer> customers) throws IOException {
		super(readPath, writePath);

		this._items = items;
		this._customers = customers;
	}

	public Order StringToEntity(String str) {
		Item orderItem = null;
		Customer orderCustomer = null;

		String[] values = str.split(",");

		if (values.length < 2) {
			// There is missing information
			return null;
		}
		String itemID = values[0];
		String customerID = values[1];

		for (Item item : this._items) {
			if (item.id == itemID) {
				orderItem = item;
			}
		}

		if (orderItem == null) {
			// Couldn't find item associated to order
			return null;
		}

		for (Customer customer : this._customers) {
			if (customer.id.toString() == customerID) {
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
		itemID = entity.

		return "";
	}

}
