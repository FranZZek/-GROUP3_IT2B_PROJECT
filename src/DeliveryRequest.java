public class DeliveryRequest {
    private String id;
    private String customerUsername;
    private String productId;
    private int quantity;
    private String status; // "pending", "fulfilled", "rejected"
    private String date;

    public DeliveryRequest(String id, String customerUsername, String productId, int quantity,
                           String status, String date) {
        this.id = id;
        this.customerUsername = customerUsername;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.date = date;
    }

    public String getId() { return id; }
    public String getCustomerUsername() { return customerUsername; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return date + " | " + id + " | " + productId + " x" + quantity + " | " + status;
    }
}
