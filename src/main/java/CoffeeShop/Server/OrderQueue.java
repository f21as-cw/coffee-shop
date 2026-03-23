package CoffeeShop.Server;

import java.util.LinkedList;
import java.util.Queue;

import CoffeeShop.Logger;
import CoffeeShop.Order;

public class OrderQueue {
	Queue<Order> queue;
	private boolean isQueueStarted = false;

	public OrderQueue(Queue<Order> queue) {
		this.queue = queue;
	}
	public synchronized void addOrder(Order order) {
		queue.add(order);
	}

	public synchronized Order getOrder() throws InterruptedException {
		return queue.poll();
	}

	public synchronized void isQueueStarted() throws InterruptedException {
		if (isQueueStarted)
				return;
		this.wait();
	}

	public synchronized void startQueue(){
		isQueueStarted = true;
		this.notifyAll();
	}

}
