import javax.swing.*;
import java.awt.*;

import java.util.List;
import java.util.ArrayList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.DefaultTableModel;

import javax.swing.border.TitledBorder;

public class MainView {
    private MainController controller;
    private JOptionPane mainMenuDialog;
    

    public MainView(MainController controller) {
        this.controller = controller;
    }

    public void init() {
        while (!showLoginDialog()) {
            JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.", "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        showMainMenu();
    }

    private boolean showLoginDialog() {
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        ImageIcon imageIcon = new ImageIcon("o.png");

        int result = JOptionPane.showConfirmDialog(null, panel, "Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, imageIcon);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            String expectedUsername = "admin";
            String expectedPassword = "admin";

            return username.equals(expectedUsername) && password.equals(expectedPassword);
        }

        return false;
    }

    private void showMainMenu() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
     
        JLabel title = new JLabel("Convenience Corner Inventory");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
    
      
        JButton manageProductsButton = new JButton("Manage Products");
        manageProductsButton.setFocusable(false);
        manageProductsButton.addActionListener(e -> {
            showProductManagement();
        });
    
        JButton manageCustomersButton = new JButton("Manage Customers");
        manageCustomersButton.addActionListener(e -> {
            showCustomerManagement();
        });
    
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            System.exit(0);
        });
    
     
        buttonPanel.add(manageProductsButton);
        buttonPanel.add(manageCustomersButton);
        buttonPanel.add(exitButton);
    
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        JDialog dialog = new JDialog();
        dialog.setTitle("Main Menu");
        dialog.setContentPane(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
    

    private void showProductManagement() {
        JFrame productFrame = new JFrame("Product Management");
        productFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        productFrame.setSize(800, 400);

        Object[][] data = new Object[controller.getProducts().size()][3];

        for (int i = 0; i < controller.getProducts().size(); i++) {
            Product product = controller.getProducts().get(i);
            data[i][0] = product.getName();
            data[i][1] = product.getPrice();
            data[i][2] = product.getQuantity();
        }

        JTable productTable = new JTable(controller.getProductTableModel());
        productTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(productTable);
        productFrame.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton backButton = new JButton("Back");

        addButton.addActionListener(e -> showAddProductDialog());
        updateButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow >= 0) {
                showUpdateProductDialog(selectedRow);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow >= 0) {
                controller.deleteProduct(controller.getProducts().get(selectedRow));
            }
        });
        backButton.addActionListener(e -> {
            productFrame.dispose();
            showMainMenu();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        productFrame.add(buttonPanel, BorderLayout.SOUTH);

        productFrame.setLocationRelativeTo(null);
        productFrame.setVisible(true);
    }

    private void showAddProductDialog() {
        JTextField nameField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField quantityField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Product",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            Product newProduct = new Product(name, price, quantity);
            controller.addProduct(newProduct);
        }
    }

    public void onPurchaseButtonClick(Customer customer, JTable selectedProductsTable, CustomerTableModel customerTableModel) {
        DefaultTableModel selectedProductsModel = (DefaultTableModel) selectedProductsTable.getModel();
        List<String> purchaseLines = new ArrayList<>();
        double totalPurchaseAmount = 0;
    
        for (int i = 0; i < selectedProductsModel.getRowCount(); i++) {
            String productName = (String) selectedProductsModel.getValueAt(i, 0);
            double productPrice = (double) selectedProductsModel.getValueAt(i, 1);
            int productQuantity = (int) selectedProductsModel.getValueAt(i, 2);
            String purchaseLine = productName + "," + productPrice + "," + productQuantity;
            purchaseLines.add(purchaseLine);
    
            totalPurchaseAmount += productPrice * productQuantity;
        }
    
        if (purchaseLines.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please add products to the purchase.");
            return;
        }
    
        if (customer.getBalance() < totalPurchaseAmount) {
            JOptionPane.showMessageDialog(null, "Insufficient balance. Please add funds to the customer's account.");
            return;
        }
    
        customer.setBalance(customer.getBalance() - totalPurchaseAmount);
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + (int) (totalPurchaseAmount / 10));
        controller.updateCustomer(controller.getCustomers().indexOf(customer), customer);
    
        saveCustomersToFile();
    
        customerTableModel.setCustomers(controller.getCustomers());
        customerTableModel.fireTableDataChanged(); 
    
        String fileName = customer.getName() + "_purchases.txt";
        controller.getFileManager().appendToFile(fileName, purchaseLines);
    
        selectedProductsModel.setRowCount(0);
    }
    
    private void saveCustomersToFile() {
        List<String> customerData = new ArrayList<>();
        for (Customer customer : controller.getCustomers()) {
            customerData.add(customer.getName() + "," + customer.getAddress() + "," + customer.getBalance() + ","
                    + customer.getLoyaltyPoints());
        }
        controller.getFileManager().writeToFile("customers.txt", customerData);
    }

    public void loadSelectedCustomerPurchases(Customer customer, JTable selectedProductsTable) {
        DefaultTableModel selectedProductsTableModel = (DefaultTableModel) selectedProductsTable.getModel();
        selectedProductsTableModel.setRowCount(0);

        List<Product> customerPurchases = controller.getCustomerPurchases(customer.getName());
        for (Product product : customerPurchases) {
            Object[] rowData = new Object[] { product.getName(), product.getPrice(), product.getQuantity() };
            selectedProductsTableModel.addRow(rowData);
        }
    }

    private void showUpdateCustomerDialog(int rowIndex) {

        Customer customer = controller.getCustomers().get(rowIndex);

        JTextField nameField = new JTextField(customer.getName());
        JTextField addressField = new JTextField(customer.getAddress());
        JTextField balanceField = new JTextField(Double.toString(customer.getBalance()));
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Balance:"));
        panel.add(balanceField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Update Customer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String address = addressField.getText();
            double balance = Double.parseDouble(balanceField.getText());
            customer.setName(name);
            customer.setAddress(address);
            customer.setBalance(balance);
        }
    }

    private void showAddCustomerDialog(CustomerTableModel customerTableModel) {
        JTextField nameField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField balanceField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Balance:"));
        panel.add(balanceField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Customer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    String name = nameField.getText();
                    String address = addressField.getText();
                    double balance = Double.parseDouble(balanceField.getText());
                    Customer newCustomer = new Customer(name, address, balance);
                    controller.addCustomer(newCustomer);
            
    
                    customerTableModel.setCustomers(controller.getCustomers());
                    customerTableModel.fireTableDataChanged();
                }

    }

    private void showCustomerManagement() {
        JFrame customerFrame = new JFrame("Customer Management");
        customerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        customerFrame.setSize(800, 400);

        CustomerTableModel customerTableModel = new CustomerTableModel(controller.getCustomers());
        JTable customerTable = new JTable(controller.getCustomerTableModel());
        customerTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        customerFrame.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton purchaseButton = new JButton("Purchase");
        JButton viewHistoricalPurchasesButton = new JButton("View Historical Purchases");

        viewHistoricalPurchasesButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow >= 0) {
                showHistoricalPurchasesDialog(controller.getCustomers().get(selectedRow));
            }
        });
        buttonPanel.add(viewHistoricalPurchasesButton);

        JButton backButton = new JButton("Back");

        addButton.addActionListener(e -> showAddCustomerDialog(customerTableModel));

        updateButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow >= 0) {
                showUpdateCustomerDialog(selectedRow);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow >= 0) {
                controller.deleteCustomer(controller.getCustomers().get(selectedRow));
            }
        });
        backButton.addActionListener(e -> {
            customerFrame.dispose();
            showMainMenu();
        });

        purchaseButton.addActionListener(e -> showPurchaseDialogForSelectedCustomer(customerTable, customerTableModel));
   
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(purchaseButton);
        buttonPanel.add(backButton);
        customerFrame.add(buttonPanel, BorderLayout.SOUTH);

        customerFrame.setLocationRelativeTo(null);
        customerFrame.setVisible(true);
    }

    private void showHistoricalPurchasesDialog(Customer customer) {
        JFrame historicalPurchasesFrame = new JFrame("Historical Purchases for " + customer.getName());
        historicalPurchasesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        historicalPurchasesFrame.setSize(800, 600);

        DefaultTableModel purchasedProductsTableModel = new DefaultTableModel(new Object[][] {},
                new String[] { "Name", "Price", "Quantity" });
        JTable purchasedProductsTable = new JTable(purchasedProductsTableModel);
        purchasedProductsTable.setFillsViewportHeight(true);
        JScrollPane purchasedProductsScrollPane = new JScrollPane(purchasedProductsTable);
        TitledBorder purchasedProductsBorder = BorderFactory.createTitledBorder("Historical Purchases");
        purchasedProductsScrollPane.setBorder(purchasedProductsBorder);

        historicalPurchasesFrame.add(purchasedProductsScrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton("Back");

        JButton deletePurchaseButton = new JButton("Delete Purchase");
        deletePurchaseButton.addActionListener(e -> {
            int selectedRow = purchasedProductsTable.getSelectedRow();
            if (selectedRow >= 0) {
                deleteSelectedPurchase(customer, purchasedProductsTable, selectedRow);
            }
        });

        JLabel totalAmountLabel = new JLabel("Total: $0.00");
        buttonPanel.add(totalAmountLabel);

        buttonPanel.add(deletePurchaseButton);

        backButton.addActionListener(e -> historicalPurchasesFrame.dispose());
        buttonPanel.add(backButton);
        historicalPurchasesFrame.add(buttonPanel, BorderLayout.SOUTH);

        historicalPurchasesFrame.setLocationRelativeTo(null);
        historicalPurchasesFrame.setVisible(true);

        loadHistoricalPurchases(customer, purchasedProductsTable, totalAmountLabel);

    }

    private void deleteSelectedPurchase(Customer customer, JTable purchasedProductsTable, int selectedRow) {
        DefaultTableModel purchasedProductsTableModel = (DefaultTableModel) purchasedProductsTable.getModel();
        purchasedProductsTableModel.removeRow(selectedRow);

        List<String> purchaseLines = new ArrayList<>();
        for (int i = 0; i < purchasedProductsTableModel.getRowCount(); i++) {
            String productName = (String) purchasedProductsTableModel.getValueAt(i, 0);
            double productPrice = (double) purchasedProductsTableModel.getValueAt(i, 1);
            int productQuantity = (int) purchasedProductsTableModel.getValueAt(i, 2);
            String purchaseLine = productName + "," + productPrice + "," + productQuantity;
            purchaseLines.add(purchaseLine);
        }

        String fileName = customer.getName() + "_purchases.txt";
        controller.getFileManager().saveToFile(fileName, purchaseLines);
    }

    private void showPurchaseDialog(Customer customer, CustomerTableModel customerTableModel) {
        JFrame purchaseFrame = new JFrame("Purchase for " + customer.getName());
        purchaseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        purchaseFrame.setSize(800, 600);

        JTable availableProductsTable = new JTable(controller.getProductTableModel());
        availableProductsTable.setFillsViewportHeight(true);
        JScrollPane availableProductsScrollPane = new JScrollPane(availableProductsTable);
        TitledBorder availableProductsBorder = BorderFactory.createTitledBorder("Available Products");
        availableProductsScrollPane.setBorder(availableProductsBorder);

        DefaultTableModel selectedProductsModel = new DefaultTableModel(new Object[][] {},
                new String[] { "Name", "Price", "Quantity" });
        JTable selectedProductsTable = new JTable(selectedProductsModel);
        selectedProductsTable.setFillsViewportHeight(true);
        JScrollPane selectedProductsScrollPane = new JScrollPane(selectedProductsTable);
        TitledBorder selectedProductsBorder = BorderFactory.createTitledBorder("Selected Products");
        selectedProductsScrollPane.setBorder(selectedProductsBorder);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, availableProductsScrollPane,
                selectedProductsScrollPane);
        splitPane.setResizeWeight(0.5);
        purchaseFrame.add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Product");
        JButton removeButton = new JButton("Remove Product");
        JButton purchaseButton = new JButton("Purchase");
        JButton backButton = new JButton("Back");

        addButton.addActionListener(e -> {
            int selectedRow = availableProductsTable.getSelectedRow();
            if (selectedRow >= 0) {
                Product selectedProduct = controller.getProducts().get(selectedRow);
                selectedProductsModel.addRow(new Object[] { selectedProduct.getName(), selectedProduct.getPrice(), 1 });
            }
        });

        removeButton.addActionListener(e -> {
            int selectedRow = selectedProductsTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedProductsModel.removeRow(selectedRow);
            }
        });

        purchaseButton.addActionListener(e -> onPurchaseButtonClick(customer, selectedProductsTable, customerTableModel));

        backButton.addActionListener(e -> purchaseFrame.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(purchaseButton);
        buttonPanel.add(backButton);
        purchaseFrame.add(buttonPanel, BorderLayout.SOUTH);

        purchaseFrame.setLocationRelativeTo(null);
        purchaseFrame.setVisible(true);
    }

    public void showCustomerPurchases(Customer customer) {
        JFrame purchasesFrame = new JFrame("Purchases for " + customer.getName());
        purchasesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        purchasesFrame.setSize(800, 600);

        DefaultTableModel purchasedProductsModel = new DefaultTableModel(new Object[][] {},
                new String[] { "Name", "Price", "Quantity" });
        JTable purchasedProductsTable = new JTable(purchasedProductsModel);
        purchasedProductsTable.setFillsViewportHeight(true);
        JScrollPane purchasedProductsScrollPane = new JScrollPane(purchasedProductsTable);
        TitledBorder purchasedProductsBorder = BorderFactory.createTitledBorder("Purchased Products");
        purchasedProductsScrollPane.setBorder(purchasedProductsBorder);

        JLabel totalAmountLabel = new JLabel("Total: $0.00");

        purchasedProductsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    loadHistoricalPurchases(customer, purchasedProductsTable, totalAmountLabel);
                }
            }
        });

        purchasesFrame.add(purchasedProductsScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> purchasesFrame.dispose());

        bottomPanel.add(totalAmountLabel);

        bottomPanel.add(backButton);
        purchasesFrame.add(bottomPanel, BorderLayout.SOUTH);

        purchasesFrame.setLocationRelativeTo(null);
        purchasesFrame.setVisible(true);

        loadHistoricalPurchases(customer, purchasedProductsTable, totalAmountLabel);
    }

    public void loadHistoricalPurchases(Customer customer, JTable purchasedProductsTable, JLabel totalAmountLabel) {
        DefaultTableModel purchasedProductsTableModel = (DefaultTableModel) purchasedProductsTable.getModel();
        purchasedProductsTableModel.setRowCount(0);

        String fileName = customer.getName() + "_purchases.txt";
        List<String> purchaseLines = controller.getFileManager().loadFromFile(fileName);
        double totalAmount = 0;
        for (String line : purchaseLines) {
            String[] tokens = line.split(",");
            if (tokens.length >= 3) {
                String productName = tokens[0];
                double productPrice = Double.parseDouble(tokens[1]);
                int productQuantity = Integer.parseInt(tokens[2]);

                Object[] rowData = new Object[] { productName, productPrice, productQuantity };
                purchasedProductsTableModel.addRow(rowData);

                totalAmount += productPrice * productQuantity;
            }
        }
        totalAmountLabel.setText(String.format("Total: $%.2f", totalAmount));
    }

    private void showPurchaseDialogForSelectedCustomer(JTable customerTable, CustomerTableModel customerTableModel) {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            Customer customer = controller.getCustomers().get(selectedRow);
            showPurchaseDialog(customer, customerTableModel);

        } else {
            JOptionPane.showMessageDialog(null, "Please select a customer.");
        }
    }

    private void showUpdateProductDialog(int rowIndex) {

        Product product = controller.getProducts().get(rowIndex);

        JTextField nameField = new JTextField(product.getName());
        JTextField priceField = new JTextField(Double.toString(product.getPrice()));
        JTextField quantityField = new JTextField(Integer.toString(product.getQuantity()));
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Update Product",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            product.setName(name);
            product.setPrice(price);
            product.setQuantity(quantity);
        }
    }

}
