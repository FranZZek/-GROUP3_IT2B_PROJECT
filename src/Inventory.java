import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;


public class Inventory {
    private ArrayList<Product> products = new ArrayList<>();

    public void loadProducts() {
        File file = new File("products.csv");

        if (!file.exists()) {
            System.out.println("No products file found — starting with an empty inventory.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0];
                String name = parts[1];
                int stock = Integer.parseInt(parts[2]);
                double price = Double.parseDouble(parts[3]);
                products.add(new Product(id, name, stock, price));
            }
        } catch (IOException e) {
            System.out.println("Error loading products.");
        }
    }

    // ID is generated automatically ("P1", "P2", ...) instead of being typed in
    // by the admin, so it can't collide or be malformed.
    public Product createProduct(String name, int stock, double price) {
        String id = "P" + (products.size() + 1);
        Product newProduct = new Product(id, name, stock, price);
        products.add(newProduct);
        saveProduct(newProduct);
        return newProduct;
    }

    public Product getProduct(String id) {
        for (Product p : products) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    public boolean addStock(String id, int amount) {
        Product p = getProduct(id);
        if (p == null) return false;

        p.addStock(amount);
        rewriteFile();
        return true;
    }

    public boolean deductStock(String id, int amount) {
        Product p = getProduct(id);
        if (p == null) return false;

        boolean ok = p.deductStock(amount);
        if (ok) rewriteFile();
        return ok;
    }

    public void listProducts() {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        for (Product p : products) {
            System.out.println(p.getId() + " - " + p.getName() + " | stock: " + p.getStock() + " | price: ₱" + Main.money(p.getPrice()));
        }
    }

    private void saveProduct(Product p) {
        try (FileWriter fw = new FileWriter("products.csv", true)) {
            fw.write(p.getId() + "," + p.getName() + "," + p.getStock() + "," + p.getPrice() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving product.");
        }
    }

    private void rewriteFile() {
        try (FileWriter fw = new FileWriter("products.csv")) {
            for (Product p : products) {
                fw.write(p.getId() + "," + p.getName() + "," + p.getStock() + "," + p.getPrice() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error updating products file.");
        }
    }
}
