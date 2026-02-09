package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.activity.Activity;
import model.activity.ActivityParticipant;

public class ActivityDAO extends DBContext {

    // 1. Thêm mới Activity (Sử dụng Transaction chuẩn DB)
    public boolean insertActivity(Activity activity, List<Integer> participantIds) {
        String sqlActivity = "INSERT INTO activities "
                + "(title, type, description, lead_id, customer_id, opportunity_id, due_date, reminder_at, status, priority, created_by, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
        
        // Role trong DB có ràng buộc CHECK ('Owner', 'Participant')
        String sqlParticipant = "INSERT INTO activity_participants (activity_id, user_id, role) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement psAct = null;
        PreparedStatement psPart = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // --- BƯỚC 1: Insert bảng ACTIVITIES ---
            psAct = conn.prepareStatement(sqlActivity, Statement.RETURN_GENERATED_KEYS);
            
            psAct.setString(1, activity.getTitle());
            psAct.setString(2, activity.getType()); // Phải khớp: 'Call', 'Email',...
            psAct.setString(3, activity.getDescription());

            // Xử lý Null cho Lead (BIGINT)
            if (activity.getLeadId() != null) {
                psAct.setLong(4, activity.getLeadId());
            } else {
                psAct.setNull(4, Types.BIGINT); 
            }

            // Xử lý Null cho Customer (INT)
            if (activity.getCustomerId() != null) {
                psAct.setInt(5, activity.getCustomerId());
            } else {
                psAct.setNull(5, Types.INTEGER);
            }

            // Xử lý Null cho Opportunity (INT)
            if (activity.getOpportunityId() != null) {
                psAct.setInt(6, activity.getOpportunityId());
            } else {
                psAct.setNull(6, Types.INTEGER);
            }

            psAct.setTimestamp(7, activity.getDueDate());
            psAct.setTimestamp(8, activity.getReminderAt());
            
            // Default DB là 'Planned' và 'Medium', nhưng nên set cứng từ code để chắc chắn
            psAct.setString(9, activity.getStatus() != null ? activity.getStatus() : "Planned");
            psAct.setString(10, activity.getPriority() != null ? activity.getPriority() : "Medium");
            psAct.setInt(11, activity.getCreatedBy());

            int affectedRows = psAct.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating activity failed, no rows affected.");
            }

            // Lấy ID vừa tạo (Identity)
            int activityId = 0;
            rs = psAct.getGeneratedKeys();
            if (rs.next()) {
                activityId = rs.getInt(1);
            } else {
                throw new SQLException("Creating activity failed, no ID obtained.");
            }

            // --- BƯỚC 2: Insert bảng PARTICIPANTS ---
            if (participantIds != null && !participantIds.isEmpty()) {
                psPart = conn.prepareStatement(sqlParticipant);
                
                // Quy ước: Người đầu tiên trong list là Owner, còn lại là Participant
                for (int i = 0; i < participantIds.size(); i++) {
                    Integer userId = participantIds.get(i);
                    psPart.setInt(1, activityId);
                    psPart.setInt(2, userId);
                    
                    // Logic Role: Check phần tử đầu tiên
                    String role = (i == 0) ? "Owner" : "Participant";
                    psPart.setString(3, role);
                    
                    psPart.addBatch();
                }
                psPart.executeBatch();
            }

            conn.commit(); // Hoàn tất Transaction
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Đóng resources thủ công để tránh leak
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (psPart != null) psPart.close(); } catch (SQLException e) {}
            try { if (psAct != null) psAct.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }


    // 2. Lấy danh sách hoạt động của một nhân viên cụ thể
    public List<Activity> getActivitiesByUserId(int userId) {
        List<Activity> list = new ArrayList<>();
        String sql = "SELECT a.* FROM activities a "
                   + "JOIN activity_participants ap ON a.id = ap.activity_id "
                   + "WHERE ap.user_id = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Activity act = new Activity();
                act.setId(rs.getInt("id"));
                act.setTitle(rs.getString("title"));
                act.setType(rs.getString("type"));
                act.setDueDate(rs.getTimestamp("due_date"));
                act.setStatus(rs.getString("status"));
                act.setPriority(rs.getString("priority"));
                list.add(act);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
