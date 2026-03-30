package CoffeeShop.Discounts;

import CoffeeShop.Item;
import CoffeeShop.Order;

import java.util.List;
import java.util.UUID;

public interface IDiscount {
    UUID discountID = UUID.randomUUID();

    DiscountsData DiscountEval(List<Order> orders);

    IDiscount linkToRealItems(List<Item> availableItems);

    String StringToEntity();

    String EntityToString();
}
