package gui;

import dao.BillingReportsDAO;
import dao.UserDAO;
import model.User;
import session.Session;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class BillingReportsFrame extends JFrame {
    BillingReportsDAO dao = new BillingReportsDAO();

    JComboBox<String> pharmaBox = new JComboBox<>();
    ArrayList<Integer> pharmaIds = new ArrayList<>();   // pharmacist id for each combo item (0 = all)

    DefaultTableModel billModel = makeModel("Bill ID", "Prescription ID", "Pharmacist ID", "Bill Date", "Bill Amount");
    DefaultTableModel medModel = makeModel("Pharmacist ID", "Pharmacist", "Medicine", "Quantity Sold", "Amount");
    DefaultTableModel sumModel = makeModel("Pharmacist ID", "Pharmacist", "Bills Handled", "Medicine Units", "Total Billed");

    public BillingReportsFrame() {
        // access control: only ADMIN can open this page
        if (!"ADMIN".equals(Session.role)) {
            JOptionPane.showMessageDialog(null, "Access denied. Please login as Admin.");
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Billing Reports");
        setSize(850, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // fill the pharmacist drop-down
        pharmaBox.addItem("All Pharmacists");
        pharmaIds.add(0);
        try {
            for (User u : new UserDAO().getAllUsers()) {
                if (u.getRole().equals("PHARMACIST")) {
                    pharmaBox.addItem(u.getUsername() + " (ID " + u.getId() + ")");
                    pharmaIds.add(u.getId());
                }
            }
        } catch (SQLException ex) {
            msg("Cannot load pharmacists. Please check the database connection.");
        }

        JLabel title = new JLabel("Billing Reports");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        JButton refreshBtn = new JButton("Refresh");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        top.add(title);
        top.add(new JLabel("Pharmacist:"));
        top.add(pharmaBox);
        top.add(refreshBtn);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Bills", new JScrollPane(new JTable(billModel)));
        tabs.addTab("Medicines Handled", new JScrollPane(new JTable(medModel)));
        tabs.addTab("Pharmacist Summary", new JScrollPane(new JTable(sumModel)));

        JButton backBtn = new JButton("Back to Admin Home");
        JPanel bottom = new JPanel();
        bottom.add(backBtn);

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        pharmaBox.addActionListener(e -> load());
        refreshBtn.addActionListener(e -> load());
        backBtn.addActionListener(e -> {
            new AdminHomeFrame().setVisible(true);
            dispose();
        });

        load();
    }

    // loads all three tables for the selected pharmacist
    void load() {
        int pid = pharmaIds.get(pharmaBox.getSelectedIndex());
        try {
            // 1. bills (the last row shows the total)
            billModel.setRowCount(0);
            double billTotal = 0;
            for (Object[] r : dao.getBills(pid)) {
                billTotal += (Double) r[4];
                billModel.addRow(new Object[]{r[0], r[1], r[2], r[3], money((Double) r[4])});
            }
            billModel.addRow(new Object[]{"", "", "", "TOTAL", money(billTotal)});

            // 2. medicines handled
            medModel.setRowCount(0);
            int units = 0;
            double medTotal = 0;
            for (Object[] r : dao.getMedicinesHandled(pid)) {
                units += (Integer) r[3];
                medTotal += (Double) r[4];
                medModel.addRow(new Object[]{r[0], r[1], r[2], r[3], money((Double) r[4])});
            }
            medModel.addRow(new Object[]{"", "", "TOTAL", units, money(medTotal)});

            // 3. summary of every pharmacist
            sumModel.setRowCount(0);
            int bills = 0, sumUnits = 0;
            double grand = 0;
            for (Object[] r : dao.getSummary()) {
                bills += (Integer) r[2];
                sumUnits += (Integer) r[3];
                grand += (Double) r[4];
                sumModel.addRow(new Object[]{r[0], r[1], r[2], r[3], money((Double) r[4])});
            }
            sumModel.addRow(new Object[]{"", "TOTAL", bills, sumUnits, money(grand)});

        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1146) {
                msg("Billing tables not found. Please run billing_reports.sql in MySQL first.");
            } else {
                msg("Cannot load the billing report. Please check the database connection.");
            }
        }
    }

    String money(double d) {
        return String.format("%.2f", d);
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