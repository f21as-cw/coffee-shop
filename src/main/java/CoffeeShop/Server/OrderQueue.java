package CoffeeShop.Server;

import java.util.LinkedList;
import java.util.Queue;

import CoffeeShop.Logger;
import CoffeeShop.Order;

public class OrderQueue {
	Queue<Order> queue;

	public OrderQueue(Queue<Order> queue) {
		this.queue = queue;
	}
	public synchronized void addOrder(Order order) {
		queue.add(order);
		this.notify();
	}

	public synchronized Order getOrder() throws InterruptedException {
		while (queue.isEmpty()) {
			this.wait();
		}
		return queue.poll();
	}
}
