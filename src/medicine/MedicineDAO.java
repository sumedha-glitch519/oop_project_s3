package medicine;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;

public class MedicineDAO {

        // =====================================================
        // ADD MEDICINE
        // =====================================================

        public boolean addMedicine(Medicine medicine) {

                String sql = "INSERT INTO medicines " +
                                "(medicine_id, medicine_name, category, price, stock_quantity, expiry_date, prescription_required) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";

                try (
                                Connection connection = DatabaseConnection.getConnection();
                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setInt(
                                        1,
                                        medicine.getId());

                        statement.setString(
                                        2,
                                        medicine.getName());

                        statement.setString(
                                        3,
                                        medicine.getCategory());

                        statement.setDouble(
                                        4,
                                        medicine.getPrice());

                        statement.setInt(
                                        5,
                                        medicine.getQuantity());

                        statement.setDate(
                                        6,
                                        Date.valueOf(
                                                        medicine.getExpiryDate()));

                        statement.setBoolean(
                                        7,
                                        medicine.isPrescriptionRequired());

                        int rows = statement.executeUpdate();

                        return rows > 0;

                } catch (Exception e) {

                        System.out.println(
                                        "Error adding medicine.");

                        e.printStackTrace();

                        return false;
                }
        }

        // =====================================================
        // VIEW ALL MEDICINES
        // =====================================================

        public ArrayList<Medicine> getAllMedicines() {

                ArrayList<Medicine> medicines = new ArrayList<>();

                String sql = "SELECT * FROM medicines";

                try (
                                Connection connection = DatabaseConnection.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql);

                                ResultSet result = statement.executeQuery()) {

                        while (result.next()) {

                                Medicine medicine = new Medicine(

                                                result.getInt("medicine_id"),

                                                result.getString("medicine_name"),

                                                result.getString("category"),

                                                result.getDouble("price"),

                                                result.getInt("stock_quantity"),

                                                result.getDate(
                                                                "expiry_date").toString(),

                                                result.getBoolean(
                                                                "prescription_required"));

                                medicines.add(medicine);
                        }

                } catch (Exception e) {

                        System.out.println(
                                        "Error viewing medicines.");

                        e.printStackTrace();
                }

                return medicines;
        }

        // =====================================================
        // SEARCH MEDICINE BY ID
        // =====================================================

        public Medicine searchMedicineById(int id) {

                String sql = "SELECT * FROM medicines WHERE medicine_id = ?";

                try (
                                Connection connection = DatabaseConnection.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setInt(
                                        1,
                                        id);

                        ResultSet result = statement.executeQuery();

                        if (result.next()) {

                                return new Medicine(

                                                result.getInt("medicine_id"),

                                                result.getString("medicine_name"),

                                                result.getString("category"),

                                                result.getDouble("price"),

                                                result.getInt("stock_quantity"),

                                                result.getDate(
                                                                "expiry_date").toString(),

                                                result.getBoolean(
                                                                "prescription_required"));
                        }

                } catch (Exception e) {

                        System.out.println(
                                        "Error searching medicine by ID.");

                        e.printStackTrace();
                }

                return null;
        }

        // =====================================================
        // SEARCH MEDICINE BY NAME
        // =====================================================

        public ArrayList<Medicine> searchMedicineByName(
                        String name) {

                ArrayList<Medicine> medicines = new ArrayList<>();

                String sql = "SELECT * FROM medicines " +
                                "WHERE medicine_name LIKE ?";

                try (
                                Connection connection = DatabaseConnection.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(
                                        1,
                                        "%" + name + "%");

                        ResultSet result = statement.executeQuery();

                        while (result.next()) {

                                Medicine medicine = new Medicine(

                                                result.getInt("medicine_id"),

                                                result.getString("medicine_name"),

                                                result.getString("category"),

                                                result.getDouble("price"),

                                                result.getInt("stock_quantity"),

                                                result.getDate(
                                                                "expiry_date").toString(),

                                                result.getBoolean(
                                                                "prescription_required"));

                                medicines.add(medicine);
                        }

                } catch (Exception e) {

                        System.out.println(
                                        "Error searching medicine by name.");

                        e.printStackTrace();
                }

                return medicines;
        }

        // =====================================================
        // UPDATE MEDICINE
        // =====================================================

        public boolean updateMedicine(
                        Medicine medicine) {

                String sql = "UPDATE medicines SET " +
                                "medicine_name = ?, " +
                                "category = ?, " +
                                "price = ?, " +
                                "stock_quantity = ?, " +
                                "expiry_date = ?, " +
                                "prescription_required = ? " +
                                "WHERE medicine_id = ?";

                try (
                                Connection connection = DatabaseConnection.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(
                                        1,
                                        medicine.getName());

                        statement.setString(
                                        2,
                                        medicine.getCategory());

                        statement.setDouble(
                                        3,
                                        medicine.getPrice());

                        statement.setInt(
                                        4,
                                        medicine.getQuantity());

                        statement.setDate(
                                        5,
                                        Date.valueOf(
                                                        medicine.getExpiryDate()));

                        statement.setBoolean(
                                        6,
                                        medicine.isPrescriptionRequired());

                        statement.setInt(
                                        7,
                                        medicine.getId());

                        int rows = statement.executeUpdate();

                        return rows > 0;

                } catch (Exception e) {

                        System.out.println(
                                        "Error updating medicine.");

                        e.printStackTrace();

                        return false;
                }
        }

        // =====================================================
        // DELETE MEDICINE
        // =====================================================

        public boolean deleteMedicine(int id) {

                String sql = "DELETE FROM medicines WHERE medicine_id = ?";

                try (
                                Connection connection = DatabaseConnection.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setInt(
                                        1,
                                        id);

                        int rows = statement.executeUpdate();

                        return rows > 0;

                } catch (Exception e) {

                        System.out.println(
                                        "Error deleting medicine.");

                        e.printStackTrace();

                        return false;
                }
        }

        // =====================================================
        // GET STOCK AND EXPIRY INFORMATION
        // =====================================================

        public ArrayList<Medicine> getStockAndExpiry() {

                ArrayList<Medicine> medicines = new ArrayList<>();

                String sql = "SELECT * FROM medicines " +
                                "ORDER BY expiry_date ASC";

                try (
                                Connection connection = DatabaseConnection.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql);

                                ResultSet result = statement.executeQuery()) {

                        while (result.next()) {

                                Medicine medicine = new Medicine(

                                                result.getInt("medicine_id"),

                                                result.getString("medicine_name"),

                                                result.getString("category"),

                                                result.getDouble("price"),

                                                result.getInt("stock_quantity"),

                                                result.getDate(
                                                                "expiry_date").toString(),

                                                result.getBoolean(
                                                                "prescription_required"));

                                medicines.add(medicine);
                        }

                } catch (Exception e) {

                        System.out.println(
                                        "Error checking stock and expiry.");

                        e.printStackTrace();
                }

                return medicines;
        }
}