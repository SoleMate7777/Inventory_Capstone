import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class InventoryGUI extends JFrame {

    private JTextField productNameField;
    private JLabel productNameLabel;
    private JTextField amountInStockField;
    private JLabel amountInStockLabel;
    private JTextField costToMakeField;
    private JLabel costToMakeLabel;
    private JTextField priceToSellField;
    private JLabel priceToSellLabel;
    private JTextField colorField;
    private JLabel colorLabel;
    private JTextField sizeField;
    private JLabel sizeLabel;
    private JButton addInventoryButton;
    private JButton viewInventoryButton;
    private JButton deleteInventoryButton;
    private JButton soldButton;

    private ArrayList<String> inventoryEntries; // List to store inventory entries

    public InventoryGUI() {
        super("Inventory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(9, 2, 10, 10)); // 9 rows, 2 columns, 10px spacing

        productNameLabel = new JLabel("Product Name");
        productNameField = new JTextField(20);
        amountInStockLabel = new JLabel("Amount in Stock");
        amountInStockField = new JTextField(20);
        costToMakeLabel = new JLabel("Cost to Make");
        costToMakeField = new JTextField(20);
        priceToSellLabel = new JLabel("Price to Sell");
        priceToSellField = new JTextField(20);
        colorLabel = new JLabel("Color");
        colorField = new JTextField(20);
        sizeLabel = new JLabel("Size");
        sizeField = new JTextField(20);
        addInventoryButton = new JButton("Add Inventory");
        viewInventoryButton = new JButton("View Inventory List");
        deleteInventoryButton = new JButton("Delete Inventory");
        soldButton = new JButton("Sold");

        // Align labels and fields to the center
        productNameLabel.setHorizontalAlignment(JLabel.CENTER);
        amountInStockLabel.setHorizontalAlignment(JLabel.CENTER);
        costToMakeLabel.setHorizontalAlignment(JLabel.CENTER);
        priceToSellLabel.setHorizontalAlignment(JLabel.CENTER);
        colorLabel.setHorizontalAlignment(JLabel.CENTER);
        sizeLabel.setHorizontalAlignment(JLabel.CENTER);
        productNameField.setHorizontalAlignment(JTextField.CENTER);
        amountInStockField.setHorizontalAlignment(JTextField.CENTER);
        costToMakeField.setHorizontalAlignment(JTextField.CENTER);
        priceToSellField.setHorizontalAlignment(JTextField.CENTER);
        colorField.setHorizontalAlignment(JTextField.CENTER);
        sizeField.setHorizontalAlignment(JTextField.CENTER);

        add(productNameLabel);
        add(productNameField);
        add(amountInStockLabel);
        add(amountInStockField);
        add(costToMakeLabel);
        add(costToMakeField);
        add(priceToSellLabel);
        add(priceToSellField);
        add(colorLabel);
        add(colorField);
        add(sizeLabel);
        add(sizeField);
        add(addInventoryButton);
        add(viewInventoryButton);
        add(deleteInventoryButton);
        add(soldButton);

        addInventoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String productName = productNameField.getText();
                int amountInStock = Integer.parseInt(amountInStockField.getText());
                double totalCostToMake = parseCurrencyInput(costToMakeField.getText());
                double priceToSell = parseCurrencyInput(priceToSellField.getText());
                String color = colorField.getText();
                String size = sizeField.getText();

                double totalMadeIfSold = amountInStock * priceToSell;
                totalCostToMake = totalCostToMake * amountInStock;
                double profit = totalMadeIfSold - totalCostToMake;

                System.out.println("Product name: " + productName);
                System.out.println("Amount in stock: " + amountInStock);
                System.out.println("Cost to make: " + totalCostToMake);
                System.out.println("Price to sell: " + priceToSell);
                System.out.println("Total made if sold: " + totalMadeIfSold);
                System.out.println("Profit: " + profit);
                System.out.println("Color: " + color);
                System.out.println("Size: " + size);

                // Save the inventory information to a text file
                saveInventory(productName, amountInStock, totalCostToMake, priceToSell, totalMadeIfSold, profit, color, size);
            }
        });

        viewInventoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Display the inventory from the text file
                viewInventory();
            }
        });

        deleteInventoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Delete the selected inventory entry
                String selectedEntry = (String) JOptionPane.showInputDialog(
                        InventoryGUI.this,
                        "Select an entry to delete:",
                        "Delete Inventory",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        inventoryEntries.toArray(),
                        inventoryEntries.get(0)
                );
                deleteInventory(selectedEntry);
            }
        });

        soldButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Show the list of sold products
                showSoldProducts();
            }
        });

        inventoryEntries = new ArrayList<>(); // Initialize the inventory entries list

        loadInventoryEntries(); // Load existing inventory entries

        pack(); // Automatically adjust the size of the frame to fit the components
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }

    private double parseCurrencyInput(String input) {
        // Remove the dollar sign ($) and any commas from the input string
        String cleanedInput = input.replace("$", "").replace(",", "");
        return Double.parseDouble(cleanedInput);
    }

    private void saveInventory(String productName, int amountInStock, double totalCostToMake, double priceToSell,
                               double totalMadeIfSold, double profit, String color, String size) {
        String inventoryEntry = "Product name: " + productName +
                "\nAmount in stock: " + amountInStock +
                "\nCost to make: $" + totalCostToMake +
                "\nPrice to sell: $" + priceToSell +
                "\nTotal made if sold: $" + totalMadeIfSold +
                "\nProfit: $" + profit +
                "\nColor: " + color +
                "\nSize: " + size +
                "\n---------------------------------\n";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("inventory.txt", true));
            writer.write(inventoryEntry);
            writer.close();
            JOptionPane.showMessageDialog(this, "Inventory saved successfully!");

            inventoryEntries.add(productName); // Add the new entry to the list
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving inventory.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewInventory() {
        try {
            String inventory = new String(Files.readAllBytes(Paths.get("inventory.txt")));
            JTextArea textArea = new JTextArea(inventory);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));
            JOptionPane.showMessageDialog(this, scrollPane, "Inventory List", JOptionPane.PLAIN_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading inventory.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteInventory(String selectedEntry) {
        try {
            File inputFile = new File("inventory.txt");
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;
            boolean entryFound = false;

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.contains("Product name: " + selectedEntry)) {
                    // Skip the inventory entry with the selected product name
                    for (int i = 0; i < 8; i++) {
                        reader.readLine();
                    }
                    entryFound = true;
                } else {
                    writer.write(currentLine + "\n");
                }
            }

            writer.close();
            reader.close();

            if (entryFound) {
                if (inputFile.delete()) {
                    if (tempFile.renameTo(inputFile)) {
                        JOptionPane.showMessageDialog(this, "Inventory deleted successfully!");
                        inventoryEntries.remove(selectedEntry); // Remove the entry from the list
                    } else {
                        JOptionPane.showMessageDialog(this, "Error deleting inventory.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting inventory.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Inventory entry not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting inventory.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSoldProducts() {
        String[] soldProducts = inventoryEntries.toArray(new String[0]);

        String selectedProduct = (String) JOptionPane.showInputDialog(
                InventoryGUI.this,
                "Select a product you sold:",
                "Sold Products",
                JOptionPane.PLAIN_MESSAGE,
                null,
                soldProducts,
                soldProducts[0]
        );

        if (selectedProduct != null) {
            String quantitySoldStr = JOptionPane.showInputDialog(
                    this,
                    "Enter the quantity sold for " + selectedProduct + ":",
                    "Sold Products",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (quantitySoldStr != null) {
                try {
                    int quantitySold = Integer.parseInt(quantitySoldStr);
                    if (quantitySold > 0) {
                        updateSoldQuantity(selectedProduct, quantitySold);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid quantity sold.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid quantity sold.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void updateSoldQuantity(String selectedProduct, int quantitySold) {
        try {
            File inputFile = new File("inventory.txt");
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.contains("Product name: " + selectedProduct)) {
                    // Update the quantity sold and the total made if sold
                    int startIndex = currentLine.lastIndexOf("Amount in stock: ") + 17;
                    int endIndex = currentLine.length();
                    int amountInStock = Integer.parseInt(currentLine.substring(startIndex, endIndex));
                    int newAmountInStock = amountInStock - quantitySold;
                    writer.write(currentLine.replace("Amount in stock: " + amountInStock,
                            "Amount in stock: " + newAmountInStock) + "\n");

                    startIndex = currentLine.lastIndexOf("Total made if sold: $") + 21;
                    endIndex = currentLine.lastIndexOf(", profit");
                    double totalMadeIfSold = Double.parseDouble(currentLine.substring(startIndex, endIndex));
                    double newTotalMadeIfSold = totalMadeIfSold + (quantitySold * getPriceToSell(selectedProduct));
                    writer.write(currentLine.replace("Total made if sold: $" + totalMadeIfSold,
                            "Total made if sold: $" + newTotalMadeIfSold) + "\n");
                } else {
                    writer.write(currentLine + "\n");
                }
            }

            writer.close();
            reader.close();

            if (inputFile.delete()) {
                if (tempFile.renameTo(inputFile)) {
                    JOptionPane.showMessageDialog(this, "Inventory updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error updating inventory.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error updating inventory.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error updating inventory.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double getPriceToSell(String productName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("inventory.txt"));
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.contains("Product name: " + productName)) {
                    String priceToSellLine = reader.readLine();
                    reader.close();
                    int startIndex = priceToSellLine.lastIndexOf("Sell: $") + 7;
                    int endIndex = priceToSellLine.length();
                    return Double.parseDouble(priceToSellLine.substring(startIndex, endIndex));
                }
            }

            reader.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading inventory.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return 0.0;
    }

    private void loadInventoryEntries() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("inventory.txt"));
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.contains("Product name: ")) {
                    String productName = currentLine.replace("Product name: ", "");
                    inventoryEntries.add(productName);
                }
            }

            reader.close();
        } catch (IOException ex) {
            // Handle the exception if the inventory file does not exist yet
        }
    }

    public static void main(String[] args) {
        new InventoryGUI();
    }
}
