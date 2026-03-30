package CoffeeShop.Server;

import CoffeeShop.Customer;
import CoffeeShop.Item;
import CoffeeShop.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ProcessedOrdersHashMapTest {

    @Test
    void testAddOrderToNewServer() {
        ProcessedOrdersHashMap map = new ProcessedOrdersHashMap();
        UUID serverId = UUID.randomUUID();

        Item item = new Item("DRINK-001", 1.0f, 5);
        Customer customer = new Customer("John");
        Order order = new Order(item, customer);

        map.addOrder(serverId, order);

        ArrayList<Order> orders = map.getHashMap().get(serverId);
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(order, orders.get(0));
    }

    @Test
    void testAddMultipleOrdersToSameServer() {
        ProcessedOrdersHashMap map = new ProcessedOrdersHashMap();
        UUID serverId = UUID.randomUUID();

        Item item1 = new Item("DRINK-001", 1.0f, 5);
        Item item2 = new Item("SNACK-002", 2.0f, 3);
        Customer customer = new Customer("John");

        Order order1 = new Order(item1, customer);
        Order order2 = new Order(item2, customer);

        map.addOrder(serverId, order1);
        map.addOrder(serverId, order2);

        ArrayList<Order> orders = map.getHashMap().get(serverId);
        assertEquals(2, orders.size());
        assertEquals(order1, orders.get(0));
        assertEquals(order2, orders.get(1));
    }

    @Test
    void testAddOrdersFromMultipleServers() {
        ProcessedOrdersHashMap map = new ProcessedOrdersHashMap();
        UUID serverId1 = UUID.randomUUID();
        UUID serverId2 = UUID.randomUUID();

        Item item = new Item("DRINK-001", 1.0f, 5);
        Customer customer = new Customer("John");

        Order order1 = new Order(item, customer);
        Order order2 = new Order(item, customer);

        map.addOrder(serverId1, order1);
        map.addOrder(serverId2, order2);

        assertEquals(1, map.getHashMap().get(serverId1).size());
        assertEquals(1, map.getHashMap().get(serverId2).size());
        assertEquals(2, map.getHashMap().size());
    }

    @Test
    void testGetHashMap() {
        ProcessedOrdersHashMap map = new ProcessedOrdersHashMap();
        assertNotNull(map.getHashMap());
        assertTrue(map.getHashMap().isEmpty());
    }
}
