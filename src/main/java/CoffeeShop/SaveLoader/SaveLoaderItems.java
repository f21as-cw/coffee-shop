package CoffeeShop.SaveLoader;

import java.io.IOException;
import java.util.List;

import CoffeeShop.Items.*;;

public class SaveLoaderItems extends ASaveLoader<Item> {

	SaveLoaderItems(String readPath, String writePath) throws IOException {
		super(readPath, writePath);
	}

	@Override
	public List<Item> LoadData(String path) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'LoadData'");
	}

	@Override
	public void SaveData(Item data) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'StoreData'");
	}


}
