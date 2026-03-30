package CoffeeShop.Discounts;

import CoffeeShop.Exceptions.InvalidDiscountException;
import CoffeeShop.Item;
import CoffeeShop.Order;

import java.util.LinkedList;
import java.util.List;

public class DiscountPercentage implements IDiscount {
    public Item _item;
    public float _percentage;

    public DiscountPercentage(Item item, float percentage) throws InvalidDiscountException {
        if (percentage <= 0) {
            throw new InvalidDiscountException(
                    String.format("Expected percentage to be greater than 0, instead got %f",
                            percentage));
        }
        if (percentage >= 1) {
            throw new InvalidDiscountException(
                    String.format("Expected percentage to be smaller than 1, instead got %f",
                            percentage));
        }

        _item = item;
        _percentage = percentage;
    }

    // Approach
    // items = orders
    // .filter(o -> o.getItem() == item))
    // .size();
    // discount = items * item.getCost() * percentage
    @Override
    public DiscountsData DiscountEval(List<Order> orders) {
        List<Order> used = new LinkedList<Order>();
        for (Order order : orders) {
            if (!order.getItem().equals(_item))
                continue;
            used.add(order);
        }

        int items = used.size();
        float discount = items * _item.getCost() * _percentage;

        return new DiscountsData(used, discount);
    }

    @Override
    public String StringToEntity() {
        return "";
    }

    @Override
    public String EntityToString() {
        return this.getClass().getName() + "," + discountID + ",(" + _item.getID() + ":" + _percentage + ")";
    }

    @Override
    public IDiscount linkToRealItems(List<Item> availableItems) {
        return availableItems.stream()
                .filter(i -> i.equals(this._item))
                .findFirst()
                .map(realItem -> new DiscountPercentage(realItem, this._percentage))
                .orElse(null); // Returns null if item isn't available
    }

    @Override
    public String toString() {
        return String.format("%s -%.0f", _item.getID(), _percentage * 100) + "% OFF";
    }
}
