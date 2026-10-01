package gui;

import dao.MedicineDAO;
import model.Medicine;
import session.Session;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class ManageMedicinesFrame extends JFrame {
    static final int LOW_STOCK = 10;     // quantity at or below this is "low"
    static final int SOON_DAYS = 30;     // expiry within this many days is "soon"

    MedicineDAO dao = new MedicineDAO();
    JTabbedPane tabs = new JTabbedPane();

    // table models (one for each tab that shows a table)
    String[] cols = {"ID", "Name", "Category", "Price", "Quantity", "Expiry", "Rx Required"};
    DefaultTableModel viewModel = makeModel(cols);
    DefaultTableModel manageModel = makeModel(cols);
    DefaultTableModel searchModel = makeModel(cols);
    DefaultTableModel stockModel = makeModel("ID", "Name", "Category", "Quantity", "Expiry", "Status");

    // dashboard labels
    JLabel totalL = new JLabel("0", JLabel.CENTER);
    JLabel lowL = new JLabel("0", JLabel.CENTER);
    JLabel outL = new JLabel("0", JLabel.CENTER);
    JLabel expiredL = new JLabel("0", JLabel.CENTER);
    JLabel soonL = new JLabel("0", JLabel.CENTER);
    JLabel valueL = new JLabel("0", JLabel.CENTER);

    // form fields: name, category, price, quantity, expiry  (+ a Yes/No box for prescription)
    JTextField[] addF = newFields();
    JComboBox<String> addRx = newRxBox();
    JTextField[] manF = newFields();
    JComboBox<String> manRx = newRxBox();
    JTable manageTable = new JTable(manageModel);
    int selectedId = 0;

    JTextField searchField = new JTextField(20);
    JComboBox<String> filterBox = new JComboBox<>(new String[]{"All Alerts", "Low Stock", "Expired", "Expiring Soon"});

    public ManageMedicinesFrame() {
        // access control: only ADMIN can open this page
        if (!"ADMIN".equals(Session.role)) {
            JOptionPane.showMessageDialog(null, "Access denied. Please login as Admin.");
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Manage Medicines");
        setSize(900, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabs.addTab("Dashboard", dashboardTab());
        tabs.addTab("Add Medicine", addTab());
        tabs.addTab("Manage Medicine", manageTab());
        tabs.addTab("Search Medicine", searchTab());
        tabs.addTab("View Medicines", viewTab());
        tabs.addTab("Stock & Expiry", stockTab());
        tabs.addChangeListener(e -> refreshAll());

        JButton backBtn = new JButton("Back to Admin Home");
        backBtn.addActionListener(e -> {
            new AdminHomeFrame().setVisible(true);
            dispose();
        });
        JPanel bottom = new JPanel();
        bottom.add(backBtn);

        add(tabs, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        refreshAll();
    }

    // ---------------- tabs ----------------

    JPanel dashboardTab() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        grid.add(card("Total Medicines", totalL));
        grid.add(card("Low Stock (<= " + LOW_STOCK + ")", lowL));
        grid.add(card("Out of Stock", outL));
        grid.add(card("Expired", expiredL));
        grid.add(card("Expiring in " + SOON_DAYS + " Days", soonL));
        grid.add(card("Total Stock Value", valueL));
        return grid;
    }

    JPanel addTab() {
        JButton saveBtn = new JButton("Save Medicine");
        JButton clearBtn = new JButton("Clear");
        saveBtn.addActionListener(e -> addMedicine());
        clearBtn.addActionListener(e -> clear(addF, addRx));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btns.add(saveBtn);
        btns.add(clearBtn);

        JPanel box = new JPanel(new BorderLayout());
        box.add(formPanel(addF, addRx), BorderLayout.CENTER);
        box.add(btns, BorderLayout.SOUTH);

        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        p.add(box);
        return p;
    }

    JPanel manageTab() {
        // clicking a row fills the form below
        manageTable.getSelectionModel().addListSelectionListener(e -> {
            int r = manageTable.getSelectedRow();
            if (e.getValueIsAdjusting() || r == -1) return;
            selectedId = (int) manageModel.getValueAt(r, 0);
            for (int i = 0; i < 5; i++) {
                manF[i].setText(manageModel.getValueAt(r, i + 1).toString());
            }
            manRx.setSelectedItem(manageModel.getValueAt(r, 6).toString());
        });

        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        updateBtn.addActionListener(e -> updateMedicine());
        deleteBtn.addActionListener(e -> deleteMedicine());
        clearBtn.addActionListener(e -> clearManage());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btns.add(updateBtn);
        btns.add(deleteBtn);
        btns.add(clearBtn);

        JPanel south = new JPanel(new BorderLayout());
        south.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        south.add(formPanel(manF, manRx), BorderLayout.CENTER);
        south.add(btns, BorderLayout.SOUTH);

        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(manageTable), BorderLayout.CENTER);
        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    JPanel searchTab() {
        JButton goBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        goBtn.addActionListener(e -> search());
        searchField.addActionListener(e -> search());
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            searchModel.setRowCount(0);
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        top.add(new JLabel("Medicine name or category:"));
        top.add(searchField);
        top.add(goBtn);
        top.add(clearBtn);

        JPanel p = new JPanel(new BorderLayout());
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(new JTable(searchModel)), BorderLayout.CENTER);
        return p;
    }

    JPanel viewTab() {
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshAll());
        JPanel bottom = new JPanel();
        bottom.add(refreshBtn);

        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(new JTable(viewModel)), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    JPanel stockTab() {
        filterBox.addActionListener(e -> refreshAll());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        top.add(new JLabel("Show:"));
        top.add(filterBox);
        top.add(new JLabel("(Low stock = " + LOW_STOCK + " or less, Expiring soon = within "
                + SOON_DAYS + " days)"));

        JPanel p = new JPanel(new BorderLayout());
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(new JTable(stockModel)), BorderLayout.CENTER);
        return p;
    }

    // ---------------- actions ----------------

    void addMedicine() {
        Medicine m = readForm(addF, addRx, 0);
        if (m == null) return;

        if (LocalDate.parse(m.getExpiry()).isBefore(LocalDate.now())) {
            msg("Cannot add an already expired medicine.");
            return;
        }
        try {
            if (dao.nameExists(m.getName())) {
                msg("This medicine already exists. Use Manage Medicine to update it.");
                return;
            }
            dao.add(m);
            msg("Medicine added successfully.");
            clear(addF, addRx);
            refreshAll();
        } catch (SQLException ex) {
            msg("Database error. Could not add the medicine.");
        }
    }

    void updateMedicine() {
        if (selectedId == 0) { msg("Please select a medicine from the table first."); return; }
        Medicine m = readForm(manF, manRx, selectedId);
        if (m == null) return;
        try {
            dao.update(m);
            msg("Medicine updated successfully.");
            clearManage();
            refreshAll();
        } catch (SQLException ex) {
            msg("Database error. Could not update the medicine.");
        }
    }

    void deleteMedicine() {
        if (selectedId == 0) { msg("Please select a medicine from the table first."); return; }
        int ok = JOptionPane.showConfirmDialog(this, "Delete this medicine?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(selectedId);
            msg("Medicine deleted successfully.");
            clearManage();
            refreshAll();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                // foreign key error: medicine is used in old prescriptions
                msg("This medicine is used in prescriptions and cannot be deleted.");
            } else {
                msg("Database error. Could not delete the medicine.");
            }
        }
    }

    void search() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) { msg("Please enter a medicine name or category."); return; }
        try {
            ArrayList<Medicine> list = dao.search(text);
            fill(searchModel, list);
            if (list.isEmpty()) msg("No medicines found.");
        } catch (SQLException ex) {
            msg("Database error. Could not search medicines.");
        }
    }

    // reloads every table and the dashboard from the database
    void refreshAll() {
        try {
            ArrayList<Medicine> list = dao.getAll();
            fillDashboard(list);
            fill(viewModel, list);
            fill(manageModel, list);
            fillStock(list);
        } catch (SQLException ex) {
            msg("Cannot load medicines. Please check the database connection.");
        }
    }

    // ---------------- filling tables ----------------

    void fill(DefaultTableModel m, ArrayList<Medicine> list) {
        m.setRowCount(0);
        for (Medicine x : list) {
            m.addRow(new Object[]{x.getId(), x.getName(), x.getCategory(),
                    String.format("%.2f", x.getPrice()), x.getQuantity(), x.getExpiry(),
                    x.isPrescriptionRequired() ? "Yes" : "No"});
        }
    }

    void fillStock(ArrayList<Medicine> list) {
        stockModel.setRowCount(0);
        String f = (String) filterBox.getSelectedItem();
        for (Medicine x : list) {
            boolean show;
            if (f.equals("Low Stock")) show = isLow(x);
            else if (f.equals("Expired")) show = isExpired(x);
            else if (f.equals("Expiring Soon")) show = isSoon(x);
            else show = isLow(x) || isExpired(x) || isSoon(x);

            if (show) {
                stockModel.addRow(new Object[]{x.getId(), x.getName(), x.getCategory(),
                        x.getQuantity(), x.getExpiry(), status(x)});
            }
        }
    }

    void fillDashboard(ArrayList<Medicine> list) {
        int low = 0, out = 0, expired = 0, soon = 0;
        double value = 0;
        for (Medicine x : list) {
            if (isLow(x)) low++;
            if (x.getQuantity() == 0) out++;
            if (isExpired(x)) expired++;
            if (isSoon(x)) soon++;
            value += x.getPrice() * x.getQuantity();
        }
        totalL.setText("" + list.size());
        lowL.setText("" + low);
        outL.setText("" + out);
        expiredL.setText("" + expired);
        soonL.setText("" + soon);
        valueL.setText("Rs. " + String.format("%.2f", value));
    }

    // ---------------- stock / expiry checks ----------------

    boolean isLow(Medicine m) {
        return m.getQuantity() <= LOW_STOCK;
    }

    boolean isExpired(Medicine m) {
        return LocalDate.parse(m.getExpiry()).isBefore(LocalDate.now());
    }

    boolean isSoon(Medicine m) {
        LocalDate d = LocalDate.parse(m.getExpiry());
        return !d.isBefore(LocalDate.now()) && !d.isAfter(LocalDate.now().plusDays(SOON_DAYS));
    }

    String status(Medicine m) {
        String s = "";
        if (isExpired(m)) s += "EXPIRED ";
        else if (isSoon(m)) s += "EXPIRING SOON ";
        if (m.getQuantity() == 0) s += "OUT OF STOCK";
        else if (isLow(m)) s += "LOW STOCK";
        return s.isEmpty() ? "OK" : s.trim();
    }

    // ---------------- form helpers ----------------

    static JTextField[] newFields() {
        JTextField[] f = new JTextField[5];
        for (int i = 0; i < 5; i++) f[i] = new JTextField(14);
        return f;
    }

    static JComboBox<String> newRxBox() {
        return new JComboBox<>(new String[]{"No", "Yes"});
    }

    JPanel formPanel(JTextField[] f, JComboBox<String> rx) {
        String[] names = {"Medicine Name:", "Category:", "Price (Rs.):",
                "Quantity:", "Expiry (yyyy-MM-dd):"};
        JPanel p = new JPanel(new GridLayout(3, 4, 10, 10));
        for (int i = 0; i < 5; i++) {
            p.add(new JLabel(names[i]));
            p.add(f[i]);
        }
        p.add(new JLabel("Prescription Required:"));
        p.add(rx);
        return p;
    }

    // reads and validates the form; returns null if something is wrong
    Medicine readForm(JTextField[] f, JComboBox<String> rx, int id) {
        String name = f[0].getText().trim();
        String cat = f[1].getText().trim();

        if (name.isEmpty()) { msg("Medicine name cannot be empty."); return null; }
        if (cat.isEmpty()) { msg("Category cannot be empty."); return null; }

        double price;
        int qty;
        LocalDate exp;
        try {
            price = Double.parseDouble(f[2].getText().trim());
        } catch (NumberFormatException e) {
            msg("Price must be a number.");
            return null;
        }
        if (price <= 0) { msg("Price must be greater than 0."); return null; }

        try {
            qty = Integer.parseInt(f[3].getText().trim());
        } catch (NumberFormatException e) {
            msg("Quantity must be a whole number.");
            return null;
        }
        if (qty < 0) { msg("Quantity cannot be negative."); return null; }

        try {
            exp = LocalDate.parse(f[4].getText().trim());
        } catch (DateTimeParseException e) {
            msg("Expiry date must be in yyyy-MM-dd format, e.g. 2027-12-31.");
            return null;
        }

        boolean rxRequired = rx.getSelectedItem().equals("Yes");
        return new Medicine(id, name, cat, price, qty, exp.toString(), rxRequired);
    }

    void clear(JTextField[] f, JComboBox<String> rx) {
        for (JTextField t : f) t.setText("");
        rx.setSelectedIndex(0);
        f[0].requestFocus();
    }

    void clearManage() {
        clear(manF, manRx);
        selectedId = 0;
        manageTable.clearSelection();
    }

    JPanel card(String title, JLabel value) {
        value.setFont(new Font("Arial", Font.BOLD, 28));
        JLabel t = new JLabel(title, JLabel.CENTER);
        t.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        p.add(value, BorderLayout.CENTER);
        p.add(t, BorderLayout.SOUTH);
        return p;
    }

    static DefaultTableModel makeModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
    }

    void msg(String text) {
        JOptionPane.showMessageDialog(this, text);
    }
}