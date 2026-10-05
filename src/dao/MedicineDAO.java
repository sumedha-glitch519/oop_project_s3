package dao;

import database.DatabaseConnection;
import model.Medicine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database access for the "medicines" table:
 * searching by name, checking stock, and updating stock after billing.
 */
public class MedicineDAO {

    /** Searches ALL medicines whose name contains the given text (used by the prescription workflow). */
    public List<Medicine> searchByName(String name) throws SQLException {
        String sql = "SELECT medicine_id, medicine_name, category, price, stock_quantity, " +
                     "expiry_date, prescription_required FROM medicines WHERE medicine_name LIKE ?";
        return runNameSearch(sql, name);
    }

    /**
     * Searches only medicines that do NOT require a prescription
     * (used by the "Issue Medicine Without Prescription" counter-sale screen).
     */
    public List<Medicine> searchAvailableWithoutPrescription(String name) throws SQLException {
        String sql = "SELECT medicine_id, medicine_name, category, price, stock_quantity, " +
                     "expiry_date, prescription_required FROM medicines " +
                     "WHERE medicine_name LIKE ? AND prescription_required = FALSE";
        return runNameSearch(sql, name);
    }

    private List<Medicine> runNameSearch(String sql, String name) throws SQLException {
        List<Medicine> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    /** Retrieves one medicine by ID. Returns null if not found. */
    public Medicine getMedicineById(int medicineId) throws SQLException {
        String sql = "SELECT * FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, medicineId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Returns medicines that need attention: low stock, already expired,
     * or expiring within the next 30 days.
     */
    public List<Medicine> getAttentionNeededMedicines(int lowStockThreshold) throws SQLException {
        String sql = "SELECT medicine_id, medicine_name, category, price, stock_quantity, " +
                     "expiry_date, prescription_required FROM medicines " +
                     "WHERE stock_quantity <= ? " +
                     "OR expiry_date < CURDATE() " +
                     "OR expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)";

        List<Medicine> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lowStockThreshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    /**
     * Reduces stock_quantity for a medicine by the given quantity.
     * Meant to be called only during Finalize Bill, within an existing
     * transaction (pass the same Connection used there).
     */
    public boolean reduceStock(Connection conn, int medicineId, int quantity) throws SQLException {
        String sql = "UPDATE medicines SET stock_quantity = stock_quantity - ? " +
                     "WHERE medicine_id = ? AND stock_quantity >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, medicineId);
            ps.setInt(3, quantity);
            return ps.executeUpdate() > 0;
        }
    }

    private Medicine mapRow(ResultSet rs) throws SQLException {
        return new Medicine(
                rs.getInt("medicine_id"),
                rs.getString("medicine_name"),
                rs.getString("category"),
                rs.getDouble("price"),
                rs.getInt("stock_quantity"),
                rs.getDate("expiry_date"),
                rs.getBoolean("prescription_required")
        );
    }

    // ==================================================================
    // MERGE: added from login_module (Admin module)
    // ==================================================================
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
                    rs.getBoolean("prescription_required")));
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
            ps.setBoolean(6, m.isPrescriptionRequired());
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
            ps.setBoolean(6, m.isPrescriptionRequired());
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
