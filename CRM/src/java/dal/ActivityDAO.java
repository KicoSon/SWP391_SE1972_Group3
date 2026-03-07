package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.activity.Activity;
import model.activity.ActivityAttachment;
import model.activity.ActivityParticipant;

public class ActivityDAO extends DBContext {

    // 1. Thêm mới Activity (Sử dụng Transaction chuẩn DB)
    public int insertActivity(Activity activity, List<Integer> participantIds) {
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
            return activityId;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return -1;
        } finally {
            // Đóng resources thủ công để tránh leak
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (psPart != null) {
                    psPart.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (psAct != null) {
                    psAct.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public List<Activity> getActivitiesByOpportunityId(int opportunityId) {
        List<Activity> list = new ArrayList<>();
        String sql = "SELECT * FROM activities WHERE opportunity_id = ? ORDER BY created_at DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, opportunityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Activity act = new Activity();
                act.setId(rs.getInt("id"));
                act.setTitle(rs.getString("title"));
                act.setType(rs.getString("type"));
                act.setDescription(rs.getString("description"));
                act.setDueDate(rs.getTimestamp("due_date"));
                act.setStatus(rs.getString("status"));
                act.setPriority(rs.getString("priority"));
                act.setCreatedBy(rs.getInt("created_by"));
                act.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(act);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertActivity(Activity activity) {
        return insertActivity2(activity, new ArrayList<>());
    }

    public boolean insertActivity2(Activity activity, List<Integer> participantIds) {
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
            if (activity.getLeadId() != null) {
                psAct.setLong(4, activity.getLeadId());
            } else {
                psAct.setNull(4, Types.BIGINT);
            }
            if (activity.getCustomerId() != null) {
                psAct.setInt(5, activity.getCustomerId());
            } else {
                psAct.setNull(5, Types.INTEGER);
            }
            if (activity.getOpportunityId() != null) {
                psAct.setInt(6, activity.getOpportunityId());
            } else {
                psAct.setNull(6, Types.INTEGER);
            }
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
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return false;
    }

    public int insertActivity3(Activity activity, List<Integer> participantIds) {
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
            return activityId;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return -1;
        } finally {
            // Đóng resources thủ công để tránh leak
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (psPart != null) {
                    psPart.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (psAct != null) {
                    psAct.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public boolean updateActivity(Activity activity) {
        String sql = "UPDATE activities SET "
                + "title = ?, type = ?, description = ?, lead_id = ?, customer_id = ?, "
                + "opportunity_id = ?, due_date = ?, reminder_at = ?, status = ?, priority = ?, "
                + "updated_at = GETDATE() "
                + "WHERE id = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, activity.getTitle());
            ps.setString(2, activity.getType());
            ps.setString(3, activity.getDescription());

            if (activity.getLeadId() != null) {
                ps.setLong(4, activity.getLeadId());
            } else {
                ps.setNull(4, Types.BIGINT);
            }

            if (activity.getCustomerId() != null) {
                ps.setInt(5, activity.getCustomerId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            if (activity.getOpportunityId() != null) {
                ps.setInt(6, activity.getOpportunityId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setTimestamp(7, activity.getDueDate());
            ps.setTimestamp(8, activity.getReminderAt());
            ps.setString(9, activity.getStatus());
            ps.setString(10, activity.getPriority());
            ps.setInt(11, activity.getId());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insertAttachment(int activityId, String fileName, String filePath) {
        String sql = "INSERT INTO activity_attachments (activity_id, file_name, file_path) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, activityId);
            ps.setString(2, fileName);
            ps.setString(3, filePath);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Activity> getActivitiesForDashboard(Integer userId) {
        List<Activity> list = new ArrayList<>();

        // 1. JOIN 2 lần vào bảng users (1 cho người tạo, 1 cho người thực hiện)
        String sql = "SELECT DISTINCT a.id, a.title, a.type,a.description, a.due_date, a.status, a.priority, a.customer_id, a.created_at, a.created_by, "
                + "c.full_name AS customer_name, l.full_name AS lead_name, "
                + "u_creator.full_name AS creator_name, " // Thay full_name thành tên cột của bạn nếu cần
                + "u_owner.full_name AS assignee_name " // Thay full_name thành tên cột của bạn nếu cần
                + "FROM activities a "
                + "LEFT JOIN customers c ON a.customer_id = c.id "
                + "LEFT JOIN leads l ON a.lead_id = l.id "
                + "LEFT JOIN users u_creator ON a.created_by = u_creator.id "
                // Chỉ lấy tên của người đóng vai trò là 'Owner' (Người được giao chính)
                + "LEFT JOIN activity_participants ap_owner ON a.id = ap_owner.activity_id AND ap_owner.role = 'Owner' "
                + "LEFT JOIN users u_owner ON ap_owner.user_id = u_owner.id ";

        // 2. LOGIC LỌC DỮ LIỆU (Nếu là Sale thì chỉ thấy việc của mình)
        // 2. LOGIC LỌC DỮ LIỆU CHUẨN CRM (Thấy việc của mình HOẶC việc do mình tạo ra)
        if (userId != null) {
            // Đổi JOIN thành LEFT JOIN kết hợp điều kiện ở WHERE
            sql += "LEFT JOIN activity_participants ap_filter ON a.id = ap_filter.activity_id "
                    + "WHERE (ap_filter.user_id = ? OR a.created_by = ?) ";
        }

        sql += "ORDER BY a.created_at DESC";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            if (userId != null) {
                ps.setInt(1, userId); // Dấu ? thứ nhất (ap_filter.user_id)
                ps.setInt(2, userId); // Dấu ? thứ hai (a.created_by)
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Activity act = new Activity();
                    act.setId(rs.getInt("id"));
                    act.setTitle(rs.getString("title"));
                    act.setType(rs.getString("type"));
                    act.setDescription(rs.getString("description"));
                    act.setDueDate(rs.getTimestamp("due_date"));
                    act.setStatus(rs.getString("status"));
                    act.setPriority(rs.getString("priority"));
                    act.setCustomerId(rs.getObject("customer_id") != null ? rs.getInt("customer_id") : null);

                    // FIXED: Lấy thời gian tạo và ID người tạo
                    act.setCreatedAt(rs.getTimestamp("created_at"));
                    act.setCreatedBy(rs.getInt("created_by"));

                    // FIXED: Lấy Tên người tạo và Tên người được giao
                    act.setCreatorName(rs.getString("creator_name"));
                    act.setAssigneeName(rs.getString("assignee_name"));

                    String customerName = rs.getString("customer_name");
                    String leadName = rs.getString("lead_name");

                    if (customerName != null) {
                        act.setCustomerName(customerName);
                    } else if (leadName != null) {
                        act.setLeadName(leadName);
                    }

                    list.add(act);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Activity getActivityById(int id) {
        String sql = "SELECT a.*, c.full_name AS customer_name, l.full_name AS lead_name, "
                + "o.title AS opportunity_title, u.full_name AS creator_name "
                + "FROM activities a "
                + "LEFT JOIN customers c ON a.customer_id = c.id "
                + "LEFT JOIN leads l ON a.lead_id = l.id "
                + "LEFT JOIN opportunities o ON a.opportunity_id = o.id " // Móc bảng Cơ hội
                + "LEFT JOIN users u ON a.created_by = u.id "
                + "WHERE a.id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Activity act = new Activity();
                    // Cột cơ bản
                    act.setId(rs.getInt("id"));
                    act.setTitle(rs.getString("title"));
                    act.setType(rs.getString("type"));
                    act.setDescription(rs.getString("description"));
                    act.setStatus(rs.getString("status"));
                    act.setPriority(rs.getString("priority"));
                    act.setOutcomeNotes(rs.getString("outcome_notes"));

                    // Xử lý an toàn cho ID khóa ngoại
                    if (rs.getObject("lead_id") != null) {
                        act.setLeadId(rs.getLong("lead_id"));
                    }
                    if (rs.getObject("customer_id") != null) {
                        act.setCustomerId(rs.getInt("customer_id"));
                    }
                    if (rs.getObject("opportunity_id") != null) {
                        act.setOpportunityId(rs.getInt("opportunity_id"));
                    }

                    // Nhóm Thời gian (Time & Dates)
                    act.setDueDate(rs.getTimestamp("due_date"));
                    act.setReminderAt(rs.getTimestamp("reminder_at"));
                    act.setCompletedAt(rs.getTimestamp("completed_at"));
                    act.setCreatedAt(rs.getTimestamp("created_at"));
                    act.setUpdatedAt(rs.getTimestamp("updated_at"));
                    act.setCreatedBy(rs.getInt("created_by"));

                    // Nhóm Tên hiển thị (Thuộc tính ảo)
                    act.setCustomerName(rs.getString("customer_name"));
                    act.setLeadName(rs.getString("lead_name"));
                    act.setOpportunityTitle(rs.getString("opportunity_title"));
                    act.setCreatorName(rs.getString("creator_name"));

                    return act;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. Lấy danh sách Người tham gia (Kèm theo Tên thật của họ)
    // Lưu ý: Tôi dùng DTO ảo bằng String mảng/hoặc ghép chuỗi cho tiện hiển thị lên giao diện
    public List<String> getParticipantsFullInfo(int activityId) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT u.full_name, ap.role "
                + "FROM activity_participants ap "
                + "JOIN users u ON ap.user_id = u.id "
                + "WHERE ap.activity_id = ? "
                + "ORDER BY ap.role DESC"; // Owner lên trước
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, activityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("full_name");
                    String role = rs.getString("role");
                    list.add(name + " (" + role + ")"); // Ví dụ: "Chu Việt Hải (Owner)"
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ActivityAttachment> getAttachmentsByActivityId(int activityId) {
        List<ActivityAttachment> list = new ArrayList<>();
        String sql = "SELECT * FROM activity_attachments WHERE activity_id = ? ORDER BY uploaded_at DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, activityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ActivityAttachment att = new ActivityAttachment();
                    att.setId(rs.getInt("id"));
                    att.setActivityId(rs.getInt("activity_id"));
                    att.setFileName(rs.getString("file_name"));
                    att.setFilePath(rs.getString("file_path"));
                    att.setUploadedAt(rs.getTimestamp("uploaded_at"));
                    list.add(att);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- PHẦN COMMENT (MỚI THÊM) ---
    // 1. Lấy danh sách comment của 1 Activity
    public List<model.activity.ActivityComment> getCommentsByActivityId(int activityId) {
        List<model.activity.ActivityComment> list = new ArrayList<>();
        // JOIN bảng comment với bảng users để lấy tên đầy đủ
        String sql = "SELECT c.*, u.full_name "
                + "FROM activity_comments c "
                + "JOIN users u ON c.user_id = u.id "
                + "WHERE c.activity_id = ? "
                + "ORDER BY c.created_at DESC";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, activityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.activity.ActivityComment ac = new model.activity.ActivityComment();
                    ac.setId(rs.getInt("id"));
                    ac.setActivityId(rs.getInt("activity_id"));
                    ac.setUserId(rs.getInt("user_id"));
                    ac.setContent(rs.getString("content"));
                    ac.setCreatedAt(rs.getTimestamp("created_at"));
                    ac.setCommenterName(rs.getString("full_name"));
                    list.add(ac);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Thêm comment mới
    public void insertComment(int activityId, int userId, String content) {
        // Cột created_at đã có DEFAULT GETDATE() trong SQL nên không cần insert
        String sql = "INSERT INTO activity_comments (activity_id, user_id, content) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, activityId);
            ps.setInt(2, userId);
            ps.setString(3, content);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updateActivityStatus(int id, String status, String outcomeNotes) {
        String sql = "UPDATE activities SET status = ?, outcome_notes = ?, completed_at = GETDATE(), updated_at = GETDATE() WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, outcomeNotes);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
