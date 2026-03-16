package dal;

import model.TicketFeedback;

import java.sql.*;
import java.util.*;

/**
 * DAO cho bảng ticket_feedback
 * Package: dal | extends DBContext
 *
 * Bảng liên quan:
 *   ticket_feedback  (id, ticket_id, customer_id, rating, comments, created_at)
 *   support_tickets  (id, title, priority, status, ...)
 *   customers        (id, full_name, ...)
 */
public class TicketFeedbackDAO extends DBContext {

    // SQL base dùng chung
    private static final String BASE_SELECT =
        "SELECT tf.id, tf.ticket_id, tf.customer_id, tf.rating, tf.comments, tf.created_at, " +
        "       c.full_name  AS customer_name, " +
        "       t.title      AS ticket_title, " +
        "       t.priority   AS ticket_priority, " +
        "       t.status     AS ticket_status " +
        "FROM ticket_feedback tf " +
        "LEFT JOIN support_tickets t ON t.id = tf.ticket_id " +
        "LEFT JOIN customers       c ON c.id = tf.customer_id ";

    // =========================================================
    // 1. SUBMIT — customer gửi feedback (1 ticket chỉ 1 lần)
    // =========================================================
    public boolean submitFeedback(TicketFeedback fb) {
        String sql =
            "INSERT INTO ticket_feedback (ticket_id, customer_id, rating, comments) " +
            "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, fb.getTicketId());
            ps.setInt(2, fb.getCustomerId());
            ps.setInt(3, fb.getRating());
            ps.setString(4, fb.getComments());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // Nếu vi phạm UNIQUE (đã feedback rồi) → trả false
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================
    // 2. HAS FEEDBACK — kiểm tra ticket đã được feedback chưa
    // =========================================================
    public boolean hasFeedback(int ticketId) {
        String sql = "SELECT COUNT(1) FROM ticket_feedback WHERE ticket_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // =========================================================
    // 3. GET BY TICKET — lấy feedback của 1 ticket cụ thể
    // =========================================================
    public TicketFeedback getFeedbackByTicketId(int ticketId) {
        String sql = BASE_SELECT + "WHERE tf.ticket_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // =========================================================
    // 4. GET BY RATING — staff xem danh sách, lọc theo rating
    //    ratingFilter = 0 → trả về tất cả
    // =========================================================
    public List<TicketFeedback> getFeedbacksByRating(int ratingFilter) {
        List<TicketFeedback> list = new ArrayList<>();
        String sql = BASE_SELECT +
            (ratingFilter >= 1 && ratingFilter <= 5 ? "WHERE tf.rating = ? " : "") +
            "ORDER BY tf.created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (ratingFilter >= 1 && ratingFilter <= 5) ps.setInt(1, ratingFilter);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================
    // 5. GET STATS — thống kê cho Dashboard & FeedbackManagement
    //    Keys:
    //      "total"           → int
    //      "avgRating"       → double
    //      "count1"~"count5" → int
    //      "pct1"~"pct5"     → double
    //      "recentFeedbacks" → List<Map<String,String>> (5 gần nhất)
    // =========================================================
    public Map<String, Object> getFeedbackStats() {
        Map<String, Object> stats = new HashMap<>();

        String sqlSummary =
            "SELECT COUNT(1) AS total, AVG(CAST(rating AS FLOAT)) AS avg_rating " +
            "FROM ticket_feedback";

        String sqlDist =
            "SELECT rating, COUNT(1) AS cnt FROM ticket_feedback " +
            "GROUP BY rating ORDER BY rating";

        String sqlRecent =
            "SELECT TOP 5 tf.rating, tf.comments, tf.created_at, c.full_name AS customer_name " +
            "FROM ticket_feedback tf " +
            "LEFT JOIN customers c ON c.id = tf.customer_id " +
            "ORDER BY tf.created_at DESC";

        try {
            // Tổng & trung bình
            try (PreparedStatement ps = connection.prepareStatement(sqlSummary);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    double avg = rs.getDouble("avg_rating");
                    stats.put("total",     total);
                    stats.put("avgRating", Math.round(avg * 10.0) / 10.0);
                }
            }

            // Phân bổ
            int total = (Integer) stats.getOrDefault("total", 0);
            int[] counts = new int[6];
            try (PreparedStatement ps = connection.prepareStatement(sqlDist);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int r = rs.getInt("rating");
                    if (r >= 1 && r <= 5) counts[r] = rs.getInt("cnt");
                }
            }
            for (int i = 1; i <= 5; i++) {
                stats.put("count" + i, counts[i]);
                double pct = total > 0 ? Math.round(counts[i] * 1000.0 / total) / 10.0 : 0.0;
                stats.put("pct" + i, pct);
            }

            // Recent feedbacks
            List<Map<String, String>> recent = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sqlRecent);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new LinkedHashMap<>();
                    row.put("rating",       String.valueOf(rs.getInt("rating")));
                    row.put("comments",     rs.getString("comments") != null ? rs.getString("comments") : "");
                    row.put("createdAt",    rs.getString("created_at"));
                    row.put("customerName", rs.getString("customer_name"));
                    recent.add(row);
                }
            }
            stats.put("recentFeedbacks", recent);

        } catch (SQLException e) {
            e.printStackTrace();
            stats.putIfAbsent("total",           0);
            stats.putIfAbsent("avgRating",       0.0);
            for (int i = 1; i <= 5; i++) {
                stats.putIfAbsent("count" + i,   0);
                stats.putIfAbsent("pct"   + i,   0.0);
            }
            stats.putIfAbsent("recentFeedbacks", new ArrayList<>());
        }
        return stats;
    }

    // ── Private helper ────────────────────────────────────────
    private TicketFeedback mapRow(ResultSet rs) throws SQLException {
        TicketFeedback fb = new TicketFeedback();
        fb.setId(rs.getInt("id"));
        fb.setTicketId(rs.getInt("ticket_id"));
        fb.setCustomerId(rs.getInt("customer_id"));
        fb.setRating(rs.getInt("rating"));
        fb.setComments(rs.getString("comments"));
        fb.setCreatedAt(rs.getString("created_at"));
        fb.setCustomerName(rs.getString("customer_name"));
        fb.setTicketTitle(rs.getString("ticket_title"));
        fb.setTicketPriority(rs.getString("ticket_priority"));
        fb.setTicketStatus(rs.getString("ticket_status"));
        return fb;
    }
}