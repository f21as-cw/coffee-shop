package CoffeeShop;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");

		CoffeeShopManager coffeShopManager = new CoffeeShopManager();
		coffeShopManager.CreateCustomer("GonzaloPro");
		coffeShopManager.SaveData();
	}

}
