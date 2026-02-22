package CoffeeShop;

import CoffeeShop.Items.Item;
import CoffeeShop.Items.ItemDrink;
import CoffeeShop.Items.ItemMain;
import CoffeeShop.Items.ItemSnack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class BillTest {

    Customer _Customer;

    @BeforeEach
    void setupCustomer(){
        _Customer = new Customer();
    }

    @Test
    void AddOrders_RemoveOrders() throws Exception {

        Bill bill = new Bill(_Customer);
        Order order1 = new Order(new ItemDrink(2.50f), _Customer);
        bill.addOrder(order1);
        Order order2 = new Order(new ItemMain(5.34f), _Customer);
        bill.addOrder(order2);
        Order order3 = new Order(new ItemSnack(1.29f), _Customer);
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
    void CostTest1(){
        Bill bill = new Bill(_Customer);
        bill.addOrder(new Order(new ItemMain(2.5f), _Customer));
        bill.addOrder(new Order(new ItemSnack(5.4f), _Customer));
        bill.addOrder(new Order(new ItemDrink(6.3f), _Customer));
        bill.addOrder(new Order(new ItemMain(9.3f), _Customer));
        bill.addOrder(new Order(new ItemDrink(2.4f), _Customer));
        bill.addOrder(new Order(new ItemSnack(8.4f), _Customer));

        assert bill.GetCost() == 34.3f;
    }

    boolean TestRandomBill(int size){
        Bill bill = new Bill(_Customer);
        Random random = new Random();
        float current = 0;
        for (int i = 0; i < size; i++) {
            float rnd = random.nextFloat(100);
            bill.addOrder(new Order(new ItemDrink(rnd), _Customer));
            current += rnd;
        }
        return current == bill.GetCost();
    }

    @Test
    void rndCostTest1(){
        assert TestRandomBill(10);
    }

    @Test
    void rndCostTest2(){
        assert TestRandomBill(25);
    }

    @Test
    void rndCostTest3(){
        assert TestRandomBill(100);
    }

    @Test
    void rndCostTest4(){
        assert TestRandomBill(1000);
    }

    @Test
    void rndCostTest5(){
        assert TestRandomBill(10000);
    }
}
