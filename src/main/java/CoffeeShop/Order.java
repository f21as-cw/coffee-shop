package CoffeeShop;

import CoffeeShop.Items.IItem;

public class Order {
	IItem _item;
	Customer _customer;

	public Order(IItem item, Customer customer) {
		_item = item;
		_customer = customer;
	}

	public IItem getItem() {
		return _item;
	}

	public void setItem(IItem item) {
		_item = item;
	}
}
