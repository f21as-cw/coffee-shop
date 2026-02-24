package CoffeeShop;

import CoffeeShop.Exceptions.CustomerNotFoundException;
import CoffeeShop.Exceptions.ItemNotFoundException;

public class Order {
	Item _item;
	Customer _customer;

	public Order(Item item, Customer customer)
	{
		if (item == null)
			throw new ItemNotFoundException("Not item in order");

		if (customer == null)
			throw new CustomerNotFoundException("No customer in order");

		_item = item;
		_customer = customer;
	}

	public Item getItem() {
		return _item;
	}

	public void setItem(Item item) {
		_item = item;
	}

	@Override
	public String toString() {
		return _item + ":" + _customer.name;
	}
}
