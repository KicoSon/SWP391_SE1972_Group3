import java.sql.*;

public class DbChecker2 {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=CRM;trustServerCertificate=true;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try (Connection c = DriverManager.getConnection(url, "sa", "123");
             Statement stmt = c.createStatement()) {
            
            System.out.println("--- Columns in 'pipeline_stages' table ---");
            DatabaseMetaData metaData = c.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, "pipeline_stages", null);
            while (rs.next()) {
                System.out.println(rs.getString("COLUMN_NAME"));
            }
            rs.close();

            System.out.println("--- Columns in 'lost_reasons' table ---");
            ResultSet rs2 = metaData.getColumns(null, null, "lost_reasons", null);
            while (rs2.next()) {
                System.out.println(rs2.getString("COLUMN_NAME"));
            }
            rs2.close();

            System.out.println("--- Columns in 'opportunities' table (with type) ---");
            ResultSet rs3 = metaData.getColumns(null, null, "opportunities", null);
            while (rs3.next()) {
                System.out.println(rs3.getString("COLUMN_NAME") + " - " + rs3.getString("TYPE_NAME"));
            }
            rs3.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
