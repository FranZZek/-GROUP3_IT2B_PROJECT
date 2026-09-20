import java.util.Scanner;

public class Cashier extends Account {
    public Cashier(String username, String password, String status, double creditLimit, double balance) {
        super(username, password, "cashier", status, creditLimit, balance);
    }

    @Override
    public void showMenu(Scanner sc, Store store) {
        Inventory inventory = store.getInventory();
        AccountManager accountManager = store.getAccountManager();
        TransactionManager transactionManager = store.getTransactionManager();
        DeliveryRequestManager deliveryRequestManager = store.getDeliveryRequestManager();

        int choice;
        do {
            System.out.println("1. Process Sale");
            System.out.println("2. Record Payment");
            System.out.println("3. Check Customer Balance");
            System.out.println("4. View Approved Delivery Requests");   // <-- ADDED
            System.out.println("5. Finalize Delivery Request (Sale)");   // <-- ADDED
            System.out.println("0. Logout");
            System.out.print("Choose: ");
            choice = Main.getIntInput(sc);

            switch (choice) {
                case 1 -> {
                    System.out.print("Customer username: ");
                    String customer = sc.next();
                    System.out.print("Product ID: ");
                    String pid = sc.next();
                    Product p = inventory.getProduct(pid);
                    if (p == null) {
                        System.out.println("Product not found.");
                        Main.pauseAndClear(sc);
                        break;
                    }

                    System.out.print("Quantity: ");
                    int qty = Main.getIntInput(sc);

                    if (qty <= 0) {
                        System.out.println("Quantity must be greater than 0.");
                        Main.pauseAndClear(sc);
                        break;
                    }

                    if (qty > p.getStock()) {
                        System.out.println("Not enough stock!");
                        Main.pauseAndClear(sc);
                        break;
                    }

                    double total = qty * p.getPrice();

                    System.out.print("Payment type (CASH/CREDIT): ");
                    String type = sc.next().toUpperCase();

                    Account customerAcc = accountManager.getAccount(customer);
                    if (customerAcc == null) {
                        System.out.println("Customer not found.");
                        Main.pauseAndClear(sc);
                        break;
                    }

                    if (type.equals("CREDIT") && !customerAcc.canBuyOnCredit(total)) {
                        System.out.println("Blocked: exceeds credit limit!");
                        Main.pauseAndClear(sc);
                        break;
                    }

                    boolean stockOk = inventory.deductStock(pid, qty);
                    if (!stockOk) {
                        System.out.println("Not enough stock!");
                        Main.pauseAndClear(sc);
                        break;
                    }

                    if (type.equals("CREDIT")) {
                        customerAcc.addToBalance(total);
                        accountManager.rewriteFile();
                    }

                    Transaction t = transactionManager.recordTransaction(customer, pid, qty, total, type);
                    System.out.println("Sale recorded! Total: ₱" + Main.money(total));
                    transactionManager.generateReceipt(t, p);
                    Main.pauseAndClear(sc);
                }
                case 2 -> {
                    System.out.print("Customer username: ");
                    String u = sc.next();
                    System.out.print("Payment amount: ");
                    double amt = Main.getDoubleInput(sc);
                    if (amt <= 0) {
                        System.out.println("Payment amount must be greater than 0.");
                        Main.pauseAndClear(sc);
                        break;
                    }
                    boolean ok = accountManager.recordPayment(u, amt);
                    if (ok) {
                        Transaction t = transactionManager.recordTransaction(u, "-", 0, amt, "PAYMENT");
                        System.out.println("Payment recorded!");
                        transactionManager.generateReceipt(t, null);
                    } else {
                        System.out.println("Customer not found.");
                    }
                    Main.pauseAndClear(sc);
                }
                case 3 -> {
                    System.out.print("Customer username: ");
                    String u = sc.next();
                    Account a = accountManager.getAccount(u);
                    if (a != null) AccountManager.printBalance(a);
                    else System.out.println("Not found.");
                    Main.pauseAndClear(sc);
                }
                case 4 -> {
                    deliveryRequestManager.listApprovedRequests();
                    Main.pauseAndClear(sc);
                }
                // <-- ADDED: entire case 5 block is new
                case 5 -> {
                    System.out.print("Request ID: ");
                    String id = sc.next();
                    DeliveryRequest req = deliveryRequestManager.getRequest(id);
                    if (req == null) {
                        System.out.println("Request not found.");
                        Main.pauseAndClear(sc);
                        break;
                    }
                    if (!req.getStatus().equals("approved")) {
                        System.out.println("This request isn't ready — it's currently '" + req.getStatus() + "'.");
                        Main.pauseAndClear(sc);
                        break;
                    }

                    // Same automated sale pipeline as "Process Sale" — stock deduction,
                    // credit check, transaction record, and receipt — just fed from an
                    // approved delivery request instead of a walk-in item/qty entry.
                    Product product = inventory.getProduct(req.getProductId());
                    if (product == null) {
                        System.out.println("That product no longer exists — can't finalize this request.");
                        Main.pauseAndClear(sc);
                        break;
                    }
                    if (req.getQuantity() > product.getStock()) {
                        System.out.println("Not enough stock to finalize this request! (" +
                                product.getStock() + " in stock, " + req.getQuantity() + " requested)");
                        Main.pauseAndClear(sc);
                        break;
                    }

                    Account customerAcc = accountManager.getAccount(req.getCustomerUsername());
                    if (customerAcc == null) {
                        System.out.println("That customer account no longer exists.");
                        Main.pauseAndClear(sc);
                        break;
                    }

                    double total = req.getQuantity() * product.getPrice();

                    System.out.print("Payment type (CASH/CREDIT): ");
                    String payType = sc.next().toUpperCase();
                    if (!payType.equals("CASH") && !payType.equals("CREDIT")) {
                        System.out.println("Payment type must be CASH or CREDIT.");
                        Main.pauseAndClear(sc);
                        break;
                    }
                    if (payType.equals("CREDIT") && !customerAcc.canBuyOnCredit(total)) {
                        System.out.println("Blocked: exceeds this customer's credit limit!");
                        Main.pauseAndClear(sc);
                        break;
                    }

                    inventory.deductStock(product.getId(), req.getQuantity());
                    if (payType.equals("CREDIT")) {
                        customerAcc.addToBalance(total);
                        accountManager.rewriteFile();
                    }

                    Transaction t = transactionManager.recordTransaction(
                            req.getCustomerUsername(), product.getId(), req.getQuantity(), total, payType);
                    deliveryRequestManager.updateStatus(id, "fulfilled");

                    System.out.println("Request finalized! Total: ₱" + Main.money(total));
                    transactionManager.generateReceipt(t, product);
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
