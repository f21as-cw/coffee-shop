package CoffeeShop.Server;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.Test;

import CoffeeShop.Customer;
import CoffeeShop.Item;
import CoffeeShop.Order;

import static org.junit.jupiter.api.Assertions.*;

public class OrderQueueTest {

    @Test
    void testAddAndGetOrder() {
        Queue<Order> queue = new LinkedBlockingQueue<>();
        OrderQueue orderQueue = new OrderQueue(queue);

        Item item = new Item("DRINK-001", 1.0f, 5);
        Customer customer = new Customer("John");
        Order order = new Order(item, customer);

        orderQueue.addOrder(order);

        Order retrieved = orderQueue.getOrder();
        assertNotNull(retrieved);
        assertEquals(order, retrieved);
    }

    @Test
    void testGetOrderFromEmptyQueue() {
        Queue<Order> queue = new LinkedBlockingQueue<>();
        OrderQueue orderQueue = new OrderQueue(queue);

        Order retrieved = orderQueue.getOrder();
        assertNull(retrieved);
    }

    @Test
    void testMultipleOrders() {
        Queue<Order> queue = new LinkedBlockingQueue<>();
        OrderQueue orderQueue = new OrderQueue(queue);

        Item item1 = new Item("DRINK-001", 1.0f, 5);
        Item item2 = new Item("SNACK-002", 2.0f, 3);
        Customer customer = new Customer("John");

        Order order1 = new Order(item1, customer);
        Order order2 = new Order(item2, customer);

        orderQueue.addOrder(order1);
        orderQueue.addOrder(order2);

        assertEquals(order1, orderQueue.getOrder());
        assertEquals(order2, orderQueue.getOrder());
        assertNull(orderQueue.getOrder());
    }
}
