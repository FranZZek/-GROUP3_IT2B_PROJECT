import java.util.Scanner;

public class SuperAdmin extends Account {
    public SuperAdmin(String username, String password, String status, double creditLimit, double balance) {
        super(username, password, "superadmin", status, creditLimit, balance);
    }

    @Override
    public void showMenu(Scanner sc, Store store) {
        AccountManager accountManager = store.getAccountManager();

        int choice;
        do {
            System.out.println("--- Superadmin Menu ---");
            System.out.println("1. View Pending Accounts");
            System.out.println("2. Approve Account");
            System.out.println("3. Create Cashier Account");
            System.out.println("0. Logout");
            System.out.print("Choose: ");
            choice = Main.getIntInput(sc);

            switch (choice) {
                case 1 -> {
                    accountManager.listPendingAccounts();
                    Main.pauseAndClear(sc);
                }
                case 2 -> {
                    System.out.print("Username to approve: ");
                    String u = sc.next();
                    boolean ok = accountManager.approveAccount(u);
                    System.out.println(ok ? "Approved!" : "User not found.");
                    Main.pauseAndClear(sc);
                }
                case 3 -> {
                    System.out.print("New cashier username: ");
                    String u = sc.next();
                    System.out.print("New cashier password: ");
                    String p = sc.next();
                    if (Main.hasComma(u) || Main.hasComma(p)) {
                        System.out.println("Username and password can't contain commas.");
                        Main.pauseAndClear(sc);
                        break;
                    }
                    boolean ok = accountManager.createCashierAccount(u, p);
                    System.out.println(ok ? "Cashier account created!" : "Username taken.");
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
