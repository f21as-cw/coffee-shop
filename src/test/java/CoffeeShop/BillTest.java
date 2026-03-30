package CoffeeShop;

import CoffeeShop.Discounts.DiscountMealDeal;
import CoffeeShop.Discounts.DiscountPercentage;
import CoffeeShop.Discounts.DiscountX4X;
import CoffeeShop.Discounts.IDiscount;
import CoffeeShop.Exceptions.InvalidDiscountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BillTest {

    Customer _Customer;

    @BeforeEach
    void setupCustomer() {
        _Customer = new Customer("JohnSmith");
    }

    @Test
    void AddOrders_RemoveOrders() throws Exception {

        Bill bill = new Bill(_Customer);
        Order order1 = new Order(new Item("MAIN-01", 2.50f), _Customer);
        bill.addOrder(order1);
        Order order2 = new Order(new Item("DRINK-01", 5.34f), _Customer);
        bill.addOrder(order2);
        Order order3 = new Order(new Item("SNACK-01", 1.29f), _Customer);
        bill.addOrder(order3);

        assert bill.Orders.size() == 3;
        assert bill.Orders.contains(order1);
        assert bill.Orders.contains(order2);
        assert bill.Orders.contains(order3);
        bill.RemoveOrder(order1);
        assert bill.Orders.size() == 2;
        assert !bill.Orders.contains(order1);
        bill.RemoveOrder(order2);
        bill.RemoveOrder(order3);
        assert !bill.Orders.contains(order1);
        assert !bill.Orders.contains(order2);
        assert !bill.Orders.contains(order3);

        try {
            bill.RemoveOrder(order1);
        } catch (Exception e) {
            assert true;
        }

    }

    @Test
    void CostTest1() {
        Bill bill = new Bill(_Customer);
        bill.addOrder(new Order(new Item("MAIN-01", 2.5f), _Customer));
        bill.addOrder(new Order(new Item("SNACK-01", 5.4f), _Customer));
        bill.addOrder(new Order(new Item("DRINK-01", 6.3f), _Customer));
        bill.addOrder(new Order(new Item("MAIN-02", 9.3f), _Customer));
        bill.addOrder(new Order(new Item("DRINK-02", 2.4f), _Customer));
        bill.addOrder(new Order(new Item("SNACK-02", 8.4f), _Customer));

        assert bill.GetCost() == 34.3f;
    }

    boolean TestRandomBill(int size) {
        Bill bill = new Bill(_Customer);
        Random random = new Random();
        float current = 0;
        for (int i = 0; i < size; i++) {
            float rnd = random.nextFloat(100);
            bill.addOrder(new Order(new Item("TEST-" + i, rnd), _Customer));
            current += rnd;
        }
        return current == bill.GetCost();
    }

    @Test
    void rndCostTest1() throws Exception {
        assert TestRandomBill(10);
    }

    @Test
    void rndCostTest2() {
        assert TestRandomBill(25);
    }

    @Test
    void rndCostTest3() {
        assert TestRandomBill(100);
    }

    @Test
    void rndCostTest4() {
        assert TestRandomBill(1000);
    }

    @Test
    void rndCostTest5() {
        assert TestRandomBill(10000);
    }

    @Test
    void DiscountTest1() throws InvalidDiscountException {
        Bill bill = new Bill(_Customer);
        Item snack = new Item("SNACK-001", 2.5f);
        List<Item> items = new ArrayList<>(List.of(
                snack,
                snack,
                new Item("MAIN-001", 8.4f),
                new Item("DRINK-001", 8.5f),
                new Item("DRINK-002", 1.2f),
                new Item("SNACK-002", 1.2f)
        ));
        for (Item item : items) {
            bill.addOrder(new Order(item, _Customer));
        }

        List<IDiscount> discounts = new ArrayList<>();
        discounts.add(new DiscountX4X(snack, 2, 1));
        float finalCost = bill.GetTotalCost(discounts);
        discounts.add(new DiscountMealDeal(new ArrayList<>(List.of(items.get(2), items.get(3), items.get(4))), 10f));
        finalCost = bill.GetTotalCost(discounts);
        discounts.add(new DiscountPercentage(items.get(5), .20f));
        finalCost = bill.GetTotalCost(discounts);

        assert finalCost == 13.46f;
    }
}
