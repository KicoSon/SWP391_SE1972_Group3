package dal;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO extends DBContext {

    // 1. Lấy TẤT CẢ khách hàng đang hoạt động (Dành cho Admin, Manager, Marketing)
    public List<Customer> getAllActiveCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE status = 'Active' ORDER BY full_name ASC";
        
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Lấy khách hàng THEO NGƯỜI PHỤ TRÁCH (Dành cho Sale Staff)
    // Chỉ hiện những khách mà Sale này được assign (owner_id)
    public List<Customer> getCustomersByOwnerId(int ownerId) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE status = 'Active' AND owner_id = ? ORDER BY full_name ASC";
        
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, ownerId); // Truyền ID của Sale đang login vào đây
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Hàm phụ trợ: Map dữ liệu từ ResultSet sang Object Customer (để đỡ viết lại code nhiều lần)
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id"));
        c.setFullName(rs.getString("full_name"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setAddress(rs.getString("address"));
        
        // Các trường số nguyên có thể null trong DB, nhưng int trong Java không null
        // getInt trả về 0 nếu null, logic này ổn với DB của bạn
        c.setTierId(rs.getInt("tier_id")); 
        
        c.setStatus(rs.getString("status"));
        c.setProfilePicUrl(rs.getString("profile_pic_url"));
        c.setOwnerId(rs.getInt("owner_id"));
        
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return c;
    }
    
    // (Optional) Hàm lấy khách hàng theo ID cụ thể (Dùng khi xem chi tiết hoặc Edit)
    public Customer getCustomerById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}