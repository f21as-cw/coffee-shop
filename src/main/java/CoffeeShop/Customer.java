package CoffeeShop;

import java.util.Objects;
import java.util.UUID;

public class Customer {
    public final String name;
    public final UUID id;

    public Customer(String name) {
        this.name = name;
        this.id = UUID.randomUUID();
    }

    public Customer(String name, UUID id) {
        this.name = name;
        this.id = id;
    }

	@Override
	public String toString() {
		return name + ":" + id.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Customer that = (Customer) o;
		return Objects.equals(id, that.id);
	}
}
