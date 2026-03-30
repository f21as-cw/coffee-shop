package CoffeeShop.Server;

import java.util.UUID;

import CoffeeShop.CoffeeShopManager;
import CoffeeShop.Logger;
import CoffeeShop.Order;

public class Server implements Runnable {
	private final OrderQueue orderQueue;
	private final ProcessedOrdersHashMap processedOrders;
	private final UUID id;

	private volatile String status = "Idle";
	private volatile float progress = 0.0f;

	public float getProgress(){ return this.progress; }
	public String getStatus(){ return this.status; }

	public Server(UUID id, OrderQueue orderQueue, ProcessedOrdersHashMap processedOrders) {
		this.id = id;
		this.orderQueue = orderQueue;
		this.processedOrders = processedOrders;
	}

	@Override
	public void run() {
		try{
			orderQueue.isQueueStarted();
			Logger.getInstance().log("Starting Server Thread " + id);
			while (!Thread.currentThread().isInterrupted()){
				status = "Waiting for new Order...";
				Order order = this.orderQueue.getOrder();
				if (order == null){
					Logger.getInstance().log("No Orders in Queue, terminating server");
					break;
				}
				long dur = order.getItem().getDuration();
				for (int i = 0; i < 100; i++) {
					if (Thread.currentThread().isInterrupted()) return;
					this.progress = (float) i / 100;
					this.status = "Processing Order " + order.getItem().getID() + " : [" + progress * 100 + "%]";
					Thread.sleep((long) ((dur * 1000L) / (100 * CoffeeShopManager.SimSpeed)));
				}
				processedOrders.addOrder(this.id, order);
				this.progress = 0.0f;
				Logger.getInstance().log("Order " + order.getItem().getID() + " completed");
			}
		} catch (InterruptedException exc) {
			Thread.currentThread().interrupt();
		}
	}
}
