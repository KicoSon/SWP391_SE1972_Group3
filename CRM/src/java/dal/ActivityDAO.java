package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.activity.Activity;
import model.activity.ActivityParticipant;

public class ActivityDAO extends DBContext {

    // 1. Thêm mới Activity (Sử dụng Transaction)
    public boolean insertActivity(Activity activity, List<Integer> participantIds) {
        String sqlActivity = "INSERT INTO activities (title, type, description, lead_id, customer_id, opportunity_id, due_date, reminder_at, status, priority, created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlParticipant = "INSERT INTO activity_participants (activity_id, user_id, role) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // Chèn vào bảng activities
            PreparedStatement psAct = conn.prepareStatement(sqlActivity, Statement.RETURN_GENERATED_KEYS);
            psAct.setString(1, activity.getTitle());
            psAct.setString(2, activity.getType());
            psAct.setString(3, activity.getDescription());
            if (activity.getLeadId() != null) psAct.setLong(4, activity.getLeadId()); else psAct.setNull(4, Types.BIGINT);
            if (activity.getCustomerId() != null) psAct.setInt(5, activity.getCustomerId()); else psAct.setNull(5, Types.INTEGER);
            if (activity.getOpportunityId() != null) psAct.setInt(6, activity.getOpportunityId()); else psAct.setNull(6, Types.INTEGER);
            psAct.setTimestamp(7, activity.getDueDate());
            psAct.setTimestamp(8, activity.getReminderAt());
            psAct.setString(9, activity.getStatus());
            psAct.setString(10, activity.getPriority());
            psAct.setInt(11, activity.getCreatedBy());
            
            psAct.executeUpdate();

            // Lấy ID vừa tạo để chèn vào bảng Participants
            ResultSet rs = psAct.getGeneratedKeys();
            int activityId = 0;
            if (rs.next()) {
                activityId = rs.getInt(1);
            }

            // Chèn danh sách người tham gia
            PreparedStatement psPart = conn.prepareStatement(sqlParticipant);
            for (Integer userId : participantIds) {
                psPart.setInt(1, activityId);
                psPart.setInt(2, userId);
                // Giả định người đầu tiên trong list là Owner, còn lại là Participant
                psPart.setString(3, (userId.equals(participantIds.get(0))) ? "Owner" : "Participant");
                psPart.addBatch();
            }
            psPart.executeBatch();

            conn.commit(); // Hoàn tất Transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        }
        return false;
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
