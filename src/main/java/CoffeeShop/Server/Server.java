package CoffeeShop.Server;

import java.util.UUID;

import CoffeeShop.Order;

public class Server implements Runnable {
	private final OrderQueue orderQueue;
	private final ProcessedOrdersHashMap processedOrders;
	private final UUID id;

	public Server(UUID id, OrderQueue orderQueue, ProcessedOrdersHashMap processedOrders) {
		this.id = id;
		this.orderQueue = orderQueue;
		this.processedOrders = processedOrders;
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				Order order = this.orderQueue.getOrder();
				if (order == null) {
					break;
				}
				Thread.sleep(order.getItem().getDuration() * 1000);
				processedOrders.addOrder(this.id, order);
			}
		} catch (InterruptedException exc) {
			Thread.currentThread().interrupt();
		}
	}
}
