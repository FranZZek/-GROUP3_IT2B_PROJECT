public class Transaction {
    private String id;
    private String customerUsername;
    private String productId;
    private int quantity;
    private double totalAmount;
    private String paymentType; // "CASH" or "CREDIT" or "PAYMENT"
    private String date;

    public Transaction(String id, String customerUsername, String productId, int quantity,
                       double totalAmount, String paymentType, String date) {
        this.id = id;
        this.customerUsername = customerUsername;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.paymentType = paymentType;
        this.date = date;
    }

    public String getId() { return id; }
    public String getCustomerUsername() { return customerUsername; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentType() { return paymentType; }
    public String getDate() { return date; }

    @Override
    public String toString() {
        return date + " | " + paymentType + " | " + productId + " x" + quantity + " | ₱" + Main.money(totalAmount);
    }
}
