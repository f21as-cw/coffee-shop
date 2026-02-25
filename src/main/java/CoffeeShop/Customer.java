package CoffeeShop;

import java.util.UUID;

public class Customer {
    public final String name;
    public final UUID id;

    public Customer(String name) {
        this.name = name;
        this.id = UUID.randomUUID();
    }

	@Override
	public String toString() {
		return name + ":" + id.toString();
	}
}
