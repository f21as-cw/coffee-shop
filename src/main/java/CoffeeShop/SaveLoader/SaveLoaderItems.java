package CoffeeShop.SaveLoader;

import CoffeeShop.Item;
import CoffeeShop.Exceptions.InvalidItemFormatException;

public class SaveLoaderItems extends ASaveLoader<Item> {

	public SaveLoaderItems(String readPath, String writePath) {
		super(readPath, writePath);
	}

	@Override
	Item StringToEntity(String str) {
		String[] values = str.split(",");

		if (values.length < 4) {
			return null;
		}

		String id = values[0];
		String cost = values[1];
		String duration = values[2];
		String description = values[3];

		int parsedDuration = 0;
		Float parsedCost;
		try {
			parsedDuration = Integer.parseInt(duration);
			parsedCost = Float.parseFloat(cost);
		}
		catch (NumberFormatException e) {
			return null;
		}


		try {
			return new Item(id, parsedCost, parsedDuration, description);
		} catch (InvalidItemFormatException e) {
			return null;
		}
	}

	@Override
	String EntityToString(Item entity) {
		String id = entity.getID().toString();
		float cost = entity.getCost();
		int duration = entity.getDuration();
		String description = entity.getDescription();

		return id + "," + cost + "," + duration + "," + description;
	}


}
