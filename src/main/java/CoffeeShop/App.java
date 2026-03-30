package CoffeeShop;

import CoffeeShop.GUI.GUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Scanner;

public class App {

	public static void main(String[] args) {
		Logger.getInstance().log("App is running...");
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


		//THIS IS JUST FOR TESTING, FEEL FREE TO REMOVE
//		CoffeeShopManager csm = new CoffeeShopManager();
//		csm.LoadData();
//
//		csm.CreateNewOrder("DRINK-001", "bed72c83-3a21-456e-9eac-f2cbd1049359");
//		csm.CreateNewOrder("MAIN-003", "bed72c83-3a21-456e-9eac-f2cbd1049359");
//		csm.CreateNewOrder("SNACK-002", "bed72c83-3a21-456e-9eac-f2cbd1049359");
//		csm.CreateNewOrder("DRINK-002", "bed72c83-3a21-456e-9eac-f2cbd1049359");
//
//		csm.addServer();
//		csm.addServer();
//		csm.addServer();
//
//		csm.Start();

	}
}
