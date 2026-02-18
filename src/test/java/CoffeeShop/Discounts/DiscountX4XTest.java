package CoffeeShop.Discounts;

import org.junit.jupiter.api.Test;

import CoffeeShop.Order;
import CoffeeShop.Customer;
import CoffeeShop.Items.IItem;
import CoffeeShop.Items.Item;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;

public class DiscountX4XTest {
	@Test
	public void testDiscount041() {
		IItem item = new Item(1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountX4X(item, 0, 1));
	}

	@Test
	public void testDiscount140() {
		IItem item = new Item(1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountX4X(item, 1, 0));
	}

	@Test
	public void testDiscount142() {
		IItem item = new Item(1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountX4X(item, 1, 2));
	}

	@Test
	public void testDiscountEval241NoItem() throws InvalidDiscountException {
		IItem item = new Item(1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		LinkedList<Order> orders = new LinkedList<>();

		float discount = two4one.DiscountEval(orders);
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEval241OneItem() throws InvalidDiscountException {
		IItem item = new Item(1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Customer customer = new Customer();
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);

		float discount = two4one.DiscountEval(orders);
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEval241DifferentItem() throws InvalidDiscountException {
		IItem item = new Item(1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Customer customer = new Customer();
		IItem other = new Item(2.0f);
		Order order = new Order(other, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);

		float discount = two4one.DiscountEval(orders);
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEval241TwoItems() throws InvalidDiscountException {
		IItem item = new Item(1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Customer customer = new Customer();
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(order);

		float discount = two4one.DiscountEval(orders);
		assertEquals(item.getCost(), discount);
	}

	@Test
	public void testDiscountEval241ThreeItems() throws InvalidDiscountException {
		IItem item = new Item(1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Customer customer = new Customer();
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(order);
		orders.push(order);

		float discount = two4one.DiscountEval(orders);
		assertEquals(item.getCost(), discount);
	}

	@Test
	public void testDiscountEval342ThreeItems() throws InvalidDiscountException {
		IItem item = new Item(1.0f);
		IDiscount two4one = new DiscountX4X(item, 3, 2);
		Customer customer = new Customer();
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(order);
		orders.push(order);

		float discount = two4one.DiscountEval(orders);
		assertEquals(item.getCost(), discount);
	}
}
