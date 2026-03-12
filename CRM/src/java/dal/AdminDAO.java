package dal;

import java.sql.*;
import java.util.*;
import model.*;
import model.admin.DashboardTrend;

public class AdminDAO extends DBContext {

    /* ===================== TOTAL STAT ===================== */

    public double getTotalRevenue() {

        String sql = "SELECT SUM(total_amount) FROM orders WHERE status='PAID'";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTotalOrders() {
        return count("SELECT COUNT(*) FROM orders");
    }

    public int getTotalCustomers() {
        return count("SELECT COUNT(*) FROM customers");
    }

    public int getTotalProducts() {
        return count("SELECT COUNT(*) FROM products");
    }

    public int getTotalStaff() {
        return count("SELECT COUNT(*) FROM users WHERE role='STAFF'");
    }

    public int getTotalTickets() {
        return count("SELECT COUNT(*) FROM support_tickets");
    }

    private int count(String sql) {

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /* ===================== REVENUE TREND ===================== */

    public List<DashboardTrend> getRevenueByMonth() {

        List<DashboardTrend> list = new ArrayList<>();

        String sql = """
            SELECT 
                FORMAT(created_at,'MM-yyyy') AS month,
                SUM(total_amount) revenue
            FROM orders
            WHERE status='PAID'
            GROUP BY FORMAT(created_at,'MM-yyyy')
            ORDER BY MIN(created_at)
        """;

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                DashboardTrend t = new DashboardTrend();

                t.setLabel(rs.getString("month"));
                t.setValue(rs.getDouble("revenue"));

                list.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /* ===================== ORDERS STATUS ===================== */

    public Map<String, Integer> getOrdersByStatus() {

        Map<String, Integer> map = new LinkedHashMap<>();

        String sql = """
            SELECT status, COUNT(*) total
            FROM orders
            GROUP BY status
        """;

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString("status"), rs.getInt("total"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /* ===================== TICKET STATUS ===================== */

    public Map<String, Integer> getTicketsByStatus() {

        Map<String, Integer> map = new LinkedHashMap<>();

        String sql = """
            SELECT status, COUNT(*) total
            FROM support_tickets
            GROUP BY status
        """;

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString("status"), rs.getInt("total"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /* ===================== TICKET PRIORITY ===================== */

    public Map<String, Integer> getTicketsByPriority() {

        Map<String, Integer> map = new LinkedHashMap<>();

        String sql = """
            SELECT priority, COUNT(*) total
            FROM support_tickets
            GROUP BY priority
        """;

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString("priority"), rs.getInt("total"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /* ===================== TOP PRODUCTS ===================== */

    public List<Map<String, Object>> getTopProducts(int limit) {

        List<Map<String, Object>> list = new ArrayList<>();

        String sql = """
            SELECT TOP (?) 
                p.name,
                SUM(op.quantity) totalSold,
                SUM(op.quantity * op.unit_price) revenue
            FROM opportunity_products op
            JOIN products p ON op.product_id = p.id
            GROUP BY p.name
            ORDER BY totalSold DESC
        """;

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, limit);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Map<String, Object> map = new HashMap<>();

                map.put("name", rs.getString("name"));
                map.put("totalSold", rs.getInt("totalSold"));
                map.put("revenue", rs.getDouble("revenue"));

                list.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /* ===================== RECENT ORDERS ===================== */

//    public List<Order> getRecentOrders(int limit) {
//
//        List<Order> list = new ArrayList<>();
//
//        String sql = """
//            SELECT TOP (?) 
//                o.id,
//                c.full_name,
//                o.total_amount,
//                o.status,
//                o.created_at
//            FROM orders o
//            LEFT JOIN customers c ON o.customer_id = c.id
//            ORDER BY o.created_at DESC
//        """;
//
//        try {
//
//            PreparedStatement ps = connection.prepareStatement(sql);
//            ps.setInt(1, limit);
//
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//
//                Order o = new Order();
//
//                o.setId(rs.getInt("id"));
//                o.setCustomerName(rs.getString("full_name"));
//                o.setTotalAmount(rs.getDouble("total_amount"));
//                o.setStatus(rs.getString("status"));
//                o.setOrderDate(rs.getTimestamp("created_at"));
//
//                list.add(o);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
}