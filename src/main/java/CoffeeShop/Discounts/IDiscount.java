package CoffeeShop.Discounts;

import java.util.List;

import CoffeeShop.Order;

;
public interface IDiscount {
	record DiscountsData(List<Order> OrdersUsed, float CostChange){};
	DiscountsData DiscountEval(List<Order> orders);

}
