package CoffeeShop;

import CoffeeShop.Discounts.DiscountMealDeal;
import CoffeeShop.Discounts.DiscountPercentage;
import CoffeeShop.Discounts.DiscountX4X;
import CoffeeShop.Discounts.IDiscount;
import CoffeeShop.GUI.GUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class App {

	public static void main(String[] args) {
		// Set up Swing on the Event Dispatch Thread
//		SwingUtilities.invokeLater(() -> {
//			CoffeeShopManager csm = new CoffeeShopManager();
//			try {
//				csm.LoadData();
//			} catch (Exception e) {
//				System.err.println("Error loading data: " + e.getMessage());
//				// Initialize with empty collections if loading fails
//				csm.setAvaliableItems(new ArrayList<>());
//				csm.setAvailableDiscounts(new ArrayList<>());
//			}
//
//			// Initialize discounts if not already set
//			if (csm.getAvailableDiscounts() == null) {
//				csm.setAvailableDiscounts(new ArrayList<>());
//			}
//
//			// Launch the GUI
//			GUI gui = new GUI(csm);
//			gui.show();
//		});

		CoffeeShopManager csm = new CoffeeShopManager();
		csm.LoadData();
		Random rnd = new Random();
		for (int i = 0; i < 10; i++) {
			IDiscount discount = new DiscountPercentage(csm.getAvaliableItems().get(i), rnd.nextFloat(0.99f));
			csm.CreateDiscount(discount);
		}

		List<Item> items = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			items.add(csm.getAvaliableItems().get(rnd.nextInt(csm.getAvaliableItems().size())));
		}
		IDiscount discount = new DiscountMealDeal(items, rnd.nextFloat(15));
		csm.CreateDiscount(discount);

		for (int i = 0; i < 3; i++) {
			int x = rnd.nextInt(1, 3);
			IDiscount discount1 = new DiscountX4X(csm.getAvaliableItems().get(i), x + rnd.nextInt(1, 3), x);
			csm.CreateDiscount(discount1);
		}
		csm.SaveData();

	}
}
