
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MedicineDAO {

        private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        private Date convertDate(String date) {

                LocalDate localDate = LocalDate.parse(date, formatter);

                return Date.valueOf(localDate);
        }

        private String formatDate(Date date) {

                return date.toLocalDate()
                                .format(formatter);
        }

        public boolean addMedicine(Medicine medicine) {

                String sql = "INSERT INTO medicine " +
                                "(id, name, category, price, quantity, expiry_date, prescription_required) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";

                try (
                                Connection connection = DBConnection.getConnection();
                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setInt(1, medicine.getId());
                        statement.setString(2, medicine.getName());
                        statement.setString(3, medicine.getCategory());
                        statement.setDouble(4, medicine.getPrice());
                        statement.setInt(5, medicine.getQuantity());
                        statement.setDate(6, convertDate(medicine.getExpiryDate()));
                        statement.setBoolean(7, medicine.isPrescriptionRequired());

                        int rows = statement.executeUpdate();

                        return rows > 0;

                } catch (Exception e) {

                        System.out.println("Error adding medicine.");
                        e.printStackTrace();

                        return false;
                }
        }

        public ArrayList<Medicine> getAllMedicines() {

                ArrayList<Medicine> medicines = new ArrayList<>();

                String sql = "SELECT * FROM medicine";

                try (
                                Connection connection = DBConnection.getConnection();
                                PreparedStatement statement = connection.prepareStatement(sql);
                                ResultSet result = statement.executeQuery()) {

                        while (result.next()) {

                                Medicine medicine = new Medicine(
                                                result.getInt("id"),
                                                result.getString("name"),
                                                result.getString("category"),
                                                result.getDouble("price"),
                                                result.getInt("quantity"),
                                                formatDate(result.getDate("expiry_date")),
                                                result.getBoolean("prescription_required"));

                                medicines.add(medicine);
                        }

                } catch (Exception e) {

                        System.out.println("Error viewing medicines.");
                        e.printStackTrace();
                }

                return medicines;
        }

        public Medicine searchMedicineById(int id) {

                String sql = "SELECT * FROM medicine WHERE id = ?";

                try (
                                Connection connection = DBConnection.getConnection();
                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setInt(1, id);

                        ResultSet result = statement.executeQuery();

                        if (result.next()) {

                                return new Medicine(
                                                result.getInt("id"),
                                                result.getString("name"),
                                                result.getString("category"),
                                                result.getDouble("price"),
                                                result.getInt("quantity"),
                                                formatDate(result.getDate("expiry_date")),
                                                result.getBoolean("prescription_required"));
                        }

                } catch (Exception e) {

                        System.out.println("Error searching medicine by ID.");
                        e.printStackTrace();
                }

                return null;
        }

        public ArrayList<Medicine> searchMedicineByName(String name) {

                ArrayList<Medicine> medicines = new ArrayList<>();

                String sql = "SELECT * FROM medicine WHERE name LIKE ?";

                try (
                                Connection connection = DBConnection.getConnection();
                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, "%" + name + "%");

                        ResultSet result = statement.executeQuery();

                        while (result.next()) {

                                Medicine medicine = new Medicine(
                                                result.getInt("id"),
                                                result.getString("name"),
                                                result.getString("category"),
                                                result.getDouble("price"),
                                                result.getInt("quantity"),
                                                formatDate(result.getDate("expiry_date")),
                                                result.getBoolean("prescription_required"));

                                medicines.add(medicine);
                        }

                } catch (Exception e) {

                        System.out.println("Error searching medicine by name.");
                        e.printStackTrace();
                }

                return medicines;
        }

        public boolean updateMedicine(Medicine medicine) {

                String sql = "UPDATE medicine SET " +
                                "name = ?, " +
                                "category = ?, " +
                                "price = ?, " +
                                "quantity = ?, " +
                                "expiry_date = ?, " +
                                "prescription_required = ? " +
                                "WHERE id = ?";

                try (
                                Connection connection = DBConnection.getConnection();
                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, medicine.getName());
                        statement.setString(2, medicine.getCategory());
                        statement.setDouble(3, medicine.getPrice());
                        statement.setInt(4, medicine.getQuantity());
                        statement.setDate(5, convertDate(medicine.getExpiryDate()));
                        statement.setBoolean(6, medicine.isPrescriptionRequired());
                        statement.setInt(7, medicine.getId());

                        int rows = statement.executeUpdate();

                        return rows > 0;

                } catch (Exception e) {

                        System.out.println("Error updating medicine.");
                        e.printStackTrace();

                        return false;
                }
        }

        public boolean deleteMedicine(int id) {

                String sql = "DELETE FROM medicine WHERE id = ?";

                try (
                                Connection connection = DBConnection.getConnection();
                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setInt(1, id);

                        int rows = statement.executeUpdate();

                        return rows > 0;

                } catch (Exception e) {

                        System.out.println("Error deleting medicine.");
                        e.printStackTrace();

                        return false;
                }
        }

        public ArrayList<Medicine> getStockAndExpiry() {

                ArrayList<Medicine> medicines = new ArrayList<>();

                String sql = "SELECT * FROM medicine ORDER BY expiry_date ASC";

                try (
                                Connection connection = DBConnection.getConnection();
                                PreparedStatement statement = connection.prepareStatement(sql);
                                ResultSet result = statement.executeQuery()) {

                        while (result.next()) {

                                Medicine medicine = new Medicine(
                                                result.getInt("id"),
                                                result.getString("name"),
                                                result.getString("category"),
                                                result.getDouble("price"),
                                                result.getInt("quantity"),
                                                formatDate(result.getDate("expiry_date")),
                                                result.getBoolean("prescription_required"));

                                medicines.add(medicine);
                        }

                } catch (Exception e) {

                        System.out.println("Error checking stock and expiry.");
                        e.printStackTrace();
                }

                return medicines;
        }
}
