package CoffeeShop.Discounts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import CoffeeShop.Order;
import CoffeeShop.Customer;
import CoffeeShop.Item;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class DiscountMealDealTest {
	private Customer customer;
	@BeforeEach
	void setup(){
		customer = new Customer("JohnSmith");
	}

	@Test
	public void testDiscountMealDealNoItems() {
		ArrayList<Item> items = new ArrayList<Item>();
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountMealDeal(items, 1.0f));
	}

	@Test
	public void testDiscountMealDealNoCost() {
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new Item("MAIN-1", 1.0f));
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountMealDeal(items, 0.0f));
	}

	@Test
	public void testDiscountEvalNoItem() throws InvalidDiscountException {
		Item main = new Item("MAIN-1", 2.0f);
		Item snack = new Item("SNACK-1", 1.0f);
		Item drink = new Item("DRINK-1", 1.0f);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(main);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		LinkedList<Order> orders = new LinkedList<>();

		DiscountsData data = mealDeal.DiscountEval(orders);
		assertEquals(0.0f, data.CostChange());
		assertTrue(data.OrdersUsed().isEmpty());
	}

	@Test
	public void testDiscountEvalOneItem() throws InvalidDiscountException {
		Item main = new Item("MAIN-1", 2.0f);
		Item snack = new Item("SNACK-1", 1.0f);
		Item drink = new Item("DRINK-1", 1.0f);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(main);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(new Order(main, customer));

		DiscountsData data = mealDeal.DiscountEval(orders);
		assertEquals(0.0f, data.CostChange());
		assertTrue(data.OrdersUsed().isEmpty());
	}

	@Test
	public void testDiscountEvalTwoItems() throws InvalidDiscountException {
		Item main = new Item("MAIN-1", 2.0f);
		Item snack = new Item("SNACK-1", 1.0f);
		Item drink = new Item("DRINK-1", 1.0f);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(main);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(new Order(main, customer));
		orders.push(new Order(snack, customer));

		DiscountsData data = mealDeal.DiscountEval(orders);
		assertEquals(0.0f, data.CostChange());
		assertTrue(data.OrdersUsed().isEmpty());
	}

	@Test
	public void testDiscountEvalThreeItems() throws InvalidDiscountException {
		Item main = new Item("MAIN-1", 2.0f);
		Item snack = new Item("SNACK-1", 1.0f);
		Item drink = new Item("DRINK-1", 1.0f);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(main);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		LinkedList<Order> orders = new LinkedList<>();
		Order mainOrder = new Order(main, customer);
		Order snackOrder = new Order(snack, customer);
		Order drinkOrder = new Order(drink, customer);
		orders.push(mainOrder);
		orders.push(snackOrder);
		orders.push(drinkOrder);

		DiscountsData data = mealDeal.DiscountEval(orders);
		assertEquals(1.0f, data.CostChange());
		assertEquals(3, data.OrdersUsed().size());
		assertTrue(data.OrdersUsed().contains(mainOrder));
		assertTrue(data.OrdersUsed().contains(snackOrder));
		assertTrue(data.OrdersUsed().contains(drinkOrder));
	}

	@Test
	public void testDiscountEvalItem() throws InvalidDiscountException {
		Item main = new Item("MAIN-1", 2.0f);
		Item snack = new Item("SNACK-1", 1.0f);
		Item drink = new Item("DRINK-1", 1.0f);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(main);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(new Order(new Item("MAIN-2", 1.0f), customer));
		orders.push(new Order(new Item("SNACK-2", 1.0f), customer));
		orders.push(new Order(new Item("DRINK-2", 1.0f), customer));

		DiscountsData data = mealDeal.DiscountEval(orders);
		assertEquals(0.0f, data.CostChange());
		assertTrue(data.OrdersUsed().isEmpty());
	}
}
