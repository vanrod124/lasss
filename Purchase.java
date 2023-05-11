
import java.util.ArrayList;
import java.util.List;

public class Purchase {

    private Customer customer;
    private List<Product> products;

    public Purchase(Customer customer) {

        this.customer = customer;
        this.products = new ArrayList<>();
    }

    public Customer getCustomer() {
        return customer;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public List<Product> getProducts() {
        return products;
    }

    public double getTotalPrice() {
        double totalPrice = 0;
        for (Product product : products) {
            totalPrice += product.getTotalPrice();
        }
        return totalPrice;
    }

    public int calculateLoyaltyPoints() {
        return (int) (getTotalPrice() / 10);
    }

}
