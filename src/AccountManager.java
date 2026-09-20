import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

public class AccountManager {
    private ArrayList<Account> accounts = new ArrayList<>();

    // Factory method: decides which subclass to instantiate based on role
    private Account createAccountByType(String username, String password, String type,
                                        String status, double creditLimit, double balance) {
        return switch (type) {
            case "superadmin" -> new SuperAdmin(username, password, status, creditLimit, balance);
            case "admin" -> new Admin(username, password, status, creditLimit, balance);
            case "cashier" -> new Cashier(username, password, status, creditLimit, balance);
            default -> new Customer(username, password, status, creditLimit, balance);
        };
    }

    public boolean createAccount(String user, String pass) {
        if (getAccount(user) != null) return false;
        Account newAcc = createAccountByType(user, pass, "customer", "pending", 0, 0);
        accounts.add(newAcc);
        saveAccount(newAcc);
        return true;
    }

    public boolean login(String user, String pass) {
        Account a = getAccount(user);
        if (a != null && a.getPassword().equals(pass)) {
            if (a.getStatus().equals("pending")) {
                System.out.println("Account not approved yet. Wait for superadmin approval.");
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean approveAccount(String user) {
        Account a = getAccount(user);
        if (a == null) return false;
        a.setStatus("approved");
        rewriteFile();
        return true;
    }

    public void saveAccount(Account a) {
        try (FileWriter fw = new FileWriter("accounts.csv", true)) {
            fw.write(a.getUsername() + "," + a.getPassword() + "," + a.getType() + "," +
                    a.getStatus() + "," + a.getCreditLimit() + "," + a.getBalance() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving account.");
        }
    }

    public void rewriteFile() {
        try (FileWriter fw = new FileWriter("accounts.csv")) {
            for (Account a : accounts) {
                fw.write(a.getUsername() + "," + a.getPassword() + "," + a.getType() + "," +
                        a.getStatus() + "," + a.getCreditLimit() + "," + a.getBalance() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error updating accounts file.");
        }
    }

    public void loadAccounts() {
        File file = new File("accounts.csv");

        if (!file.exists()) {
            Account superAdmin = createAccountByType("superadmin", "super123", "superadmin", "approved", 0, 0);
            Account admin = createAccountByType("admin", "admin123", "admin", "approved", 0, 0);
            accounts.add(superAdmin);
            accounts.add(admin);
            saveAccount(superAdmin);
            saveAccount(admin);
            System.out.println("First run — superadmin and admin accounts created.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                accounts.add(createAccountByType(
                        parts[0], parts[1], parts[2], parts[3],
                        Double.parseDouble(parts[4]), Double.parseDouble(parts[5])
                ));
            }
        } catch (IOException e) {
            System.out.println("Error loading accounts.");
        }
    }

    public Account getAccount(String user) {
        for (Account a : accounts) {
            if (a.getUsername().equals(user)) return a;
        }
        return null;
    }

    public void listPendingAccounts() {
        ArrayList<String[]> rows = new ArrayList<>();
        for (Account a : accounts) {
            if (a.getStatus().equals("pending")) {
                rows.add(new String[]{a.getUsername(), a.getType(), a.getStatus()});
            }
        }
        if (rows.isEmpty()) {
            System.out.println("No pending accounts found.");
            return;
        }
        Main.printTable(
                new String[]{"Username", "Type", "Status"},
                new boolean[]{false, false, false},
                rows);
    }

    // Shows one account's credit standing as a table (used by cashier lookup and
    // by the customer's own "View My Balance").
    public static void printBalance(Account a) {
        ArrayList<String[]> rows = new ArrayList<>();
        rows.add(new String[]{a.getUsername(), "₱" + Main.money(a.getBalance()),
                "₱" + Main.money(a.getCreditLimit()),
                "₱" + Main.money(Math.max(0, a.getCreditLimit() - a.getBalance()))});
        Main.printTable(
                new String[]{"Customer", "Balance", "Credit Limit", "Available Credit"},
                new boolean[]{false, true, true, true},
                rows);
    }

    public boolean createCashierAccount(String user, String pass) {
        if (getAccount(user) != null) return false;
        Account newAcc = createAccountByType(user, pass, "cashier", "approved", 0, 0);
        accounts.add(newAcc);
        saveAccount(newAcc);
        return true;
    }

    public boolean setCreditLimit(String user, double limit) {
        Account a = getAccount(user);
        if (a == null) return false;
        a.setCreditLimit(limit);
        rewriteFile();
        return true;
    }

    public boolean recordPayment(String user, double amount) {
        Account a = getAccount(user);
        if (a == null) return false;
        a.subtractFromBalance(amount);
        rewriteFile();
        return true;
    }
}
