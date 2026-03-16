package dal;

import model.CustomerFeedback;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO cho bảng customer_feedback
 * Đặt trong package dal, extends DBContext
 *
 * Bảng liên quan:
 *   customer_feedback (id, customer_id, rating, comments, created_at)
 *   customers         (id, full_name, ...)
 */
public class CustomerFeedbackDAO extends DBContext {

    // =========================================================
    // 1. GET ALL — toàn bộ feedback, JOIN với customers
    // =========================================================
    public List<CustomerFeedback> getAllFeedbacks() {
        List<CustomerFeedback> list = new ArrayList<>();
        String sql =
            "SELECT cf.id, cf.customer_id, cf.rating, cf.comments, cf.created_at, " +
            "       c.full_name AS customer_name " +
            "FROM customer_feedback cf " +
            "LEFT JOIN customers c ON c.id = cf.customer_id " +
            "ORDER BY cf.created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================
    // 2. GET BY RATING — lọc theo mức rating
    //    ratingFilter = 0 → trả về tất cả
    //    ratingFilter = 1..5 → lọc theo mức đó
    // =========================================================
    public List<CustomerFeedback> getFeedbacksByRating(int ratingFilter) {
        if (ratingFilter <= 0 || ratingFilter > 5) {
            return getAllFeedbacks();
        }

        List<CustomerFeedback> list = new ArrayList<>();
        String sql =
            "SELECT cf.id, cf.customer_id, cf.rating, cf.comments, cf.created_at, " +
            "       c.full_name AS customer_name " +
            "FROM customer_feedback cf " +
            "LEFT JOIN customers c ON c.id = cf.customer_id " +
            "WHERE cf.rating = ? " +
            "ORDER BY cf.created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ratingFilter);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================
    // 3. GET STATS — thống kê tổng hợp cho dashboard & Excel
    //    Keys trả về:
    //      "total"          → tổng số feedback (int)
    //      "avgRating"      → trung bình rating (double)
    //      "count1"~"count5"→ số lượng từng mức (int)
    //      "pct1"~"pct5"    → % từng mức        (double)
    // =========================================================
    public Map<String, Object> getFeedbackStats() {
        Map<String, Object> stats = new HashMap<>();

        String sqlSummary =
            "SELECT COUNT(1) AS total, " +
            "       AVG(CAST(rating AS FLOAT)) AS avg_rating " +
            "FROM customer_feedback";

        String sqlDist =
            "SELECT rating, COUNT(1) AS cnt " +
            "FROM customer_feedback " +
            "GROUP BY rating " +
            "ORDER BY rating";

        try {
            // Tổng số và trung bình
            try (PreparedStatement ps = connection.prepareStatement(sqlSummary);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int    total     = rs.getInt("total");
                    double avgRating = rs.getDouble("avg_rating");
                    stats.put("total",     total);
                    stats.put("avgRating", Math.round(avgRating * 10.0) / 10.0);
                }
            }

            // Phân bổ từng mức
            int total = (Integer) stats.getOrDefault("total", 0);
            int[] counts = new int[6]; // index 1..5, bỏ index 0

            try (PreparedStatement ps = connection.prepareStatement(sqlDist);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int r = rs.getInt("rating");
                    if (r >= 1 && r <= 5) counts[r] = rs.getInt("cnt");
                }
            }

            for (int i = 1; i <= 5; i++) {
                stats.put("count" + i, counts[i]);
                double pct = (total > 0)
                        ? Math.round(counts[i] * 1000.0 / total) / 10.0
                        : 0.0;
                stats.put("pct" + i, pct);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Giá trị mặc định để JSP không bị NPE
            stats.putIfAbsent("total",     0);
            stats.putIfAbsent("avgRating", 0.0);
            for (int i = 1; i <= 5; i++) {
                stats.putIfAbsent("count" + i, 0);
                stats.putIfAbsent("pct"   + i, 0.0);
            }
        }
        return stats;
    }

    // =========================================================
    // Private helper: ResultSet → CustomerFeedback
    // =========================================================
    private CustomerFeedback mapRow(ResultSet rs) throws SQLException {
        CustomerFeedback fb = new CustomerFeedback();
        fb.setId(rs.getInt("id"));
        fb.setCustomerId(rs.getInt("customer_id"));
        fb.setRating(rs.getInt("rating"));
        fb.setComments(rs.getString("comments"));
        fb.setCreatedAt(rs.getString("created_at"));
        fb.setCustomerName(rs.getString("customer_name"));
        return fb;
    }
}