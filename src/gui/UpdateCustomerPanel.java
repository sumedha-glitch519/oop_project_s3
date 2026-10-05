package gui;

import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.sql.SQLException;

import java.util.List;

/**
 * Content panel for "Update Customer". First lets the pharmacist search
 * and select a customer from a JTable, then loads that customer's
 * details into an editable form.
 *
 * Database table used: customers
 */
public class UpdateCustomerPanel extends JPanel {

    private final CustomerDAO customerDAO = new CustomerDAO();

    private JTextField searchField;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private List<Customer> lastResults;

    private JTextField idField;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JTextField dateRegisteredField;
    private JButton updateButton;

    public UpdateCustomerPanel(PharmacistHomeFrame homeFrame) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel header = new JLabel("Update Customer");
        header.setFont(new Font("Arial", Font.BOLD, 16));

        // --- Search panel ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search (ID / Name / Phone)");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        // --- Results table ---
        tableModel = new DefaultTableModel(
                new Object[]{"Customer ID", "Name", "Phone", "Email", "Address"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(resultsTable);
        tableScroll.setPreferredSize(new Dimension(100, 180));

        // --- Edit form ---
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder("Edit Selected Customer"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        editPanel.add(new JLabel("Customer ID:"), gbc);
        idField = new JTextField(15);
        idField.setEditable(false);
        gbc.gridx = 1;
        editPanel.add(idField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        editPanel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(15);
        gbc.gridx = 1;
        editPanel.add(nameField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        editPanel.add(new JLabel("Phone:"), gbc);
        phoneField = new JTextField(15);
        gbc.gridx = 1;
        editPanel.add(phoneField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        editPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(15);
        gbc.gridx = 1;
        editPanel.add(emailField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        editPanel.add(new JLabel("Address:"), gbc);
        addressField = new JTextField(15);
        gbc.gridx = 1;
        editPanel.add(addressField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        editPanel.add(new JLabel("Date Registered:"), gbc);
        dateRegisteredField = new JTextField(15);
        dateRegisteredField.setEditable(false);
        gbc.gridx = 1;
        editPanel.add(dateRegisteredField, gbc);
        row++;

        updateButton = new JButton("Update");
        updateButton.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        editPanel.add(updateButton, gbc);

        setFieldsEnabled(false);

        add(topPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(editPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> performSearch());
        resultsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCustomer();
            }
        });
        updateButton.addActionListener(e -> updateCustomer());
    }

    private void setFieldsEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
        phoneField.setEnabled(enabled);
        emailField.setEnabled(enabled);
        addressField.setEnabled(enabled);
        updateButton.setEnabled(enabled);
    }

    private void performSearch() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Customer ID, name, or phone to search.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            lastResults = customerDAO.searchByIdOrNameOrPhone(text);

            tableModel.setRowCount(0);
            for (Customer c : lastResults) {
                tableModel.addRow(new Object[]{
                        c.getCustomerId(), c.getName(), c.getPhone(), c.getEmail(), c.getAddress()
                });
            }

            if (lastResults.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No matching customers found.",
                        "No Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedCustomer() {
        int row = resultsTable.getSelectedRow();
        if (row == -1 || lastResults == null || row >= lastResults.size()) {
            return;
        }
        Customer c = lastResults.get(row);
        idField.setText(String.valueOf(c.getCustomerId()));
        nameField.setText(c.getName());
        phoneField.setText(c.getPhone());
        emailField.setText(c.getEmail());
        addressField.setText(c.getAddress());
        dateRegisteredField.setText(c.getDateRegistered() != null ? c.getDateRegistered().toString() : "");
        setFieldsEnabled(true);
    }

    private void updateCustomer() {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer first.",
                    "No Customer Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (phone.isEmpty() || !phone.matches("\\d{7,15}")) {
            JOptionPane.showMessageDialog(this, "Phone must contain 7-15 valid digits.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }


        Customer customer = new Customer();
        customer.setCustomerId(Integer.parseInt(idField.getText().trim()));
        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(emailField.getText().trim());
        customer.setAddress(addressField.getText().trim());
        try {
            boolean updated = customerDAO.updateCustomer(customer);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Customer updated successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                performSearch();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. Customer may no longer exist.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
