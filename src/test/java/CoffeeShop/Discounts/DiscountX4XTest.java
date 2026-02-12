package CoffeeShop.Discounts;

import org.junit.jupiter.api.Test;

import CoffeeShop.Item;
import CoffeeShop.Order;
import CoffeeShop.Customer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;

public class DiscountX4XTest {
	@Test
	public void testDiscountEval241NoItem() {
		Item item = new Item(1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		LinkedList<Order> orders = new LinkedList<>();
		float currentPrice = 0.0f;
		for (Order o : orders) {
			currentPrice += o.getItem().getCost();
		}

		float discount = two4one.DiscountEval(orders, currentPrice);
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEval241OneItem() {
		Item item = new Item(1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Customer customer = new Customer();
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		float currentPrice = 0.0f;
		for (Order o : orders) {
			currentPrice += o.getItem().getCost();
		}

		float discount = two4one.DiscountEval(orders, currentPrice);
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEval241DifferentItem() {
		Item item = new Item(1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Customer customer = new Customer();
		Item other = new Item(2.0f);
		Order order = new Order(other, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		float currentPrice = 0.0f;
		for (Order o : orders) {
			currentPrice += o.getItem().getCost();
		}

		float discount = two4one.DiscountEval(orders, currentPrice);
		assertEquals(0.0f, discount);
	}

	@Test
	public void testDiscountEval241TwoItems() {
		Item item = new Item(1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Customer customer = new Customer();
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(order);
		float currentPrice = 0.0f;
		for (Order o : orders) {
			currentPrice += o.getItem().getCost();
		}

		float discount = two4one.DiscountEval(orders, currentPrice);
		assertEquals(item.getCost(), discount);
	}

	@Test
	public void testDiscountEval241ThreeItems() {
		Item item = new Item(1.0f);
		IDiscount two4one = new DiscountX4X(item, 2, 1);
		Customer customer = new Customer();
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(order);
		orders.push(order);
		float currentPrice = 0.0f;
		for (Order o : orders) {
			currentPrice += o.getItem().getCost();
		}

		float discount = two4one.DiscountEval(orders, currentPrice);
		assertEquals(item.getCost(), discount);
	}

	@Test
	public void testDiscountEval342ThreeItems() {
		Item item = new Item(1.0f);
		IDiscount two4one = new DiscountX4X(item, 3, 2);
		Customer customer = new Customer();
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(order);
		orders.push(order);
		float currentPrice = 0.0f;
		for (Order o : orders) {
			currentPrice += o.getItem().getCost();
		}

		float discount = two4one.DiscountEval(orders, currentPrice);
		assertEquals(item.getCost(), discount);
	}
}
