package medicine;

import javax.swing.*;
import java.awt.*;

public class MedicineGUI {

    static JFrame frame;
    static JPanel contentPanel;

    // CHANGED: was main(String[] args). Now opened from the Admin dashboard.
    public static void open() {

        // ==============================
        // MAIN WINDOW
        // ==============================

        frame = new JFrame("Medicine Management");

        frame.setSize(950, 650);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLocationRelativeTo(null);

        frame.setLayout(new BorderLayout());

        // ==============================
        // LEFT NAVIGATION BAR
        // ==============================

        JPanel menuPanel = new JPanel();

        menuPanel.setPreferredSize(
                new Dimension(190, 650));

        menuPanel.setLayout(
                new GridLayout(7, 1, 5, 5));

        menuPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 10, 15, 10));

        JButton dashboardButton = new JButton("Dashboard");

        JButton addMedicineButton = new JButton("Add Medicine");

        JButton manageButton = new JButton("Manage Medicines");

        JButton searchButton = new JButton("Search Medicine");

        JButton viewButton = new JButton("View Medicines");

        JButton stockExpiryButton = new JButton("Stock & Expiry");

        // ADDED: return to the Admin dashboard
        JButton backButton = new JButton("Back to Admin Home");

        // Smaller navbar text

        dashboardButton.setFont(
                new Font("Arial", Font.PLAIN, 13));

        addMedicineButton.setFont(
                new Font("Arial", Font.PLAIN, 13));

        manageButton.setFont(
                new Font("Arial", Font.PLAIN, 13));

        searchButton.setFont(
                new Font("Arial", Font.PLAIN, 13));

        viewButton.setFont(
                new Font("Arial", Font.PLAIN, 13));

        stockExpiryButton.setFont(
                new Font("Arial", Font.PLAIN, 13));

        backButton.setFont(
                new Font("Arial", Font.PLAIN, 13));

        menuPanel.add(dashboardButton);
        menuPanel.add(addMedicineButton);
        menuPanel.add(manageButton);
        menuPanel.add(searchButton);
        menuPanel.add(viewButton);
        menuPanel.add(stockExpiryButton);
        menuPanel.add(backButton);

        // ==============================
        // CONTENT AREA
        // ==============================

        contentPanel = new JPanel();

        contentPanel.setLayout(
                new BorderLayout());

        contentPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 25, 20, 25));

        frame.add(
                menuPanel,
                BorderLayout.WEST);

        frame.add(
                contentPanel,
                BorderLayout.CENTER);

        // ==============================
        // NAVIGATION
        // ==============================

        dashboardButton.addActionListener(e -> showDashboard());

        addMedicineButton.addActionListener(e -> showAddMedicine());

        manageButton.addActionListener(e -> showManageMedicines());

        searchButton.addActionListener(e -> showSearchMedicine());

        viewButton.addActionListener(e -> showViewMedicines());

        stockExpiryButton.addActionListener(e -> showStockExpiry());

        backButton.addActionListener(e -> {
            frame.dispose();
            new gui.AdminHomeFrame().setVisible(true);
        });

        showDashboard();

        frame.setVisible(true);
    }

    // =====================================================
    // DASHBOARD
    // =====================================================

    // =====================================================
    // DASHBOARD
    // =====================================================

    static void showDashboard() {

        contentPanel.removeAll();

        JLabel title = new JLabel(
                "Medicine Management System",
                SwingConstants.CENTER);

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        26));

        contentPanel.setLayout(
                new BorderLayout());

        contentPanel.add(
                title,
                BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }
    // =====================================================
    // ADD MEDICINE
    // =====================================================

    static void showAddMedicine() {

        contentPanel.removeAll();

        JPanel panel = new JPanel();

        panel.setLayout(
                new GridLayout(
                        9,
                        2,
                        10,
                        10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        40,
                        20,
                        40));

        JLabel title = new JLabel("Add Medicine");

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22));

        JTextField idField = new JTextField();

        JTextField nameField = new JTextField();

        JTextField categoryField = new JTextField();

        JTextField priceField = new JTextField();

        JTextField quantityField = new JTextField();

        JTextField expiryField = new JTextField();

        JCheckBox prescriptionBox = new JCheckBox("Yes");

        JButton addButton = new JButton("Add Medicine");

        // Field size

        idField.setPreferredSize(
                new Dimension(200, 28));

        nameField.setPreferredSize(
                new Dimension(200, 28));

        categoryField.setPreferredSize(
                new Dimension(200, 28));

        priceField.setPreferredSize(
                new Dimension(200, 28));

        quantityField.setPreferredSize(
                new Dimension(200, 28));

        expiryField.setPreferredSize(
                new Dimension(200, 28));

        panel.add(title);
        panel.add(new JLabel(""));

        panel.add(
                new JLabel("Medicine ID:"));

        panel.add(idField);

        panel.add(
                new JLabel("Medicine Name:"));

        panel.add(nameField);

        panel.add(
                new JLabel("Category:"));

        panel.add(categoryField);

        panel.add(
                new JLabel("Price:"));

        panel.add(priceField);

        panel.add(
                new JLabel("Quantity:"));

        panel.add(quantityField);

        panel.add(
                new JLabel("Expiry Date:"));

        panel.add(expiryField);

        panel.add(
                new JLabel(
                        "Prescription Required:"));

        panel.add(prescriptionBox);

        panel.add(new JLabel(""));
        panel.add(addButton);

        // ==============================
        // ADD TO MYSQL
        // ==============================

        addButton.addActionListener(e -> {

            try {

                int id = Integer.parseInt(
                        idField.getText());

                String name = nameField.getText();

                String category = categoryField.getText();

                double price = Double.parseDouble(
                        priceField.getText());

                int quantity = Integer.parseInt(
                        quantityField.getText());

                String expiryDate = expiryField.getText();

                boolean prescriptionRequired = prescriptionBox.isSelected();

                Medicine medicine = new Medicine(
                        id,
                        name,
                        category,
                        price,
                        quantity,
                        expiryDate,
                        prescriptionRequired);

                MedicineDAO dao = new MedicineDAO();

                boolean result = dao.addMedicine(medicine);

                if (result) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Medicine added successfully!");

                    idField.setText("");
                    nameField.setText("");
                    categoryField.setText("");
                    priceField.setText("");
                    quantityField.setText("");
                    expiryField.setText("");

                    prescriptionBox.setSelected(false);

                } else {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Medicine could not be added.");
                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter valid details.");
            }
        });

        contentPanel.add(
                panel,
                BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // =====================================================
    // MANAGE MEDICINES
    // =====================================================

    // =====================================================
    // MANAGE MEDICINES
    // =====================================================

    static void showManageMedicines() {

        contentPanel.removeAll();

        JPanel mainPanel = new JPanel();

        mainPanel.setLayout(
                new BoxLayout(
                        mainPanel,
                        BoxLayout.Y_AXIS));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        40,
                        80,
                        40,
                        80));

        JLabel title = new JLabel(
                "Manage Medicines");

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        23));

        title.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        mainPanel.add(title);

        mainPanel.add(
                Box.createVerticalStrut(25));

        // ==============================
        // SELECT MEDICINE
        // ==============================

        JPanel selectionPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        10,
                        5));

        JLabel selectLabel = new JLabel(
                "Select Medicine:");

        JComboBox<String> medicineList = new JComboBox<>();

        medicineList.setPreferredSize(
                new Dimension(
                        200,
                        28));

        // First option

        medicineList.addItem(
                "Select Medicine");

        // ==============================
        // LOAD MEDICINES FROM MYSQL
        // ==============================

        MedicineDAO dao = new MedicineDAO();

        java.util.ArrayList<Medicine> medicines = dao.getAllMedicines();

        for (Medicine medicine : medicines) {

            medicineList.addItem(
                    medicine.getId()
                            + " - "
                            + medicine.getName());
        }

        selectionPanel.add(selectLabel);

        selectionPanel.add(medicineList);

        mainPanel.add(selectionPanel);

        mainPanel.add(
                Box.createVerticalStrut(15));

        // ==============================
        // UPDATE / DELETE BUTTONS
        // ==============================

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        12,
                        5));

        JButton updateButton = new JButton("Update");

        JButton deleteButton = new JButton("Delete");

        updateButton.setPreferredSize(
                new Dimension(
                        100,
                        30));

        deleteButton.setPreferredSize(
                new Dimension(
                        100,
                        30));

        updateButton.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13));

        deleteButton.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13));

        buttonPanel.add(updateButton);

        buttonPanel.add(deleteButton);

        mainPanel.add(buttonPanel);

        // =====================================================
        // UPDATE BUTTON
        // =====================================================

        updateButton.addActionListener(e -> {

            if (medicineList.getSelectedIndex() == 0) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please select a medicine.");

                return;
            }

            String selected = medicineList
                    .getSelectedItem()
                    .toString();

            // Get ID from "101 - Paracetamol"

            int id = Integer.parseInt(
                    selected
                            .split(" - ")[0]);

            showUpdateMedicine(id);
        });

        // =====================================================
        // DELETE BUTTON
        // =====================================================

        deleteButton.addActionListener(e -> {

            if (medicineList.getSelectedIndex() == 0) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please select a medicine.");

                return;
            }

            String selected = medicineList
                    .getSelectedItem()
                    .toString();

            int id = Integer.parseInt(
                    selected
                            .split(" - ")[0]);

            String medicineName = selected
                    .substring(
                            selected.indexOf(" - ") + 3);

            int choice = JOptionPane.showConfirmDialog(
                    frame,
                    "Delete "
                            + medicineName
                            + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {

                MedicineDAO deleteDAO = new MedicineDAO();

                boolean result = deleteDAO.deleteMedicine(id);

                if (result) {

                    JOptionPane.showMessageDialog(
                            frame,
                            medicineName
                                    + " deleted successfully.");

                    // Refresh the Manage Medicines screen

                    showManageMedicines();

                } else {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Medicine could not be deleted.");
                }
            }
        });

        contentPanel.add(
                mainPanel,
                BorderLayout.CENTER);

        contentPanel.revalidate();

        contentPanel.repaint();
    }

    // =====================================================
    // UPDATE MEDICINE
    // =====================================================

    static void showUpdateMedicine(int id) {

        contentPanel.removeAll();

        // ==============================
        // GET MEDICINE FROM MYSQL
        // ==============================

        MedicineDAO dao = new MedicineDAO();

        Medicine medicine = dao.searchMedicineById(id);

        if (medicine == null) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Medicine not found.");

            showManageMedicines();

            return;
        }

        JPanel mainPanel = new JPanel();

        mainPanel.setLayout(
                new BorderLayout());

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        50,
                        20,
                        50));

        // ==============================
        // TITLE
        // ==============================

        JLabel title = new JLabel(
                "Update Medicine",
                SwingConstants.CENTER);

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22));

        mainPanel.add(
                title,
                BorderLayout.NORTH);

        // ==============================
        // FORM
        // ==============================

        JPanel formPanel = new JPanel();

        formPanel.setLayout(
                new GridLayout(
                        7,
                        2,
                        10,
                        10));

        // ID

        JTextField idField = new JTextField(
                String.valueOf(
                        medicine.getId()));

        idField.setEditable(false);

        // Name

        JTextField nameField = new JTextField(
                medicine.getName());

        // Category

        JTextField categoryField = new JTextField(
                medicine.getCategory());

        // Price

        JTextField priceField = new JTextField(
                String.valueOf(
                        medicine.getPrice()));

        // Quantity

        JTextField quantityField = new JTextField(
                String.valueOf(
                        medicine.getQuantity()));

        // Expiry

        JTextField expiryField = new JTextField(
                medicine.getExpiryDate());

        // Prescription

        JCheckBox prescriptionBox = new JCheckBox("Yes");

        prescriptionBox.setSelected(
                medicine.isPrescriptionRequired());

        formPanel.add(
                new JLabel("Medicine ID:"));

        formPanel.add(idField);

        formPanel.add(
                new JLabel("Medicine Name:"));

        formPanel.add(nameField);

        formPanel.add(
                new JLabel("Category:"));

        formPanel.add(categoryField);

        formPanel.add(
                new JLabel("Price:"));

        formPanel.add(priceField);

        formPanel.add(
                new JLabel("Quantity:"));

        formPanel.add(quantityField);

        formPanel.add(
                new JLabel("Expiry Date:"));

        formPanel.add(expiryField);

        formPanel.add(
                new JLabel(
                        "Prescription Required:"));

        formPanel.add(
                prescriptionBox);

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER);

        // ==============================
        // UPDATE BUTTON
        // ==============================

        JButton updateButton = new JButton(
                "Update Medicine");

        updateButton.setPreferredSize(
                new Dimension(
                        140,
                        30));

        updateButton.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13));

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER));

        buttonPanel.add(
                updateButton);

        mainPanel.add(
                buttonPanel,
                BorderLayout.SOUTH);

        // =====================================================
        // UPDATE MYSQL
        // =====================================================

        updateButton.addActionListener(e -> {

            try {

                String name = nameField
                        .getText()
                        .trim();

                String category = categoryField
                        .getText()
                        .trim();

                double price = Double.parseDouble(
                        priceField
                                .getText()
                                .trim());

                int quantity = Integer.parseInt(
                        quantityField
                                .getText()
                                .trim());

                String expiryDate = expiryField
                        .getText()
                        .trim();

                boolean prescription = prescriptionBox
                        .isSelected();

                Medicine updatedMedicine = new Medicine(
                        id,
                        name,
                        category,
                        price,
                        quantity,
                        expiryDate,
                        prescription);

                MedicineDAO updateDAO = new MedicineDAO();

                boolean result = updateDAO.updateMedicine(
                        updatedMedicine);

                if (result) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Medicine updated successfully!");

                    // Return to Manage Medicines

                    showManageMedicines();

                } else {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Medicine could not be updated.");
                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter valid details.");
            }
        });

        contentPanel.add(
                mainPanel,
                BorderLayout.CENTER);

        contentPanel.revalidate();

        contentPanel.repaint();
    }

    // =====================================================
    // SEARCH MEDICINE
    // =====================================================

    static void showSearchMedicine() {

        contentPanel.removeAll();

        JPanel mainPanel = new JPanel();

        mainPanel.setLayout(
                new BoxLayout(
                        mainPanel,
                        BoxLayout.Y_AXIS));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        40, 60, 40, 60));

        // ==============================
        // TITLE
        // ==============================

        JLabel title = new JLabel("Search Medicine");

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        23));

        title.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        mainPanel.add(title);

        mainPanel.add(
                Box.createVerticalStrut(25));

        // ==============================
        // SEARCH AREA
        // ==============================

        JPanel searchPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        10,
                        5));

        JLabel searchLabel = new JLabel(
                "Medicine Name or ID:");

        JTextField searchField = new JTextField();

        searchField.setPreferredSize(
                new Dimension(200, 28));

        JButton searchButton = new JButton("Search");

        searchButton.setPreferredSize(
                new Dimension(85, 28));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        mainPanel.add(searchPanel);

        mainPanel.add(
                Box.createVerticalStrut(20));

        // ==============================
        // RESULT AREA
        // ==============================

        JTextArea resultArea = new JTextArea();

        resultArea.setEditable(false);

        resultArea.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14));

        resultArea.setBorder(
                BorderFactory.createLineBorder(
                        Color.GRAY));

        JScrollPane resultScroll = new JScrollPane(
                resultArea);

        resultScroll.setPreferredSize(
                new Dimension(500, 200));

        mainPanel.add(resultScroll);

        // ==============================
        // SEARCH BUTTON ACTION
        // ==============================

        searchButton.addActionListener(e -> {

            String searchText = searchField.getText().trim();

            if (searchText.isEmpty()) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter a medicine name or ID.");

                return;
            }

            MedicineDAO dao = new MedicineDAO();

            // Clear previous result

            resultArea.setText("");

            // ==============================
            // SEARCH BY ID
            // ==============================

            try {

                int id = Integer.parseInt(
                        searchText);

                Medicine medicine = dao.searchMedicineById(id);

                if (medicine != null) {

                    resultArea.append(
                            "Medicine ID: "
                                    + medicine.getId()
                                    + "\n");

                    resultArea.append(
                            "Name: "
                                    + medicine.getName()
                                    + "\n");

                    resultArea.append(
                            "Category: "
                                    + medicine.getCategory()
                                    + "\n");

                    resultArea.append(
                            "Price: "
                                    + medicine.getPrice()
                                    + "\n");

                    resultArea.append(
                            "Quantity: "
                                    + medicine.getQuantity()
                                    + "\n");

                    resultArea.append(
                            "Expiry Date: "
                                    + medicine.getExpiryDate()
                                    + "\n");

                    resultArea.append(
                            "Prescription Required: "
                                    + (medicine.isPrescriptionRequired()
                                            ? "Yes"
                                            : "No"));

                } else {

                    resultArea.setText(
                            "No medicine found with ID: "
                                    + id);
                }

            } catch (NumberFormatException ex) {

                // ==============================
                // SEARCH BY NAME
                // ==============================

                java.util.ArrayList<Medicine> medicines = dao.searchMedicineByName(
                        searchText);

                if (medicines.isEmpty()) {

                    resultArea.setText(
                            "No medicine found with name: "
                                    + searchText);

                } else {

                    for (Medicine medicine : medicines) {

                        resultArea.append(
                                "Medicine ID: "
                                        + medicine.getId()
                                        + "\n");

                        resultArea.append(
                                "Name: "
                                        + medicine.getName()
                                        + "\n");

                        resultArea.append(
                                "Category: "
                                        + medicine.getCategory()
                                        + "\n");

                        resultArea.append(
                                "Price: "
                                        + medicine.getPrice()
                                        + "\n");

                        resultArea.append(
                                "Quantity: "
                                        + medicine.getQuantity()
                                        + "\n");

                        resultArea.append(
                                "Expiry Date: "
                                        + medicine.getExpiryDate()
                                        + "\n");

                        resultArea.append(
                                "Prescription Required: "
                                        + (medicine.isPrescriptionRequired()
                                                ? "Yes"
                                                : "No"));

                        resultArea.append(
                                "\n\n-------------------------\n\n");
                    }
                }
            }
        });

        // ==============================
        // ADD TO CONTENT PANEL
        // ==============================

        contentPanel.add(
                mainPanel,
                BorderLayout.CENTER);

        contentPanel.revalidate();

        contentPanel.repaint();
    }
    // =====================================================
    // VIEW MEDICINES
    // =====================================================

    static void showViewMedicines() {

        contentPanel.removeAll();

        JLabel title = new JLabel(
                "All Medicines",
                SwingConstants.CENTER);

        title.setFont(
                new Font("Arial", Font.BOLD, 23));

        // Get medicines from MySQL
        MedicineDAO dao = new MedicineDAO();

        java.util.ArrayList<Medicine> medicines = dao.getAllMedicines();

        // Table column names
        String[] columns = {
                "ID",
                "Name",
                "Category",
                "Price",
                "Quantity",
                "Expiry Date",
                "Prescription"
        };

        // Create table data
        String[][] data = new String[medicines.size()][7];

        for (int i = 0; i < medicines.size(); i++) {

            Medicine medicine = medicines.get(i);

            data[i][0] = String.valueOf(
                    medicine.getId());

            data[i][1] = medicine.getName();

            data[i][2] = medicine.getCategory();

            data[i][3] = String.valueOf(
                    medicine.getPrice());

            data[i][4] = String.valueOf(
                    medicine.getQuantity());

            data[i][5] = medicine.getExpiryDate();

            data[i][6] = medicine.isPrescriptionRequired()
                    ? "Yes"
                    : "No";
        }

        // Create table
        JTable table = new JTable(
                data,
                columns);

        table.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(table);

        contentPanel.add(
                title,
                BorderLayout.NORTH);

        contentPanel.add(
                scrollPane,
                BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }
    // =====================================================
    // STOCK & EXPIRY
    // =====================================================

    // =====================================================
    // STOCK & EXPIRY
    // =====================================================

    static void showStockExpiry() {

        contentPanel.removeAll();

        JLabel title = new JLabel(
                "Stock & Expiry",
                SwingConstants.CENTER);

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        23));

        // ==============================
        // GET DATA FROM MYSQL
        // ==============================

        MedicineDAO dao = new MedicineDAO();

        java.util.ArrayList<Medicine> medicines = dao.getStockAndExpiry();

        // ==============================
        // TABLE COLUMNS
        // ==============================

        String[] columns = {

                "ID",
                "Medicine",
                "Quantity",
                "Expiry Date",
                "Status"

        };

        // ==============================
        // CREATE TABLE DATA
        // ==============================

        String[][] data = new String[medicines.size()][5];

        for (int i = 0; i < medicines.size(); i++) {

            Medicine medicine = medicines.get(i);

            data[i][0] = String.valueOf(
                    medicine.getId());

            data[i][1] = medicine.getName();

            data[i][2] = String.valueOf(
                    medicine.getQuantity());

            data[i][3] = medicine.getExpiryDate();

            // ==============================
            // STOCK / EXPIRY STATUS
            // ==============================

            int quantity = medicine.getQuantity();

            String expiryDate = medicine.getExpiryDate();

            java.time.LocalDate expiry = java.time.LocalDate.parse(
                    expiryDate);

            java.time.LocalDate today = java.time.LocalDate.now();

            if (expiry.isBefore(today)) {

                data[i][4] = "Expired";

            } else if (quantity <= 10) {

                data[i][4] = "Low Stock";

            } else {

                data[i][4] = "Available";
            }
        }

        // ==============================
        // CREATE TABLE
        // ==============================

        JTable table = new JTable(
                data,
                columns);

        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(
                table);

        // ==============================
        // ADD TO CONTENT PANEL
        // ==============================

        contentPanel.add(
                title,
                BorderLayout.NORTH);

        contentPanel.add(
                scrollPane,
                BorderLayout.CENTER);

        contentPanel.revalidate();

        contentPanel.repaint();
    }
}