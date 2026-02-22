package CoffeeShop;

import CoffeeShop.Discounts.IDiscount;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Bill {

	public Bill(Customer customer) {
		this.customer = customer;
	}

	public Customer customer;
	public List<Order> Orders = new ArrayList<>();

	public void addOrder(Order order) {
		Orders.add(order);
	}

	public void RemoveOrder(Order order) throws Exception {
		if (!Orders.contains(order))
			throw new Exception("Order does not Exist");
		Orders.remove(order);
	}

	public float GetTotalCost(List<IDiscount> allDiscounts){
		List<Order> tmpOrders = Orders;
		List<IDiscount> DiscountsUsed = List.of();

		float totalCost = GetCost();
		for (IDiscount discount : allDiscounts) {
			float costchange = discount.DiscountEval((LinkedList<Order>) tmpOrders);
			if (costchange != 0){
				DiscountsUsed.add(discount);
				totalCost -= costchange;
			}
		}
		System.out.println("Final Cost : £" + totalCost);
		return totalCost;
	}

	public float GetCost(){
		float _cost = 0;
		for (Order order : Orders) {
			_cost += order.getItem().getCost();
		}
		return _cost;
	}
}
