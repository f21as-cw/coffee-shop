package CoffeeShop;

public class App {

	public static void main(String[] args) {
		CoffeeShopManager coffeeShopManager = new CoffeeShopManager();
		coffeeShopManager.LoadData();
		coffeeShopManager.CreateCustomer("GonzaloPro");

	}
}
