package dal;

import java.sql.*;
import java.util.*;

/**
 * DashboardDAO - getFeedbackStats() gộp chung
 * customer_feedback + ticket_feedback bằng UNION ALL.
 */
public class CSDashboardDAO extends DBContext {

    // =========================================================
    // TICKET STATS — không thay đổi
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
            stats.putIfAbsent("total",         0); stats.putIfAbsent("open",     0);
            stats.putIfAbsent("inProgress",    0); stats.putIfAbsent("resolved", 0);
            stats.putIfAbsent("low",           0); stats.putIfAbsent("medium",   0);
            stats.putIfAbsent("high",          0); stats.putIfAbsent("urgent",   0);
            stats.putIfAbsent("recentTickets", new ArrayList<>());
        }
        return stats;
    }

    // =========================================================
    // FEEDBACK STATS — gộp customer_feedback + ticket_feedback
    //
    // Keys trả về:
    //   "total"            → tổng gộp cả 2 bảng     (int)
    //   "totalCustomer"    → chỉ customer_feedback   (int)
    //   "totalTicket"      → chỉ ticket_feedback     (int)
    //   "avgRating"        → trung bình gộp          (double)
    //   "count1"~"count5"  → số lượng từng mức gộp  (int)
    //   "pct1"~"pct5"      → % từng mức gộp          (double)
    //   "recentFeedbacks"  → 5 gần nhất từ cả 2      (List<Map>)
    //                        Map có thêm key "source" = "customer"|"ticket"
    // =========================================================
    public Map<String, Object> getFeedbackStats() {
        Map<String, Object> stats = new HashMap<>();

        // Đếm riêng từng bảng để hiển thị phân biệt trên Dashboard
        String sqlCount =
            "SELECT " +
            "  (SELECT COUNT(1) FROM customer_feedback) AS cnt_customer, " +
            "  (SELECT COUNT(1) FROM ticket_feedback)   AS cnt_ticket";

        // Tổng gộp + trung bình
        String sqlSummary =
            "SELECT COUNT(1) AS total, AVG(CAST(rating AS FLOAT)) AS avg_rating " +
            "FROM ( " +
            "  SELECT rating FROM customer_feedback " +
            "  UNION ALL " +
            "  SELECT rating FROM ticket_feedback " +
            ") combined";

        // Phân bổ từng mức gộp
        String sqlDist =
            "SELECT rating, COUNT(1) AS cnt " +
            "FROM ( " +
            "  SELECT rating FROM customer_feedback " +
            "  UNION ALL " +
            "  SELECT rating FROM ticket_feedback " +
            ") combined " +
            "GROUP BY rating ORDER BY rating";

        // 5 feedback mới nhất từ cả 2 bảng, kèm cột source để JSP phân biệt
        String sqlRecent =
            "SELECT TOP 5 rating, comments, created_at, customer_name, source " +
            "FROM ( " +
            "  SELECT cf.rating, cf.comments, cf.created_at, " +
            "         c.full_name AS customer_name, 'customer' AS source " +
            "  FROM customer_feedback cf " +
            "  LEFT JOIN customers c ON c.id = cf.customer_id " +
            "  UNION ALL " +
            "  SELECT tf.rating, tf.comments, tf.created_at, " +
            "         c.full_name AS customer_name, 'ticket' AS source " +
            "  FROM ticket_feedback tf " +
            "  LEFT JOIN customers c ON c.id = tf.customer_id " +
            ") combined " +
            "ORDER BY created_at DESC";

        try {
            // Đếm riêng
            try (PreparedStatement ps = connection.prepareStatement(sqlCount);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("totalCustomer", rs.getInt("cnt_customer"));
                    stats.put("totalTicket",   rs.getInt("cnt_ticket"));
                }
            }

            // Tổng gộp + avg
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

            // Recent gộp
            List<Map<String, String>> recent = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sqlRecent);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new LinkedHashMap<>();
                    row.put("rating",       String.valueOf(rs.getInt("rating")));
                    row.put("comments",     rs.getString("comments") != null ? rs.getString("comments") : "");
                    row.put("createdAt",    rs.getString("created_at"));
                    row.put("customerName", rs.getString("customer_name"));
                    row.put("source",       rs.getString("source")); // "customer" | "ticket"
                    recent.add(row);
                }
            }
            stats.put("recentFeedbacks", recent);

        } catch (SQLException e) {
            e.printStackTrace();
            stats.putIfAbsent("total",           0);
            stats.putIfAbsent("totalCustomer",   0);
            stats.putIfAbsent("totalTicket",     0);
            stats.putIfAbsent("avgRating",       0.0);
            for (int i = 1; i <= 5; i++) {
                stats.putIfAbsent("count" + i,   0);
                stats.putIfAbsent("pct"   + i,   0.0);
            }
            stats.putIfAbsent("recentFeedbacks", new ArrayList<>());
        }
        return stats;
    }
    public Map<String, Object> getOverdueStats() {
        Map<String, Object> stats = new HashMap<>();
 
        // Đếm từng loại priority quá hạn SLA
        String sqlCount =
            "SELECT " +
            "  SUM(CASE WHEN priority='Urgent' " +
            "       AND DATEDIFF(HOUR, created_at, GETDATE()) > 4   THEN 1 ELSE 0 END) AS overdue_urgent, " +
            "  SUM(CASE WHEN priority='High' " +
            "       AND DATEDIFF(HOUR, created_at, GETDATE()) > 24  THEN 1 ELSE 0 END) AS overdue_high, " +
            "  SUM(CASE WHEN priority='Medium' " +
            "       AND DATEDIFF(HOUR, created_at, GETDATE()) > 72  THEN 1 ELSE 0 END) AS overdue_medium " +
            "FROM support_tickets " +
            "WHERE status IN ('Open','In Progress')";
 
        // 5 ticket quá hạn lâu nhất để hiện trong dashboard
        String sqlList =
            "SELECT TOP 5 " +
            "  t.id, t.title, t.priority, t.status, " +
            "  DATEDIFF(HOUR, t.created_at, GETDATE()) AS hours_elapsed, " +
            "  c.full_name AS customer_name " +
            "FROM support_tickets t " +
            "LEFT JOIN customers c ON c.id = t.customer_id " +
            "WHERE t.status IN ('Open','In Progress') " +
            "AND ( " +
            "  (t.priority='Urgent' AND DATEDIFF(HOUR,t.created_at,GETDATE()) > 4) OR " +
            "  (t.priority='High'   AND DATEDIFF(HOUR,t.created_at,GETDATE()) > 24) OR " +
            "  (t.priority='Medium' AND DATEDIFF(HOUR,t.created_at,GETDATE()) > 72) OR " +
            "  (t.priority='Low'    AND DATEDIFF(HOUR,t.created_at,GETDATE()) > 168) " +
            ") " +
            "ORDER BY DATEDIFF(HOUR,t.created_at,GETDATE()) DESC";
 
        try {
            // Đếm
            try (PreparedStatement ps = connection.prepareStatement(sqlCount);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int urgent = rs.getInt("overdue_urgent");
                    int high   = rs.getInt("overdue_high");
                    int medium = rs.getInt("overdue_medium");
                    stats.put("overdueUrgent", urgent);
                    stats.put("overdueHigh",   high);
                    stats.put("overdueMedium", medium);
                    stats.put("overdueTotal",  urgent + high + medium);
                }
            }
 
            // Danh sách
            List<Map<String, String>> list = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sqlList);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new LinkedHashMap<>();
                    row.put("id",           String.valueOf(rs.getInt("id")));
                    row.put("title",        rs.getString("title"));
                    row.put("priority",     rs.getString("priority"));
                    row.put("status",       rs.getString("status"));
                    row.put("hoursElapsed", String.valueOf(rs.getInt("hours_elapsed")));
                    row.put("customerName", rs.getString("customer_name"));
                    list.add(row);
                }
            }
            stats.put("overdueList", list);
 
        } catch (SQLException e) {
            e.printStackTrace();
            stats.putIfAbsent("overdueUrgent", 0);
            stats.putIfAbsent("overdueHigh",   0);
            stats.putIfAbsent("overdueMedium", 0);
            stats.putIfAbsent("overdueTotal",  0);
            stats.putIfAbsent("overdueList",   new ArrayList<>());
        }
        return stats;
    }
}