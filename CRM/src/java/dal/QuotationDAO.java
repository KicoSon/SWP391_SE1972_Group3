package dal;

import model.sales.Quotation;
import model.sales.QuotationItem;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class QuotationDAO extends DBContext {

    // ============================================================
    // GET BY OPPORTUNITY ID
    // ============================================================
    public List<Quotation> getByOpportunityId(int opportunityId) {
        List<Quotation> list = new ArrayList<>();
        String sql = "SELECT q.*, o.title AS opp_title, c.full_name AS customer_name, " +
                     "       u.full_name AS created_by_name " +
                     "FROM quotations q " +
                     "JOIN opportunities o ON q.opportunity_id = o.id " +
                     "LEFT JOIN customers c ON o.customer_id = c.id " +
                     "LEFT JOIN users u ON q.created_by = u.id " +
                     "WHERE q.opportunity_id = ? ORDER BY q.version DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, opportunityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ============================================================
    // GET BY ID
    // ============================================================
    public Quotation getById(int id) {
        String sql = "SELECT q.*, o.title AS opp_title, c.full_name AS customer_name, " +
                     "       u.full_name AS created_by_name " +
                     "FROM quotations q " +
                     "JOIN opportunities o ON q.opportunity_id = o.id " +
                     "LEFT JOIN customers c ON o.customer_id = c.id " +
                     "LEFT JOIN users u ON q.created_by = u.id " +
                     "WHERE q.id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ============================================================
    // GET LATEST VERSION
    // ============================================================
    public Quotation getLatestVersion(int opportunityId) {
        String sql = "SELECT TOP 1 q.*, o.title AS opp_title, c.full_name AS customer_name, " +
                     "       u.full_name AS created_by_name " +
                     "FROM quotations q " +
                     "JOIN opportunities o ON q.opportunity_id = o.id " +
                     "LEFT JOIN customers c ON o.customer_id = c.id " +
                     "LEFT JOIN users u ON q.created_by = u.id " +
                     "WHERE q.opportunity_id = ? ORDER BY q.version DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, opportunityId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ============================================================
    // INSERT (TRANSACTION with items)
    // DB columns: id, opportunity_id, version, status, total_amount,
    //             sent_at, approved_at, created_by, created_at
    // ============================================================
    public boolean insert(Quotation q, List<QuotationItem> items) {
        String sqlQ = "INSERT INTO quotations (opportunity_id, version, status, " +
                      "total_amount, created_by, created_at) " +
                      "VALUES (?,?,?,?,?,GETDATE())";
        String sqlI = "INSERT INTO quotation_items (quotation_id, product_id, product_name, " +
                      "quantity, unit_price, discount, tax_rate, line_total) VALUES (?,?,?,?,?,?,?,?)";
        try {
            connection.setAutoCommit(false);
            PreparedStatement psQ = connection.prepareStatement(sqlQ, Statement.RETURN_GENERATED_KEYS);
            psQ.setInt(1, q.getOpportunityId());
            psQ.setInt(2, q.getVersion() > 0 ? q.getVersion() : 1);
            psQ.setString(3, q.getStatus() != null ? q.getStatus() : "Draft");
            psQ.setBigDecimal(4, q.getTotalAmount() != null ? q.getTotalAmount() : BigDecimal.ZERO);
            psQ.setInt(5, q.getCreatedBy());
            psQ.executeUpdate();

            ResultSet keys = psQ.getGeneratedKeys();
            if (!keys.next()) throw new Exception("No generated key");
            int quotationId = keys.getInt(1);

            PreparedStatement psI = connection.prepareStatement(sqlI);
            for (QuotationItem item : items) {
                psI.setInt(1, quotationId);
                psI.setInt(2, item.getProductId());
                psI.setString(3, item.getProductName());
                psI.setInt(4, item.getQuantity());
                psI.setBigDecimal(5, item.getUnitPrice());
                psI.setBigDecimal(6, item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO);
                psI.setBigDecimal(7, item.getTaxRate() != null ? item.getTaxRate() : BigDecimal.ZERO);
                psI.setBigDecimal(8, item.getLineTotal());
                psI.addBatch();
            }
            psI.executeBatch();
            connection.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try { connection.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    // ============================================================
    // UPDATE (only Draft)
    // ============================================================
    public boolean update(Quotation q, List<QuotationItem> items) {
        String sqlQ = "UPDATE quotations SET total_amount=? WHERE id=? AND status='Draft'";
        String sqlDel = "DELETE FROM quotation_items WHERE quotation_id=?";
        String sqlI = "INSERT INTO quotation_items (quotation_id, product_id, product_name, " +
                      "quantity, unit_price, discount, tax_rate, line_total) VALUES (?,?,?,?,?,?,?,?)";
        try {
            connection.setAutoCommit(false);
            PreparedStatement psQ = connection.prepareStatement(sqlQ);
            psQ.setBigDecimal(1, q.getTotalAmount());
            psQ.setInt(2, q.getId());
            if (psQ.executeUpdate() == 0) throw new Exception("Update failed or quotation not in Draft status");

            PreparedStatement psDel = connection.prepareStatement(sqlDel);
            psDel.setInt(1, q.getId());
            psDel.executeUpdate();

            PreparedStatement psI = connection.prepareStatement(sqlI);
            for (QuotationItem item : items) {
                psI.setInt(1, q.getId());
                psI.setInt(2, item.getProductId());
                psI.setString(3, item.getProductName());
                psI.setInt(4, item.getQuantity());
                psI.setBigDecimal(5, item.getUnitPrice());
                psI.setBigDecimal(6, item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO);
                psI.setBigDecimal(7, item.getTaxRate() != null ? item.getTaxRate() : BigDecimal.ZERO);
                psI.setBigDecimal(8, item.getLineTotal());
                psI.addBatch();
            }
            psI.executeBatch();
            connection.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try { connection.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    // ============================================================
    // UPDATE STATUS
    // ============================================================
    public boolean updateStatus(int id, String status, Integer approvedBy) {
        String sql;
        if ("Approved".equalsIgnoreCase(status)) {
            sql = "UPDATE quotations SET status=?, approved_at=GETDATE() WHERE id=?";
        } else if ("Sent".equalsIgnoreCase(status)) {
            sql = "UPDATE quotations SET status=?, sent_at=GETDATE() WHERE id=?";
        } else {
            sql = "UPDATE quotations SET status=? WHERE id=?";
        }
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // ============================================================
    // CREATE NEW VERSION
    // ============================================================
    public Quotation createNewVersion(int quotationId) {
        Quotation src = getById(quotationId);
        if (src == null) return null;
        List<QuotationItem> srcItems = getItemsByQuotationId(quotationId);

        Quotation newQ = new Quotation();
        newQ.setOpportunityId(src.getOpportunityId());
        newQ.setVersion(src.getVersion() + 1);
        newQ.setStatus("Draft");
        newQ.setTotalAmount(src.getTotalAmount());
        newQ.setCreatedBy(src.getCreatedBy());

        if (insert(newQ, srcItems)) return getLatestVersion(src.getOpportunityId());
        return null;
    }

    // ============================================================
    // GET ITEMS BY QUOTATION ID
    // ============================================================
    public List<QuotationItem> getItemsByQuotationId(int id) {
        List<QuotationItem> list = new ArrayList<>();
        String sql = "SELECT * FROM quotation_items WHERE quotation_id=? ORDER BY id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                QuotationItem item = new QuotationItem();
                item.setId(rs.getInt("id"));
                item.setQuotationId(rs.getInt("quotation_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getBigDecimal("unit_price"));
                item.setDiscount(rs.getBigDecimal("discount"));
                item.setTaxRate(rs.getBigDecimal("tax_rate"));
                item.setLineTotal(rs.getBigDecimal("line_total"));
                list.add(item);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ============================================================
    // GENERATE CODE: QUO-YYYYMMDD-XXX (generated in Java, not stored in DB)
    // ============================================================
    public String generateQuotationCode() {
        String datePart = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        String sql = "SELECT COUNT(*) FROM quotations WHERE CONVERT(DATE, created_at) = CONVERT(DATE, GETDATE())";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return String.format("QUO-%s-%03d", datePart, count);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "QUO-" + datePart + "-001";
    }

    // ============================================================
    // HELPER - map DB columns to Quotation object
    // DB: id, opportunity_id, version, status, total_amount,
    //     sent_at, approved_at, created_by, created_at
    // ============================================================
    private Quotation mapRow(ResultSet rs) throws SQLException {
        Quotation q = new Quotation();
        q.setId(rs.getInt("id"));
        q.setOpportunityId(rs.getInt("opportunity_id"));
        q.setVersion(rs.getInt("version"));
        q.setStatus(rs.getString("status"));
        q.setCreatedBy(rs.getInt("created_by"));
        q.setTotalAmount(rs.getBigDecimal("total_amount"));
        q.setCreatedAt(rs.getTimestamp("created_at"));

        // These columns exist in DB but may not have corresponding model fields
        try { q.setValidUntil(rs.getDate("sent_at")); } catch (Exception ignored) {}

        // Join aliases - may not be present in all queries
        try { q.setOpportunityTitle(rs.getString("opp_title")); } catch (Exception ignored) {}
        try { q.setCustomerName(rs.getString("customer_name")); } catch (Exception ignored) {}
        try { q.setCreatedByName(rs.getString("created_by_name")); } catch (Exception ignored) {}

        // Generate a display code from id since quotation_code column doesn't exist
        q.setQuotationCode("QUO-" + String.format("%05d", q.getId()));

        return q;
    }
}
