import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryApp extends JFrame {

    private InventoryManager inventoryManager;
    private List<InventoryItem> inventoryList;
    private DefaultTableModel tableModel;
    private JTable inventoryTable;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel notificationPanel;
    private JButton toggleNotificationsButton;
    private boolean notificationsVisible = false;

    // Add New Item Page Components
    private JTextField addProductIDField;
    private JTextField addProductNameField;
    private JTextField addQuantityField;
    private JTextField addPriceField;
    private JTextField addCategoryField;

    // Edit Item Page Components
    private JTextField editProductIDField;
    private JTextField editProductNameField;
    private JTextField editQuantityField;
    private JTextField editPriceField;
    private JTextField editCategoryField;
    private int selectedRowIndex = -1; // To keep track of the selected row for editing

    public InventoryApp() {
        inventoryManager = new InventoryManager();
        inventoryList = inventoryManager.loadInventory();

        setTitle("Inventory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        setupInventoryListPage();
        setupAddNewItemPage();
        setupEditItemPage();
        setupNotificationPanel();

        add(mainPanel, BorderLayout.CENTER);
        add(notificationPanel, BorderLayout.SOUTH); // Initially hidden

        setVisible(true);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                inventoryManager.saveInventory(inventoryList);
            }
        });

        updateNotifications(); // Initial update
        Timer timer = new Timer(5000, e -> updateNotifications()); // Update notifications periodically
        timer.start();
    }

    private void setupInventoryListPage() {
        JPanel inventoryListPage = new JPanel(new BorderLayout());

        String[] columnNames = {"Product ID", "Product Name", "Quantity", "Price", "Category"};
        Object[][] data = new Object[inventoryList.size()][5];
        for (int i = 0; i < inventoryList.size(); i++) {
            InventoryItem item = inventoryList.get(i);
            data[i][0] = item.getProductID();
            data[i][1] = item.getProductName();
            data[i][2] = item.getQuantity();
            data[i][3] = item.getPrice();
            data[i][4] = item.getCategory();
        }
        tableModel = new DefaultTableModel(data, columnNames);
        inventoryTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        inventoryListPage.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add New Item");
        JButton editButton = new JButton("Edit Selected Item");
        JButton deleteButton = new JButton("Delete Selected Item");

        addButton.addActionListener(e -> cardLayout.show(mainPanel, "AddNewItem"));

        editButton.addActionListener(e -> {
            selectedRowIndex = inventoryTable.getSelectedRow();
            if (selectedRowIndex >= 0) {
                InventoryItem selectedItem = inventoryList.get(selectedRowIndex);
                editProductIDField.setText(selectedItem.getProductID());
                editProductNameField.setText(selectedItem.getProductName());
                editQuantityField.setText(String.valueOf(selectedItem.getQuantity()));
                editPriceField.setText(String.valueOf(selectedItem.getPrice()));
                editCategoryField.setText(selectedItem.getCategory());
                cardLayout.show(mainPanel, "EditItem");
            } else {
                JOptionPane.showMessageDialog(InventoryApp.this, "Please select an item to edit", "Selection Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow >= 0) {
                String productIDToDelete = (String) tableModel.getValueAt(selectedRow, 0); // Get Product ID from the table

                int confirmation = JOptionPane.showConfirmDialog(
                        InventoryApp.this,
                        "Are you sure you want to delete item with Product ID: " + productIDToDelete + "?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmation == JOptionPane.YES_OPTION) {
                    deleteItem(productIDToDelete); // Call the deleteItem method
                    JOptionPane.showMessageDialog(InventoryApp.this, "Item deleted successfully", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(InventoryApp.this, "Please select an item to delete", "Selection Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);

        inventoryListPage.add(buttonsPanel, BorderLayout.SOUTH);

        mainPanel.add(inventoryListPage, "InventoryList");
        cardLayout.show(mainPanel, "InventoryList");
    }

    private void setupAddNewItemPage() {
        JPanel addNewItemPage = new JPanel(new GridLayout(6, 2, 5, 5)); // 6 rows, 2 columns, horizontal and vertical gap

        JLabel productIDLabel = new JLabel("Product ID:");
        addProductIDField = new JTextField(10);
        JLabel productNameLabel = new JLabel("Product Name:");
        addProductNameField = new JTextField(20);
        JLabel quantityLabel = new JLabel("Quantity:");
        addQuantityField = new JTextField(5);
        JLabel priceLabel = new JLabel("Price:");
        addPriceField = new JTextField(10);
        JLabel categoryLabel = new JLabel("Category:");
        addCategoryField = new JTextField(15);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String productID = addProductIDField.getText().trim();
            String productName = addProductNameField.getText().trim();
            String quantityStr = addQuantityField.getText().trim();
            String priceStr = addPriceField.getText().trim();
            String category = addCategoryField.getText().trim();

            if (!productID.isEmpty() && !productName.isEmpty() && !quantityStr.isEmpty() && !priceStr.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    double price = Double.parseDouble(priceStr);
                    InventoryItem newItem = new InventoryItem(productID, productName, quantity, price, category);
                    addItem(newItem); // Add to the list and update table
                    // Clear the input fields
                    addProductIDField.setText("");
                    addProductNameField.setText("");
                    addQuantityField.setText("");
                    addPriceField.setText("");
                    addCategoryField.setText("");
                    cardLayout.show(mainPanel, "InventoryList"); // Go back to the list page
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(InventoryApp.this, "Invalid Quantity or Price", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(InventoryApp.this, "Please fill in all required fields", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            // Clear the input fields
            addProductIDField.setText("");
            addProductNameField.setText("");
            addQuantityField.setText("");
            addPriceField.setText("");
            addCategoryField.setText("");
            cardLayout.show(mainPanel, "InventoryList"); // Go back to the list page
        });

        addNewItemPage.add(productIDLabel);
        addNewItemPage.add(addProductIDField);
        addNewItemPage.add(productNameLabel);
        addNewItemPage.add(addProductNameField);
        addNewItemPage.add(quantityLabel);
        addNewItemPage.add(addQuantityField);
        addNewItemPage.add(priceLabel);
        addNewItemPage.add(addPriceField);
        addNewItemPage.add(categoryLabel);
        addNewItemPage.add(addCategoryField);
        addNewItemPage.add(saveButton);
        addNewItemPage.add(cancelButton);

        mainPanel.add(addNewItemPage, "AddNewItem");
    }

    private void setupEditItemPage() {
        JPanel editItemPage = new JPanel(new GridLayout(6, 2, 5, 5));

        JLabel productIDLabel = new JLabel("Product ID:");
        editProductIDField = new JTextField(10);
        editProductIDField.setEditable(false); // Product ID should likely not be editable
        JLabel productNameLabel = new JLabel("Product Name:");
        editProductNameField = new JTextField(20);
        JLabel quantityLabel = new JLabel("Quantity:");
        editQuantityField = new JTextField(5);
        JLabel priceLabel = new JLabel("Price:");
        editPriceField = new JTextField(10);
        JLabel categoryLabel = new JLabel("Category:");
        editCategoryField = new JTextField(15);

        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            if (selectedRowIndex >= 0) {
                String productID = editProductIDField.getText().trim();
                String productName = editProductNameField.getText().trim();
                String quantityStr = editQuantityField.getText().trim();
                String priceStr = editPriceField.getText().trim();
                String category = editCategoryField.getText().trim();

                if (!productName.isEmpty() && !quantityStr.isEmpty() && !priceStr.isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        double price = Double.parseDouble(priceStr);
                        InventoryItem updatedItem = new InventoryItem(productID, productName, quantity, price, category);
                        updateItem(updatedItem); // Update the list and table
                        // Reset selected row
                        selectedRowIndex = -1;
                        cardLayout.show(mainPanel, "InventoryList"); // Go back to the list page
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(InventoryApp.this, "Invalid Quantity or Price", "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(InventoryApp.this, "Please fill in all required fields (except Product ID)", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(InventoryApp.this, "No item selected for editing", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            selectedRowIndex = -1; // Reset selected row
            cardLayout.show(mainPanel, "InventoryList"); // Go back to the list page
        });

        editItemPage.add(productIDLabel);
        editItemPage.add(editProductIDField);
        editItemPage.add(productNameLabel);
        editItemPage.add(editProductNameField);
        editItemPage.add(quantityLabel);
        editItemPage.add(editQuantityField);
        editItemPage.add(priceLabel);
        editItemPage.add(editPriceField);
        editItemPage.add(categoryLabel);
        editItemPage.add(editCategoryField);
        editItemPage.add(saveButton);
        editItemPage.add(cancelButton);

        mainPanel.add(editItemPage, "EditItem");
    }

    private void setupNotificationPanel() {
        notificationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toggleNotificationsButton = new JButton("Show Notifications");
        toggleNotificationsButton.addActionListener(e -> {
            notificationsVisible = !notificationsVisible;
            if (notificationsVisible) {
                toggleNotificationsButton.setText("Hide Notifications");
                updateNotificationsDisplay();
                notificationPanel.setPreferredSize(null); // Let it expand
            } else {
                toggleNotificationsButton.setText("Show Notifications");
                notificationPanel.removeAll();
                notificationPanel.add(toggleNotificationsButton);
                notificationPanel.setPreferredSize(new Dimension(getWidth(), 30)); // Small when hidden
            }
            notificationPanel.revalidate();
            notificationPanel.repaint();
        });
        notificationPanel.add(toggleNotificationsButton);
        notificationPanel.setPreferredSize(new Dimension(getWidth(), 30)); // Initially small
    }

    private void updateNotifications() {
        if (notificationsVisible) {
            updateNotificationsDisplay();
        }
    }

    private void updateNotificationsDisplay() {
        notificationPanel.removeAll();
        notificationPanel.add(toggleNotificationsButton);

        List<InventoryItem> lowStockItems = checkLowStock();
        List<InventoryItem> outOfStockItems = checkOutOfStock();

        if (!lowStockItems.isEmpty()) {
            JLabel lowStockLabel = new JLabel("Low Stock (< 10): " + lowStockItems.stream().map(InventoryItem::getProductName).collect(Collectors.joining(", ")));
            lowStockLabel.setForeground(Color.ORANGE);
            notificationPanel.add(lowStockLabel);
        }

        if (!outOfStockItems.isEmpty()) {
            JLabel outOfStockLabel = new JLabel("Out of Stock (0): " + outOfStockItems.stream().map(InventoryItem::getProductName).collect(Collectors.joining(", ")));
            outOfStockLabel.setForeground(Color.RED);
            notificationPanel.add(outOfStockLabel);
        }

        if (lowStockItems.isEmpty() && outOfStockItems.isEmpty() && notificationsVisible) {
            JLabel allGoodLabel = new JLabel("All items in stock.");
            allGoodLabel.setForeground(new Color(0, 150, 0)); // Dark green
            notificationPanel.add(allGoodLabel);
        }

        notificationPanel.revalidate();
        notificationPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryApp());
    }

    public List<InventoryItem> getInventoryList() {
        return inventoryList;
    }

    public void addItem(InventoryItem item) {
        inventoryList.add(item);
        Object[] rowData = {item.getProductID(), item.getProductName(), item.getQuantity(), item.getPrice(), item.getCategory()};
        tableModel.addRow(rowData);
        inventoryManager.saveInventory(inventoryList);
        updateNotifications();
    }

    public void updateItem(InventoryItem updatedItem) {
        if (selectedRowIndex >= 0 && selectedRowIndex < inventoryList.size()) {
            inventoryList.set(selectedRowIndex, updatedItem);
            tableModel.setValueAt(updatedItem.getProductID(), selectedRowIndex, 0);
            tableModel.setValueAt(updatedItem.getProductName(), selectedRowIndex, 1);
            tableModel.setValueAt(updatedItem.getQuantity(), selectedRowIndex, 2);
            tableModel.setValueAt(updatedItem.getPrice(), selectedRowIndex, 3);
            tableModel.setValueAt(updatedItem.getCategory(), selectedRowIndex, 4);
            inventoryManager.saveInventory(inventoryList);
            selectedRowIndex = -1; // Reset after update
            updateNotifications();
        }
    }

    public void deleteItem(String productID) {
        for (int i = 0; i < inventoryList.size(); i++) {
            if (inventoryList.get(i).getProductID().equals(productID)) {
                inventoryList.remove(i);
                tableModel.removeRow(i);
                inventoryManager.saveInventory(inventoryList);
                updateNotifications();
                return;
            }
        }
    }

    public List<InventoryItem> checkLowStock() {
        return inventoryList.stream()
                .filter(item -> item.getQuantity() < 10 && item.getQuantity() > 0)
                .collect(Collectors.toList());
    }

    public List<InventoryItem> checkOutOfStock() {
        return inventoryList.stream()
                .filter(item -> item.getQuantity() == 0)
                .collect(Collectors.toList());
    }
}