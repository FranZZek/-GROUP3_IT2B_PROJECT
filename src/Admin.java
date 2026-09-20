import java.util.Scanner;

public class Admin extends Account {
    public Admin(String username, String password, String status, double creditLimit, double balance) {
        super(username, password, "admin", status, creditLimit, balance);
    }

    @Override
    public void showMenu(Scanner sc, Store store) {
        Inventory inventory = store.getInventory();
        AccountManager accountManager = store.getAccountManager();
        DeliveryRequestManager deliveryRequestManager = store.getDeliveryRequestManager();

        int choice;
        do {
            System.out.println("--- Admin Menu ---");
            System.out.println("1. Add Product");
            System.out.println("2. Add Stock");
            System.out.println("3. List Products");
            System.out.println("4. Set Customer Credit Limit");
            System.out.println("5. View Delivery Requests");
            System.out.println("6. Approve/Reject Delivery Request");
            System.out.println("0. Logout");
            System.out.print("Choose: ");
            choice = Main.getIntInput(sc);

            switch (choice) {
                case 1 -> {
                    System.out.print("Name: ");
                    String name = sc.next();
                    if (Main.hasComma(name)) {
                        System.out.println("Product name can't contain commas.");
                        Main.pauseAndClear(sc);
                        break;
                    }
                    System.out.print("Stock: ");
                    int stock = Main.getIntInput(sc);
                    if (stock < 0) {
                        System.out.println("Stock can't be negative.");
                        Main.pauseAndClear(sc);
                        break;
                    }
                    System.out.print("Price: ");
                    double price = Main.getDoubleInput(sc);
                    if (price <= 0) {
                        System.out.println("Price must be greater than 0.");
                        Main.pauseAndClear(sc);
                        break;
                    }
                    Product p = inventory.createProduct(name, stock, price);
                    System.out.println("Product added! ID: " + p.getId());
                    Main.pauseAndClear(sc);
                }
                case 2 -> {
                    System.out.print("Product ID: ");
                    String id = sc.next();
                    System.out.print("Amount to add: ");
                    int amt = Main.getIntInput(sc);
                    if (amt <= 0) {
                        System.out.println("Amount must be greater than 0.");
                        Main.pauseAndClear(sc);
                        break;
                    }
                    boolean ok = inventory.addStock(id, amt);
                    System.out.println(ok ? "Stock updated!" : "Product not found.");
                    Main.pauseAndClear(sc);
                }
                case 3 -> {
                    inventory.listProducts();
                    Main.pauseAndClear(sc);
                }
                case 4 -> {
                    System.out.print("Customer username: ");
                    String u = sc.next();
                    System.out.print("New credit limit: ");
                    double limit = Main.getDoubleInput(sc);
                    if (limit < 0) {
                        System.out.println("Credit limit can't be negative.");
                        Main.pauseAndClear(sc);
                        break;
                    }
                    boolean ok = accountManager.setCreditLimit(u, limit);
                    System.out.println(ok ? "Limit set!" : "User not found.");
                    Main.pauseAndClear(sc);
                }
                case 5 -> {
                    deliveryRequestManager.listPendingRequests();
                    Main.pauseAndClear(sc);
                }
                case 6 -> {
                    System.out.print("Request ID: ");
                    String id = sc.next();
                    DeliveryRequest req = deliveryRequestManager.getRequest(id);
                    if (req == null) {
                        System.out.println("Request not found.");
                        Main.pauseAndClear(sc);
                        break;
                    }
                    if (!req.getStatus().equals("pending")) {
                        System.out.println("This request was already " + req.getStatus() + ".");
                        Main.pauseAndClear(sc);
                        break;
                    }

                    System.out.print("New status (approved/rejected): ");
                    String status = sc.next().toLowerCase();
                    if (!status.equals("approved") && !status.equals("rejected")) {
                        System.out.println("Status must be 'approved' or 'rejected'.");
                        Main.pauseAndClear(sc);
                        break;
                    }

                    if (status.equals("rejected")) {
                        deliveryRequestManager.updateStatus(id, "rejected");
                        System.out.println("Request rejected.");
                        Main.pauseAndClear(sc);
                        break;
                    }

                    // Admin's job stops here — approving just clears the request for a sale.
                    // A Cashier is the one who actually collects payment, moves stock, and
                    // records the transaction, from their own menu.
                    Product product = inventory.getProduct(req.getProductId());
                    if (product == null) {
                        System.out.println("That product no longer exists — can't approve this request.");
                        Main.pauseAndClear(sc);
                        break;
                    }
                    if (req.getQuantity() > product.getStock()) {
                        System.out.println("Not enough stock to approve this request! (" +
                                product.getStock() + " in stock, " + req.getQuantity() + " requested)");
                        System.out.println("Add stock first, then try again — the request is still pending.");
                        Main.pauseAndClear(sc);
                        break;
                    }
                    Account customerAcc = accountManager.getAccount(req.getCustomerUsername());
                    if (customerAcc == null) {
                        System.out.println("That customer account no longer exists.");
                        Main.pauseAndClear(sc);
                        break;
                    }

                    deliveryRequestManager.updateStatus(id, "approved");
                    System.out.println("Request approved! A cashier can now finalize it as a sale.");
                    Main.pauseAndClear(sc);
                }
                case 0 -> {
                    System.out.println("Logging out...");
                    Main.clearScreen();
                    Main.printBanner();
                }
                default -> {
                    System.out.println("Invalid choice.");
                    Main.pauseAndClear(sc);
                }
            }
        } while (choice != 0);
    }
}


