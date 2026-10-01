package gui;

import dao.UserDAO;
import model.User;
import session.Session;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    JTextField userField = new JTextField(15);
    JPasswordField passField = new JPasswordField(15);

    public LoginFrame() {
        setTitle("Pharmacy Management System - Login");
        setSize(420, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel("Pharmacy Management System");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        panel.add(title, c);

        JLabel sub = new JLabel("Please login to continue");
        c.gridy = 1;
        panel.add(sub, c);

        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0; c.gridy = 2;
        panel.add(new JLabel("Username:"), c);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 1;
        panel.add(userField, c);

        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0; c.gridy = 3;
        panel.add(new JLabel("Password:"), c);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 1;
        panel.add(passField, c);

        JButton loginBtn = new JButton("Login");
        JButton clearBtn = new JButton("Clear");
        JButton exitBtn = new JButton("Exit");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.add(loginBtn);
        buttons.add(clearBtn);
        buttons.add(exitBtn);
        c.gridx = 0; c.gridy = 4; c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(buttons, c);

        add(panel);

        loginBtn.addActionListener(e -> login());
        clearBtn.addActionListener(e -> {
            userField.setText("");
            passField.setText("");
            userField.requestFocus();
        });
        exitBtn.addActionListener(e -> System.exit(0));
        getRootPane().setDefaultButton(loginBtn);
    }

    void login() {
        String u = userField.getText().trim();
        String p = new String(passField.getPassword());

        if (u.isEmpty()) { msg("Username cannot be empty."); return; }
        if (p.isEmpty()) { msg("Password cannot be empty."); return; }

        try {
            User user = new UserDAO().login(u, p);
            if (user == null) {
                msg("Invalid username or password.");
                passField.setText("");
                return;
            }

            // save logged-in user in session
            Session.userId = user.getId();
            Session.username = user.getUsername();
            Session.role = user.getRole();

            // open home page based on role
            if (user.getRole().equals("ADMIN")) {
                new AdminHomeFrame().setVisible(true);
            } else {
                new PharmacistHomeFrame().setVisible(true);
            }
            dispose();

        } catch (SQLException ex) {
            msg("Cannot connect to the database. Please check that MySQL is running.");
        }
    }

    void msg(String text) {
        JOptionPane.showMessageDialog(this, text);
    }
}