package dal;

import model.SupportTicket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO extends DBContext {

    public List<SupportTicket> filterTickets(String search, String status) {

        List<SupportTicket> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT t.*, c.full_name AS customer_name, u.full_name AS staff_name "
                + "FROM support_tickets t "
                + "LEFT JOIN customers c ON t.customer_id = c.id "
                + "LEFT JOIN users u ON t.assigned_to = u.id "
                + "WHERE 1=1 "
        );
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND title LIKE ? ");
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ? ");
        }

        sql.append(" ORDER BY created_at DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int index = 1;

            if (search != null && !search.trim().isEmpty()) {
                ps.setString(index++, "%" + search + "%");
            }

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(index++, status);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

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
                list.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insertTicket(SupportTicket t) {

        String sql = "INSERT INTO support_tickets "
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<SupportTicket> getTicketsByStaffId(int staffId) {

        List<SupportTicket> list = new ArrayList<>();

        String sql
                = "SELECT t.*, c.full_name AS customer_name, u.full_name AS staff_name "
                + "FROM support_tickets t "
                + "LEFT JOIN customers c ON t.customer_id = c.id "
                + "LEFT JOIN users u ON t.assigned_to = u.id "
                + "WHERE t.assigned_to = ? ORDER BY t.created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, staffId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

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
                list.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<SupportTicket> getTicketsByCustomerId(int customerId) {

        List<SupportTicket> list = new ArrayList<>();

        String sql
                = "SELECT t.*, c.full_name AS customer_name, u.full_name AS staff_name "
                + "FROM support_tickets t "
                + "LEFT JOIN customers c ON t.customer_id = c.id "
                + "LEFT JOIN users ON t.assigned_to = u.id "
                + "WHERE t.customer_id = ? "
                + "ORDER BY t.created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

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

                list.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
