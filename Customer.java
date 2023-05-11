import java.util.ArrayList;

public class Customer extends User {

    private String address;
    private double balance;
    private int loyaltyPoints;
    private ArrayList<Product> products;

    public Customer(String name, String address, double balance, int loyaltyPoints) {
        super(name);
        this.address = address;
        this.balance = balance;
        this.loyaltyPoints = loyaltyPoints;
        this.products = new ArrayList<>();
    }

    public Customer(String name, String address, double balance) {
        this(name, address, balance, 0);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

}
