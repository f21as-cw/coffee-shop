package CoffeeShop;

import CoffeeShop.Discounts.DiscountsData;
import CoffeeShop.Discounts.IDiscount;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
		List<Order> tmpOrders = new ArrayList<>(Orders);
		List<IDiscount> DiscountsUsed = new ArrayList<>(allDiscounts);

		float totalCost = GetCost();
		for (IDiscount discount : allDiscounts) {
			System.out.println("Discount - " + discount.toString());
			DiscountsData Data = discount.DiscountEval(tmpOrders);
			for (Order order : Data.OrdersUsed()) {
				System.out.println("	Order " + order);
			}
			totalCost -= Data.CostChange();
			System.out.println("		Cost Change - £" + Data.CostChange());

		}

		//TO remove floating point shenanigans
		totalCost = BigDecimal.valueOf(totalCost)
				.setScale(2, RoundingMode.HALF_UP)
				.floatValue();
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
