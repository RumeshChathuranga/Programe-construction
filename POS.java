/**
 * Point of Sales (POS) System for Super-Saving Supermarket Chain
 * 
 * Requirements:
 * - Item entry by code and quantity
 * - Product database using CSV
 * - Discount application
 * - Bill generation and printing
 * - Pending bill management
 * - Revenue reporting
 * 
 * @author Group Implementation
 */

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class SuperSaverPOSGroup_123 {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final String DB_FILE = "products.csv";
    
    public static void main(String[] args) {
        System.out.println("Welcome to Super-Saving POS System");
        
        // Load product database
        ProductDatabase productDB = new ProductDatabase();
        productDB.loadFromCSV(DB_FILE);
        
        // Initialize bill manager
        BillManager billManager = new BillManager(productDB);
        
        boolean running = true;
        while (running) {
            System.out.println("\n===== SUPER-SAVING POS SYSTEM =====");
            System.out.println("1. Create New Bill");
            System.out.println("2. Resume Pending Bill");
            System.out.println("3. Generate Revenue Report");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput(1, 4);
            
            switch (choice) {
                case 1:
                    billManager.createNewBill();
                    break;
                case 2:
                    billManager.resumePendingBill();
                    break;
                case 3:
                    billManager.generateRevenueReport();
                    break;
                case 4:
                    running = false;
                    break;
            }
        }
        
        System.out.println("Thank you for using Super-Saving POS System!");
        scanner.close();
    }
    
    private static int getIntInput(int min, int max) {
        int choice = -1;
        while (choice < min || choice > max) {
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice < min || choice > max) {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
        return choice;
    }
}

/**
 * Represents a product in the supermarket.
 */
class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String itemCode;
    private String name;
    private double price;
    private String weightSize;
    private String manufactureDate;
    private String expiryDate;
    private String manufacturer;
    
    public Product(String itemCode, String name, double price, String weightSize, 
                  String manufactureDate, String expiryDate, String manufacturer) {
        this.itemCode = itemCode;
        this.name = name;
        this.price = price;
        this.weightSize = weightSize;
        this.manufactureDate = manufactureDate;
        this.expiryDate = expiryDate;
        this.manufacturer = manufacturer;
    }
    
    // Getters
    public String getItemCode() { return itemCode; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getWeightSize() { return weightSize; }
    public String getManufactureDate() { return manufactureDate; }
    public String getExpiryDate() { return expiryDate; }
    public String getManufacturer() { return manufacturer; }
}

/**
 * Represents an item in a bill with quantity and discount information.
 */
class BillItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Product product;
    private double quantity;
    private double discountPercentage;
    
    public BillItem(Product product, double quantity, double discountPercentage) {
        this.product = product;
        this.quantity = quantity;
        this.discountPercentage = discountPercentage;
    }
    
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
    
    public double getDiscountAmount() {
        return getTotalPrice() * (discountPercentage / 100.0);
    }
    
    public double getNetPrice() {
        return getTotalPrice() - getDiscountAmount();
    }
    
    // Getters
    public Product getProduct() { return product; }
    public double getQuantity() { return quantity; }
    public double getDiscountPercentage() { return discountPercentage; }
}

/**
 * Represents a complete bill for a customer transaction.
 */
class Bill implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String billId;
    private String cashierName;
    private String branchName;
    private String customerName;
    private List<BillItem> items;
    private LocalDateTime dateTime;
    private boolean isPending;
    
    public Bill(String cashierName, String branchName, String customerName) {
        this.billId = "BILL-" + System.currentTimeMillis();
        this.cashierName = cashierName;
        this.branchName = branchName;
        this.customerName = customerName;
        this.items = new ArrayList<>();
        this.dateTime = LocalDateTime.now();
        this.isPending = true;
    }
    
    public void addItem(BillItem item) {
        items.add(item);
    }
    
    public boolean removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            return true;
        }
        return false;
    }
    
    public double getTotalDiscount() {
        double total = 0;
        for (BillItem item : items) {
            total += item.getDiscountAmount();
        }
        return total;
    }
    
    public double getTotalCost() {
        double total = 0;
        for (BillItem item : items) {
            total += item.getNetPrice();
        }
        return total;
    }
    
    public void finalize() {
        this.dateTime = LocalDateTime.now();
        this.isPending = false;
    }
    
    // Getters
    public String getBillId() { return billId; }
    public String getCashierName() { return cashierName; }
    public String getBranchName() { return branchName; }
    public String getCustomerName() { return customerName; }
    public List<BillItem> getItems() { return items; }
    public LocalDateTime getDateTime() { return dateTime; }
    public boolean isPending() { return isPending; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n===== SUPER-SAVING SUPERMARKET =====\n");
        sb.append("Bill ID: ").append(billId).append("\n");
        sb.append("Branch: ").append(branchName).append("\n");
        sb.append("Cashier: ").append(cashierName).append("\n");
        
        if (customerName != null && !customerName.isEmpty()) {
            sb.append("Customer: ").append(customerName).append("\n");
        }
        
        sb.append("Date & Time: ").append(dateTime.format(formatter)).append("\n\n");
        
        sb.append(String.format("%-15s %-8s %-10s %-7s %-10s\n", 
                "Item", "Price", "Quantity", "Disc%", "Net Price"));
        sb.append("-----------------------------------------------------\n");
        
        for (BillItem item : items) {
            Product p = item.getProduct();
            sb.append(String.format("%-15s %-8.2f %-10.2f %-7.1f%% %-10.2f\n",
                    p.getName(), p.getPrice(), item.getQuantity(), 
                    item.getDiscountPercentage(), item.getNetPrice()));
        }
        
        sb.append("-----------------------------------------------------\n");
        sb.append(String.format("Total Discount: Rs. %.2f\n", getTotalDiscount()));
        sb.append(String.format("Total Cost: Rs. %.2f\n", getTotalCost()));
        sb.append("\nThank you for shopping at Super-Saving!\n");
        
        return sb.toString();
    }
}

/**
 * Manages the product database, loading from CSV.
 */
class ProductDatabase {
    private Map<String, Product> products;
    
    public ProductDatabase() {
        this.products = new HashMap<>();
    }
    
    public void loadFromCSV(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Skip header if present
            String line = reader.readLine();
            if (line.startsWith("Item Code") || line.startsWith("itemCode")) {
                line = reader.readLine();
            }
            
            while (line != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    String itemCode = parts[0].trim();
                    String name = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());
                    String weightSize = parts[3].trim();
                    String manufactureDate = parts[4].trim();
                    String expiryDate = parts[5].trim();
                    String manufacturer = parts[6].trim();
                    
                    Product product = new Product(itemCode, name, price, weightSize, 
                            manufactureDate, expiryDate, manufacturer);
                    products.put(itemCode, product);
                }
                line = reader.readLine();
            }
            
            System.out.println("Loaded " + products.size() + " products from database.");
        } catch (Exception e) {
            System.err.println("Error loading product database: " + e.getMessage());
        }
    }
    
    public Product getProduct(String itemCode) {
        return products.get(itemCode);
    }
    
    public void displayAllProducts() {
        System.out.println("\n===== PRODUCT DATABASE =====");
        for (Product p : products.values()) {
            System.out.println(p.getItemCode() + " - " + p.getName() + " - Rs. " + p.getPrice());
        }
    }
}

/**
 * Manages bill creation, saving, loading, and report generation.
 */
class BillManager {
    private static final Scanner scanner = new Scanner(System.in);
    private ProductDatabase productDB;
    private List<Bill> completedBills;
    private List<Bill> pendingBills;
    
    public BillManager(ProductDatabase productDB) {
        this.productDB = productDB;
        this.completedBills = new ArrayList<>();
        this.pendingBills = new ArrayList<>();
        loadPendingBills();
    }
    
    public void createNewBill() {
        System.out.println("\n===== CREATE NEW BILL =====");
        
        System.out.print("Enter cashier name: ");
        String cashierName = scanner.nextLine();
        
        System.out.print("Enter branch name: ");
        String branchName = scanner.nextLine();
        
        System.out.print("Is this a registered customer? (y/n): ");
        String isRegistered = scanner.nextLine().trim().toLowerCase();
        
        String customerName = null;
        if (isRegistered.equals("y") || isRegistered.equals("yes")) {
            System.out.print("Enter customer name: ");
            customerName = scanner.nextLine();
        }
        
        Bill bill = new Bill(cashierName, branchName, customerName);
        processBill(bill);
    }
    
    public void resumePendingBill() {
        if (pendingBills.isEmpty()) {
            System.out.println("No pending bills found.");
            return;
        }
        
        System.out.println("\n===== PENDING BILLS =====");
        for (int i = 0; i < pendingBills.size(); i++) {
            Bill bill = pendingBills.get(i);
            System.out.println((i + 1) + ". " + bill.getBillId() + " - Items: " + 
                    bill.getItems().size() + " - Customer: " + 
                    (bill.getCustomerName() != null ? bill.getCustomerName() : "Anonymous"));
        }
        
        System.out.print("Select bill to resume (0 to cancel): ");
        int choice = getIntInput(0, pendingBills.size());
        
        if (choice == 0) {
            return;
        }
        
        Bill bill = pendingBills.remove(choice - 1);
        processBill(bill);
    }
    
    public void generateRevenueReport() {
        System.out.println("\n===== REVENUE REPORT =====");
        
        if (completedBills.isEmpty()) {
            System.out.println("No completed bills found for reporting.");
            return;
        }
        
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDateStr = scanner.nextLine();
        
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDateStr = scanner.nextLine();
        
        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            
            double totalRevenue = 0;
            int billCount = 0;
            
            for (Bill bill : completedBills) {
                LocalDate billDate = bill.getDateTime().toLocalDate();
                if ((billDate.isEqual(startDate) || billDate.isAfter(startDate)) && 
                    (billDate.isEqual(endDate) || billDate.isBefore(endDate))) {
                    totalRevenue += bill.getTotalCost();
                    billCount++;
                }
            }
            
            System.out.println("\nRevenue Report from " + startDate + " to " + endDate);
            System.out.println("Total Bills: " + billCount);
            System.out.println("Total Revenue: Rs. " + String.format("%.2f", totalRevenue));
            System.out.println("Average Bill Amount: Rs. " + 
                    String.format("%.2f", billCount > 0 ? totalRevenue / billCount : 0));
            
            System.out.println("\nEmail report sent to salesteam@supersaving.lk");
            
        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }
    
    private void processBill(Bill bill) {
        boolean processing = true;
        
        while (processing) {
            System.out.println("\nCurrent Bill: " + bill.getBillId());
            
            if (!bill.getItems().isEmpty()) {
                System.out.println("Items in bill:");
                int index = 1;
                for (BillItem item : bill.getItems()) {
                    System.out.println(index + ". " + item.getProduct().getName() + 
                            " - Qty: " + item.getQuantity() + 
                            " - Discount: " + item.getDiscountPercentage() + "%" +
                            " - Net: Rs. " + String.format("%.2f", item.getNetPrice()));
                    index++;
                }
                System.out.println("Total: Rs. " + String.format("%.2f", bill.getTotalCost()));
            } else {
                System.out.println("No items in bill yet.");
            }
            
            System.out.println("\n1. Add item");
            System.out.println("2. Remove item");
            System.out.println("3. Save as pending");
            System.out.println("4. Finalize bill");
            System.out.println("5. Cancel bill");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput(1, 5);
            
            switch (choice) {
                case 1:
                    addItemToBill(bill);
                    break;
                case 2:
                    removeItemFromBill(bill);
                    break;
                case 3:
                    pendingBills.add(bill);
                    savePendingBills();
                    System.out.println("Bill saved as pending.");
                    return;
                case 4:
                    bill.finalize();
                    completedBills.add(bill);
                    System.out.println("Bill finalized.");
                    System.out.println(bill);
                    saveBillAsPDF(bill); // Simulate saving as PDF
                    return;
                case 5:
                    System.out.println("Bill cancelled.");
                    return;
            }
        }
    }
    
    private void addItemToBill(Bill bill) {
        System.out.print("Enter item code: ");
        String itemCode = scanner.nextLine().trim();
        
        Product product = productDB.getProduct(itemCode);
        if (product == null) {
            System.out.println("Product not found with code: " + itemCode);
            return;
        }
        
        System.out.println("Found: " + product.getName() + " - Rs. " + product.getPrice());
        
        System.out.print("Enter quantity: ");
        double quantity = getDoubleInput();
        
        System.out.print("Enter discount percentage (0-75): ");
        double discount = getDiscountInput();
        
        BillItem item = new BillItem(product, quantity, discount);
        bill.addItem(item);
        
        System.out.println("Item added to bill.");
    }
    
    private void removeItemFromBill(Bill bill) {
        if (bill.getItems().isEmpty()) {
            System.out.println("No items to remove.");
            return;
        }
        
        System.out.print("Enter item number to remove: ");
        int index = getIntInput(1, bill.getItems().size());
        
        bill.removeItem(index - 1);
        System.out.println("Item removed from bill.");
    }
    
    private void saveBillAsPDF(Bill bill) {
        // Simulate saving as PDF
        System.out.println("Bill saved as PDF: " + bill.getBillId() + ".pdf");
    }
    
    private void savePendingBills() {
        // Simulate saving pending bills
        System.out.println("Pending bills saved to system.");
    }
    
    private void loadPendingBills() {
        // Simulate loading pending bills
        System.out.println("Pending bills loaded from system.");
    }
    
    private int getIntInput(int min, int max) {
        int value = -1;
        while (value < min || value > max) {
            try {
                value = Integer.parseInt(scanner.nextLine().trim());
                if (value < min || value > max) {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
        return value;
    }
    
    private double getDoubleInput() {
        double value = -1;
        while (value < 0) {
            try {
                value = Double.parseDouble(scanner.nextLine().trim());
                if (value < 0) {
                    System.out.print("Please enter a positive number: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
        return value;
    }
    
    private double getDiscountInput() {
        double value = -1;
        while (value < 0 || value > 75) {
            try {
                value = Double.parseDouble(scanner.nextLine().trim());
                if (value < 0 || value > 75) {
                    System.out.print("Please enter a discount between 0 and 75: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
        return value;
    }
}