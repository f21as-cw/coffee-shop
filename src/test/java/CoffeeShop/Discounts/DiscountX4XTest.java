package CoffeeShop.Discounts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import CoffeeShop.Order;
import CoffeeShop.Customer;
import CoffeeShop.Items.Item;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.UUID;

public class DiscountX4XTest {

	private Customer customer;

	@BeforeEach
	void setupCustomer(){
		customer = new Customer("JohnSmith", UUID.randomUUID());
	}

	@Test
	public void testDiscount041() {
		Item item = new Item("TEST-001", 1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountX4X(item, 0, 1));
	}

	@Test
	public void testDiscount140() {
		Item item = new Item("TEST-001",1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountX4X(item, 1, 0));
	}

	@Test
	public void testDiscount142() {
		Item item = new Item("TEST-001",1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountX4X(item, 1, 2));
	}

	@Test
	public void testDiscountEval241NoItem() throws InvalidDiscountException {
		Item item = new Item("TEST-001",1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		LinkedList<Order> orders = new LinkedList<>();

		float discount = two4one.DiscountEval(orders).CostChange();
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEval241OneItem() throws InvalidDiscountException {
		Item item = new Item("TEST-001",1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);

		float discount = two4one.DiscountEval(orders).CostChange();
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEval241DifferentItem() throws InvalidDiscountException {
		Item item = new Item("TEST-001",1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Item other = new Item("TEST-002",2.0f);
		Order order = new Order(other, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);

		float discount = two4one.DiscountEval(orders).CostChange();
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEval241TwoItems() throws InvalidDiscountException {
		Item item = new Item("TEST-001",1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(order);

		float discount = two4one.DiscountEval(orders).CostChange();
		assertEquals(item.getCost(), discount);
	}

	@Test
	public void testDiscountEval241ThreeItems() throws InvalidDiscountException {
		Item item = new Item("TEST-001",1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(order);
		orders.push(order);

		float discount = two4one.DiscountEval(orders).CostChange();
		assertEquals(item.getCost(), discount);
	}

	@Test
	public void testDiscountEval342ThreeItems() throws InvalidDiscountException {
		Item item = new Item("TEST-001",1.0f);
		IDiscount two4one = new DiscountX4X(item, 3, 2);
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(order);
		orders.push(order);

		float discount = two4one.DiscountEval(orders).CostChange();
		assertEquals(2, discount);
	}
}
