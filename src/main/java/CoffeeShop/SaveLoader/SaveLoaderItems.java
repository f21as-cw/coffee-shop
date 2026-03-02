package CoffeeShop.SaveLoader;

import java.io.IOException;
import java.util.List;
import java.util.Locale.Category;

import CoffeeShop.Exceptions.InvalidItemFormatException;
import CoffeeShop.Item;
import CoffeeShop.ItemException;

public class SaveLoaderItems extends ASaveLoader<Item> {

	public SaveLoaderItems(String readPath, String writePath) {
		super(readPath, writePath);
	}

	@Override
	Item StringToEntity(String str) {
		String[] values = str.split(",");

		if (values.length < 3) {
			// There is missing information
			return null;
		}

		String id = values[0];
		String cost = values[1];
		String description = values[2];

		Float parsedFloat;
		try {
			parsedFloat = Float.parseFloat(cost);
		}
		catch (NumberFormatException e) {
			return null;
		}

		try {
			return new Item(id, parsedFloat, description);
		} catch (InvalidItemFormatException e) {
			return null;
		}
	}

	@Override
	String EntityToString(Item entity) {
		String id = entity.getID().toString();
		float cost = entity.getCost();
		String description = entity.getDescription();

		return id + "," + cost + "," + description;
	}


}
