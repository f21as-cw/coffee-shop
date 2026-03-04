package CoffeeShop.Discounts;

import java.util.List;

import CoffeeShop.Order;

public record DiscountsData(List<Order> OrdersUsed, float CostChange) {
}
