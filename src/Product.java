public class Product {
    private String id;
    private String name;
    private int stock;
    private double price;

    public Product(String id, String name, int stock, double price) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getStock() { return stock; }
    public double getPrice() { return price; }

    public void addStock(int amount) {
        stock += amount;
    }

    public boolean deductStock(int amount) {
        if (amount > stock) {
            return false; // not enough stock — caller should block the sale
        }
        stock -= amount;
        return true;
    }
}
