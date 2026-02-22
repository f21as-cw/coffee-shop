package CoffeeShop.Discounts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import CoffeeShop.Order;
import CoffeeShop.Customer;
import CoffeeShop.Items.Item;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;

public class DiscountMealDealTest {
	private Customer customer;

	@BeforeEach
	void setupCustomer(){
		customer = new Customer("JohnSmith", UUID.randomUUID());
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
		items.add(new Item("SNACK-001", 1.0f));
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountMealDeal(items, 0.0f));
	}

	@Test
	public void testDiscountEvalNoItem() throws InvalidDiscountException {
		Item main = new Item("MAIN-001", 2.0f);
		Item snack = new Item("SNACK-001", 1.0f);
		Item drink = new Item("DRINK-001", 1.0f);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(main);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		LinkedList<Order> orders = new LinkedList<>();

		float discount = mealDeal.DiscountEval(orders).CostChange();
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEvalOneItem() throws InvalidDiscountException {
		Item main = new Item("MAIN-001", 2.0f);
		Item snack = new Item("SNACK-001", 1.0f);
		Item drink = new Item("DRINK-001", 1.0f);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(main);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(new Order(main, customer));

		float discount = mealDeal.DiscountEval(orders).CostChange();
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEvalTwoItems() throws InvalidDiscountException {
		Item main = new Item("MAIN-001", 2.0f);
		Item snack = new Item("SNACK-001", 1.0f);
		Item drink = new Item("DRINK-001", 1.0f);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(main);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(new Order(main, customer));
		orders.push(new Order(snack, customer));

		float discount = mealDeal.DiscountEval(orders).CostChange();
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEvalThreeItems() throws InvalidDiscountException {
		Item main = new Item("MAIN-001", 2.0f);
		Item snack = new Item("SNACK-001", 1.0f);
		Item drink = new Item("DRINK-001", 1.0f);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(main);
		items.add(snack);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(new Order(main, customer));
		orders.push(new Order(snack, customer));
		orders.push(new Order(drink, customer));

		float discount = mealDeal.DiscountEval(orders).CostChange();
		assertEquals(2.0f, discount);
	}

	@Test
	public void testDiscountEvalOtherItem() throws InvalidDiscountException {
		Item main = new Item("MAIN-001", 2.0f);
		Item snack = new Item("SNACK-001", 1.0f);
		Item drink = new Item("DRINK-001", 1.0f);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(main);
		items.add(snack);
		items.add(drink);
		IDiscount mealDeal = new DiscountMealDeal(items, 3.0f);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(new Order(new Item("MAIN-002", 1.0f), customer));
		orders.push(new Order(new Item("SNACK-002", 1.0f), customer));
		orders.push(new Order(new Item("DRINK-002", 1.0f), customer));

		float discount = mealDeal.DiscountEval(orders).CostChange();
		assertEquals(0.0f, discount);
	}
}
