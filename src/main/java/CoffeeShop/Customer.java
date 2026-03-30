package CoffeeShop;

import java.util.Objects;
import java.util.UUID;

public record Customer(String name, UUID id) {
    public Customer(String name) {
        this(name, UUID.randomUUID());
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
