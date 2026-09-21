import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/movie_ticket_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void testConnection() {
        try {
            Connection con = getConnection();
            System.out.println("Database connection successful!");
            con.close();
        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            System.out.println(e.getMessage());
        }
    }
}
