package CoffeeShop.Discounts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import CoffeeShop.Order;
import CoffeeShop.Customer;
import CoffeeShop.Item;
import CoffeeShop.ItemException;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;

public class DiscountPercentageTest {

	private Customer customer;
	@BeforeEach
	void setup(){
		customer = new Customer("JohnSmith");
	}

	@Test
	public void testDiscountPercentage0() throws ItemException {
		Item item = new Item("MAIN-1", 1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountPercentage(item, 0.0f));
	}

	@Test
	public void testDiscountPercentage1() throws ItemException {
		Item item = new Item("MAIN-1", 1.0f);
		assertThrows(
				InvalidDiscountException.class,
				() -> new DiscountPercentage(item, 1.0f));
	}

	@Test
	public void testDiscountEvalNoItem() throws InvalidDiscountException, ItemException {
		Item item = new Item("MAIN-1", 1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		LinkedList<Order> orders = new LinkedList<>();

		DiscountsData data = fifty.DiscountEval(orders);
		assertEquals(0.0f, data.CostChange());
		assertTrue(data.OrdersUsed().isEmpty());
	}

	@Test
	public void testDiscountEvalOneItem() throws InvalidDiscountException, ItemException {
		Item item = new Item("MAIN-1", 1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		Order order = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);

		DiscountsData data = fifty.DiscountEval(orders);
		assertEquals(item.getCost() * 0.5f, data.CostChange());
		assertEquals(1, data.OrdersUsed().size());
		assertTrue(data.OrdersUsed().contains(order));
	}

	@Test
	public void testDiscountEvalTwoItems() throws InvalidDiscountException, ItemException {
		Item item = new Item("MAIN-1", 1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		Order order = new Order(item, customer);
		Order otherOrder = new Order(item, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);
		orders.push(otherOrder);

		DiscountsData data = fifty.DiscountEval(orders);
		assertEquals(item.getCost(), data.CostChange());
		assertEquals(2, data.OrdersUsed().size());
		assertTrue(data.OrdersUsed().contains(order));
		assertTrue(data.OrdersUsed().contains(otherOrder));
	}

	@Test
	public void testDiscountEvalOtherItem() throws InvalidDiscountException, ItemException {
		Item item = new Item("MAIN-1", 1.0f);
		IDiscount fifty = new DiscountPercentage(item, 0.5f);
		Item other = new Item("MAIN-2", 2.0f);
		Order order = new Order(other, customer);
		LinkedList<Order> orders = new LinkedList<>();
		orders.push(order);

		DiscountsData data = fifty.DiscountEval(orders);
		assertEquals(0.0f, data.CostChange());
		assertTrue(data.OrdersUsed().isEmpty());
	}
}
