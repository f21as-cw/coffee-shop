package CoffeeShop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import CoffeeShop.Discounts.DiscountsData;
import CoffeeShop.Discounts.IDiscount;
import CoffeeShop.Exceptions.OrderNotFoundException;

public class Bill {

	public Customer customer;
	public List<Order> Orders = new ArrayList<>();

	public Bill(Customer customer) {
		this.customer = customer;
	}

	public Bill(Customer customer, List<Order> orders) {
		this.customer = customer;
		this.Orders = orders;

	}

	public void addOrder(Order order) {
		if (order == null)
			throw new NullPointerException("No order to add");
		Orders.add(order);
	}

	public void RemoveOrder(Order order) {
		if (!Orders.contains(order))
			throw new OrderNotFoundException("Order not found");
		Orders.remove(order);
	}

	public record BillInfo(List<IDiscount> DiscountsUsed, float FinalCost) {
	}

	public BillInfo GetTotalCostInfo(List<IDiscount> allDiscounts) {
		List<Order> tmpOrders = new ArrayList<>(Orders);
		List<IDiscount> DiscountsUsed = new ArrayList<>();

		float totalCost = GetCost();
		for (IDiscount discount : allDiscounts) {
			Logger.getInstance().log(
					"Discount - " + discount.toString());
			DiscountsData Data = discount.DiscountEval(tmpOrders);
			for (Order order : Data.OrdersUsed()) {
				Logger.getInstance().log("\tOrder " + order);
			}
			totalCost -= Data.CostChange();
			if (Data.CostChange() != 0)
				DiscountsUsed.add(discount);
			Logger.getInstance().log("\t\tCost Change - £" + Data.CostChange());
			tmpOrders.removeAll(Data.OrdersUsed());
		}

		// TO remove floating point shenanigans
		totalCost = BigDecimal.valueOf(totalCost)
				.setScale(2, RoundingMode.HALF_UP)
				.floatValue();
		Logger.getInstance().log("Final Cost : £" + totalCost);
		return new BillInfo(DiscountsUsed, totalCost);
	}

	public float GetTotalCost(List<IDiscount> allDiscounts) {
		return GetTotalCostInfo(allDiscounts).FinalCost;
	}

	public float GetCost() {
		float _cost = 0;
		for (Order order : Orders) {
			_cost += order.getItem().getCost();
		}
		return _cost;
	}
}
