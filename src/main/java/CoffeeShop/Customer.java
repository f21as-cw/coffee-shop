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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		return Objects.equals(this.id, other.id);
	}
}
