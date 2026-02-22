package CoffeeShop.SaveLoader;

import java.io.IOException;
import java.util.List;

import CoffeeShop.Order;

public class SaveLoaderOrders extends ASaveLoader<Order> {

	SaveLoaderOrders(String readPath, String writePath) throws IOException {
		super(readPath, writePath);
	}

	@Override
	public List<Order> LoadData(String path) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'LoadData'");
	}

	@Override
	public void SaveData(Order data) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'StoreData'");
	}


}
