package dal;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO extends DBContext {

    public List<Customer> getAllCustomers() {

        List<Customer> list = new ArrayList<>();

//        String sql = "SELECT * FROM customers";
        String sql = """
            SELECT c.*, t.tier_name
            FROM customers c
            LEFT JOIN tiers t ON c.tier_id = t.id
        """;
        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Customer c = new Customer();

                c.setId(rs.getInt("id"));
                c.setFullName(rs.getString("full_name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setPassword(rs.getString("password"));
                c.setAddress(rs.getString("address"));
                c.setTierId(rs.getInt("tier_id"));
                c.setStatus(rs.getString("status"));
                c.setProfilePicUrl(rs.getString("profile_pic_url"));
                c.setOwnerId(rs.getInt("owner_id"));

                // Convert DATETIME2 -> LocalDateTime
                if (rs.getTimestamp("created_at") != null) {
                    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }

                if (rs.getTimestamp("updated_at") != null) {
                    c.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
                c.setTierName(rs.getString("tier_name"));

                list.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean updateStatus(int id, String status) {

        String sql = "UPDATE customers SET status = ?, updated_at = GETDATE() WHERE id = ?";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, status);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
//    public static void main(String[] args) {
//        CustomerDAO cd = new CustomerDAO();
//        List<Customer> ls = cd.getAllCustomers();
//        for(Customer c: ls){
//            System.out.println(c.toString());
//        }     
//    }
}
