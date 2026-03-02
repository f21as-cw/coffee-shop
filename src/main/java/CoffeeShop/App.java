package CoffeeShop;

import CoffeeShop.GUI.GUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class App {

	public static void main(String[] args) {
		// Set up Swing on the Event Dispatch Thread
		SwingUtilities.invokeLater(() -> {
			CoffeeShopManager csm = new CoffeeShopManager();
			try {
				csm.LoadData();
			} catch (Exception e) {
				System.err.println("Error loading data: " + e.getMessage());
				// Initialize with empty collections if loading fails
				csm.setAvaliableItems(new ArrayList<>());
				csm.setAvailableDiscounts(new ArrayList<>());
			}

			// Initialize discounts if not already set
			if (csm.getAvailableDiscounts() == null) {
				csm.setAvailableDiscounts(new ArrayList<>());
			}

			// Launch the GUI
			GUI gui = new GUI(csm);
			gui.show();
		});
	}
}
