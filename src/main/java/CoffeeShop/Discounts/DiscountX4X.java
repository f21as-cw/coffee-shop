package CoffeeShop.Discounts;

import CoffeeShop.Exceptions.InvalidDiscountException;
import CoffeeShop.Item;
import CoffeeShop.Order;

import java.util.LinkedList;
import java.util.List;

public class DiscountX4X implements IDiscount {
    public Item _item;
    public int _x;
    public int _y;

    public DiscountX4X(Item item, int x, int y) throws InvalidDiscountException {
        if (x <= 0) {
            throw new InvalidDiscountException(
                    String.format("Expected x to be greater than 0, instead got %d", x));
        }
        if (y <= 0) {
            throw new InvalidDiscountException(
                    String.format("Expected y to be greater than 0, instead got %d", y));
        }
        if (y >= x) {
            throw new InvalidDiscountException(
                    String.format("Expected y to be smaller than x, instead got x=%d y=%d", x, y));
        }

        _item = item;
        _x = x;
        _y = y;
    }

    // Approach
    // items = orders
    // .filter(o -> o.getItem() == item))
    // .size();
    // itemsToPay = (items / x) * y + (items % x)
    // discount = items * item.getCost() - itemsToPay * item.getCost()
    @Override
    public DiscountsData DiscountEval(List<Order> orders) {
        LinkedList<Order> used = new LinkedList<Order>();

        for (Order order : orders) {
            if (!order.getItem().equals(_item))
                continue;
            used.add(order);
        }

        int items = used.size();
        int itemsToPay = (items / _x) * _y + (items % _x);
        float discount = items * _item.getCost() - itemsToPay * _item.getCost();

        return new DiscountsData(used, discount);
    }

    @Override
    public String StringToEntity() {
        return "";
    }

    @Override
    public String EntityToString() {
        return this.getClass().getName() + "," + discountID + ",(" + _item.getID() + ":" + _x + ":" + _y + ")";
    }

    @Override
    public IDiscount linkToRealItems(List<Item> availableItems) {
        return availableItems.stream()
                .filter(i -> i.equals(this._item))
                .findFirst()
                .map(realItem -> new DiscountX4X(realItem, this._x, this._y))
                .orElse(null); // Returns null if item isn't available
    }

    @Override
    public String toString() {
        return _item + " buy " + _y + " get " + _x;
    }
}
