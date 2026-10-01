package dao;

import database.DatabaseConnection;
import model.Medicine;
import java.sql.*;
import java.util.ArrayList;

public class MedicineDAO {

    // converts a ResultSet into a list of Medicine objects
    private ArrayList<Medicine> readList(ResultSet rs) throws SQLException {
        ArrayList<Medicine> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new Medicine(
                    rs.getInt("medicine_id"),
                    rs.getString("medicine_name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("stock_quantity"),
                    rs.getString("expiry_date"),
                    "YES".equals(rs.getString("prescription_required"))));
        }
        return list;
    }

    public ArrayList<Medicine> getAll() throws SQLException {
        String sql = "SELECT * FROM medicines ORDER BY medicine_name";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return readList(rs);
        }
    }

    // search by medicine name or category
    public ArrayList<Medicine> search(String text) throws SQLException {
        String sql = "SELECT * FROM medicines WHERE medicine_name LIKE ? OR category LIKE ? ORDER BY medicine_name";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + text + "%");
            ps.setString(2, "%" + text + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return readList(rs);
            }
        }
    }

    public boolean nameExists(String name) throws SQLException {
        String sql = "SELECT 1 FROM medicines WHERE medicine_name = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void add(Medicine m) throws SQLException {
        String sql = "INSERT INTO medicines (medicine_name, category, price, stock_quantity, expiry_date, prescription_required) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getCategory());
            ps.setDouble(3, m.getPrice());
            ps.setInt(4, m.getQuantity());
            ps.setDate(5, Date.valueOf(m.getExpiry()));
            ps.setString(6, m.isPrescriptionRequired() ? "YES" : "NO");
            ps.executeUpdate();
        }
    }

    public void update(Medicine m) throws SQLException {
        String sql = "UPDATE medicines SET medicine_name = ?, category = ?, price = ?, stock_quantity = ?, expiry_date = ?, prescription_required = ? WHERE medicine_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getCategory());
            ps.setDouble(3, m.getPrice());
            ps.setInt(4, m.getQuantity());
            ps.setDate(5, Date.valueOf(m.getExpiry()));
            ps.setString(6, m.isPrescriptionRequired() ? "YES" : "NO");
            ps.setInt(7, m.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM medicines WHERE medicine_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
