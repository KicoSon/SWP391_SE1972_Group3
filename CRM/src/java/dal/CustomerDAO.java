package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Customer;

public class CustomerDAO extends DBContext {

    public List<Customer> getAllCustomers() {

        List<Customer> list = new ArrayList<>();

        String sql = "SELECT * FROM customers";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Customer c = new Customer();

                c.setId(rs.getInt("id"));
                c.setFullName(rs.getString("full_name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setPassword(rs.getString("password"));
                c.setAddress(rs.getString("address"));
                c.setTier(rs.getInt("tier_id"));
                c.setIsActive(rs.getString("status").equalsIgnoreCase("active")?true:false);
                c.setProfileURL(rs.getString("profile_pic_url"));
                c.setOwnerId(rs.getInt("owner_id"));
                c.setCreateAt(rs.getTimestamp("created_at")+"");
                c.setUpdateAt(rs.getTimestamp("updated_at")+"");

                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public static void main(String[] args) {
        CustomerDAO cd = new CustomerDAO();
        List<Customer> ls = cd.getAllCustomers();
        System.out.println("List:");
        for(Customer c : ls){
            System.out.println(c.toString());
        }
    }

}
