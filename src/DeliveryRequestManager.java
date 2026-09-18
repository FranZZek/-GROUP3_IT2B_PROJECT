import java.util.ArrayList;
import java.io.*;
import java.time.LocalDate;

public class DeliveryRequestManager {
    private ArrayList<DeliveryRequest> requests = new ArrayList<>();

    public void loadDeliveryRequests() {
        File file = new File("deliveryrequests.csv");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                requests.add(new DeliveryRequest(p[0], p[1], p[2],
                        Integer.parseInt(p[3]), p[4], p[5]));
            }
        } catch (IOException e) {
            System.out.println("Error loading delivery requests.");
        }
    }

    public void recordRequest(String customer, String productId, int qty) {
        String id = "D" + (requests.size() + 1);
        String date = LocalDate.now().toString();
        DeliveryRequest r = new DeliveryRequest(id, customer, productId, qty, "pending", date);
        requests.add(r);

        try (FileWriter fw = new FileWriter("deliveryrequests.csv", true)) {
            fw.write(id + "," + customer + "," + productId + "," + qty + ",pending," + date + "\n");
        } catch (IOException e) {
            System.out.println("Error saving delivery request.");
        }
    }

    public void listRequestsFor(String customer) {
        boolean found = false;
        for (DeliveryRequest r : requests) {
            if (r.getCustomerUsername().equals(customer)) {
                System.out.println(r);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No delivery requests found.");
        }
    }

    public void listPendingRequests() {
        boolean found = false;
        for (DeliveryRequest r : requests) {
            if (r.getStatus().equals("pending")) {
                System.out.println(r);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No pending delivery requests found.");
        }
    }

    // Looks up a single request by ID — used when fulfilling/rejecting so we can
    // validate stock and build a transaction before flipping the status.
    public DeliveryRequest getRequest(String id) {
        for (DeliveryRequest r : requests) {
            if (r.getId().equals(id)) return r;
        }
        return null;
    }

    public boolean updateStatus(String id, String status) {
        for (DeliveryRequest r : requests) {
            if (r.getId().equals(id)) {
                r.setStatus(status);
                rewriteFile();
                return true;
            }
        }
        return false;
    }

    private void rewriteFile() {
        try (FileWriter fw = new FileWriter("deliveryrequests.csv")) {
            for (DeliveryRequest r : requests) {
                fw.write(r.getId() + "," + r.getCustomerUsername() + "," + r.getProductId() + "," +
                        r.getQuantity() + "," + r.getStatus() + "," + r.getDate() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error updating delivery requests file.");
        }
    }
}
