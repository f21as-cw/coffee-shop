package CoffeeShop.GUI;

import CoffeeShop.*;
import CoffeeShop.Discounts.DiscountMealDeal;
import CoffeeShop.Discounts.DiscountPercentage;
import CoffeeShop.Discounts.DiscountX4X;
import CoffeeShop.Discounts.IDiscount;
import CoffeeShop.Exceptions.InvalidDiscountException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * GUI for Coffee Shop Management System
 * Provides an interface to manage customers, items, orders, and billing
 */
public class GUI {
    private static final Path APP_LOG_PATH = Paths.get("app.log");
    private static final Path REPORT_PATH = Paths.get("simulation_report.txt");
    private final CoffeeShopManager manager;
    private JFrame mainFrame;
    private JTable customersTable;
    private JTable ordersTable;
    private JTable itemsTable;
    private JTable serversTable;
    private JTable queueTable;
    private JLabel billTotalLabel;
    private JComboBox<Item> itemsCombo;
    private DefaultTableModel customersModel;
    private DefaultTableModel ordersModel;
    private DefaultTableModel itemsModel;
    private DefaultTableModel discountsModel;
    private DefaultTableModel serversModel;
    private DefaultTableModel queueModel;
    private JButton startQueueBtn;
    private JLabel queueStateValueLabel;
    private JLabel activeServersValueLabel;
    private JLabel pendingOrdersValueLabel;
    private JLabel processedOrdersValueLabel;
    private JCheckBox autoExitWhenDoneCheck;
    private JTextArea eventLogArea;
    private javax.swing.Timer serverRefreshTimer;
    private JPanel billDetailsPanel;
    private boolean queueStarted = false;
    private boolean existingOrdersQueued = false;
    private boolean completionReported = false;
    private Instant simulationStartedAt;

    public GUI(CoffeeShopManager manager) {
        this.manager = manager;
        initComponents();
    }

    private void initComponents() {
        mainFrame = new JFrame("Coffee Shop Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1400, 750);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (serverRefreshTimer != null) {
                    serverRefreshTimer.stop();
                }
                if (queueStarted && !completionReported) {
                    writeSimulationReport();
                }
                Logger.getInstance().log("GUI window closing.");
            }
        });

        // Create tabbed pane for main interface
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Order Management
        JPanel orderPanel = createOrderManagementPanel();
        tabbedPane.addTab("Orders", orderPanel);

        // Tab 2: Discounts Management
        JPanel discountsPanel = createDiscountsPanel();
        tabbedPane.addTab("Manage Discounts", discountsPanel);

        // Tab 3: Items Management
        JPanel itemsPanel = createItemsManagementPanel();
        tabbedPane.addTab("Manage Items", itemsPanel);

        // Tab 4: Server Management
        JPanel serverPanel = createServerManagementPanel();
        tabbedPane.addTab("Server Control", serverPanel);

        mainFrame.add(tabbedPane);
        mainFrame.setVisible(true);
    }

    private JPanel createOrderManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left panel: Customers list
        JPanel leftPanel = createCustomersPanel();

        // Center panel: Orders and Items
        JPanel centerPanel = createOrdersPanel();

        // Right panel: Bill details
        JPanel rightPanel = createBillPanel();

        // Add panels to main panel
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createServerManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel help = new JLabel("Add servers first, then click Start Queue to let orders be processed.");
        topPanel.add(help, BorderLayout.NORTH);

        JPanel summaryPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Queue Summary"));
        summaryPanel.add(new JLabel("Queue State:"));
        queueStateValueLabel = new JLabel("Not started");
        summaryPanel.add(queueStateValueLabel);

        summaryPanel.add(new JLabel("Active Servers:"));
        activeServersValueLabel = new JLabel("0");
        summaryPanel.add(activeServersValueLabel);

        summaryPanel.add(new JLabel("Pending Orders:"));
        pendingOrdersValueLabel = new JLabel("0");
        summaryPanel.add(pendingOrdersValueLabel);

        summaryPanel.add(new JLabel("Processed Orders:"));
        processedOrdersValueLabel = new JLabel("0");
        summaryPanel.add(processedOrdersValueLabel);

        topPanel.add(summaryPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        serversModel = new DefaultTableModel(
                new String[]{"Server ID", "Status", "Progress", "Processed"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        serversTable = new JTable(serversModel);
        serversTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        queueModel = new DefaultTableModel(
                new String[]{"Position", "Customer", "Item", "Duration (s)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        queueTable = new JTable(queueModel);
        queueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel staffPanel = new JPanel(new BorderLayout());
        staffPanel.setBorder(BorderFactory.createTitledBorder("Serving Staff"));
        staffPanel.add(new JScrollPane(serversTable), BorderLayout.CENTER);

        JPanel queuePanel = new JPanel(new BorderLayout());
        queuePanel.setBorder(BorderFactory.createTitledBorder("Orders Waiting In Queue"));
        queuePanel.add(new JScrollPane(queueTable), BorderLayout.CENTER);

        JPanel livePanel = new JPanel(new GridLayout(1, 2, 10, 10));
        livePanel.add(staffPanel);
        livePanel.add(queuePanel);

        eventLogArea = new JTextArea();
        eventLogArea.setEditable(false);
        eventLogArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Live Event Log"));
        logPanel.add(new JScrollPane(eventLogArea), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, livePanel, logPanel);
        splitPane.setResizeWeight(0.7);
        panel.add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton addServerBtn = new JButton("Add Server");
        addServerBtn.addActionListener(e -> onAddServer());
        buttonPanel.add(addServerBtn);

        JButton removeServerBtn = new JButton("Remove Selected Server");
        removeServerBtn.addActionListener(e -> onRemoveServer());
        buttonPanel.add(removeServerBtn);

        startQueueBtn = new JButton("Start Queue");
        startQueueBtn.addActionListener(e -> onStartQueue());
        buttonPanel.add(startQueueBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshServersTable());
        buttonPanel.add(refreshBtn);

        JButton reportBtn = new JButton("Generate Report");
        reportBtn.addActionListener(e -> onGenerateReport());
        buttonPanel.add(reportBtn);

        buttonPanel.add(new JLabel("Sim Speed"));
        JTextField simSpeed = new JTextField(String.valueOf(CoffeeShopManager.SimSpeed), 5);
        simSpeed.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void update() {
                try {
                    String text = simSpeed.getText();
                    if (!text.isEmpty()) {
                        CoffeeShopManager.SimSpeed = Float.parseFloat(text);
                        System.out.println(CoffeeShopManager.SimSpeed);
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid input while typing
                }
            }
        });
        buttonPanel.add(simSpeed);


        autoExitWhenDoneCheck = new JCheckBox("Exit when simulation completes");
        autoExitWhenDoneCheck.setSelected(true);
        buttonPanel.add(autoExitWhenDoneCheck);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshServersTable();
        serverRefreshTimer = new javax.swing.Timer(100, e -> refreshServersTable());
        serverRefreshTimer.start();

        return panel;
    }

    private void refreshServersTable() {
        if (serversModel == null) {
            return;
        }

        String selectedId = null;
        if (serversTable != null && serversTable.getSelectedRow() >= 0) {
            selectedId = (String) serversModel.getValueAt(serversTable.getSelectedRow(), 0);
        }

        serversModel.setRowCount(0);

        Map<UUID, CoffeeShopManager.ServerStatus> progressMap;
        try {
            progressMap = manager.getAllProgress();
        } catch (Exception ex) {
            return;
        }

        Map<UUID, Integer> processedByServer = getProcessedOrderCountsByServer();
        Set<UUID> allServerIds = new HashSet<>();
        allServerIds.addAll(progressMap.keySet());
        allServerIds.addAll(processedByServer.keySet());

        List<UUID> ordered = new ArrayList<>(allServerIds);
        ordered.sort(Comparator.comparing(UUID::toString));

        for (UUID id : ordered) {
            CoffeeShopManager.ServerStatus status = progressMap.get(id);
            int processed = processedByServer.getOrDefault(id, 0);
            serversModel.addRow(new Object[]{
                    id.toString(),
                    status != null ? status.status() : "Removed",
                    status != null ? String.format("%.0f%%", status.progress() * 100.0f) : "0%",
                    processed
            });
        }

        updateServerSummary(progressMap, processedByServer);
        refreshQueueTable();
        refreshEventLog();
        checkSimulationCompletion(progressMap, processedByServer);

        if (selectedId != null) {
            for (int i = 0; i < serversModel.getRowCount(); i++) {
                if (selectedId.equals(serversModel.getValueAt(i, 0))) {
                    serversTable.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
    }

    private void updateServerSummary(
            Map<UUID, CoffeeShopManager.ServerStatus> progressMap,
            Map<UUID, Integer> processedByServer
    ) {
        int pending = getPendingOrderCount();
        if (queueStateValueLabel != null) {
            if (!queueStarted) {
                queueStateValueLabel.setText("Not started");
            } else if (pending == 0 && progressMap.isEmpty()) {
                queueStateValueLabel.setText("Completed");
            } else {
                queueStateValueLabel.setText("Started");
            }
        }
        if (activeServersValueLabel != null) {
            activeServersValueLabel.setText(String.valueOf(progressMap.size()));
        }
        if (pendingOrdersValueLabel != null) {
            pendingOrdersValueLabel.setText(pending >= 0 ? String.valueOf(pending) : "N/A");
        }
        if (processedOrdersValueLabel != null) {
            int totalProcessed = processedByServer.values().stream().mapToInt(Integer::intValue).sum();
            processedOrdersValueLabel.setText(String.valueOf(totalProcessed));
        }
    }

    private Map<UUID, Integer> getProcessedOrderCountsByServer() {
        Map<UUID, Integer> result = new HashMap<>();
        try {
            Field processedOrdersField = CoffeeShopManager.class.getDeclaredField("processedOrders");
            processedOrdersField.setAccessible(true);
            Object processedOrdersObj = processedOrdersField.get(manager);

            Method getHashMapMethod = processedOrdersObj.getClass().getMethod("getHashMap");
            Object rawMap = getHashMapMethod.invoke(processedOrdersObj);

            if (rawMap instanceof Map<?, ?> map) {
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (entry.getKey() instanceof UUID id && entry.getValue() instanceof List<?> orders) {
                        result.put(id, orders.size());
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    private int getPendingOrderCount() {
        try {
            Field orderQueueField = CoffeeShopManager.class.getDeclaredField("orderQueue");
            orderQueueField.setAccessible(true);
            Object orderQueueObj = orderQueueField.get(manager);

            Field queueField = orderQueueObj.getClass().getDeclaredField("queue");
            queueField.setAccessible(true);
            Object rawQueue = queueField.get(orderQueueObj);

            if (rawQueue instanceof java.util.Queue<?> queue) {
                return queue.size();
            }
        } catch (Exception ignored) {
        }
        return -1;
    }

    private List<Order> getExistingOrdersNotYetQueued() {
        List<Order> allOrders = new ArrayList<>();
        for (Customer customer : manager.getCustomers()) {
            try {
                allOrders.addAll(manager.GetCustomerOrders(customer));
            } catch (Exception ignored) {
            }
        }

        Set<Order> pendingSet = new HashSet<>(getPendingOrdersSnapshot());
        List<Order> toQueue = new ArrayList<>();
        for (Order order : allOrders) {
            if (!pendingSet.contains(order)) {
                toQueue.add(order);
            }
        }
        return toQueue;
    }

    private List<Order> getPendingOrdersSnapshot() {
        List<Order> result = new ArrayList<>();
        try {
            Field orderQueueField = CoffeeShopManager.class.getDeclaredField("orderQueue");
            orderQueueField.setAccessible(true);
            Object orderQueueObj = orderQueueField.get(manager);

            Field queueField = orderQueueObj.getClass().getDeclaredField("queue");
            queueField.setAccessible(true);
            Object rawQueue = queueField.get(orderQueueObj);

            if (rawQueue instanceof java.util.Queue<?> queue) {
                synchronized (queue) {
                    for (Object obj : queue) {
                        if (obj instanceof Order order) {
                            result.add(order);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    private void refreshQueueTable() {
        if (queueModel == null) {
            return;
        }

        queueModel.setRowCount(0);
        List<Order> pendingOrders = getPendingOrdersSnapshot();
        for (int i = 0; i < pendingOrders.size(); i++) {
            Order order = pendingOrders.get(i);
            queueModel.addRow(new Object[]{
                    i + 1,
                    order.getCustomer().name(),
                    order.getItem().getID(),
                    order.getItem().getDuration()
            });
        }
    }

    private void refreshEventLog() {
        if (eventLogArea == null) {
            return;
        }

        if (!Files.exists(APP_LOG_PATH)) {
            eventLogArea.setText("No app.log file yet.");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(APP_LOG_PATH);
            int start = Math.max(0, lines.size() - 120);
            String content = String.join(System.lineSeparator(), lines.subList(start, lines.size()));
            eventLogArea.setText(content);
            eventLogArea.setCaretPosition(eventLogArea.getDocument().getLength());
        } catch (IOException ex) {
            eventLogArea.setText("Failed to read app.log: " + ex.getMessage());
        }
    }

    private void checkSimulationCompletion(
            Map<UUID, CoffeeShopManager.ServerStatus> progressMap,
            Map<UUID, Integer> processedByServer
    ) {
        if (!queueStarted || completionReported) {
            return;
        }

        int pending = getPendingOrderCount();
        if (pending == 0 && progressMap.isEmpty()) {
            completionReported = true;
            Path reportPath = writeSimulationReport();
            String message = "Queue is empty. Simulation complete.\nReport saved to:\n" + reportPath.toAbsolutePath();

            if (autoExitWhenDoneCheck != null && autoExitWhenDoneCheck.isSelected()) {
                JOptionPane.showMessageDialog(mainFrame, message, "Simulation Complete", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.dispose();
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(mainFrame, message, "Simulation Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void onAddServer() {
//        if (queueStarted) {
//            JOptionPane.showMessageDialog(mainFrame, "Queue already started. Add servers before starting queue.", "Warning", JOptionPane.WARNING_MESSAGE);
//            return;
//        }

        try {
            UUID id = manager.addServer();
            refreshServersTable();
            //JOptionPane.showMessageDialog(mainFrame, "Server added: " + id, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRemoveServer() {
        if (serversTable == null || serversTable.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a server first", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String serverId = (String) serversModel.getValueAt(serversTable.getSelectedRow(), 0);
            manager.removeServer(UUID.fromString(serverId));
            refreshServersTable();
            JOptionPane.showMessageDialog(mainFrame, "Server removed: " + serverId, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onStartQueue() {
        if (queueStarted) {
            JOptionPane.showMessageDialog(mainFrame, "Queue is already started.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (startQueueBtn != null && !startQueueBtn.isEnabled()) {
            return;
        }

        if (startQueueBtn != null) {
            startQueueBtn.setEnabled(false);
        }
        if (queueStateValueLabel != null) {
            queueStateValueLabel.setText("Preparing queue...");
        }

        completionReported = false;
        simulationStartedAt = Instant.now();

        if (!existingOrdersQueued) {
            existingOrdersQueued = true;
            List<Order> startupOrders = getExistingOrdersNotYetQueued();

            Thread feeder = new Thread(() -> {
                for (Order order : startupOrders) {
                    manager.sumbitOrder(order);
                    Logger.getInstance().log("Order queued from startup data: " + order);
//                    try {
//                        Thread.sleep(1);
//                    } catch (InterruptedException ignored) {
//                        Thread.currentThread().interrupt();
//                        break;
//                    }
                }

                SwingUtilities.invokeLater(() -> startQueueProcessing(startupOrders.size()));
            }, "startup-order-feeder");
            feeder.setDaemon(true);
            feeder.start();
            return;
        }

        startQueueProcessing(0);
    }

    private void startQueueProcessing(int primedOrders) {
        try {
            manager.Start();
            queueStarted = true;
            refreshServersTable();
            String message = "Queue started. Servers can now process queued orders.";
            if (primedOrders > 0) {
                message += "\nPrimed startup orders: " + primedOrders;
            }
            JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            if (startQueueBtn != null) {
                startQueueBtn.setEnabled(true);
            }
            JOptionPane.showMessageDialog(mainFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onGenerateReport() {
        Path reportPath = writeSimulationReport();
        JOptionPane.showMessageDialog(
                mainFrame,
                "Report saved to:\n" + reportPath.toAbsolutePath(),
                "Report Generated",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private Path writeSimulationReport() {
        Map<UUID, Integer> processedByServer = getProcessedOrderCountsByServer();
        int totalProcessed = processedByServer.values().stream().mapToInt(Integer::intValue).sum();
        int pending = getPendingOrderCount();

        StringBuilder report = new StringBuilder();
        report.append("Coffee Shop Simulation Report").append(System.lineSeparator());
        report.append("Generated At: ").append(Instant.now()).append(System.lineSeparator());
        report.append("Queue State: ").append(queueStarted ? "Started" : "Not started").append(System.lineSeparator());
        if (simulationStartedAt != null) {
            long seconds = Duration.between(simulationStartedAt, Instant.now()).getSeconds();
            report.append("Elapsed Time: ").append(seconds).append("s").append(System.lineSeparator());
        }
        report.append("Pending Orders: ").append(pending >= 0 ? pending : "N/A").append(System.lineSeparator());
        report.append("Processed Orders: ").append(totalProcessed).append(System.lineSeparator());
        report.append(System.lineSeparator());
        report.append("Processed by Server:").append(System.lineSeparator());

        List<UUID> serverIds = new ArrayList<>(processedByServer.keySet());
        serverIds.sort(Comparator.comparing(UUID::toString));
        for (UUID id : serverIds) {
            report.append("- ").append(id).append(": ").append(processedByServer.get(id)).append(System.lineSeparator());
        }

        report.append(System.lineSeparator());
        report.append("Log file: ").append(APP_LOG_PATH.toAbsolutePath()).append(System.lineSeparator());

        try {
            Files.writeString(REPORT_PATH, report.toString());
        } catch (IOException ex) {
            Logger.getInstance().log("Failed to write simulation report: " + ex.getMessage());
        }

        return REPORT_PATH;
    }

    private Item createSimulationOrderItem(Item selectedItem) {
        int duration = selectedItem.getDuration();
        String id = selectedItem.getID().toUpperCase();

        if (id.startsWith("DRINK")) {
            duration = ThreadLocalRandom.current().nextInt(2, 5);
        } else if (id.startsWith("MAIN") || id.startsWith("SNACK")) {
            duration = ThreadLocalRandom.current().nextInt(6, 11);
        }

        return new Item(
                selectedItem.getID(),
                selectedItem.getCost(),
                duration,
                selectedItem.getDescription(),
                selectedItem.getIconPath()
        );
    }

    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Customers"));
        panel.setPreferredSize(new Dimension(300, 600));

        // Create table model
        customersModel = new DefaultTableModel(new String[]{"Customer Name", "Customer ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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
        itemsCombo.setRenderer(createItemComboRenderer());
        refreshItemsCombo();
        topPanel.add(itemsCombo);

        JButton addOrderBtn = new JButton("Add Order");
        addOrderBtn.addActionListener(e -> onAddOrder());
        topPanel.add(addOrderBtn);

        panel.add(topPanel, BorderLayout.NORTH);

        // Center panel: Orders table
        ordersModel = new DefaultTableModel(
                new String[]{"Item ID", "Category", "Price", "Duration (s)", "Description"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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

    private JPanel createItemsManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        itemsModel = new DefaultTableModel(
                new String[]{"Item ID", "Category", "Price", "Duration (s)", "Description"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        itemsTable = new JTable(itemsModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton addItemBtn = new JButton("Add Item");
        addItemBtn.addActionListener(e -> onAddItem());
        buttonPanel.add(addItemBtn);

        JButton removeItemBtn = new JButton("Remove Selected Item");
        removeItemBtn.addActionListener(e -> onRemoveItem());
        buttonPanel.add(removeItemBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshItemsTable());
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshItemsTable();
        return panel;
    }

    private ListCellRenderer<? super Item> createItemComboRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );

                if (value instanceof Item item) {
                    label.setText(String.format(
                            "%s | %s | £%.2f | %ds",
                            item.getID(),
                            item.getCategory(),
                            item.getCost(),
                            item.getDuration()
                    ));
                }
                return label;
            }
        };
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

    private JPanel createDiscountsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel: Create discount section
        JPanel createPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        createPanel.setBorder(BorderFactory.createTitledBorder("Create New Discount"));

        JButton percentageBtn = new JButton("Percentage Discount");
        percentageBtn.addActionListener(e -> showCreatePercentageDialog());
        createPanel.add(percentageBtn);

        JButton mealDealBtn = new JButton("Meal Deal");
        mealDealBtn.addActionListener(e -> showCreateMealDealDialog());
        createPanel.add(mealDealBtn);

        JButton x4xBtn = new JButton("X for Y Discount");
        x4xBtn.addActionListener(e -> showCreateX4XDialog());
        createPanel.add(x4xBtn);

        panel.add(createPanel, BorderLayout.NORTH);

        // Center panel: Discounts table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Available Discounts"));

        discountsModel = new DefaultTableModel(
                new String[]{"Discount Type", "Details"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable discountsTable = new JTable(discountsModel);
        discountsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        discountsTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(discountsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Refresh discounts table
        refreshDiscountsTable(discountsModel);

        panel.add(tablePanel, BorderLayout.CENTER);

        // Bottom panel: Delete button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton deleteBtn = new JButton("Delete Selected Discount");
        deleteBtn.addActionListener(e -> {
            int selectedRow = discountsTable.getSelectedRow();
            if (selectedRow >= 0) {
                List<IDiscount> discounts = manager.getAvailableDiscounts();
                if (selectedRow < discounts.size()) {
                    try {
                        manager.RemoveDiscount(discounts.get(selectedRow));
                        refreshDiscountsTable(discountsModel);
                        JOptionPane.showMessageDialog(mainFrame, "Discount removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(mainFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Please select a discount to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        bottomPanel.add(deleteBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshDiscountsTable(discountsModel));
        bottomPanel.add(refreshBtn);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showCreatePercentageDialog() {
        JDialog dialog = new JDialog(mainFrame, "Create Percentage Discount", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(mainFrame);

        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel.add(new JLabel("Select Item:"));
        JComboBox<Item> itemCombo = new JComboBox<>();
        List<Item> items = manager.getAvaliableItems();
        if (items != null) {
            for (Item item : items) {
                itemCombo.addItem(item);
            }
        }
        contentPanel.add(itemCombo);

        contentPanel.add(new JLabel("Percentage (0-1):"));
        JTextField percentageField = new JTextField("0.1");
        contentPanel.add(percentageField);

        contentPanel.add(new JLabel(""));
        JButton createBtn = new JButton("Create");
        contentPanel.add(createBtn);

        dialog.add(contentPanel);

        createBtn.addActionListener(e -> {
            try {
                Item selectedItem = (Item) itemCombo.getSelectedItem();
                float percentage = Float.parseFloat(percentageField.getText());

                if (selectedItem != null) {
                    DiscountPercentage discount = new DiscountPercentage(selectedItem, percentage);
                    manager.CreateDiscount(discount);
                    if (discountsModel != null) {
                        refreshDiscountsTable(discountsModel);
                    }
                    JOptionPane.showMessageDialog(dialog, "Discount created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please select an item", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid percentage format", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidDiscountException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void showCreateX4XDialog() {
        JDialog dialog = new JDialog(mainFrame, "Create X for Y Discount", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(mainFrame);

        JPanel contentPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel.add(new JLabel("Select Item:"));
        JComboBox<Item> itemCombo = new JComboBox<>();
        List<Item> items = manager.getAvaliableItems();
        if (items != null) {
            for (Item item : items) {
                itemCombo.addItem(item);
            }
        }
        contentPanel.add(itemCombo);

        contentPanel.add(new JLabel("X (Buy how many):"));
        JTextField xField = new JTextField("3");
        contentPanel.add(xField);

        contentPanel.add(new JLabel("Y (Get discount on):"));
        JTextField yField = new JTextField("1");
        contentPanel.add(yField);

        contentPanel.add(new JLabel(""));
        JButton createBtn = new JButton("Create");
        contentPanel.add(createBtn);

        dialog.add(contentPanel);

        createBtn.addActionListener(e -> {
            try {
                Item selectedItem = (Item) itemCombo.getSelectedItem();
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());

                if (selectedItem != null) {
                    DiscountX4X discount = new DiscountX4X(selectedItem, x, y);
                    manager.CreateDiscount(discount);
                    if (discountsModel != null) {
                        refreshDiscountsTable(discountsModel);
                    }
                    JOptionPane.showMessageDialog(dialog, "Discount created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please select an item", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidDiscountException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void showCreateMealDealDialog() {
        JDialog dialog = new JDialog(mainFrame, "Create Meal Deal", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(mainFrame);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Items selection
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Select Items for Meal Deal"));

        DefaultListModel<Item> listModel = new DefaultListModel<>();
        List<Item> items = manager.getAvaliableItems();
        if (items != null) {
            for (Item item : items) {
                listModel.addElement(item);
            }
        }

        JList<Item> itemsList = new JList<>(listModel);
        itemsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(itemsList);
        itemsPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(itemsPanel, BorderLayout.CENTER);

        // Cost input
        JPanel costPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        costPanel.add(new JLabel("Meal Deal Price (£):"));
        JTextField costField = new JTextField("5.0", 10);
        costPanel.add(costField);

        contentPanel.add(costPanel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton createBtn = new JButton("Create Meal Deal");
        createBtn.addActionListener(e -> {
            try {
                List<Item> selectedItems = itemsList.getSelectedValuesList();
                float cost = Float.parseFloat(costField.getText());

                if (selectedItems.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please select at least one item", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    DiscountMealDeal discount = new DiscountMealDeal(selectedItems, cost);
                    manager.CreateDiscount(discount);
                    if (discountsModel != null) {
                        refreshDiscountsTable(discountsModel);
                    }
                    JOptionPane.showMessageDialog(dialog, "Meal Deal created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid cost format", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidDiscountException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonsPanel.add(createBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonsPanel.add(cancelBtn);

        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    private void refreshDiscountsTable(DefaultTableModel model) {
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }

        List<IDiscount> discounts = manager.getAvailableDiscounts();
        if (discounts != null) {
            for (IDiscount discount : discounts) {
                String type = discount.getClass().getSimpleName();
                String details = getDiscountDetails(discount);
                model.addRow(new Object[]{type, details});
            }
        }
    }

    private String getDiscountDetails(IDiscount discount) {
        if (discount instanceof DiscountPercentage d) {
            return String.format("%s: %.0f%% off", d._item.getID(), d._percentage * 100);
        } else if (discount instanceof DiscountX4X d) {
            return String.format("%s: Buy %d, get %d free", d._item.getID(), d._x, d._y);
        } else if (discount instanceof DiscountMealDeal d) {
            StringBuilder sb = new StringBuilder("Items: ");
            for (Item item : d._items) {
                sb.append(item.getID()).append(", ");
            }
            sb.setLength(sb.length() - 2);
            sb.append(" -> £").append(String.format("%.2f", d._cost));
            return sb.toString();
        }
        return "Unknown";
    }

    private void onCustomerSelected() {
        Customer selectedCustomer = getSelectedCustomer();
        if (selectedCustomer != null) {
            refreshOrdersTable(selectedCustomer);
            refreshBillPanel(selectedCustomer);
        }
    }

    private Customer getSelectedCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        String customerId = (String) customersModel.getValueAt(selectedRow, 1);
        return findCustomerById(customerId);
    }

    private Customer findCustomerById(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return null;
        }

        return manager.getCustomers().stream()
                .filter(c -> c.id().toString().equals(customerId))
                .findFirst()
                .orElse(null);
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
                    item.getDuration(),
                    item.getDescription()
            });
        }
    }

    private void refreshBillPanel(Customer customer) {
        billDetailsPanel.removeAll();

        Bill bill = manager.GetCustomerBill(customer);
        JLabel customerLabel = new JLabel("Customer: " + customer.name());
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
            customersModel.addRow(new Object[]{customer.name(), customer.id().toString()});
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
        Customer customerToRemove = getSelectedCustomer();
        if (customerToRemove != null) {
            String customerName = customerToRemove.name();
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
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a customer first", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onAddOrder() {
        Customer selectedCustomer = getSelectedCustomer();
        if (selectedCustomer != null) {
            if (itemsCombo.getSelectedItem() != null) {
                try {
                    Item selectedItem = (Item) itemsCombo.getSelectedItem();
                    Item simulationItem = createSimulationOrderItem(selectedItem);
                    manager.CreateNewOrder(simulationItem, selectedCustomer);
                    refreshOrdersTable(selectedCustomer);
                    refreshBillPanel(selectedCustomer);
                    refreshServersTable();
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "Order added successfully! Simulated processing time: " + simulationItem.getDuration() + "s",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Please select an item first", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a customer first", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onRemoveOrder() {
        int selectedRowOrder = ordersTable.getSelectedRow();

        Customer selectedCustomer = getSelectedCustomer();
        if (selectedCustomer != null && selectedRowOrder >= 0) {
            try {
                List<Order> orders = manager.GetCustomerOrders(selectedCustomer);
                if (selectedRowOrder < orders.size()) {
                    Order orderToRemove = orders.get(selectedRowOrder);
                    manager.RemoveOrder(orderToRemove);
                    refreshOrdersTable(selectedCustomer);
                    refreshBillPanel(selectedCustomer);
                    refreshServersTable();
                    JOptionPane.showMessageDialog(mainFrame, "Order removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select both a customer and an order", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onCalculateBill() {
        Customer selectedCustomer = getSelectedCustomer();
        if (selectedCustomer != null) {
            try {
                Bill.BillInfo billInfo = manager.GetCustomerBillInfo(selectedCustomer);
                String totalCost = String.format("£%.2f", billInfo.FinalCost());
                billTotalLabel.setText("Total: " + totalCost);

                StringBuilder discountInfo = new StringBuilder("Applied Discounts:\n");
                for (IDiscount discount : billInfo.DiscountsUsed()) {
                    discountInfo.append("- ").append(discount.toString()).append("\n");
                }

                if (billInfo.DiscountsUsed().isEmpty()) {
                    discountInfo.append("- None\n");
                }

                JOptionPane.showMessageDialog(mainFrame,
                        discountInfo + "\nFinal Total: " + totalCost,
                        "Bill Calculation",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a customer first", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onCloseoutCustomer() {
        Customer selectedCustomer = getSelectedCustomer();
        if (selectedCustomer != null) {
            String customerName = selectedCustomer.name();
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
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a customer first", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshItemsTable() {
        if (itemsModel == null) {
            return;
        }

        itemsModel.setRowCount(0);
        List<Item> items = manager.getAvaliableItems();
        if (items == null) {
            return;
        }

        for (Item item : items) {
            itemsModel.addRow(new Object[]{
                    item.getID(),
                    item.getCategory(),
                    String.format("£%.2f", item.getCost()),
                    item.getDuration(),
                    item.getDescription()
            });
        }
    }

    private void onAddItem() {
        JTextField idField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField descriptionField = new JTextField();

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        formPanel.add(new JLabel("Item ID (e.g. DRINK-101):"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Price (£):"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Duration (seconds):"));
        formPanel.add(durationField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(
                mainFrame,
                formPanel,
                "Add Item",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String id = idField.getText().trim();
            float price = Float.parseFloat(priceField.getText().trim());
            int duration = Integer.parseInt(durationField.getText().trim());
            String description = descriptionField.getText().trim();

            if (id.isEmpty()) {
                throw new IllegalArgumentException("Item ID is required.");
            }
            if (price < 0) {
                throw new IllegalArgumentException("Price cannot be negative.");
            }
            if (duration < 0) {
                throw new IllegalArgumentException("Duration cannot be negative.");
            }

            Item newItem = new Item(id, price, duration, description);
            manager.AddItem(newItem);
            refreshItemsTable();
            refreshItemsCombo();

            Customer selectedCustomer = getSelectedCustomer();
            if (selectedCustomer != null) {
                refreshOrdersTable(selectedCustomer);
                refreshBillPanel(selectedCustomer);
            }

            JOptionPane.showMessageDialog(mainFrame, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Price and duration must be numeric", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRemoveItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(mainFrame, "Please select an item first", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemId = (String) itemsModel.getValueAt(selectedRow, 0);
        Item itemToRemove = manager.getAvaliableItems().stream()
                .filter(i -> i.getID().equals(itemId))
                .findFirst()
                .orElse(null);

        if (itemToRemove == null) {
            JOptionPane.showMessageDialog(mainFrame, "Selected item not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                mainFrame,
                "Remove item " + itemId + " from available items?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            manager.RemoveItem(itemToRemove);
            refreshItemsTable();
            refreshItemsCombo();

            Customer selectedCustomer = getSelectedCustomer();
            if (selectedCustomer != null) {
                refreshOrdersTable(selectedCustomer);
                refreshBillPanel(selectedCustomer);
            }

            JOptionPane.showMessageDialog(mainFrame, "Item removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
