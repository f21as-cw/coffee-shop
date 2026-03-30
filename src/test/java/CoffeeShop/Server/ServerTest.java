package CoffeeShop.Server;

import CoffeeShop.Customer;
import CoffeeShop.Item;
import CoffeeShop.Order;
import org.junit.jupiter.api.Test;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ServerTest {

    @Test
    void testServerProcessesOrder() throws InterruptedException {
        Queue<Order> queue = new LinkedBlockingQueue<>();
        OrderQueue orderQueue = new OrderQueue(queue);
        ProcessedOrdersHashMap processedOrders = new ProcessedOrdersHashMap();
        UUID serverId = UUID.randomUUID();

        Item item = new Item("DRINK-001", 1.0f, 1);
        Customer customer = new Customer("John");
        Order order = new Order(item, customer);

        orderQueue.addOrder(order);

        Server server = new Server(serverId, orderQueue, processedOrders);
        Thread thread = new Thread(server);
        thread.start();
        // Sleep for 100 ms to ensure the server is waiting for the queue to start.
        // Without this delay, orderQueue.startQueue() may be called before the server
        // invokes orderQueue.isQueueStarted(), causing it to wait indefinitely.
        Thread.sleep(100);
        orderQueue.startQueue();
        thread.join(2000);

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
        // Sleep for 100 ms to ensure the server is waiting for the queue to start.
        // Without this delay, orderQueue.startQueue() may be called before the server
        // invokes orderQueue.isQueueStarted(), causing it to wait indefinitely.
        Thread.sleep(100);
        orderQueue.startQueue();
        thread.join(1000);


        assertFalse(thread.isAlive());
    }

    @Test
    void testServerProcessesMultipleOrders() throws InterruptedException {
        Queue<Order> queue = new LinkedBlockingQueue<>();
        OrderQueue orderQueue = new OrderQueue(queue);
        ProcessedOrdersHashMap processedOrders = new ProcessedOrdersHashMap();
        UUID serverId = UUID.randomUUID();

        Item item1 = new Item("DRINK-001", 1.0f, 1);
        Item item2 = new Item("SNACK-002", 2.0f, 1);
        Customer customer = new Customer("John");

        Order order1 = new Order(item1, customer);
        Order order2 = new Order(item2, customer);

        orderQueue.addOrder(order1);
        orderQueue.addOrder(order2);

        Server server = new Server(serverId, orderQueue, processedOrders);
        Thread thread = new Thread(server);
        thread.start();
        // Sleep for 100 ms to ensure the server is waiting for the queue to start.
        // Without this delay, orderQueue.startQueue() may be called before the server
        // invokes orderQueue.isQueueStarted(), causing it to wait indefinitely.
        Thread.sleep(100);
        orderQueue.startQueue();
        thread.join(3000);

        assertEquals(1, processedOrders.getHashMap().get(serverId).size());
    }

    @Test
    void testServerHandlesInterruptionDuringOrderSleep() throws InterruptedException {
        Queue<Order> queue = new LinkedBlockingQueue<>();
        OrderQueue orderQueue = new OrderQueue(queue);
        ProcessedOrdersHashMap processedOrders = new ProcessedOrdersHashMap();
        UUID serverId = UUID.randomUUID();

        Item item = new Item("DRINK-001", 1.0f, 10);
        Customer customer = new Customer("John");
        Order order = new Order(item, customer);
        orderQueue.addOrder(order);

        Server server = new Server(serverId, orderQueue, processedOrders);
        Thread thread = new Thread(server);
        thread.start();
        // Sleep for 100 ms to ensure the server is waiting for the queue to start.
        // Without this delay, orderQueue.startQueue() may be called before the server
        // invokes orderQueue.isQueueStarted(), causing it to wait indefinitely.
        Thread.sleep(100);
        orderQueue.startQueue();

        Thread.sleep(50);
        thread.interrupt();
        thread.join(1000);

        assertFalse(thread.isAlive());
    }

    @Test
    void testServerHandlesInterruptionDuringQueueSleep() throws InterruptedException {
        Queue<Order> queue = new LinkedBlockingQueue<>();
        OrderQueue orderQueue = new OrderQueue(queue);
        ProcessedOrdersHashMap processedOrders = new ProcessedOrdersHashMap();
        UUID serverId = UUID.randomUUID();

        Item item = new Item("DRINK-001", 1.0f, 10);
        Customer customer = new Customer("John");
        Order order = new Order(item, customer);
        orderQueue.addOrder(order);

        Server server = new Server(serverId, orderQueue, processedOrders);
        Thread thread = new Thread(server);
        thread.start();
        Thread.sleep(100);

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

        Item item = new Item("DRINK-001", 1.0f, 1);
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
        // Sleep for 100 ms to ensure the servers are waiting for the queue to start.
        // Without this delay, orderQueue.startQueue() may be called before the servers
        // invoke orderQueue.isQueueStarted(), causing them to wait indefinitely.
        Thread.sleep(100);
        orderQueue.startQueue();
        thread1.join(2000);
        thread2.join(2000);

        assertEquals(1, processedOrders.getHashMap().get(serverId1).size());
        assertEquals(1, processedOrders.getHashMap().get(serverId2).size());
    }
}
