package CoffeeShop.Discounts;

import java.util.List;

import CoffeeShop.Order;

record DiscountsData(List<Order> OrdersUsed, float CostChange) {
};

public interface IDiscount {
	DiscountsData DiscountEval(List<Order> orders);
}
