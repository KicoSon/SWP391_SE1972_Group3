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
        String sql = "SELECT so.*, c.full_name AS customer_name, q.quotation_code " +
                     "FROM sales_orders so " +
                     "LEFT JOIN opportunities o ON so.opportunity_id = o.id " +
                     "LEFT JOIN customers c ON o.customer_id = c.id " +
                     "LEFT JOIN quotations q ON so.quotation_id = q.id " +
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
        String sql = "SELECT so.*, c.full_name AS customer_name, q.quotation_code " +
                     "FROM sales_orders so " +
                     "LEFT JOIN opportunities o ON so.opportunity_id = o.id " +
                     "LEFT JOIN customers c ON o.customer_id = c.id " +
                     "LEFT JOIN quotations q ON so.quotation_id = q.id " +
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
        String sql = "SELECT so.*, c.full_name AS customer_name, q.quotation_code " +
                     "FROM sales_orders so " +
                     "LEFT JOIN opportunities o ON so.opportunity_id = o.id " +
                     "LEFT JOIN customers c ON o.customer_id = c.id " +
                     "LEFT JOIN quotations q ON so.quotation_id = q.id " +
                     "WHERE o.assigned_sales_id = ? " +
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
    // INSERT
    // ============================================================
    public boolean insert(SalesOrder order) {
        String sql = "INSERT INTO sales_orders " +
                     "(quotation_id, opportunity_id, order_code, status, total_amount, " +
                     " order_date, delivery_date, shipping_address, payment_method, " +
                     " payment_status, created_by, created_at, updated_at) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,GETDATE(),GETDATE())";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, order.getQuotationId());
            ps.setInt(2, order.getOpportunityId());
            ps.setString(3, order.getOrderCode() != null ? order.getOrderCode() : generateOrderCode());
            ps.setString(4, order.getStatus() != null ? order.getStatus() : "Confirmed");
            ps.setBigDecimal(5, order.getTotalAmount());
            if (order.getOrderDate() != null) ps.setDate(6, new java.sql.Date(order.getOrderDate().getTime())); else ps.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            if (order.getDeliveryDate() != null) ps.setDate(7, new java.sql.Date(order.getDeliveryDate().getTime())); else ps.setNull(7, Types.DATE);
            ps.setString(8, order.getShippingAddress());
            ps.setString(9, order.getPaymentMethod());
            ps.setString(10, order.getPaymentStatus() != null ? order.getPaymentStatus() : "Pending");
            ps.setInt(11, order.getCreatedBy());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // ============================================================
    // UPDATE STATUS
    // ============================================================
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE sales_orders SET status=?, updated_at=GETDATE() WHERE id=?";
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
        String sql = "SELECT COUNT(*) FROM sales_orders WHERE order_code LIKE ?";
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
        String getCustId = "SELECT o.customer_id FROM sales_orders so " +
                           "JOIN opportunities o ON so.opportunity_id = o.id WHERE so.id=?";
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
        o.setOpportunityId(rs.getInt("opportunity_id"));
        o.setOrderCode(rs.getString("order_code"));
        o.setStatus(rs.getString("status"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setOrderDate(rs.getDate("order_date"));
        o.setDeliveryDate(rs.getDate("delivery_date"));
        o.setShippingAddress(rs.getString("shipping_address"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setPaymentStatus(rs.getString("payment_status"));
        o.setCreatedBy(rs.getInt("created_by"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        o.setUpdatedAt(rs.getTimestamp("updated_at"));
        try { o.setCustomerName(rs.getString("customer_name")); } catch (Exception ignored) {}
        try { o.setQuotationCode(rs.getString("quotation_code")); } catch (Exception ignored) {}
        return o;
    }
}
