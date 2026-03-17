package dal;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO extends DBContext {

    // ── SQL base dùng chung cho filterCustomers + getCustomerById ──
    private static final String BASE_SELECT =
        "SELECT id, full_name, email, phone, profile_pic_url, " +
        "       created_at, status, tier_id " +
        "FROM customers ";

    // =========================================================
    // 1. FILTER — danh sách có search + status
    // =========================================================
    public List<Customer> filterCustomers(String search, String statusFilter) {
        List<Customer> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(BASE_SELECT).append("WHERE 1=1 ");

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (full_name LIKE ? OR email LIKE ?) ");
        }
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            sql.append("AND status = ? ");
        }
        sql.append("ORDER BY id DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            if (search != null && !search.trim().isEmpty()) {
                ps.setString(idx++, "%" + search.trim() + "%");
                ps.setString(idx++, "%" + search.trim() + "%");
            }
            if (statusFilter != null && !statusFilter.trim().isEmpty()) {
                ps.setString(idx++, statusFilter);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRowSimple(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================
    // 2. GET BY ID — dùng cho ViewCustomerServlet
    // =========================================================
    public Customer getCustomerById(int id) {
        String sql = BASE_SELECT + "WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRowSimple(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // =========================================================
    // 3. GET BY ID (full) — dùng cho edit form kèm JOIN tier + owner
    // =========================================================
    public Customer getCustomerByID(int id) {
        String sql =
            "SELECT c.id, c.full_name, c.email, c.phone, c.address, " +
            "       c.tier_id, t.tier_name, c.status, " +
            "       c.owner_id, u.full_name AS owner_name, c.created_at " +
            "FROM customers c " +
            "LEFT JOIN tiers t ON c.tier_id = t.id " +
            "LEFT JOIN users u ON c.owner_id = u.id " +
            "WHERE c.id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
                if (rs.getTimestamp("created_at") != null) {
                    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
                return c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // =========================================================
    // 4. GET ALL — dùng cho các module khác cần toàn bộ danh sách
    // =========================================================
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql =
            "SELECT c.*, t.tier_name " +
            "FROM customers c " +
            "LEFT JOIN tiers t ON c.tier_id = t.id";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
                c.setTierName(rs.getString("tier_name"));
                if (rs.getTimestamp("created_at") != null) {
                    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
                if (rs.getTimestamp("updated_at") != null) {
                    c.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================
    // 5. GET ALL ACTIVE — dropdown chọn customer
    // =========================================================
    public List<Customer> getAllActiveCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE status = 'active' ORDER BY full_name ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRowSimple(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================
    // 6. GET BY OWNER ID
    // =========================================================
    public List<Customer> getCustomersByOwnerId(int ownerId) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE status = 'active' AND owner_id = ? ORDER BY full_name ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRowSimple(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================
    // 7. UPDATE STATUS — toggle active/inactive
    // =========================================================
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE customers SET status = ?, updated_at = GETDATE() WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // =========================================================
    // 8. UPDATE WITHOUT PASSWORD
    //    FIX: version cũ gọi executeUpdate() 2 lần → update 2 lần
    // =========================================================
    public boolean updateWithoutPassword(Customer c) {
        String sql =
            "UPDATE customers " +
            "SET full_name = ?, email = ?, phone = ?, " +
            "    address = ?, owner_id = ?, status = ?, " +
            "    updated_at = GETDATE() " +
            "WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getAddress());
            ps.setInt(5, c.getOwnerId());
            ps.setString(6, c.getStatus());
            ps.setInt(7, c.getId());
            return ps.executeUpdate() > 0; // FIX: chỉ gọi 1 lần
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // =========================================================
    // 9. UPDATE WITH PASSWORD
    //    FIX: version cũ gọi executeUpdate() 2 lần → update 2 lần
    // =========================================================
    public boolean updateWithPassword(Customer c) {
        String sql =
            "UPDATE customers " +
            "SET full_name = ?, email = ?, phone = ?, password = ?, " +
            "    address = ?, owner_id = ?, status = ?, " +
            "    updated_at = GETDATE() " +
            "WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getPassword());
            ps.setString(5, c.getAddress());
            ps.setInt(6, c.getOwnerId());
            ps.setString(7, c.getStatus());
            ps.setInt(8, c.getId());
            return ps.executeUpdate() > 0; // FIX: chỉ gọi 1 lần
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // =========================================================
    // 10. INSERT
    // =========================================================
    public boolean insert(Customer c) {
        String sql =
            "INSERT INTO customers " +
            "(full_name, email, phone, password, address, owner_id, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

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

    public int insertAndReturnId(Customer c) {
        String sql =
            "INSERT INTO customers " +
            "(full_name, email, phone, password, address, owner_id, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getPassword());
            ps.setString(5, c.getAddress());
            ps.setInt(6, c.getOwnerId());
            ps.setString(7, c.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // =========================================================
    // Private helper: map ResultSet → Customer (các field cơ bản)
    // =========================================================
    private Customer mapRowSimple(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id"));
        c.setFullName(rs.getString("full_name"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setProfileURL(rs.getString("profile_pic_url"));
        c.setStatus(rs.getString("status"));
        c.setTier(rs.getInt("tier_id"));
        if (rs.getTimestamp("created_at") != null) {
            c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return c;
    }

//    public List<Customer> filterCustomers(String search, String statusFilter) {
//
//        List<Customer> list = new ArrayList<>();
//
//        StringBuilder sql = new StringBuilder(
//                "SELECT id, full_name, email, phone, profile_pic_url, created_at, status, tier_id "
//                + "FROM customers WHERE 1=1 "
//        );
//
//        if (search != null && !search.trim().isEmpty()) {
//            sql.append(" AND (full_name LIKE ? OR email LIKE ?) ");
//        }
//
//        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
//            sql.append(" AND status = ? ");
//        }
//
//        sql.append(" ORDER BY id DESC ");
//
//        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
//
//            int index = 1;
//
//            if (search != null && !search.trim().isEmpty()) {
//                ps.setString(index++, "%" + search + "%");
//                ps.setString(index++, "%" + search + "%");
//            }
//
//            if (statusFilter != null && !statusFilter.trim().isEmpty()) {
//                ps.setString(index++, statusFilter);
//            }
//
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//
//                Customer c = new Customer();
//
//                c.setId(rs.getInt("id"));
//                c.setFullName(rs.getString("full_name"));
//                c.setEmail(rs.getString("email"));
//                c.setPhone(rs.getString("phone"));
//                c.setProfileURL(rs.getString("profile_pic_url"));
//                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
//                c.setStatus(rs.getString("status"));
//                c.setTier(rs.getInt("tier_id"));
//
//                list.add(c);
//            }
//
//            System.out.println("Loaded customers: " + list.size());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
//
//    public Customer getCustomerById(int id) {
//
//        String sql = """
//        SELECT id,
//               full_name,
//               email,
//               phone,
//               profile_pic_url,
//               created_at,
//               status,
//               tier_id
//        FROM customers
//        WHERE id = ?
//    """;
//
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//
//            ps.setInt(1, id);
//
//            ResultSet rs = ps.executeQuery();
//
//            if (rs.next()) {
//
//                Customer c = new Customer();
//
//                c.setId(rs.getInt("id"));
//                c.setFullName(rs.getString("full_name"));
//                c.setEmail(rs.getString("email"));
//                c.setPhone(rs.getString("phone"));
//                c.setProfileURL(rs.getString("profile_pic_url"));
//                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
//                c.setStatus(rs.getString("status"));
//                c.setTier(rs.getInt("tier_id"));
//
//                return c;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
    public void updatePasswordByEmail(String email, String password) throws Exception {

        String sql = "UPDATE customers SET password=? WHERE email=?";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, password);
        ps.setString(2, email);

        ps.executeUpdate();
    }

    public boolean checkEmailExists(String email) throws Exception {

        String sql = "SELECT 1 FROM customers WHERE email = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();

        return rs.next();
    }

    public void updatePassword(int id, String password) {

        String sql = "UPDATE Customer SET password = ? WHERE id = ?";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, password);
            ps.setInt(2, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
