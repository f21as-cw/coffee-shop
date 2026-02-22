package CoffeeShop;

import java.util.UUID;

public class Customer {
    public final String name;
	public final UUID id;

    public Customer(String name, UUID id) {
        this.name = name;
        this.id = id;
    }

	@Override
	public String toString() {
		return name + ":" + id.toString();
	}
}
