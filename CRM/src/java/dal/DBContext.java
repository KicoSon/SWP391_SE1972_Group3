package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBContext {

    protected Connection connection;

    public DBContext() {
        try {
            // Kết nối tới named instance MSSQLSERVER01
            String url = "jdbc:sqlserver://localhost:1433;databaseName =CRM";
            String username = "sa";
            String password = "123";

            // Load driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, username, password);

            if (connection != null && !connection.isClosed()) {
                System.out.println("KẾT NỐI DB THÀNH CÔNG -> " + url);
            } else {
                System.out.println("KẾT NỐI DB THẤT BẠI (connection null hoặc closed).");
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Không tìm thấy SQLServer Driver. Hãy thêm driver vào classpath.");
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.err.println("Lỗi khi kết nối tới DB:");
            ex.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void testConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                System.out.println("TEST: Connection is open.");
            } else {
                System.out.println("TEST: Connection is closed or null.");
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi test connection:");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DBContext db = new DBContext();
        db.testConnection();
        try {
            if (db.getConnection() != null && !db.getConnection().isClosed()) {
                db.getConnection().close();
                System.out.println("Đã đóng connection.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}