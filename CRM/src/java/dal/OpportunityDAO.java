package dal;

import model.sales.Opportunity;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * Data Access Object for the opportunities table. Schema
 * (sales_pipeline_schema.sql): assigned_sales_id INT NOT NULL REFERENCES
 * staffs(id) stage NVARCHAR(100) -- plain text, not FK close_probability FLOAT
 * lost_reason NVARCHAR(500) -- plain text, not FK
 */
public class OpportunityDAO extends DBContext {

    // ------------------------------------------------------------------ BASE SELECT
    private static final String BASE_SELECT
            = "SELECT o.id, o.title, o.customer_id, o.lead_id, o.assigned_to AS assigned_sales_id, "
            + "       ps.name AS stage, o.status, o.expected_value, o.win_probability AS close_probability, "
            + "       o.expected_close_date, '' AS notes, o.created_by, o.created_at, "
            + "       lr.reason AS lost_reason, "
            + "       c.full_name  AS customer_name, "
            + "       u.full_name  AS sales_name "
            + "FROM opportunities o "
            + "LEFT JOIN customers c ON o.customer_id = c.id "
            + "LEFT JOIN users u ON o.assigned_to = u.id "
            + "LEFT JOIN pipeline_stages ps ON o.stage_id = ps.id "
            + "LEFT JOIN lost_reasons lr ON o.lost_reason_id = lr.id ";

    // ------------------------------------------------------------------ MAP ROW
    private Opportunity mapRow(ResultSet rs) throws SQLException {
        Opportunity o = new Opportunity();
        o.setId(rs.getInt("id"));
        o.setTitle(rs.getString("title"));

        int cid = rs.getInt("customer_id");
        o.setCustomerId(rs.wasNull() ? null : cid);

        long lid = rs.getLong("lead_id");
        o.setLeadId(rs.wasNull() ? null : lid);

        o.setAssignedSalesId(rs.getInt("assigned_sales_id"));
        o.setStage(rs.getString("stage"));
        o.setStatus(rs.getString("status"));
        o.setExpectedValue(rs.getBigDecimal("expected_value"));
        o.setCloseProbability(rs.getDouble("close_probability"));
        o.setExpectedCloseDate(rs.getDate("expected_close_date"));
        o.setNotes(rs.getString("notes"));
        o.setCreatedBy(rs.getInt("created_by"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        o.setCustomerName(rs.getString("customer_name"));
        o.setAssignedSalesName(rs.getString("sales_name"));
        o.setLostReason(rs.getString("lost_reason"));
        return o;
    }

    // ================================================================== QUERIES
    /**
     * Flexible filter – any parameter may be null to skip that filter.
     */
    public List<Opportunity> filterOpportunities(String search, String stage,
            String status, Integer salesId) {
        List<Opportunity> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            sql.append("AND o.title LIKE ? ");
            params.add("%" + search.trim() + "%");
        }
        if (stage != null && !stage.isBlank()) {
            sql.append("AND ps.name = ? ");
            params.add(stage);
        }
        if (status != null && !status.isBlank()) {
            sql.append("AND o.status = ? ");
            params.add(status);
        }
        if (salesId != null) {
            sql.append("AND o.assigned_to = ? ");
            params.add(salesId);
        }
        sql.append("ORDER BY o.created_at DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Opportunity getById(int id) {
        String sql = BASE_SELECT + "WHERE o.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Delegates to filterOpportunities with no salesId filter.
     */
    public List<Opportunity> getForExport(String stage, String status) {
        return filterOpportunities(null, stage, status, null);
    }

    // ================================================================== AGGREGATES
    public Map<String, Integer> countByStage() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql
                = "SELECT ps.name AS stage, COUNT(o.id) AS cnt "
                + "FROM opportunities o "
                + "JOIN pipeline_stages ps ON o.stage_id = ps.id "
                + "WHERE o.status = 'Open' "
                + "GROUP BY ps.name "
                + "ORDER BY ps.name";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("stage"), rs.getInt("cnt"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Integer> countByStageForSales(int salesId) {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql
                = "SELECT ps.name AS stage, COUNT(o.id) AS cnt "
                + "FROM opportunities o "
                + "JOIN pipeline_stages ps ON o.stage_id = ps.id "
                + "WHERE o.status = 'Open' AND o.assigned_to = ? "
                + "GROUP BY ps.name "
                + "ORDER BY ps.name";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, salesId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("stage"), rs.getInt("cnt"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, BigDecimal> valueByStage() {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        String sql
                = "SELECT ps.name AS stage, SUM(o.expected_value) AS total "
                + "FROM opportunities o "
                + "JOIN pipeline_stages ps ON o.stage_id = ps.id "
                + "WHERE o.status = 'Open' "
                + "GROUP BY ps.name "
                + "ORDER BY ps.name";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BigDecimal v = rs.getBigDecimal("total");
                map.put(rs.getString("stage"), v != null ? v : BigDecimal.ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Monthly Won revenue for the given year. Returns List of
     * Object[]{monthNumber(int), revenue(BigDecimal)}.
     */
    public List<Object[]> getMonthlyRevenue(int year) {
        List<Object[]> list = new ArrayList<>();
        String sql
                = "SELECT MONTH(o.expected_close_date) AS mnth, "
                + "       SUM(o.expected_value) AS revenue "
                + "FROM opportunities o "
                + "WHERE o.status = 'Won' AND YEAR(o.expected_close_date) = ? "
                + "GROUP BY MONTH(o.expected_close_date) "
                + "ORDER BY mnth";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{rs.getInt("mnth"), rs.getBigDecimal("revenue")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ================================================================== WRITE
    public void insert(Opportunity opp) {
        String sql
                = "INSERT INTO opportunities "
                + "(title, customer_id, lead_id, assigned_to, stage_id, status, "
                + " expected_value, win_probability, expected_close_date, "
                + " created_by, created_at) "
                + "VALUES (?,?,?,?,(SELECT TOP 1 id FROM pipeline_stages WHERE name=?),?,?,?,?,?,GETDATE())";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, opp.getTitle());
            setNullableInt(ps, 2, opp.getCustomerId());
            setNullableLong(ps, 3, opp.getLeadId());
            ps.setInt(4, opp.getAssignedSalesId());
            ps.setString(5, opp.getStage() != null ? opp.getStage() : "Qualification");
            ps.setString(6, opp.getStatus() != null ? opp.getStatus() : "Open");
            ps.setBigDecimal(7, opp.getExpectedValue());
            ps.setDouble(8, opp.getCloseProbability());
            setNullableDate(ps, 9, opp.getExpectedCloseDate());
            ps.setInt(10, opp.getCreatedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                opp.setId(keys.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Opportunity opp) {
        String sql
                = "UPDATE opportunities SET "
                + "title=?, customer_id=?, assigned_to=?, "
                + "stage_id=(SELECT TOP 1 id FROM pipeline_stages WHERE name=?), "
                + "status=?, "
                + "expected_value=?, win_probability=?, expected_close_date=?, "
                + "lost_reason_id=(SELECT TOP 1 id FROM lost_reasons WHERE reason=?) "
                + "WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, opp.getTitle());
            setNullableInt(ps, 2, opp.getCustomerId());
            ps.setInt(3, opp.getAssignedSalesId());
            ps.setString(4, opp.getStage() != null ? opp.getStage() : "Qualification");
            ps.setString(5, opp.getStatus() != null ? opp.getStatus() : "Open");
            ps.setBigDecimal(6, opp.getExpectedValue());
            ps.setDouble(7, opp.getCloseProbability());
            setNullableDate(ps, 8, opp.getExpectedCloseDate());
            ps.setString(9, opp.getLostReason());
            ps.setInt(10, opp.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update only the stage column (used by drag-and-drop pipeline board).
     */
    public void updateStage(int id, String stageName) {
        String sql = "UPDATE opportunities SET stage_id = (SELECT TOP 1 id FROM pipeline_stages WHERE name=?) WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, stageName);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void assignToSales(int id, int newSalesId) {
        String sql = "UPDATE opportunities SET assigned_to = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newSalesId);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Close an opportunity as Won or Lost.
     */
    public void closeOpportunity(int id, String status, String lostReasonText) {
        try {
            if ("Won".equalsIgnoreCase(status)) {
                String sql = "UPDATE opportunities SET status='Won', "
                        + "stage_id=(SELECT TOP 1 id FROM pipeline_stages WHERE name='Closed Won'), "
                        + "lost_reason_id=NULL WHERE id=?";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            } else {
                String sql = "UPDATE opportunities SET status='Lost', "
                        + "stage_id=(SELECT TOP 1 id FROM pipeline_stages WHERE name='Closed Lost'), "
                        + "lost_reason_id=(SELECT TOP 1 id FROM lost_reasons WHERE reason=?) WHERE id=?";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, lostReasonText);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM opportunities WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================================================================== HELPERS
    private void setNullableInt(PreparedStatement ps, int idx, Integer val) throws SQLException {
        if (val != null) {
            ps.setInt(idx, val);
        } else {
            ps.setNull(idx, Types.INTEGER);
        }
    }

    private void setNullableLong(PreparedStatement ps, int idx, Long val) throws SQLException {
        if (val != null) {
            ps.setLong(idx, val);
        } else {
            ps.setNull(idx, Types.BIGINT);
        }
    }

    private void setNullableDate(PreparedStatement ps, int idx, java.util.Date date) throws SQLException {
        if (date != null) {
            ps.setDate(idx, new java.sql.Date(date.getTime()));
        } else {
            ps.setNull(idx, Types.DATE);
        }
    }

    public List<Opportunity> getAllOpportunities() {
        List<Opportunity> list = new ArrayList<>();
        String sql = "SELECT id, title, customer_id FROM opportunities";
        try (PreparedStatement ps = getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            Opportunity o = new Opportunity();
            o.setId(rs.getInt("id"));
            o.setTitle(rs.getString("title"));
            o.setCustomerId(rs.getInt("customer_id"));
            list.add(o);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ================================================================== NEW MODULE 2 METHODS

    public void addOpportunityProduct(model.sales.OpportunityProduct op) {
        String sql = "INSERT INTO opportunity_products (opportunity_id, product_id, quantity, unit_price, discount) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, op.getOpportunityId());
            ps.setInt(2, op.getProductId());
            ps.setInt(3, op.getQuantity());
            ps.setBigDecimal(4, op.getUnitPrice());
            ps.setBigDecimal(5, op.getDiscount());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<model.sales.OpportunityProduct> getOpportunityProducts(int opportunityId) {
        List<model.sales.OpportunityProduct> list = new ArrayList<>();
        String sql = "SELECT op.*, p.name AS product_name FROM opportunity_products op JOIN products p ON op.product_id = p.id WHERE op.opportunity_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, opportunityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.sales.OpportunityProduct op = new model.sales.OpportunityProduct();
                op.setId(rs.getInt("id"));
                op.setOpportunityId(rs.getInt("opportunity_id"));
                op.setProductId(rs.getInt("product_id"));
                op.setQuantity(rs.getInt("quantity"));
                op.setUnitPrice(rs.getBigDecimal("unit_price"));
                op.setDiscount(rs.getBigDecimal("discount"));
                try { op.setTotalPrice(rs.getBigDecimal("total_price")); } catch (Exception ignored) {
                    // total_price column may not exist, calculate it
                    java.math.BigDecimal price = op.getUnitPrice() != null ? op.getUnitPrice() : java.math.BigDecimal.ZERO;
                    java.math.BigDecimal disc = op.getDiscount() != null ? op.getDiscount() : java.math.BigDecimal.ZERO;
                    op.setTotalPrice(price.multiply(java.math.BigDecimal.valueOf(op.getQuantity())).subtract(disc));
                }
                try { op.setNotes(rs.getString("notes")); } catch (Exception ignored) {}
                try { op.setCreatedAt(rs.getTimestamp("created_at")); } catch (Exception ignored) {}
                op.setProductName(rs.getString("product_name"));
                list.add(op);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<model.sales.StageHistory> getOpportunityTimeline(int opportunityId) {
        List<model.sales.StageHistory> list = new ArrayList<>();
        String sql = "SELECT sh.*, u.full_name FROM stage_history sh LEFT JOIN users u ON sh.changed_by = u.id WHERE sh.opportunity_id = ? ORDER BY sh.changed_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, opportunityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.sales.StageHistory sh = new model.sales.StageHistory();
                sh.setId(rs.getInt("id"));
                sh.setOpportunityId(rs.getInt("opportunity_id"));
                sh.setOldStage(rs.getString("old_stage"));
                sh.setNewStage(rs.getString("new_stage"));
                sh.setChangedBy(rs.getInt("changed_by"));
                sh.setChangedAt(rs.getTimestamp("changed_at"));
                sh.setNotes(rs.getString("notes"));
                sh.setChangedByName(rs.getString("full_name"));
                list.add(sh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Opportunity> getOpportunitiesByStage(String stageName) {
        return filterOpportunities(null, stageName, "Open", null);
    }

    public boolean convertLeadToOpportunity(long leadId, Opportunity opp) {
        try {
            connection.setAutoCommit(false);
            
            // 1. Insert Opportunity
            insert(opp);
            
            // 2. Update Lead status
            String updateLead = "UPDATE leads SET status = 'Converted' WHERE id = ?";
            try(PreparedStatement ps = connection.prepareStatement(updateLead)) {
                ps.setLong(1, leadId);
                ps.executeUpdate();
            }
            
            connection.commit();
            return true;
        } catch (Exception e) {
            try { connection.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (Exception ex) {}
        }
    }
    
    // Additional metrics for Phase 12 Sales Dashboard
    public BigDecimal getPipelineValue() {
        String sql = "SELECT SUM(expected_value) AS total FROM opportunities WHERE status = 'Open'";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal("total");
        } catch (Exception e) { e.printStackTrace(); }
        return BigDecimal.ZERO;
    }

    public double getWinRate() {
        String sql = "SELECT CAST(COUNT(CASE WHEN status='Won' THEN 1 END) AS FLOAT) * 100.0 / NULLIF(COUNT(*), 0) AS win_rate FROM opportunities";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("win_rate");
        } catch (Exception e) { e.printStackTrace(); }
        return 0.0;
    }

    public BigDecimal getForecastRevenue() {
        String sql = "SELECT SUM(expected_value * (win_probability / 100.0)) AS forecast FROM opportunities WHERE status = 'Open'";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal("forecast");
        } catch (Exception e) { e.printStackTrace(); }
        return BigDecimal.ZERO;
    }
}
