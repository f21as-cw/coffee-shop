package CoffeeShop.SaveLoader;

import java.util.UUID;

import CoffeeShop.Customer;
import CoffeeShop.Item;
import CoffeeShop.Order;

public class SaveLoaderOrders extends ASaveLoader<Order> {

	public SaveLoaderOrders(String readPath, String writePath)  {
		super(readPath, writePath);
	}


	public Order StringToEntity(String str) {
		String[] values = str.split(",");

		if (values.length < 2) {
			// There is missing information
			return null;
		}
		String customerID = values[0];
		String itemID = values[1];


        Item item = null;
        Customer customer = null;
        try {
            item = new Item(itemID, 0);
            customer = new Customer("", UUID.fromString(customerID));
        } catch (Exception e) {
            return null;
        }

        if (item == null || customer == null) return null;

		return new Order(item, customer);
	}

	public String EntityToString(Order entity) {
		return entity.getCustomer().id.toString() + "," + entity.getItem().getID();
	}

}
