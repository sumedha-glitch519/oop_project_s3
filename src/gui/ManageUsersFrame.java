package gui;

import dao.UserDAO;
import model.User;
import session.Session;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class ManageUsersFrame extends JFrame {
    UserDAO dao = new UserDAO();

    DefaultTableModel model = new DefaultTableModel(new String[]{"User ID", "Username", "Role"}, 0) {
        public boolean isCellEditable(int row, int col) { return false; }
    };
    JTable table = new JTable(model);

    JTextField userField = new JTextField(10);
    JPasswordField passField = new JPasswordField(10);
    JComboBox<String> roleBox = new JComboBox<>(new String[]{"PHARMACIST", "ADMIN"});

    public ManageUsersFrame() {
        // access control: only ADMIN can open this page
        if (!"ADMIN".equals(Session.role)) {
            JOptionPane.showMessageDialog(null, "Access denied. Please login as Admin.");
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Manage Users");
        setSize(650, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("Manage Users", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        // form row
        JPanel form = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        form.add(new JLabel("Username:"));
        form.add(userField);
        form.add(new JLabel("Password:"));
        form.add(passField);
        form.add(new JLabel("Role:"));
        form.add(roleBox);

        // button row
        JButton addBtn = new JButton("Add User");
        JButton deleteBtn = new JButton("Delete Selected User");
        JButton clearBtn = new JButton("Clear");
        JButton backBtn = new JButton("Back");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        buttons.add(addBtn);
        buttons.add(deleteBtn);
        buttons.add(clearBtn);
        buttons.add(backBtn);

        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.add(form);
        bottom.add(buttons);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        add(title, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addUser());
        deleteBtn.addActionListener(e -> deleteUser());
        clearBtn.addActionListener(e -> clearFields());
        backBtn.addActionListener(e -> {
            new AdminHomeFrame().setVisible(true);
            dispose();
        });

        loadUsers();
    }

    // fills the table with all users from the database
    void loadUsers() {
        model.setRowCount(0);
        try {
            for (User u : dao.getAllUsers()) {
                model.addRow(new Object[]{u.getId(), u.getUsername(), u.getRole()});
            }
        } catch (SQLException ex) {
            msg("Cannot load users. Please check the database connection.");
        }
    }

    void addUser() {
        String u = userField.getText().trim();
        String p = new String(passField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if (u.isEmpty()) { msg("Username cannot be empty."); return; }
        if (p.isEmpty()) { msg("Password cannot be empty."); return; }
        if (p.length() < 4) { msg("Password must be at least 4 characters."); return; }

        try {
            if (dao.usernameExists(u)) {
                msg("Username already exists. Please choose another.");
                return;
            }
            dao.addUser(u, p, role);
            msg("User added successfully.");
            clearFields();
            loadUsers();
        } catch (SQLException ex) {
            msg("Database error. Could not add the user.");
        }
    }

    void deleteUser() {
        int row = table.getSelectedRow();
        if (row == -1) { msg("Please select a user from the table."); return; }

        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);

        if (id == Session.userId) { msg("You cannot delete your own account."); return; }

        int ok = JOptionPane.showConfirmDialog(this, "Delete user '" + name + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            dao.deleteUser(id);
            msg("User deleted successfully.");
            loadUsers();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                // foreign key error: this user already has prescriptions
                msg("This user has prescription history and cannot be deleted.");
            } else {
                msg("Database error. Could not delete the user.");
            }
        }
    }
    void clearFields() {
        userField.setText("");
        passField.setText("");
        roleBox.setSelectedIndex(0);
        userField.requestFocus();
    }

    void msg(String text) {
        JOptionPane.showMessageDialog(this, text);
    }
}
