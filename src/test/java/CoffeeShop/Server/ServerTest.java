package CoffeeShop.Server;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import CoffeeShop.Customer;
import CoffeeShop.Item;
import CoffeeShop.Order;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {

    @Test
    void testServerProcessesOrder() throws InterruptedException {
        Queue<Order> queue = new LinkedBlockingQueue<>();
        OrderQueue orderQueue = new OrderQueue(queue);
        ProcessedOrdersHashMap processedOrders = new ProcessedOrdersHashMap();
        UUID serverId = UUID.randomUUID();

        Item item = new Item("DRINK-001", 1.0f, 0);
        Customer customer = new Customer("John");
        Order order = new Order(item, customer);

        orderQueue.addOrder(order);

        Server server = new Server(serverId, orderQueue, processedOrders);
        Thread thread = new Thread(server);
        thread.start();
        thread.join(1000);

        assertEquals(1, processedOrders.getHashMap().get(serverId).size());
        assertEquals(order, processedOrders.getHashMap().get(serverId).get(0));
    }

    @Test
    void testServerExitsOnEmptyQueue() throws InterruptedException {
        Queue<Order> queue = new LinkedBlockingQueue<>();
        OrderQueue orderQueue = new OrderQueue(queue);
        ProcessedOrdersHashMap processedOrders = new ProcessedOrdersHashMap();
        UUID serverId = UUID.randomUUID();

        Server server = new Server(serverId, orderQueue, processedOrders);
        Thread thread = new Thread(server);
        thread.start();
        thread.join(1000);

        assertFalse(thread.isAlive());
    }

    @Test
    void testServerProcessesMultipleOrders() throws InterruptedException {
        Queue<Order> queue = new LinkedBlockingQueue<>();
        OrderQueue orderQueue = new OrderQueue(queue);
        ProcessedOrdersHashMap processedOrders = new ProcessedOrdersHashMap();
        UUID serverId = UUID.randomUUID();

        Item item1 = new Item("DRINK-001", 1.0f, 0);
        Item item2 = new Item("SNACK-002", 2.0f, 0);
        Customer customer = new Customer("John");

        Order order1 = new Order(item1, customer);
        Order order2 = new Order(item2, customer);

        orderQueue.addOrder(order1);
        orderQueue.addOrder(order2);

        Server server = new Server(serverId, orderQueue, processedOrders);
        Thread thread = new Thread(server);
        thread.start();
        thread.join(1000);

        assertEquals(2, processedOrders.getHashMap().get(serverId).size());
    }

    @Test
    void testServerHandlesInterruptionDuringSleep() throws InterruptedException {
        Queue<Order> queue = new LinkedBlockingQueue<>();
        OrderQueue orderQueue = new OrderQueue(queue);
        ProcessedOrdersHashMap processedOrders = new ProcessedOrdersHashMap();
        UUID serverId = UUID.randomUUID();

        Item item = new Item("DRINK-001", 1.0f, 100);
        Customer customer = new Customer("John");
        Order order = new Order(item, customer);
        orderQueue.addOrder(order);

        Server server = new Server(serverId, orderQueue, processedOrders);
        Thread thread = new Thread(server);
        thread.start();
        
        Thread.sleep(50);
        thread.interrupt();
        thread.join(1000);

        assertFalse(thread.isAlive());
    }

	@Test
	void testMultipleServersWithDifferentIds() throws InterruptedException {
		Queue<Order> queue = new LinkedBlockingQueue<>();
		OrderQueue orderQueue = new OrderQueue(queue);
		ProcessedOrdersHashMap processedOrders = new ProcessedOrdersHashMap();
		
		UUID serverId1 = UUID.randomUUID();
		UUID serverId2 = UUID.randomUUID();

		Item item = new Item("DRINK-001", 1.0f, 0);
		Customer customer = new Customer("John");

		Order order1 = new Order(item, customer);
		Order order2 = new Order(item, customer);

		Server server1 = new Server(serverId1, orderQueue, processedOrders);
		Server server2 = new Server(serverId2, orderQueue, processedOrders);

		orderQueue.addOrder(order1);
		orderQueue.addOrder(order2);

		Thread thread1 = new Thread(server1);
		Thread thread2 = new Thread(server2);
		thread1.start();
		thread2.start();
		thread1.join(1000);
		thread2.join(1000);

		assertEquals(1, processedOrders.getHashMap().get(serverId1).size());
		assertEquals(1, processedOrders.getHashMap().get(serverId2).size());
	}
}
