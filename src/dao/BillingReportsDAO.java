package dao;

import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

// Reads billing data for the admin's Billing Reports page.
// pharmacistId = 0 means "all pharmacists".
public class BillingReportsDAO {

    // rows: bill_id, prescription_id, pharmacist_id, bill_date, bill_amount
    public ArrayList<Object[]> getBills(int pharmacistId) throws SQLException {
        String sql = "SELECT b.bill_id, b.prescription_id, p.pharmacist_id, b.bill_date, b.bill_amount "
                + "FROM bills b JOIN prescriptions p ON b.prescription_id = p.prescription_id "
                + "WHERE (? = 0 OR p.pharmacist_id = ?) "
                + "ORDER BY b.bill_date, b.bill_id";
        ArrayList<Object[]> rows = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pharmacistId);
            ps.setInt(2, pharmacistId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{rs.getInt(1), rs.getInt(2), rs.getInt(3),
                            rs.getString(4), rs.getDouble(5)});
                }
            }
        }
        return rows;
    }

    // rows: pharmacist_id, pharmacist, medicine_name, quantity sold, amount
    public ArrayList<Object[]> getMedicinesHandled(int pharmacistId) throws SQLException {
        String sql = "SELECT p.pharmacist_id, u.username, m.medicine_name, "
                + "SUM(pm.quantity), SUM(pm.total_price) "
                + "FROM bills b "
                + "JOIN prescriptions p ON b.prescription_id = p.prescription_id "
                + "JOIN prescription_medicines pm ON pm.prescription_id = p.prescription_id "
                + "JOIN medicines m ON pm.medicine_id = m.medicine_id "
                + "JOIN users u ON p.pharmacist_id = u.user_id "
                + "WHERE (? = 0 OR p.pharmacist_id = ?) "
                + "GROUP BY p.pharmacist_id, u.username, m.medicine_id, m.medicine_name "
                + "ORDER BY p.pharmacist_id, m.medicine_name";
        ArrayList<Object[]> rows = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pharmacistId);
            ps.setInt(2, pharmacistId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getInt(4), rs.getDouble(5)});
                }
            }
        }
        return rows;
    }

    // rows: pharmacist_id, pharmacist, bills handled, medicine units, total billed
    public ArrayList<Object[]> getSummary() throws SQLException {
        String sql = "SELECT u.user_id, u.username, "
                + "(SELECT COUNT(*) FROM bills b JOIN prescriptions p ON b.prescription_id = p.prescription_id "
                + "  WHERE p.pharmacist_id = u.user_id), "
                + "(SELECT COALESCE(SUM(pm.quantity), 0) FROM bills b "
                + "  JOIN prescriptions p ON b.prescription_id = p.prescription_id "
                + "  JOIN prescription_medicines pm ON pm.prescription_id = p.prescription_id "
                + "  WHERE p.pharmacist_id = u.user_id), "
                + "(SELECT COALESCE(SUM(b.bill_amount), 0) FROM bills b "
                + "  JOIN prescriptions p ON b.prescription_id = p.prescription_id "
                + "  WHERE p.pharmacist_id = u.user_id) "
                + "FROM users u WHERE u.role = 'PHARMACIST' ORDER BY u.user_id";
        ArrayList<Object[]> rows = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getInt(3),
                        rs.getInt(4), rs.getDouble(5)});
            }
        }
        return rows;
    }
}