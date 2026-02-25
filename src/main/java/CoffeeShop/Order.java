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

	public Customer getCustomer() {
		return _customer;
	}

	public void setSustomer(Customer customer) {
		this._customer = _customer;
	}


	@Override
	public String toString() {
		return _item + ":" + _customer.name;
	}
}
