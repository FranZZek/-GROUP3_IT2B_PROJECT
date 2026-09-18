import java.util.Scanner;

public class Customer extends Account {
    public Customer(String username, String password, String status, double creditLimit, double balance) {
        super(username, password, "customer", status, creditLimit, balance);
    }

    @Override
    public void showMenu(Scanner sc, Store store) {
        Inventory inventory = store.getInventory();
        TransactionManager transactionManager = store.getTransactionManager();
        DeliveryRequestManager deliveryRequestManager = store.getDeliveryRequestManager();

        int choice;
        do {
            System.out.println("--- Customer Menu ---");
            System.out.println("1. View My Balance");
            System.out.println("2. View Transaction History");
            System.out.println("3. View Inventory");
            System.out.println("4. Request Bulk Delivery");
            System.out.println("0. Logout");
            System.out.print("Choose: ");
            choice = Main.getIntInput(sc);

            switch (choice) {
                case 1 -> {
                    System.out.println("Balance: ₱" + Main.money(getBalance()) + " / Limit: ₱" + Main.money(getCreditLimit()));
                    Main.pauseAndClear(sc);
                }
                case 2 -> {
                    transactionManager.listTransactionsFor(getUsername());
                    Main.pauseAndClear(sc);
                }
                case 3 -> {
                    inventory.listProducts();
                    Main.pauseAndClear(sc);
                }
                case 4 -> {
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

                    deliveryRequestManager.recordRequest(getUsername(), pid, qty);
                    System.out.println("Delivery request submitted! It's pending staff approval.");
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
