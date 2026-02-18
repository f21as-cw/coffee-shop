package CoffeeShop.Discounts;

import org.junit.jupiter.api.Test;

import CoffeeShop.Order;
import CoffeeShop.Customer;
import CoffeeShop.Items.IItem;
import CoffeeShop.Items.Item;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;

public class DiscountPercentageTest {
	@Test
	public void testDiscountPercentage0() {
		IItem item = new Item(1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountPercentage(item, 0.0f));
	}

	@Test
	public void testDiscountPercentage1() {
		IItem item = new Item(1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountPercentage(item, 1.0f));
	}

	@Test
	public void testDiscountEvalNoItem() throws InvalidDiscountException {
		IItem item = new Item(1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		LinkedList<Order> orders = new LinkedList<>();

		float discount = fifty.DiscountEval(orders);
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEvalOneItem() throws InvalidDiscountException {
		IItem item = new Item(1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		Customer customer = new Customer();
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);

		float discount = fifty.DiscountEval(orders);
		assertEquals(item.getCost() * 0.5f, discount);
	}

	@Test
	public void testDiscountEvalTwoItems() throws InvalidDiscountException {
		IItem item = new Item(1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		Customer customer = new Customer();
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(order);

		float discount = fifty.DiscountEval(orders);
		assertEquals(item.getCost(), discount);
	}

	@Test
	public void testDiscountEvalOtherItem() throws InvalidDiscountException {
		IItem item = new Item(1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		Customer customer = new Customer();
		IItem other = new Item(2.0f);
		Order order = new Order(other, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);

		float discount = fifty.DiscountEval(orders);
		assertEquals(0.0f, discount);
	}
}
