package CoffeeShop.SaveLoader;

import java.io.IOException;
import java.util.UUID;

import CoffeeShop.Customer;

public class SaveLoaderCustomers extends ASaveLoader<Customer> {

	public SaveLoaderCustomers(String readPath, String writePath) {
		super(readPath, writePath);
	}

	@Override
	Customer StringToEntity(String str) {
		String[] values = str.split(",");

		if (values.length < 2) {
			// There is missing information
			return null;
		}

		UUID id = UUID.fromString(values[0]);
		String name = values[1];
		return new Customer(name, id);
	}

	@Override
	String EntityToString(Customer entity) {
		return entity.id.toString() + "," + entity.name;
	}

}
