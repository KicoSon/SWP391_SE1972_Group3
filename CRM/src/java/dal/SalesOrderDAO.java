package dal;

import model.sales.SalesOrder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class SalesOrderDAO extends DBContext {

    // ============================================================
    // GET BY ID
    // ============================================================
    public SalesOrder getById(int id) {
        String sql = "SELECT so.*, so.order_number AS order_code, c.full_name AS customer_name " +
                     "FROM orders so " +
                     "LEFT JOIN customers c ON so.customer_id = c.id " +
                     "WHERE so.id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ============================================================
    // GET BY QUOTATION ID
    // ============================================================
    public SalesOrder getByQuotationId(int quotationId) {
        String sql = "SELECT so.*, so.order_number AS order_code, c.full_name AS customer_name " +
                     "FROM orders so " +
                     "LEFT JOIN customers c ON so.customer_id = c.id " +
                     "WHERE so.quotation_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, quotationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ============================================================
    // GET BY SALES ID
    // ============================================================
    public List<SalesOrder> getBySalesId(int salesId) {
        List<SalesOrder> list = new ArrayList<>();
        String sql = "SELECT so.*, so.order_number AS order_code, c.full_name AS customer_name " +
                     "FROM orders so " +
                     "LEFT JOIN customers c ON so.customer_id = c.id " +
                     "LEFT JOIN quotations q ON so.quotation_id = q.id " +
                     "LEFT JOIN opportunities o ON q.opportunity_id = o.id " +
                     "WHERE o.assigned_to = ? " +
                     "ORDER BY so.created_at DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, salesId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ============================================================
    // GET BY OPPORTUNITY ID
    // ============================================================
    public List<SalesOrder> getByOpportunityId(int opportunityId) {
        List<SalesOrder> list = new ArrayList<>();
        String sql = "SELECT so.*, so.order_number AS order_code, c.full_name AS customer_name " +
                     "FROM orders so " +
                     "LEFT JOIN customers c ON so.customer_id = c.id " +
                     "LEFT JOIN quotations q ON so.quotation_id = q.id " +
                     "WHERE q.opportunity_id = ? " +
                     "ORDER BY so.created_at DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, opportunityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ============================================================
    // INSERT
    // ============================================================
    public boolean insert(SalesOrder order) {
        String sql = "INSERT INTO orders " +
                     "(quotation_id, order_number, total_amount, status, customer_id, created_at) " +
                     "VALUES (?,?,?,?,?,GETDATE())";
        try {
            // we have to get customer from opportunity since opportunity is not in order
            PreparedStatement ps1 = connection.prepareStatement("SELECT c.id FROM quotations q JOIN opportunities o ON q.opportunity_id = o.id JOIN customers c ON o.customer_id = c.id WHERE q.id = ?");
            ps1.setInt(1, order.getQuotationId());
            ResultSet rs = ps1.executeQuery();
            Integer customerId = null;
            if (rs.next()) {
                customerId = rs.getInt(1);
            }

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, order.getQuotationId());
            ps.setString(2, order.getOrderCode() != null ? order.getOrderCode() : generateOrderCode());
            ps.setBigDecimal(3, order.getTotalAmount());
            ps.setString(4, order.getStatus() != null ? order.getStatus() : "Confirmed");
            if (customerId != null) {
                ps.setInt(5, customerId);
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // ============================================================
    // UPDATE STATUS
    // ============================================================
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE orders SET status=? WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // ============================================================
    // GENERATE ORDER CODE: ORD-YYYYMMDD-XXX
    // ============================================================
    public String generateOrderCode() {
        String datePart = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        String sql = "SELECT COUNT(*) FROM orders WHERE order_number LIKE ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "ORD-" + datePart + "-%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return String.format("ORD-%s-%03d", datePart, count);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "ORD-" + datePart + "-001";
    }

    // ============================================================
    // SYNC TO CUSTOMER CORE
    // ============================================================
    public boolean syncToCustomerCore(int orderId) {
        SalesOrder order = getById(orderId);
        if (order == null) return false;

        // Get customer_id from opportunity
        String getCustId = "SELECT so.customer_id FROM orders so WHERE so.id=?";
        String insertTx = "INSERT INTO transaction_history " +
                          "(customer_id, order_id, transaction_date, amount, type, description) " +
                          "VALUES (?,?,GETDATE(),?,?,?)";
        try {
            PreparedStatement ps1 = connection.prepareStatement(getCustId);
            ps1.setInt(1, orderId);
            ResultSet rs = ps1.executeQuery();
            if (!rs.next()) return false;
            int custId = rs.getInt("customer_id");
            if (rs.wasNull()) return false;

            PreparedStatement ps2 = connection.prepareStatement(insertTx);
            ps2.setInt(1, custId);
            ps2.setInt(2, orderId);
            ps2.setBigDecimal(3, order.getTotalAmount());
            ps2.setString(4, "Purchase");
            ps2.setString(5, "Đơn hàng " + order.getOrderCode());
            return ps2.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // ============================================================
    // HELPER
    // ============================================================
    private SalesOrder mapRow(ResultSet rs) throws SQLException {
        SalesOrder o = new SalesOrder();
        o.setId(rs.getInt("id"));
        o.setQuotationId(rs.getInt("quotation_id"));
        o.setOrderCode(rs.getString("order_number"));
        o.setStatus(rs.getString("status"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        try { o.setCustomerName(rs.getString("customer_name")); } catch (Exception ignored) {}
        try { o.setQuotationCode("QUO-" + String.format("%05d", o.getQuotationId())); } catch (Exception ignored) {}
        return o;
    }
}
