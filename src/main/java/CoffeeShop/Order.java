package CoffeeShop;

public class Order {
	Item _item;
	Customer _customer;

	public Order(Item item, Customer customer) {
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
