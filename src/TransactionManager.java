import java.util.ArrayList;
import java.io.*;
import java.time.LocalDate;

public class TransactionManager {
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public void loadTransactions() {
        File file = new File("transactions.csv");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                transactions.add(new Transaction(p[0], p[1], p[2],
                        Integer.parseInt(p[3]), Double.parseDouble(p[4]), p[5], p[6]));
            }
        } catch (IOException e) {
            System.out.println("Error loading transactions.");
        }
    }

    public Transaction recordTransaction(String customer, String productId, int qty,
                                  double total, String paymentType) {
        String id = "T" + (transactions.size() + 1);
        String date = LocalDate.now().toString();
        Transaction t = new Transaction(id, customer, productId, qty, total, paymentType, date);
        transactions.add(t);

        try (FileWriter fw = new FileWriter("transactions.csv", true)) {
            fw.write(id + "," + customer + "," + productId + "," + qty + "," + total + "," + paymentType + "," + date + "\n");
        } catch (IOException e) {
            System.out.println("Error saving transaction.");
        }
        return t;
    }

    public void listTransactionsFor(String customer) {
        boolean found = false;
        for (Transaction t : transactions) {
            if (t.getCustomerUsername().equals(customer)) {
                System.out.println(t);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No transactions found.");
        }
    }

    // Generates a simple digital receipt: prints it to the console and saves a
    // copy as a .txt file under receipts/, named after the transaction ID.
    // product is null for plain balance payments (no item involved).
    public void generateReceipt(Transaction t, Product product) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("       TindaLista Prototype System\n");
        sb.append("             OFFICIAL RECEIPT\n");
        sb.append("========================================\n");
        sb.append("Receipt No.: ").append(t.getId()).append("\n");
        sb.append("Date: ").append(t.getDate()).append("\n");
        sb.append("Customer: ").append(t.getCustomerUsername()).append("\n");
        sb.append("----------------------------------------\n");
        if (product != null) {
            sb.append(product.getName()).append(" (").append(product.getId()).append(") x")
              .append(t.getQuantity()).append(" @ ₱").append(Main.money(product.getPrice())).append("\n");
        }
        sb.append("Payment type: ").append(t.getPaymentType()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("TOTAL: ₱").append(Main.money(t.getTotalAmount())).append("\n");
        sb.append("========================================\n");

        System.out.println(sb);

        File dir = new File("receipts");
        if (!dir.exists()) dir.mkdir();
        try (FileWriter fw = new FileWriter("receipts/" + t.getId() + ".txt")) {
            fw.write(sb.toString());
        } catch (IOException e) {
            System.out.println("Error saving receipt file.");
        }
    }
}
