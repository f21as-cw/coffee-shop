package CoffeeShop.SaveLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import CoffeeShop.Customer;
import CoffeeShop.Order;

public class SaveLoaderOrders extends ASaveLoader<Order> {

	List<Customer> _customers;

	SaveLoaderOrders(String readPath, String writePath, List<Customer> customers) throws IOException {
		super(readPath, writePath);

		this._customers = customers;
	}

	private Order ParseLine(String line) {
		String[] values = line.split(",");

		for (String value : values ) {

		}

		return null;
	}

	@Override
	public List<Order> LoadData(String path) throws LoadingException  {
		List<Order> orders = new ArrayList<Order>();
		List<String> lines = this.ReadFile();

		for (String line : lines) {
			Order order = this.ParseLine(line);

			if (order != null) {
				orders.add(order);
			}

		}

		return orders;
	}

	@Override
	public void SaveData(Order data) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'StoreData'");
	}


}
