package CoffeeShop.GUI;

import CoffeeShop.*;
import CoffeeShop.Discounts.IDiscount;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * GUI for Coffee Shop Management System
 * Provides an interface to manage customers, items, orders, and billing
 */
public class GUI {
    private JFrame mainFrame;
    private CoffeeShopManager manager;
    private JTable customersTable;
    private JTable ordersTable;
    private JLabel billTotalLabel;
    private JLabel discountInfoLabel;
    private JComboBox<Item> itemsCombo;
    private DefaultTableModel customersModel;
    private DefaultTableModel ordersModel;
    private JPanel billDetailsPanel;

    public GUI(CoffeeShopManager manager) {
        this.manager = manager;
        initComponents();
    }

    private void initComponents() {
        mainFrame = new JFrame("Coffee Shop Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 700);
        mainFrame.setLocationRelativeTo(null);

        // Create main panels
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left panel: Customers list
        JPanel leftPanel = createCustomersPanel();
        
        // Center panel: Orders and Items
        JPanel centerPanel = createOrdersPanel();
        
        // Right panel: Bill details
        JPanel rightPanel = createBillPanel();

        // Add panels to main panel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // Add main panel to frame
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
    }

    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Customers"));
        panel.setPreferredSize(new Dimension(300, 600));

        // Create table model
        customersModel = new DefaultTableModel(new String[]{"Customer Name"}, 0);
        customersTable = new JTable(customersModel);
        customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onCustomerSelected();
            }
        });

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(customersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        JButton addCustomerBtn = new JButton("Add Customer");
        addCustomerBtn.addActionListener(e -> onAddCustomer());
        buttonPanel.add(addCustomerBtn);

        JButton removeCustomerBtn = new JButton("Remove Customer");
        removeCustomerBtn.addActionListener(e -> onRemoveCustomer());
        buttonPanel.add(removeCustomerBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshCustomersTable());
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Initial load
        refreshCustomersTable();

        return panel;
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Orders and Items"));

        // Top panel: Item selector and add order button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Add Order"));
        
        topPanel.add(new JLabel("Select Item:"));
        itemsCombo = new JComboBox<>();
        itemsCombo.setPreferredSize(new Dimension(200, 30));
        refreshItemsCombo();
        topPanel.add(itemsCombo);

        JButton addOrderBtn = new JButton("Add Order");
        addOrderBtn.addActionListener(e -> onAddOrder());
        topPanel.add(addOrderBtn);

        panel.add(topPanel, BorderLayout.NORTH);

        // Center panel: Orders table
        ordersModel = new DefaultTableModel(
            new String[]{"Item ID", "Category", "Price", "Description"}, 0
        );
        ordersTable = new JTable(ordersModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel: Remove order button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton removeOrderBtn = new JButton("Remove Selected Order");
        removeOrderBtn.addActionListener(e -> onRemoveOrder());
        bottomPanel.add(removeOrderBtn);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBillPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Bill Details"));
        panel.setPreferredSize(new Dimension(350, 600));

        // Scrollable bill details panel
        billDetailsPanel = new JPanel();
        billDetailsPanel.setLayout(new BoxLayout(billDetailsPanel, BoxLayout.Y_AXIS));
        billDetailsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(billDetailsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        JButton calculateBillBtn = new JButton("Calculate Bill");
        calculateBillBtn.addActionListener(e -> onCalculateBill());
        bottomPanel.add(calculateBillBtn);

        JButton closeoutBtn = new JButton("Closeout Customer");
        closeoutBtn.addActionListener(e -> onCloseoutCustomer());
        bottomPanel.add(closeoutBtn);

        JButton saveBtn = new JButton("Save Data");
        saveBtn.addActionListener(e -> onSaveData());
        bottomPanel.add(saveBtn);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void onCustomerSelected() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow >= 0) {
            String customerName = (String) customersModel.getValueAt(selectedRow, 0);
            Customer selectedCustomer = manager.getCustomers().stream()
                .filter(c -> c.name.equals(customerName))
                .findFirst()
                .orElse(null);

            if (selectedCustomer != null) {
                refreshOrdersTable(selectedCustomer);
                refreshBillPanel(selectedCustomer);
            }
        }
    }

    private void refreshOrdersTable(Customer customer) {
        while (ordersModel.getRowCount() > 0) {
            ordersModel.removeRow(0);
        }

        List<Order> orders = manager.GetCustomerOrders(customer);
        for (Order order : orders) {
            Item item = order.getItem();
            ordersModel.addRow(new Object[]{
                item.getID(),
                item.getCategory(),
                String.format("£%.2f", item.getCost()),
                item.getDescription()
            });
        }
    }

    private void refreshBillPanel(Customer customer) {
        billDetailsPanel.removeAll();
        
        Bill bill = manager.GetCustomerBill(customer);
        JLabel customerLabel = new JLabel("Customer: " + customer.name);
        customerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        billDetailsPanel.add(customerLabel);

        JLabel subtotalLabel = new JLabel(String.format("Subtotal: £%.2f", bill.GetCost()));
        billDetailsPanel.add(subtotalLabel);

        billDetailsPanel.add(Box.createVerticalStrut(10));

        JLabel discountsHeader = new JLabel("Available Discounts:");
        discountsHeader.setFont(new Font("Arial", Font.BOLD, 12));
        billDetailsPanel.add(discountsHeader);

        List<IDiscount> discounts = manager.getAvailableDiscounts();
        if (discounts != null && !discounts.isEmpty()) {
            for (IDiscount discount : discounts) {
                JLabel discountLabel = new JLabel("- " + discount.toString());
                billDetailsPanel.add(discountLabel);
            }
        } else {
            JLabel noDiscountsLabel = new JLabel("  (No discounts available)");
            noDiscountsLabel.setForeground(Color.GRAY);
            billDetailsPanel.add(noDiscountsLabel);
        }

        billTotalLabel = new JLabel("Total: £0.00");
        billTotalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        billTotalLabel.setForeground(new Color(0, 100, 0));
        billDetailsPanel.add(Box.createVerticalStrut(20));
        billDetailsPanel.add(billTotalLabel);

        billDetailsPanel.revalidate();
        billDetailsPanel.repaint();
    }

    private void refreshCustomersTable() {
        while (customersModel.getRowCount() > 0) {
            customersModel.removeRow(0);
        }

        List<Customer> customers = manager.getCustomers();
        for (Customer customer : customers) {
            customersModel.addRow(new Object[]{customer.name});
        }
    }

    private void refreshItemsCombo() {
        itemsCombo.removeAllItems();
        List<Item> items = manager.getAvaliableItems();
        if (items != null) {
            for (Item item : items) {
                itemsCombo.addItem(item);
            }
        }
    }

    private void onAddCustomer() {
        String name = JOptionPane.showInputDialog(mainFrame, "Enter customer name:", "Add Customer", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            try {
                manager.CreateCustomer(name.trim());
                refreshCustomersTable();
                JOptionPane.showMessageDialog(mainFrame, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onRemoveCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow >= 0) {
            String customerName = (String) customersModel.getValueAt(selectedRow, 0);
            Customer customerToRemove = manager.getCustomers().stream()
                .filter(c -> c.name.equals(customerName))
                .findFirst()
                .orElse(null);

            if (customerToRemove != null) {
                int confirm = JOptionPane.showConfirmDialog(mainFrame, 
                    "Are you sure you want to remove " + customerName + "?", 
                    "Confirm Delete", 
                    JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        manager.RemoveCustomer(customerToRemove);
                        refreshCustomersTable();
                        billDetailsPanel.removeAll();
                        ordersModel.setRowCount(0);
                        JOptionPane.showMessageDialog(mainFrame, "Customer removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a customer first", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onAddOrder() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow >= 0) {
            String customerName = (String) customersModel.getValueAt(selectedRow, 0);
            Customer selectedCustomer = manager.getCustomers().stream()
                .filter(c -> c.name.equals(customerName))
                .findFirst()
                .orElse(null);

            if (selectedCustomer != null && itemsCombo.getSelectedItem() != null) {
                try {
                    Item selectedItem = (Item) itemsCombo.getSelectedItem();
                    manager.CreateNewOrder(selectedItem, selectedCustomer);
                    refreshOrdersTable(selectedCustomer);
                    refreshBillPanel(selectedCustomer);
                    JOptionPane.showMessageDialog(mainFrame, "Order added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a customer first", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onRemoveOrder() {
        int selectedRowCustomer = customersTable.getSelectedRow();
        int selectedRowOrder = ordersTable.getSelectedRow();

        if (selectedRowCustomer >= 0 && selectedRowOrder >= 0) {
            String customerName = (String) customersModel.getValueAt(selectedRowCustomer, 0);
            Customer selectedCustomer = manager.getCustomers().stream()
                .filter(c -> c.name.equals(customerName))
                .findFirst()
                .orElse(null);

            if (selectedCustomer != null) {
                try {
                    List<Order> orders = manager.GetCustomerOrders(selectedCustomer);
                    if (selectedRowOrder < orders.size()) {
                        Order orderToRemove = orders.get(selectedRowOrder);
                        manager.RemoveOrder(orderToRemove);
                        refreshOrdersTable(selectedCustomer);
                        refreshBillPanel(selectedCustomer);
                        JOptionPane.showMessageDialog(mainFrame, "Order removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select both a customer and an order", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onCalculateBill() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow >= 0) {
            String customerName = (String) customersModel.getValueAt(selectedRow, 0);
            Customer selectedCustomer = manager.getCustomers().stream()
                .filter(c -> c.name.equals(customerName))
                .findFirst()
                .orElse(null);

            if (selectedCustomer != null) {
                try {
                    Bill.BillInfo billInfo = manager.GetCustomerBillInfo(selectedCustomer);
                    String totalCost = String.format("£%.2f", billInfo.FinalCost());
                    billTotalLabel.setText("Total: " + totalCost);
                    
                    StringBuilder discountInfo = new StringBuilder("Applied Discounts:\n");
                    for (IDiscount discount : billInfo.DiscountsUsed()) {
                        discountInfo.append("- ").append(discount.toString()).append("\n");
                    }
                    
                    JOptionPane.showMessageDialog(mainFrame, 
                        discountInfo.toString() + "\nFinal Total: " + totalCost, 
                        "Bill Calculation", 
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a customer first", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onCloseoutCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow >= 0) {
            String customerName = (String) customersModel.getValueAt(selectedRow, 0);
            Customer selectedCustomer = manager.getCustomers().stream()
                .filter(c -> c.name.equals(customerName))
                .findFirst()
                .orElse(null);

            if (selectedCustomer != null) {
                int confirm = JOptionPane.showConfirmDialog(mainFrame, 
                    "Close out customer " + customerName + " and remove from system?", 
                    "Confirm Closeout", 
                    JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        manager.CloseoutCustomer(selectedCustomer, true);
                        refreshCustomersTable();
                        billDetailsPanel.removeAll();
                        ordersModel.setRowCount(0);
                        JOptionPane.showMessageDialog(mainFrame, "Customer closed out successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a customer first", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onSaveData() {
        try {
            manager.SaveData();
            JOptionPane.showMessageDialog(mainFrame, "Data saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, "Error saving data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void show() {
        mainFrame.setVisible(true);
    }
}
