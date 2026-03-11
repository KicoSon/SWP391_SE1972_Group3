package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmailDAO extends DBContext {
    public boolean insertEmailLog(int fromUserId, int toCustomerId, String toEmail, String subject, String content, String status) {
        String sql = "INSERT INTO emails (subject, content, from_user_id, to_customer_id, to_email, status, sent_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, subject);
            ps.setString(2, content);
            ps.setInt(3, fromUserId);
            
            if (toCustomerId > 0) {
                ps.setInt(4, toCustomerId);
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            
            ps.setString(5, toEmail);
            ps.setString(6, status);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}