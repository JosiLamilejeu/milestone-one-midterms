import java.sql.*;
/**
 *
 * @author chesca
 */

public class Database {
    private static final String URL = "jdbc:sqlite:Milestone1.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Connected to SQLite database!");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }
}