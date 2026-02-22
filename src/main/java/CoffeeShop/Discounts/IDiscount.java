package CoffeeShop.Discounts;

import java.util.LinkedList;

import CoffeeShop.Order;

public interface IDiscount {
	float DiscountEval(LinkedList<Order> orders);
}
