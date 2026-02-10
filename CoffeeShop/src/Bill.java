import java.util.List;

public class Bill {

    public Bill(Customer customer){
        this.customer = customer;
    }

    public Customer customer;
    public List<Order> Orders;

    public void addOrder(Order order) {
        Orders.add(order);
    }
}
