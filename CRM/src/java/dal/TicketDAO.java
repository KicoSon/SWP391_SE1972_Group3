package dal;

import model.SupportTicket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO extends DBContext {

    // ── Private helper: map ResultSet → SupportTicket ────────
    private SupportTicket mapRow(ResultSet rs) throws SQLException {
        SupportTicket t = new SupportTicket();
        t.setId(rs.getInt("id"));
        t.setCustomerId(rs.getInt("customer_id"));
        t.setOrderId((Integer) rs.getObject("order_id"));
        t.setTitle(rs.getString("title"));
        t.setDescription(rs.getString("description"));
        t.setPriority(rs.getString("priority"));
        t.setStatus(rs.getString("status"));
        t.setAssignedTo(rs.getInt("assigned_to"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        t.setUpdateAt(rs.getTimestamp("update_at"));
        t.setCustomerName(rs.getString("customer_name"));
        t.setAssignedName(rs.getString("staff_name"));
        return t;
    }

    // SQL base dùng chung — JOIN đầy đủ
    private static final String BASE_SELECT
            = "SELECT t.*, "
            + "       c.full_name AS customer_name, "
            + "       u.full_name AS staff_name "
            + "FROM support_tickets t "
            + "LEFT JOIN customers c ON c.id = t.customer_id "
            + "LEFT JOIN users     u ON u.id = t.assigned_to ";

    // =========================================================
    // 1. FILTER — staff xem tất cả + search/status filter
    // =========================================================
    public List<SupportTicket> filterTickets(String search, String status) {
        List<SupportTicket> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT).append("WHERE 1=1 ");

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND t.title LIKE ? ");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND t.status = ? ");
        }
        sql.append("ORDER BY t.created_at DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            if (search != null && !search.trim().isEmpty()) {
                ps.setString(idx++, "%" + search.trim() + "%");
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(idx++, status);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================
    // 2. GET BY ID — kèm JOIN đầy đủ
    // =========================================================
    public SupportTicket getTicketById(int id) {
        String sql = BASE_SELECT + "WHERE t.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // =========================================================
    // 3. GET BY CUSTOMER
    // =========================================================
    public List<SupportTicket> getTicketsByCustomerId(int customerId) {
        List<SupportTicket> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE t.customer_id = ? ORDER BY t.created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================
    // 4. GET BY STAFF — "My Tickets"
    // =========================================================
    public List<SupportTicket> getTicketsByStaffId(int staffId) {
        List<SupportTicket> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE t.assigned_to = ? ORDER BY t.created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================
    // 5. INSERT
    // =========================================================
    public void insertTicket(SupportTicket t) {
        String sql
                = "INSERT INTO support_tickets "
                + "(customer_id, title, description, priority, status, assigned_to, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, t.getCustomerId());
            ps.setString(2, t.getTitle());
            ps.setString(3, t.getDescription());
            ps.setString(4, t.getPriority());
            ps.setString(5, t.getStatus());
            ps.setInt(6, t.getAssignedTo());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // 6. UPDATE STATUS
    // =========================================================
    public void updateTicketStatus(int id, String status) {
        String sql = "UPDATE support_tickets SET status = ?, update_at = GETDATE() WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // 7. UPDATE ASSIGNED TO — re-assign ticket (THÊM MỚI)
    // =========================================================
    public void updateAssignedTo(int ticketId, int staffId) {
        String sql = "UPDATE support_tickets SET assigned_to = ?, update_at = GETDATE() WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ps.setInt(2, ticketId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // 8. GET STAFF LIST — dropdown assign/re-assign
    // =========================================================
    public List<String[]> getStaffList() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id, full_name FROM users "
                + "WHERE is_active = 1 AND department = 'Customer Service' "
                + "ORDER BY full_name";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{String.valueOf(rs.getInt("id")), rs.getString("full_name")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================
    // 9. GET CUSTOMER LIST — dropdown chọn customer
    // =========================================================
    public List<String[]> getCustomerList() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id, full_name FROM customers WHERE status = 'active' ORDER BY full_name";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{String.valueOf(rs.getInt("id")), rs.getString("full_name")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
