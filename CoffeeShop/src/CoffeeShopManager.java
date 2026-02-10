import Discounts.IDiscount;

import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class CoffeeShopManager {

    public Map<Customer, Bill> CustomerData;
    private SaveLoader Saveloader;
    private List<Item> AvaliableItems;
    private List<IDiscount> AvaliableDiscounts;

    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
    }

    public List<Order> GetCustomerOrders(Customer customer) throws Exception {
        if (!CustomerData.containsKey(customer))
            throw new Exception("Customer does not Exist");

        return CustomerData.get(customer).Orders;

    }

    public void CreateNewOrder(Item item, Customer customer) throws Exception {

        if (!CustomerData.containsKey(customer))
            throw new Exception("Customer does not Exist");

        Bill customerBill = CustomerData.get(customer);

        Order newOrder = new Order(item, customer);
        customerBill.addOrder(newOrder);

    }

    public void RemoveOrder(Order order) {

    }

    public void CreateCustomer(){
        Customer newCustomer = new Customer();
        Bill newBill = new Bill(newCustomer);
        CustomerData.put(newCustomer, newBill);
    }

    public void RemoveCustomer(Customer customer){
        CustomerData.remove(customer);
    }


}