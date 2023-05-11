import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.table.DefaultTableModel;

import javax.swing.JOptionPane;
import javax.swing.*;

public class MainController {

    private MainView view;
    private List<Customer> customers;
    private List<Product> products;
    private ProductTableModel productTableModel;
    private CustomerTableModel customerTableModel;
    private FileManager fileManager;
    private List<Purchase> purchases;

    public MainController() {
        customers = new ArrayList<>();
        products = new ArrayList<>();
        purchases = new ArrayList<>();
        productTableModel = new ProductTableModel(products);
        customerTableModel = new CustomerTableModel(customers);
        fileManager = new FileManager();
        loadProductsFromFile("products.txt");
        loadCustomersFromFile();
        view = new MainView(this);
    }

    public void setView(MainView view) {
        this.view = view;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Product> getProducts() {
        return products;
    }

    private void loadProductsFromFile(String filename) {
        ArrayList<String> data = fileManager.loadFromFile(filename);
        for (String line : data) {
            String[] fields = line.split(",");
            if (fields.length == 3) {
                String name = fields[0];
                double price = Double.parseDouble(fields[1]);
                int quantity = Integer.parseInt(fields[2]);
                Product product = new Product(name, price, quantity);
                products.add(product);
            }
        }
        productTableModel.fireTableDataChanged();
    }

    private void saveProductsToFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Product product : products) {
                writer.println(product.getName() + "," + product.getPrice() + "," + product.getQuantity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCustomersToFile() {
        ArrayList<String> customerData = new ArrayList<>();
        for (Customer customer : customers) {
            String data = customer.getName() + "," + customer.getAddress() + "," + customer.getBalance() + ","
                    + customer.getLoyaltyPoints();
            customerData.add(data);
        }
        fileManager.saveToFile("customers.txt", customerData);
    }

    public void addCustomer(Customer newCustomer) {
        customers.add(newCustomer);
        customerTableModel.fireTableDataChanged();
        saveCustomersToFile();
    }

    public void updateCustomer(int rowIndex, Customer updatedCustomer) {
        customers.set(rowIndex, updatedCustomer);
    }

    public void deleteCustomer(Customer customer) {
        customers.remove(customer);
        customerTableModel.fireTableDataChanged();
    }

    public Customer findCustomerByName(String name) {
        for (Customer customer : customers) {
            if (customer.getName().equalsIgnoreCase(name)) {
                return customer;
            }
        }
        return null;
    }

    public Customer getMostValuableCustomer() {
        Customer mostValuable = null;
        int maxPoints = 0;

        for (Customer customer : customers) {
            if (customer.getLoyaltyPoints() > maxPoints) {
                maxPoints = customer.getLoyaltyPoints();
                mostValuable = customer;
            }
        }

        return mostValuable;
    }

    public void addProduct(Product newProduct) {
        products.add(newProduct);
        productTableModel.fireTableDataChanged();
        saveProductsToFile("products.txt");
    }

    public void updateProduct(int rowIndex, Product updatedProduct) {
        products.set(rowIndex, updatedProduct);
        productTableModel.fireTableDataChanged();
        saveProductsToFile("products.txt");
    }

    public void deleteProduct(Product product) {
        products.remove(product);
        productTableModel.fireTableDataChanged();
        saveProductsToFile("products.txt");
    }

    public Product findProductByName(String name) {
        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;
    }

    public void addPurchase(Purchase purchase) {
        purchases.add(purchase);
    }

    public void createPurchase(Customer customer, List<Product> purchasedProducts) {
        Purchase purchase = new Purchase(customer);
        double totalPurchaseAmount = 0;

        for (Product product : purchasedProducts) {
            Product originalProduct = findProductByName(product.getName());
            if (originalProduct == null) {
                JOptionPane.showMessageDialog(null, "Product not found: " + product.getName(), "Error",
                        JOptionPane.ERROR_MESSAGE);

                return;
            }

            purchase.addProduct(product);
            totalPurchaseAmount += product.getPrice() * product.getQuantity();
            originalProduct.setQuantity(originalProduct.getQuantity() - product.getQuantity());
        }

        int loyaltyPointsToAdd = (int) (totalPurchaseAmount / 10);
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + loyaltyPointsToAdd);

        addPurchase(purchase);
        saveProductsToFile("products.txt");
        saveCustomersToFile();
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public List<Product> getCustomerPurchases(String customerName) {
        String fileName = customerName + "_purchases.txt";
        List<String> fileData = fileManager.loadFromFile(fileName);
        List<Product> customerPurchases = new ArrayList<>();

        for (String line : fileData) {
            if (!line.trim().isEmpty() && !line.equals(customerName)) {
                String[] productData = line.split(",");
                if (productData.length >= 3) {
                    String productName = productData[0];
                    double productPrice = Double.parseDouble(productData[1]);
                    int quantity = Integer.parseInt(productData[2]);
                    customerPurchases.add(new Product(productName, productPrice, quantity));
                } else {
                    System.err.println("Invalid data in the file: " + line);
                }
            }
        }

        return customerPurchases;
    }

    public void loadPurchaseData(Customer customer) {
        String fileName = customer.getName() + "_purchases.txt";
        ArrayList<String> purchaseData = fileManager.loadFromFile(fileName);
        Purchase purchase = null;

        for (String line : purchaseData) {
            if (line.isEmpty()) {
                if (purchase != null) {
                    purchases.add(purchase);
                    purchase = null;
                }
                continue;
            }

            if (purchase == null) {
                purchase = new Purchase(customer);
            } else {
                String[] fields = line.split(",");
                String name = fields[0];
                double price = Double.parseDouble(fields[1]);
                int quantity = Integer.parseInt(fields[2]);
                Product product = new Product(name, price, quantity);
                purchase.addProduct(product);
            }
        }

        if (purchase != null) {
            purchases.add(purchase);
        }
    }

    public void loadCustomersFromFile() {
        ArrayList<String> customerData = fileManager.loadFromFile("customers.txt");
        for (String data : customerData) {
            String[] fields = data.split(",");
            if (fields.length >= 4) {
                String name = fields[0];
                String address = fields[1];
                double balance = Double.parseDouble(fields[2]);
                int loyaltyPoints = Integer.parseInt(fields[3]);

                Customer customer = new Customer(name, address, balance, loyaltyPoints);
                customers.add(customer);
                loadPurchaseData(customer);
            } else {
                System.err.println("Invalid data in the file: " + data);
            }
        }
    }

    public ProductTableModel getProductTableModel() {
        return productTableModel;
    }

    public CustomerTableModel getCustomerTableModel() {
        return customerTableModel;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public Product getProductByName(String name) {
        for (Product product : products) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }

    public void loadHistoricalPurchases(Customer customer, JTable purchasedProductsTable) {
        DefaultTableModel purchasedProductsModel = (DefaultTableModel) purchasedProductsTable.getModel();
        purchasedProductsModel.setRowCount(0);

        List<Product> customerPurchases = getCustomerPurchases(customer.getName());
        for (Product product : customerPurchases) {
            purchasedProductsModel
                    .addRow(new Object[] { product.getName(), product.getPrice(), product.getQuantity() });
        }
    }

}
