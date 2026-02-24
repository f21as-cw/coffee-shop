package CoffeeShop.Discounts;

import CoffeeShop.Order;

import java.util.List;

public record DiscountsData(List<Order> OrdersUsed, float CostChange) {
}
