package gui;

import session.Session;
import javax.swing.*;
import java.awt.*;

public class AdminHomeFrame extends JFrame {

    public AdminHomeFrame() {
        // access control: only ADMIN can open this page
        if (!"ADMIN".equals(Session.role)) {
            JOptionPane.showMessageDialog(null, "Access denied. Please login as Admin.");
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Admin Home");
        setSize(420, 380);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("<html><center>Pharmacy Management System<br>Welcome, "
                + Session.username + " (Admin)</center></html>", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 0, 6, 0);
        c.gridx = 0;

        JButton usersBtn = makeButton("Manage Users");
        JButton medBtn = makeButton("Manage Medicines");
        JButton reportBtn = makeButton("Billing Reports");
        JButton logoutBtn = makeButton("Logout");

        c.gridy = 0; panel.add(usersBtn, c);
        c.gridy = 1; panel.add(medBtn, c);
        c.gridy = 2; panel.add(reportBtn, c);
        c.gridy = 3; panel.add(logoutBtn, c);

        add(title, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        
        usersBtn.addActionListener(e -> {
            new ManageUsersFrame().setVisible(true);
            dispose();
        });
        medBtn.addActionListener(e -> {
            medicine.MedicineGUI.open();   // CHANGED: was new ManageMedicinesFrame().setVisible(true)
            dispose();
        });
        reportBtn.addActionListener(e -> {
            new BillingReportsFrame().setVisible(true);
            dispose();
        });

        logoutBtn.addActionListener(e -> {
            Session.clear();
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(240, 40));
        return b;
    }

    void coming(String name) {
        JOptionPane.showMessageDialog(this, name + " will be added soon.");
    }
}