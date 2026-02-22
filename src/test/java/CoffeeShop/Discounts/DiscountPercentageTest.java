package CoffeeShop.Discounts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import CoffeeShop.Order;
import CoffeeShop.Customer;
import CoffeeShop.Items.Item;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.UUID;

public class DiscountPercentageTest {
	private Customer customer;

	@BeforeEach
	void setupCustomer(){
		customer = new Customer("JohnSmith", UUID.randomUUID());
	}

	@Test
	public void testDiscountPercentage0() {
		Item item = new Item("TEST-001", 1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountPercentage(item, 0.0f));
	}

	@Test
	public void testDiscountPercentage1() {
		Item item = new Item("TEST-001",1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountPercentage(item, 1.0f));
	}

	@Test
	public void testDiscountEvalNoItem() throws InvalidDiscountException {
		Item item = new Item("TEST-001",1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		LinkedList<Order> orders = new LinkedList<>();

		float discount = fifty.DiscountEval(orders).CostChange();
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEvalOneItem() throws InvalidDiscountException {
		Item item = new Item("TEST-001",1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);

		float discount = fifty.DiscountEval(orders).CostChange();
		assertEquals(item.getCost() * 0.5f, discount);
	}

	@Test
	public void testDiscountEvalTwoItems() throws InvalidDiscountException {
		Item item = new Item("TEST-001",1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(new Order(item, customer));
		orders.push(new Order(item, customer));

		float discount = fifty.DiscountEval(orders).CostChange();
		assertEquals(1, discount);
	}

	@Test
	public void testDiscountEvalOtherItem() throws InvalidDiscountException {
		Item item = new Item("TEST-001",1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		Item other = new Item("TEST-002",2.0f);
		LinkedList<Order> orders = new LinkedList<>();
		//orders.push(order);

		float discount = fifty.DiscountEval(orders).CostChange();
		assertEquals(0.0f, discount);
	}
}
