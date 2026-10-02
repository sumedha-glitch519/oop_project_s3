package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Single reusable class for obtaining a JDBC connection to MySQL.
 * Every DAO class calls DatabaseConnection.getConnection() instead of
 * duplicating connection code.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/pharmacy_management";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sumedhaiscoding2007"; // change to your MySQL password

    /**
     * Opens and returns a new connection to the pharmacy_management database.
     * Callers are responsible for closing the connection (use try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-j to the classpath.", e);
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
