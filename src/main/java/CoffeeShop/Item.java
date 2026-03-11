package CoffeeShop;

import java.util.Objects;

import CoffeeShop.Exceptions.InvalidItemFormatException;

public class Item {
	float _cost;
	/**
	 * Duration in seconds that the item needs to be processed for
	 */
	int _duration;
	Category _category;
	int _IDNum;
	String _Description;
	String _IconPath;
	String _ID;

	public Item(String id){
		this(id, 0, 0, "", "");
	}

	public Item(String id, float cost) {
		this(id, cost, 0, "", "");
	}

	public Item(String id, float cost, int duration) {
		this(id, cost, duration, "", "");
	}

	public Item(String id, float cost, int duration, String description){
		this(id, cost, duration, description, "");
	}

	public Item(String id, float cost, int duration, String description, String iconpath){
		this._cost = cost;
		this._duration = duration;
		this._Description = description;
		this._IconPath = iconpath;

		if (id == null || !id.contains("-")) {
			throw new InvalidItemFormatException("ID must contain a hyphen: " + id);
		}

		try {
			String[] parts = id.split("-");
			this._category = Category.valueOf(parts[0].toUpperCase());
			this._IDNum = Integer.parseInt(parts[1]);
			this._ID = id;

		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			throw new InvalidItemFormatException("Invalid ID format or Category: " + id, e);
		}
	}

	public String getID() {
		return _ID;
	}

	public float getCost() {
		return _cost;
	}

	public void setCost(float cost) {
		_cost = cost;
	}

	public int getDuration() {
		return _duration;
	}

	public void setDuration(int duration) {
		this._duration = duration;
	}

	public Category getCategory() {
		return _category;
	}

	public void setCategory(Category _category) {
		this._category = _category;
	}

	public String getDescription() {
		return _Description;
	}

	public void setDescription(String _Description) {
		this._Description = _Description;
	}

	public String getIconPath() {
		return _IconPath;
	}

	public void setIconPath(String _IconPath) {
		this._IconPath = _IconPath;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		return Objects.equals(this._ID, other._ID);
	}

	public int hashCode() {
		return Objects.hash(getID());
	}

	@Override
	public String toString() {
		return _ID;
	}
}

enum Category {
	DRINK,
	MAIN,
	SNACK,
	TEST
}

