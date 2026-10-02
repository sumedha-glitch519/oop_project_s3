package gui;

import dao.CustomerDAO;
import dao.PrescriptionDAO;
import model.Customer;
import model.Prescription;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Content panel for "Search Customer". Searches by ID, Name, or Phone and
 * shows results in a JTable. From a selected row the pharmacist can start
 * a new prescription (which hides the nav bar) or view prescription history.
 *
 * Database tables used: customers, prescriptions (for history)
 */
public class SearchCustomerPanel extends JPanel {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
    private final PharmacistHomeFrame homeFrame;

    private JTextField idField;
    private JTextField nameField;
    private JTextField phoneField;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private List<Customer> lastResults;

    public SearchCustomerPanel(PharmacistHomeFrame homeFrame) {
        this.homeFrame = homeFrame;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel header = new JLabel("Search Customer");
        header.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        searchPanel.add(new JLabel("Customer ID:"), gbc);
        idField = new JTextField(8);
        gbc.gridx = 1;
        searchPanel.add(idField, gbc);

        gbc.gridx = 2;
        searchPanel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(10);
        gbc.gridx = 3;
        searchPanel.add(nameField, gbc);

        gbc.gridx = 4;
        searchPanel.add(new JLabel("Phone:"), gbc);
        phoneField = new JTextField(10);
        gbc.gridx = 5;
        searchPanel.add(phoneField, gbc);

        JButton searchButton = new JButton("Search");
        gbc.gridx = 6;
        searchPanel.add(searchButton, gbc);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(
                new Object[]{"Customer ID", "Name", "Phone", "Email", "Address"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(tableModel);

        JButton createPrescriptionButton = new JButton("Create New Prescription");
        JButton viewHistoryButton = new JButton("View Previous Prescriptions");
        JPanel actionPanel = new JPanel();
        actionPanel.add(createPrescriptionButton);
        actionPanel.add(viewHistoryButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> performSearch());
        createPrescriptionButton.addActionListener(e -> createPrescriptionForSelected());
        viewHistoryButton.addActionListener(e -> viewHistoryForSelected());
    }

    private void performSearch() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (id.isEmpty() && name.isEmpty() && phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter at least one search value.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            lastResults = customerDAO.searchCustomers(id, name, phone);
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
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Customer ID must be numeric.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Customer getSelectedCustomer() {
        int row = resultsTable.getSelectedRow();
        if (row == -1 || lastResults == null || row >= lastResults.size()) {
            JOptionPane.showMessageDialog(this, "Please select a customer from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return lastResults.get(row);
    }

    private void createPrescriptionForSelected() {
        Customer selected = getSelectedCustomer();
        if (selected == null) {
            return;
        }
        homeFrame.startPrescriptionWorkflow(selected);
    }

    private void viewHistoryForSelected() {
        Customer selected = getSelectedCustomer();
        if (selected == null) {
            return;
        }
        try {
            List<Prescription> history = prescriptionDAO.getPrescriptionsByCustomer(selected.getCustomerId());
            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No previous prescriptions for this customer.",
                        "Prescription History", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder("Prescription history for " + selected.getName() + ":\n\n");
            for (Prescription p : history) {
                sb.append("Prescription ID: ").append(p.getPrescriptionId())
                        .append("   Date: ").append(p.getPrescriptionDate())
                        .append("\n");
            }
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                    "Prescription History", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
