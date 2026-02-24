package CoffeeShop.Items;

import java.util.Objects;

public class Item {
	float _cost;
	Category _category;
	int _IDNum;
	String _Description;
	String _IconPath;
	String _ID;

	public Item(String ID, float cost) {
		_cost = cost;
		String[] parts = ID.split("-");
		_category = Category.valueOf(parts[0]);
		_IDNum = Integer.parseInt(parts[1]);
		_ID = ID;
	}

	public String getID() { return _ID; }

	public float getCost() { return _cost; }

	public void setCost(float cost) {
		_cost = cost;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Item other = (Item) obj;
		return Objects.equals(this._ID, other._ID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getID());
	}

	@Override
	public String toString() {
		return _ID;
	}
}

enum Category{
	DRINK,
	MAIN,
	SNACK,
	TEST
}
