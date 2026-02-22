package CoffeeShop.Items;

public class Item implements IItem {
	float _cost;

	public Item(float cost) {
		_cost = cost;
	}

	@Override
	public float getCost() {
		return _cost;
	}

	@Override
	public void setCost(float cost) {
		_cost = cost;
	}
}
