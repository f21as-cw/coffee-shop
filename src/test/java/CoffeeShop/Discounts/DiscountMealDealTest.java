package CoffeeShop.Discounts;

import org.junit.jupiter.api.Test;

import CoffeeShop.Order;
import CoffeeShop.Customer;
import CoffeeShop.Items.IItem;
import CoffeeShop.Items.Item;
import CoffeeShop.Items.ItemDrink;
import CoffeeShop.Items.ItemMain;
import CoffeeShop.Items.ItemSnack;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class DiscountMealDealTest {
	@Test
	public void testDiscountMealDealNoItems() {
		ArrayList<IItem> items = new ArrayList<IItem>();
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountMealDeal(items, 1.0f));
	}

	@Test
	public void testDiscountMealDealNoCost() {
		ArrayList<IItem> items = new ArrayList<IItem>();
		items.add(new Item(1.0f));
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountMealDeal(items, 0.0f));
	}

	@Test
	public void testDiscountEvalNoItem() throws InvalidDiscountException {
		IItem main = new ItemMain(2.0f);
		IItem snack = new ItemSnack(1.0f);
		IItem drink = new ItemDrink(1.0f);
		ArrayList<IItem> items = new ArrayList<IItem>();
		items.add(main);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		LinkedList<Order> orders = new LinkedList<>();

		float discount = mealDeal.DiscountEval(orders);
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEvalOneItem() throws InvalidDiscountException {
		IItem main = new ItemMain(2.0f);
		IItem snack = new ItemSnack(1.0f);
		IItem drink = new ItemDrink(1.0f);
		ArrayList<IItem> items = new ArrayList<IItem>();
		items.add(main);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		Customer customer = new Customer();
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(new Order(main, customer));

		float discount = mealDeal.DiscountEval(orders);
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEvalTwoItems() throws InvalidDiscountException {
		IItem main = new ItemMain(2.0f);
		IItem snack = new ItemSnack(1.0f);
		IItem drink = new ItemDrink(1.0f);
		ArrayList<IItem> items = new ArrayList<IItem>();
		items.add(main);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		Customer customer = new Customer();
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(new Order(main, customer));
		orders.push(new Order(snack, customer));

		float discount = mealDeal.DiscountEval(orders);
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEvalThreeItems() throws InvalidDiscountException {
		IItem main = new ItemMain(2.0f);
		IItem snack = new ItemSnack(1.0f);
		IItem drink = new ItemDrink(1.0f);
		ArrayList<IItem> items = new ArrayList<IItem>();
		items.add(main);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		Customer customer = new Customer();
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(new Order(main, customer));
		orders.push(new Order(snack, customer));
		orders.push(new Order(drink, customer));

		float discount = mealDeal.DiscountEval(orders);
		assertEquals(1.0f, discount);
	}

	@Test
	public void testDiscountEvalOtherItem() throws InvalidDiscountException {
		IItem main = new ItemMain(2.0f);
		IItem snack = new ItemSnack(1.0f);
		IItem drink = new ItemDrink(1.0f);
		ArrayList<IItem> items = new ArrayList<IItem>();
		items.add(main);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		Customer customer = new Customer();
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(new Order(new OtherItem(1.0f), customer));
		orders.push(new Order(new OtherItem(1.0f), customer));
		orders.push(new Order(new OtherItem(1.0f), customer));

		float discount = mealDeal.DiscountEval(orders);
		assertEquals(0.0f, discount);
	}
}
