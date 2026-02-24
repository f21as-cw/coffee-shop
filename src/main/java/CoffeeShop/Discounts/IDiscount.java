package CoffeeShop.Discounts;

import java.util.List;

import CoffeeShop.Order;

;

public interface IDiscount {
	DiscountsData DiscountEval(List<Order> orders);
}
