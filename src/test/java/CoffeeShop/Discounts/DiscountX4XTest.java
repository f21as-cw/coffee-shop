package CoffeeShop.Discounts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import CoffeeShop.Order;
import CoffeeShop.Customer;
import CoffeeShop.Item;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;

public class DiscountX4XTest {

	private Customer customer;
	@BeforeEach
	void setup(){
		customer = new Customer("JohnSmith");
	}

	@Test
	public void testDiscount041() {
		Item item = new Item("MAIN-1", 1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountX4X(item, 0, 1));
	}

	@Test
	public void testDiscount140() {
		Item item = new Item("MAIN-1", 1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountX4X(item, 1, 0));
	}

	@Test
	public void testDiscount142() {
		Item item = new Item("MAIN-1", 1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountX4X(item, 1, 2));
	}

	@Test
	public void testDiscountEval241NoItem() throws InvalidDiscountException {
		Item item = new Item("MAIN-1", 1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		LinkedList<Order> orders = new LinkedList<>();

		DiscountsData data = two4one.DiscountEval(orders);
		assertEquals(0.0f, data.CostChange());
		assertTrue(data.OrdersUsed().isEmpty());
	}

	@Test
	public void testDiscountEval241OneItem() throws InvalidDiscountException {
		Item item = new Item("MAIN-1", 1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);

		DiscountsData data = two4one.DiscountEval(orders);
		assertEquals(0.0f, data.CostChange());
		assertEquals(1, data.OrdersUsed().size());
		assertTrue(data.OrdersUsed().contains(order));
	}

	@Test
	public void testDiscountEval241DifferentItem() throws InvalidDiscountException {
		Item item = new Item("MAIN-1", 1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Item other = new Item("MAIN-2", 2.0f);
		Order order = new Order(other, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);

		DiscountsData data = two4one.DiscountEval(orders);
		assertEquals(0.0f, data.CostChange());
		assertTrue(data.OrdersUsed().isEmpty());
	}

	@Test
	public void testDiscountEval241TwoItems() throws InvalidDiscountException {
		Item item = new Item("MAIN-1", 1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Order order = new Order(item, customer);
		Order otherOrder = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(otherOrder);

		DiscountsData data = two4one.DiscountEval(orders);
		assertEquals(item.getCost(), data.CostChange());
		assertEquals(2, data.OrdersUsed().size());
		assertTrue(data.OrdersUsed().contains(order));
		assertTrue(data.OrdersUsed().contains(otherOrder));
	}

	@Test
	public void testDiscountEval241ThreeItems() throws InvalidDiscountException {
		Item item = new Item("MAIN-1", 1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Order order = new Order(item, customer);
		Order otherOrder = new Order(item, customer);
		Order thirdOrder = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(otherOrder);
		orders.push(thirdOrder);

		DiscountsData data = two4one.DiscountEval(orders);
		assertEquals(item.getCost(), data.CostChange());
		assertEquals(3, data.OrdersUsed().size());
		assertTrue(data.OrdersUsed().contains(order));
		assertTrue(data.OrdersUsed().contains(otherOrder));
		assertTrue(data.OrdersUsed().contains(thirdOrder));
	}

	@Test
	public void testDiscountEval342ThreeItems() throws InvalidDiscountException {
		Item item = new Item("MAIN-1", 1.0f);
		IDiscount two4one = new DiscountX4X(item, 3, 2);
		Order order = new Order(item, customer);
		Order otherOrder = new Order(item, customer);
		Order thirdOrder = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(otherOrder);
		orders.push(thirdOrder);

		DiscountsData data = two4one.DiscountEval(orders);
		assertEquals(item.getCost(), data.CostChange());
		assertEquals(3, data.OrdersUsed().size());
		assertTrue(data.OrdersUsed().contains(order));
		assertTrue(data.OrdersUsed().contains(otherOrder));
		assertTrue(data.OrdersUsed().contains(thirdOrder));
	}
}
