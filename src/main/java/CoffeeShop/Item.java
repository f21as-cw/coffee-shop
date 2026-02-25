package CoffeeShop;

import java.util.Objects;

public class Item {
	float _cost;
	Category _category;
	int _IDNum;
	String _Description;
	String _IconPath;
	String _ID;

	public Item(String ID, float cost) throws ItemException {
		if (cost <= 0) {
			throw new ItemException("Cost can't be zero or less");
		}

		_cost = cost;
		String[] parts = ID.split("-");
		_category = Category.valueOf(parts[0]);
		_IDNum = Integer.parseInt(parts[1]);
		_ID = ID;
	}

	public Item(String ID, float cost, String desciption) throws ItemException {
		this(ID, cost);
		_Description = desciption;
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

	public Category getCategory() {
		return _category;
	}

	public void setCategory(Category category) {
		this._category = category;
	}

	public String getDescription() {
		return _Description;
	}

	public void setDescription(String description) {
		this._Description = description;
	}


	@Override
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
