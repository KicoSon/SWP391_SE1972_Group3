package dal;

import model.Customer;
import java.sql.*;
import java.time.LocalDateTime;
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

    public Customer getCustomerByID(int id) {

        String sql = """
        SELECT 
            c.id,
            c.full_name,
            c.email,
            c.phone,
            c.address,
            c.tier_id,
            t.tier_name,
            c.status,
            c.owner_id,
            u.full_name AS owner_name,
            c.created_at
        FROM customers c
        LEFT JOIN tiers t ON c.tier_id = t.id
        LEFT JOIN users u ON c.owner_id = u.id
        WHERE c.id = ?
    """;

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Customer c = new Customer();

                c.setId(rs.getInt("id"));
                c.setFullName(rs.getString("full_name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setAddress(rs.getString("address"));

                c.setTierId(rs.getInt("tier_id"));
                c.setTierName(rs.getString("tier_name"));

                c.setStatus(rs.getString("status"));

                c.setOwnerId(rs.getInt("owner_id"));
                c.setOwnerName(rs.getString("owner_name"));

                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateWithoutPassword(Customer c) {

        String sql = """
        UPDATE customers
        SET full_name = ?,
            email = ?,
            phone = ?,
            address = ?,
            owner_id = ?,
            status = ?
        WHERE id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getAddress());
            ps.setInt(5, c.getOwnerId());
            ps.setString(6, c.getStatus());
            ps.setInt(7, c.getId());

            ps.executeUpdate();
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateWithPassword(Customer c) {

        String sql = """
        UPDATE customers
        SET full_name = ?,
            email = ?,
            phone = ?,
            password = ?,
            address = ?,
            owner_id = ?,
            status = ?
        WHERE id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getPassword());
            ps.setString(5, c.getAddress());
            ps.setInt(6, c.getOwnerId());
            ps.setString(7, c.getStatus());
            ps.setInt(8, c.getId());

            ps.executeUpdate();
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insert(Customer c) {

        String sql = """
        INSERT INTO customers
        (full_name, email, phone, password, address, owner_id, status)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getPassword());
            ps.setString(5, c.getAddress());
            ps.setInt(6, c.getOwnerId());
            ps.setString(7, c.getStatus());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Customer> getCustomersByOwnerId(int ownerId) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE status = 'Active' AND owner_id = ? ORDER BY full_name ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Customer> getAllActiveCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE status = 'Active' ORDER BY full_name ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
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
        
        c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        c.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        return c;
    }
     public List<Customer> filterCustomers(String search, String statusFilter) {

        List<Customer> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT id, full_name, email, phone, profile_pic_url, created_at, status, tier_id "
                + "FROM customers WHERE 1=1 "
        );

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (full_name LIKE ? OR email LIKE ?) ");
        }

        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            sql.append(" AND status = ? ");
        }

        sql.append(" ORDER BY id DESC ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int index = 1;

            if (search != null && !search.trim().isEmpty()) {
                ps.setString(index++, "%" + search + "%");
                ps.setString(index++, "%" + search + "%");
            }

            if (statusFilter != null && !statusFilter.trim().isEmpty()) {
                ps.setString(index++, statusFilter);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Customer c = new Customer();

                c.setId(rs.getInt("id"));
                c.setFullName(rs.getString("full_name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setProfileURL(rs.getString("profile_pic_url"));
                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                c.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());       
                c.setStatus(rs.getString("status"));
                c.setTier(rs.getInt("tier_id"));

                list.add(c);
            }

            System.out.println("Loaded customers: " + list.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Customer getCustomerById(int id) {

        String sql = """
        SELECT id,
               full_name,
               email,
               phone,
               profile_pic_url,
               created_at,
               status,
               tier_id
        FROM customers
        WHERE id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Customer c = new Customer();

                c.setId(rs.getInt("id"));
                c.setFullName(rs.getString("full_name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setProfileURL(rs.getString("profile_pic_url"));
                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                c.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());       
                c.setStatus(rs.getString("status"));
                c.setTier(rs.getInt("tier_id"));

                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
}
