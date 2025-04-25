import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InventoryManager {

    private static final String CSV_FILE_PATH = "inventory.csv";
    private static final String CSV_HEADER = "ProductID,ProductName,Quantity,Price,Category";

    public List<InventoryItem> loadInventory() {
        List<InventoryItem> inventory = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            br.readLine(); // Skip the header row
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    String productID = data[0].trim();
                    String productName = data[1].trim();
                    int quantity = Integer.parseInt(data[2].trim());
                    double price = Double.parseDouble(data[3].trim());
                    String category = data[4].trim();
                    inventory.add(new InventoryItem(productID, productName, quantity, price, category));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading inventory from CSV: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number in CSV: " + e.getMessage());
        }
        return inventory;
    }

    public void saveInventory(List<InventoryItem> inventory) {
        try (FileWriter writer = new FileWriter(CSV_FILE_PATH)) {
            writer.write(CSV_HEADER);
            writer.write(System.lineSeparator());
            for (InventoryItem item : inventory) {
                writer.write(String.format("%s,%s,%d,%.2f,%s%n",
                        item.getProductID(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getCategory()));
            }
        } catch (IOException e) {
            System.err.println("Error saving inventory to CSV: " + e.getMessage());
        }
    }
}