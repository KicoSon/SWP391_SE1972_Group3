import java.sql.*;

public class DbChecker {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=CRM;trustServerCertificate=true;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try (Connection c = DriverManager.getConnection(url, "sa", "123");
             Statement stmt = c.createStatement()) {
            
            System.out.println("--- Columns in 'opportunities' table ---");
            DatabaseMetaData metaData = c.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, "opportunities", null);
            while (rs.next()) {
                System.out.println(rs.getString("COLUMN_NAME"));
            }
            rs.close();

            System.out.println("--- Columns in 'users' table ---");
            ResultSet rsUsers = metaData.getColumns(null, null, "users", null);
            while (rsUsers.next()) {
                System.out.println(rsUsers.getString("COLUMN_NAME"));
            }
            rsUsers.close();
            
            System.out.println("--- Tables in 'CRM' ---");
            ResultSet rsTables = metaData.getTables(null, null, null, new String[]{"TABLE"});
            while (rsTables.next()) {
                String tableName = rsTables.getString("TABLE_NAME");
                if (tableName.equalsIgnoreCase("staffs") || tableName.equalsIgnoreCase("users")) {
                    System.out.println("Found table: " + tableName);
                }
            }
            rsTables.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
