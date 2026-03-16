package dal;

import java.sql.*;
import java.util.*;

/**
 * DAO tổng hợp số liệu cho Dashboard.
 * getFeedbackStats() đã cập nhật dùng bảng ticket_feedback.
 */
public class CSDashboardDAO extends DBContext {

    // =========================================================
    // TICKET STATS — từ bảng support_tickets
    // =========================================================
    public Map<String, Object> getTicketStats() {
        Map<String, Object> stats = new HashMap<>();

        String sqlStatus =
            "SELECT COUNT(1) AS total, " +
            "  SUM(CASE WHEN status = 'Open'        THEN 1 ELSE 0 END) AS open_count, " +
            "  SUM(CASE WHEN status = 'In Progress' THEN 1 ELSE 0 END) AS in_progress, " +
            "  SUM(CASE WHEN status = 'Resolved'    THEN 1 ELSE 0 END) AS resolved " +
            "FROM support_tickets";

        String sqlPriority =
            "SELECT " +
            "  SUM(CASE WHEN priority = 'low'    THEN 1 ELSE 0 END) AS low_count, " +
            "  SUM(CASE WHEN priority = 'medium' THEN 1 ELSE 0 END) AS medium_count, " +
            "  SUM(CASE WHEN priority = 'high'   THEN 1 ELSE 0 END) AS high_count, " +
            "  SUM(CASE WHEN priority = 'urgent' THEN 1 ELSE 0 END) AS urgent_count " +
            "FROM support_tickets";

        String sqlRecent =
            "SELECT TOP 5 t.id, t.title, t.status, t.priority, t.created_at, " +
            "             c.full_name AS customer_name " +
            "FROM support_tickets t " +
            "LEFT JOIN customers c ON c.id = t.customer_id " +
            "ORDER BY t.created_at DESC";

        try {
            try (PreparedStatement ps = connection.prepareStatement(sqlStatus);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("total",      rs.getInt("total"));
                    stats.put("open",       rs.getInt("open_count"));
                    stats.put("inProgress", rs.getInt("in_progress"));
                    stats.put("resolved",   rs.getInt("resolved"));
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(sqlPriority);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("low",    rs.getInt("low_count"));
                    stats.put("medium", rs.getInt("medium_count"));
                    stats.put("high",   rs.getInt("high_count"));
                    stats.put("urgent", rs.getInt("urgent_count"));
                }
            }

            List<Map<String, String>> recent = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sqlRecent);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new LinkedHashMap<>();
                    row.put("id",           String.valueOf(rs.getInt("id")));
                    row.put("title",        rs.getString("title"));
                    row.put("status",       rs.getString("status"));
                    row.put("priority",     rs.getString("priority"));
                    row.put("createdAt",    rs.getString("created_at"));
                    row.put("customerName", rs.getString("customer_name"));
                    recent.add(row);
                }
            }
            stats.put("recentTickets", recent);

        } catch (SQLException e) {
            e.printStackTrace();
            stats.putIfAbsent("total",         0); stats.putIfAbsent("open",    0);
            stats.putIfAbsent("inProgress",    0); stats.putIfAbsent("resolved",0);
            stats.putIfAbsent("low",           0); stats.putIfAbsent("medium",  0);
            stats.putIfAbsent("high",          0); stats.putIfAbsent("urgent",  0);
            stats.putIfAbsent("recentTickets", new ArrayList<>());
        }
        return stats;
    }

    // =========================================================
    // FEEDBACK STATS — từ bảng ticket_feedback (đã cập nhật)
    // =========================================================
    public Map<String, Object> getFeedbackStats() {
        Map<String, Object> stats = new HashMap<>();

        // Dùng ticket_feedback thay vì customer_feedback
        String sqlSummary =
            "SELECT COUNT(1) AS total, AVG(CAST(rating AS FLOAT)) AS avg_rating " +
            "FROM ticket_feedback";

        String sqlDist =
            "SELECT rating, COUNT(1) AS cnt FROM ticket_feedback " +
            "GROUP BY rating ORDER BY rating";

        // JOIN thêm support_tickets để có tiêu đề ticket
        String sqlRecent =
            "SELECT TOP 5 " +
            "  tf.rating, tf.comments, tf.created_at, " +
            "  c.full_name AS customer_name, " +
            "  t.title     AS ticket_title " +
            "FROM ticket_feedback tf " +
            "LEFT JOIN customers      c ON c.id = tf.customer_id " +
            "LEFT JOIN support_tickets t ON t.id = tf.ticket_id " +
            "ORDER BY tf.created_at DESC";

        try {
            try (PreparedStatement ps = connection.prepareStatement(sqlSummary);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    double avg = rs.getDouble("avg_rating");
                    stats.put("total",     total);
                    stats.put("avgRating", Math.round(avg * 10.0) / 10.0);
                }
            }

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

            List<Map<String, String>> recent = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sqlRecent);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new LinkedHashMap<>();
                    row.put("rating",       String.valueOf(rs.getInt("rating")));
                    row.put("comments",     rs.getString("comments") != null ? rs.getString("comments") : "");
                    row.put("createdAt",    rs.getString("created_at"));
                    row.put("customerName", rs.getString("customer_name"));
                    row.put("ticketTitle",  rs.getString("ticket_title")); // thêm mới
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
}