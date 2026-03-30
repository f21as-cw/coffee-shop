package CoffeeShop;

import CoffeeShop.Discounts.IDiscount;
import CoffeeShop.Exceptions.CustomerNotFoundException;
import CoffeeShop.Exceptions.ItemNotFoundException;
import CoffeeShop.Exceptions.SaveLoaderException;
import CoffeeShop.SaveLoader.*;
import CoffeeShop.Server.OrderQueue;
import CoffeeShop.Server.ProcessedOrdersHashMap;
import CoffeeShop.Server.Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class CoffeeShopManager {
    public static final String CUSTOMERS_CSV = "customers.csv";
    public static final String ITEMS_CSV = "items.csv";
    public static final String ORDERS_CSV = "orders.csv";
    public static final String DISCOUNTS_CSV = "discounts.csv";
    public static String DATA_DIR = "data";
    public static float SimSpeed = 1.0f;
    private final OrderQueue orderQueue = new OrderQueue(new LinkedBlockingQueue<>());
    private final ProcessedOrdersHashMap processedOrders = new ProcessedOrdersHashMap();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<UUID, Future<?>> activeServers = new HashMap<>();
    private final Map<UUID, Server> serverStatusMap = new HashMap<>();
    public Map<Customer, Bill> CustomerData = new HashMap<>();
    private List<Item> AvaliableItems;
    private List<IDiscount> AvailableDiscounts = new ArrayList<>();
    private ISaveLoader<Order> saveLoaderOrders;
    private ISaveLoader<Item> saveLoaderItems;
    private ISaveLoader<Customer> saveLoaderCustomers;
    private ISaveLoader<IDiscount> saveLoaderDiscounts;

    public CoffeeShopManager() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public CoffeeShopManager(List<Customer> customers, List<Item> items, List<Order> orders) {
        this.AvaliableItems = items;

        // 1. Initialize Customer Data
        for (Customer customer : customers) {
            CustomerData.put(customer, new Bill(customer));
        }

        // 2. Link Orders to Bills (Fail fast if customer is missing)
        for (Order order : orders) {
            Bill bill = CustomerData.get(order._customer);
            if (bill == null) {
                throw new CustomerNotFoundException("Customer not real: " + order._customer);
            }
            bill.addOrder(order);
        }

        // 3. Centralized Path & Loader Configuration
        initializeSaveLoaders();
    }

    public List<Customer> getCustomers() {
        return new ArrayList<>(CustomerData.keySet());
    }

    public List<Item> getAvaliableItems() {
        return AvaliableItems;
    }

    public void setAvaliableItems(List<Item> avaliableItems) {
        AvaliableItems = avaliableItems;
    }

    public List<IDiscount> getAvailableDiscounts() {
        return AvailableDiscounts;
    }

    public void setAvailableDiscounts(List<IDiscount> avaliableDiscounts) {
        AvailableDiscounts = avaliableDiscounts;
    }

    private void initializeSaveLoaders() {
        Path dataDir = Paths.get(DATA_DIR);

        // Ensure directory exists (Recursive creation + specific logging)
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
                Logger.getInstance().log("Data folder created: " + dataDir);
            }
        } catch (IOException e) {
            Logger.getInstance().log("CRITICAL: Data folder could not be created: " + dataDir);
        }

        Logger.getInstance().log("Configured SaveLoaders with directory: " + dataDir.toAbsolutePath());

        // Helper to resolve paths once
        String custPath = dataDir.resolve(CUSTOMERS_CSV).toString();
        String itemPath = dataDir.resolve(ITEMS_CSV).toString();
        String ordPath = dataDir.resolve(ORDERS_CSV).toString();
        String discPath = dataDir.resolve(DISCOUNTS_CSV).toString();

        this.saveLoaderCustomers = new SaveLoaderCustomers(custPath, custPath);
        this.saveLoaderItems = new SaveLoaderItems(itemPath, itemPath);
        this.saveLoaderDiscounts = new SaveLoaderDiscounts(discPath, discPath);
        this.saveLoaderOrders = new SaveLoaderOrders(ordPath, ordPath);
    }

    public void LoadData() {
        Logger.getInstance().log("Loading data...");
        List<Customer> customers = saveLoaderCustomers.LoadData();
        List<Item> items = saveLoaderItems.LoadData();

        List<IDiscount> loadeddiscounts = saveLoaderDiscounts.LoadData();

        List<Order> loadedorders = saveLoaderOrders.LoadData();
        Logger.getInstance().log("Loaded customers: " + customers.size());
        Logger.getInstance().log("Loaded items: " + items.size());
        Logger.getInstance().log("Loaded orders: " + loadedorders.size());
        Logger.getInstance().log("Loaded discounts: " + loadeddiscounts.size());

        AvaliableItems = items;
        for (Customer customer : customers) {
            Bill newBill = new Bill(customer);
            CustomerData.put(customer, newBill);
        }

        // Link orders
        List<Order> orders = new ArrayList<>();
        int skippedOrders = 0;
        for (Order order : loadedorders) {
            if (!CustomerData.containsKey(order.getCustomer())) {
                skippedOrders++;
                continue;
            }
            if (!AvaliableItems.contains(order.getItem())) {
                skippedOrders++;
                continue;
            }

            Customer customer = CustomerData.keySet().stream()
                    .filter(o -> o.equals(order.getCustomer()))
                    .findFirst()
                    .orElse(null);

            Item item = getAvaliableItems().stream()
                    .filter(o -> o.equals(order.getItem()))
                    .findFirst() // Returns an Optional<Order>
                    .orElse(null);

            orders.add(new Order(item, customer));
        }

        for (Order order : orders) {
            if (!CustomerData.containsKey(order._customer)) {
                throw new CustomerNotFoundException("Customer not real");
            }

            CustomerData.get(order._customer).addOrder(order);
        }
        Logger.getInstance().log("Linked orders: " + orders.size());
        if (skippedOrders > 0) {
            Logger.getInstance().log("Skipped orders: " + skippedOrders);
        }

        // Discount linking
        int linkedDiscounts = 0;
        for (IDiscount loaded : loadeddiscounts) {
            IDiscount linked = loaded.linkToRealItems(getAvaliableItems());

            if (linked != null) {
                CreateDiscount(linked);
                linkedDiscounts++;
            }
        }
        Logger.getInstance().log("Linked discounts: " + linkedDiscounts);
    }

    public void SaveData() {
        try {
            Logger.getInstance().log("Saving data...");
            List<Customer> customers = getCustomers();
            List<Item> items = getAvaliableItems();
            List<Order> orders = getOrders();
            List<IDiscount> discounts = getAvailableDiscounts();
            Logger.getInstance().log("\tCustomers: " + customers.size());
            Logger.getInstance().log("\tItems: " + items.size());
            Logger.getInstance().log("\tOrders: " + orders.size());
            Logger.getInstance().log("\tDiscounts: " + discounts.size());
            saveLoaderCustomers.SaveData(customers);
            saveLoaderItems.SaveData(items);
            saveLoaderOrders.SaveData(orders);
            saveLoaderDiscounts.SaveData(discounts);
            Logger.getInstance().log("Save complete.");
        } catch (SaveLoaderException e) {
            Logger.getInstance().log("Save failed: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    private List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        for (Bill bill : CustomerData.values()) {
            orders.addAll(bill.Orders);
        }
        return orders;
    }

    public List<Order> GetCustomerOrders(Customer customer) {
        if (!CustomerData.containsKey(customer))
            throw new CustomerNotFoundException("Customer not found");

        return CustomerData.get(customer).Orders;

    }

    public void submitOrder(Order order) {

    }

    public UUID addServer() {
        UUID id = UUID.randomUUID();
        Server server = new Server(id, orderQueue, processedOrders);
        Future<?> controller = executorService.submit(server);
        serverStatusMap.put(id, server);
        activeServers.put(id, controller);
        Logger.getInstance().log("Server " + id + " added");

        return id;
    }

    public void removeServer(UUID id) {
        Future<?> controller = activeServers.get(id);
        if (controller != null) {
            // interrupt() triggers the InterruptedException in your Server's run() method
            controller.cancel(true);
            activeServers.remove(id);
            Logger.getInstance().log("Server " + id + " removed");
            serverStatusMap.remove(id);
        }
    }

    public void Start() {
        orderQueue.startQueue();
        //executorService.shutdown();
    }

    public void sumbitOrder(Order order) {
        orderQueue.addOrder(order);
    }

    public Map<UUID, ServerStatus> getAllProgress() {
        Map<UUID, ServerStatus> report = new HashMap<>();

        serverStatusMap.forEach((id, server) -> {
            // Only report if the thread is still actually running
            Future<?> task = activeServers.get(id);
            if (task != null && !task.isDone()) {
                report.put(id, new ServerStatus(
                        id,
                        server.getProgress(),
                        server.getStatus()
                ));
            }
        });

        return report;
    }

    public void CreateNewOrder(Item item, Customer customer) {

        if (!CustomerData.containsKey(customer))
            throw new CustomerNotFoundException("Customer not found");

        if (!AvaliableItems.contains(item))
            throw new ItemNotFoundException("Item does not exist or isn't available");

        Bill customerBill = CustomerData.get(customer);

        Order newOrder = new Order(item, customer);
        customerBill.addOrder(newOrder);
        sumbitOrder(newOrder);
        Logger.getInstance().log("Order created: " + newOrder);

    }

    public void CreateNewOrder(String itemid, String customerid) {
        Logger.getInstance().log("Creating order from ids: item=" + itemid + ", customer=" + customerid);
        Item item = null;
        for (Item avaliableItem : getAvaliableItems()) {
            if (avaliableItem.equals(new Item(itemid))) {
                item = avaliableItem;
            }
        }
        Customer customer = null;
        for (Customer c : CustomerData.keySet()) {
            if (c.equals(new Customer("", UUID.fromString(customerid)))) {
                customer = c;
            }
        }
        if (item == null)
            throw new ItemNotFoundException("ITEM ID NOT FOUND");

        if (customer == null)
            throw new CustomerNotFoundException("CUSTOMER ID NOT FOUND");

        CreateNewOrder(item, customer);
    }

    public void RemoveOrder(Order order) {
        if (!CustomerData.containsKey(order._customer))
            throw new CustomerNotFoundException("Customer not Found");
        CustomerData.get(order._customer).RemoveOrder(order);
        Logger.getInstance().log("Order removed: " + order);

    }

    public Customer CreateCustomer(String name) {
        Customer newCustomer = new Customer(name);
        Bill newBill = new Bill(newCustomer);
        CustomerData.put(newCustomer, newBill);
        Logger.getInstance().log("Customer created: " + newCustomer);
        return newCustomer;
    }

    public void RemoveCustomer(Customer customer) {
        if (!CustomerData.containsKey(customer))
            throw new CustomerNotFoundException("Customer not Found");
        CustomerData.remove(customer);
        Logger.getInstance().log("Customer removed: " + customer);
    }

    public void CloseoutCustomer(Customer customer, boolean Remove) {
        if (!CustomerData.containsKey(customer))
            throw new CustomerNotFoundException("Customer is not found");
        Logger.getInstance().log("Closing out customer: " + customer + " (remove=" + Remove + ")");

        Bill bill = CustomerData.get(customer);

        bill.GetTotalCost(AvailableDiscounts);

        if (Remove)
            RemoveCustomer(customer);

    }

    public Bill GetCustomerBill(Customer customer) {
        if (!CustomerData.containsKey(customer))
            throw new CustomerNotFoundException("Customer not found");

        return CustomerData.get(customer);
    }

    public Bill.BillInfo GetCustomerBillInfo(Customer customer) {
        return GetCustomerBill(customer).GetTotalCostInfo(AvailableDiscounts);
    }

    public void AddItem(Item item) {
        AvaliableItems.add(item);
        Logger.getInstance().log("Item added: " + item);
    }

    public void RemoveItem(Item item) {
        AvaliableItems.remove(item);
        Logger.getInstance().log("Item removed: " + item);
    }

    public void CreateDiscount(IDiscount discount) {
        AvailableDiscounts.add(discount);
        Logger.getInstance().log("Discount added: " + discount);
    }

    public void RemoveDiscount(IDiscount discount) {
        AvailableDiscounts.remove(discount);
        Logger.getInstance().log("Discount removed: " + discount);
    }

    public record ServerStatus(UUID id, float progress, String status) {
    }

}
