package CoffeeShop.Discounts;

import org.junit.jupiter.api.Test;

import CoffeeShop.Item;
import CoffeeShop.Order;
import CoffeeShop.Customer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;

public class DiscountPercentageTest {
	@Test
	public void testDiscountEval0() {
		Item item = new Item(1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountPercentage(item, 0.0f));
	}

	@Test
	public void testDiscountEval1() {
		Item item = new Item(1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountPercentage(item, 1.0f));
	}

	@Test
	public void testDiscountEvalNoItem() throws InvalidDiscountException {
		Item item = new Item(1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		LinkedList<Order> orders = new LinkedList<>();
		float currentPrice = 0.0f;
		for (Order o : orders) {
			currentPrice += o.getItem().getCost();
		}

		float discount = fifty.DiscountEval(orders, currentPrice);
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEvalOneItem() throws InvalidDiscountException {
		Item item = new Item(1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		Customer customer = new Customer();
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		float currentPrice = 0.0f;
		for (Order o : orders) {
			currentPrice += o.getItem().getCost();
		}

		float discount = fifty.DiscountEval(orders, currentPrice);
		assertEquals(item.getCost() * 0.5f, discount);
	}

	@Test
	public void testDiscountEvalTwoItems() throws InvalidDiscountException {
		Item item = new Item(1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		Customer customer = new Customer();
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(order);
		float currentPrice = 0.0f;
		for (Order o : orders) {
			currentPrice += o.getItem().getCost();
		}

		float discount = fifty.DiscountEval(orders, currentPrice);
		assertEquals(item.getCost(), discount);
	}

	@Test
	public void testDiscountEvalOtherItem() throws InvalidDiscountException {
		Item item = new Item(1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		Customer customer = new Customer();
		Item other = new Item(2.0f);
		Order order = new Order(other, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		float currentPrice = 0.0f;
		for (Order o : orders) {
			currentPrice += o.getItem().getCost();
		}

		float discount = fifty.DiscountEval(orders, currentPrice);
		assertEquals(0.0f, discount);
	}
}
