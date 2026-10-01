package gui;

import session.Session;
import javax.swing.*;
import java.awt.*;

public class PharmacistHomeFrame extends JFrame {

    public PharmacistHomeFrame() {
        // access control: only PHARMACIST can open this page
        if (!"PHARMACIST".equals(Session.role)) {
            JOptionPane.showMessageDialog(null, "Access denied. Please login as Pharmacist.");
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Pharmacist Home");
        setSize(420, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("<html><center>Pharmacy Management System<br>Welcome, "
                + Session.username + "</center></html>", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 0, 6, 0);
        c.gridx = 0;

        JButton addBtn = makeButton("Add Customer");
        JButton updateBtn = makeButton("Update Customer");
        JButton deleteBtn = makeButton("Delete Customer");
        JButton searchBtn = makeButton("Search Customer");
        JButton issueBtn = makeButton("Issue Medicine without Prescription");
        JButton stockBtn = makeButton("Stock Refill Reminder");
        JButton logoutBtn = makeButton("Logout");

        c.gridy = 0; panel.add(addBtn, c);
        c.gridy = 1; panel.add(updateBtn, c);
        c.gridy = 2; panel.add(deleteBtn, c);
        c.gridy = 3; panel.add(searchBtn, c);
        c.gridy = 4; panel.add(issueBtn, c);
        c.gridy = 5; panel.add(stockBtn, c);
        c.gridy = 6; panel.add(logoutBtn, c);

        add(title, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        // replace these messages with teammates' frames,
        // e.g. new CustomerManagementFrame().setVisible(true); dispose();
        addBtn.addActionListener(e -> coming("Add Customer"));
        updateBtn.addActionListener(e -> coming("Update Customer"));
        deleteBtn.addActionListener(e -> coming("Delete Customer"));
        searchBtn.addActionListener(e -> coming("Search Customer"));
        issueBtn.addActionListener(e -> coming("Issue Medicine without Prescription"));
        stockBtn.addActionListener(e -> coming("Stock Refill Reminder"));

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