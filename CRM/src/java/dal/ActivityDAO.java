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
            try { if (rs != null) rs.close(); } catch (SQLException e) { }
            try { if (psPart != null) psPart.close(); } catch (SQLException e) { }
            try { if (psAct != null) psAct.close(); } catch (SQLException e) { }
            try { if (conn != null) conn.close(); } catch (SQLException e) { }
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
        }
    }

    public boolean updateActivity(Activity activity) {
        // Sửa câu SQL: Thêm logic cập nhật completed_at dựa trên status
        String sql = "UPDATE activities SET "
                + "title = ?, type = ?, description = ?, lead_id = ?, customer_id = ?, "
                + "opportunity_id = ?, due_date = ?, reminder_at = ?, priority = ?, "
                + "updated_at = GETDATE(), "
                + "status = ?, "
                + "completed_at = CASE "
                + "                 WHEN ? = 'Completed' THEN ISNULL(completed_at, GETDATE()) "
                + "                 ELSE NULL "
                + "               END "
                + "WHERE id = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, activity.getTitle());
            ps.setString(2, activity.getType());
            ps.setString(3, activity.getDescription());

            // Xử lý các trường có thể Null (Lead, Customer, Opportunity)
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
            ps.setString(9, activity.getPriority());

            // Tham số cho Status (dùng 2 lần: 1 cho cột status, 1 cho câu điều kiện CASE WHEN)
            ps.setString(10, activity.getStatus());
            ps.setString(11, activity.getStatus());

            ps.setInt(12, activity.getId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insertAttachment(int activityId, String fileName, String filePath) {
        String sql = "INSERT INTO activity_attachments (activity_id, file_name, file_path) VALUES (?, ?, ?)";
        // Mở connection riêng để tránh bị ảnh hưởng bởi connection đã đóng từ insertActivity()
        String url = "jdbc:sqlserver://localhost:1433;databaseName =CRM";
        try (Connection conn = DriverManager.getConnection(url, "sa", "123");
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            ps.setString(2, fileName);
            ps.setString(3, filePath);
            ps.executeUpdate();
            System.out.println("[insertAttachment] File saved: " + fileName + " for activityId=" + activityId);
        } catch (SQLException e) {
            System.err.println("[insertAttachment] FAILED to save attachment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Activity> getActivitiesForDashboard(Integer userId) {
        List<Activity> list = new ArrayList<>();

        // 1. JOIN 2 lần vào bảng users (1 cho người tạo, 1 cho người thực hiện)
        String sql = "SELECT DISTINCT a.id, a.title, a.type, a.description, a.due_date, a.priority, a.customer_id, a.created_at, a.created_by, "
                + "CASE "
                + "  WHEN a.status != 'Completed' AND a.due_date < GETDATE() THEN 'Overdue' "
                + "  ELSE a.status "
                + "END AS status, " // Ghi đè cột status bằng giá trị đã tính toán
                + "c.full_name AS customer_name, l.full_name AS lead_name, "
                + "u_creator.full_name AS creator_name, "
                + "u_owner.full_name AS assignee_name "
                + "FROM activities a "
                + "LEFT JOIN customers c ON a.customer_id = c.id "
                + "LEFT JOIN leads l ON a.lead_id = l.id "
                + "LEFT JOIN users u_creator ON a.created_by = u_creator.id "
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
                    String displayRole = "Owner".equals(role) ? "PIC" : role;
                    list.add(name + " (" + displayRole + ")"); // Ví dụ: "Chu Việt Hải (PIC)"
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ActivityParticipant> getParticipantsByActivityId(int activityId) {
        List<ActivityParticipant> list = new ArrayList<>();
        String sql = "SELECT id, activity_id, user_id, role "
                + "FROM activity_participants "
                + "WHERE activity_id = ? "
                + "ORDER BY CASE WHEN role = 'Owner' THEN 0 ELSE 1 END, id";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, activityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ActivityParticipant ap = new ActivityParticipant();
                    ap.setId(rs.getInt("id"));
                    ap.setActivityId(rs.getInt("activity_id"));
                    ap.setUserId(rs.getInt("user_id"));
                    ap.setRole(rs.getString("role"));
                    list.add(ap);
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

    public void deleteAttachmentsByIds(int activityId, List<Integer> attachmentIds) {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("DELETE FROM activity_attachments WHERE activity_id = ? AND id IN (");
        for (int i = 0; i < attachmentIds.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(")");

        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            ps.setInt(1, activityId);
            for (int i = 0; i < attachmentIds.size(); i++) {
                ps.setInt(i + 2, attachmentIds.get(i));
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    /**
     * Kiểm tra xem userId có thuộc danh sách người tham gia (Owner hoặc Participant)
     * của activityId không. Dùng để xác định quyền LIMITED.
     */
    public boolean isUserInvolvedInActivity(int activityId, int userId) {
        String sql = "SELECT COUNT(1) FROM activity_participants WHERE activity_id = ? AND user_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, activityId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật CHỈ trường Status của Activity.
     * Dùng cho người có quyền LIMITED (PIC / Participant - không phải Creator/Manager).
     */
    public boolean updateActivityStatusOnly(int activityId, String status) {
        String sql = "UPDATE activities SET "
                + "status = ?, "
                + "updated_at = GETDATE(), "
                + "completed_at = CASE "
                + "                 WHEN ? = 'Completed' THEN ISNULL(completed_at, GETDATE()) "
                + "                 ELSE NULL "
                + "               END "
                + "WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status); // dùng 2 lần cho câu CASE WHEN
            ps.setInt(3, activityId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 1. Hàm đếm tổng số bản ghi (Để tính xem có bao nhiêu trang)
    public int countActivities(Integer userId, String keyword, String type, String fromDate, String toDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT a.id) FROM activities a ");

        // Join các bảng để tìm kiếm tên
        sql.append("LEFT JOIN customers c ON a.customer_id = c.id ");
        sql.append("LEFT JOIN leads l ON a.lead_id = l.id ");
        sql.append("LEFT JOIN activity_participants ap_filter ON a.id = ap_filter.activity_id ");

        sql.append("WHERE 1=1 ");

        // A. Phân quyền (Nếu là Sale -> Chỉ đếm việc của mình)
        if (userId != null) {
            sql.append("AND (ap_filter.user_id = ").append(userId).append(" OR a.created_by = ").append(userId).append(") ");
        }

        // B. Lọc theo từ khóa
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (a.title LIKE ? OR c.full_name LIKE ? OR l.full_name LIKE ?) ");
        }
        // C. Lọc theo Type
        if (type != null && !type.equals("All") && !type.isEmpty()) {
            sql.append("AND a.type = ? ");
        }
        // D. Lọc ngày
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append("AND a.due_date >= ? ");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append("AND a.due_date <= ? ");
        }

        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            int index = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim() + "%";
                ps.setString(index++, searchPattern);
                ps.setString(index++, searchPattern);
                ps.setString(index++, searchPattern);
            }
            if (type != null && !type.equals("All") && !type.isEmpty()) {
                ps.setString(index++, type);
            }
            if (fromDate != null && !fromDate.isEmpty()) {
                ps.setString(index++, fromDate);
            }
            if (toDate != null && !toDate.isEmpty()) {
                ps.setString(index++, toDate + " 23:59:59");
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 2. Hàm lấy danh sách phân trang (Dùng OFFSET FETCH)
    public List<Activity> searchActivities(Integer userId, String keyword, String type, String fromDate, String toDate, int pageIndex, int pageSize) {
        List<Activity> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT a.id, a.title, a.type, a.description, a.due_date, a.priority, ");

        // THÊM LẠI LOGIC TỰ ĐỘNG TÍNH OVERDUE
        sql.append("CASE ");
        sql.append("  WHEN a.status != 'Completed' AND a.due_date < GETDATE() THEN 'Overdue' ");
        sql.append("  ELSE a.status ");
        sql.append("END AS status, "); // Cột này sẽ ghi đè status gốc

        sql.append("a.created_at, a.created_by, ");
        sql.append("c.full_name AS customer_name, l.full_name AS lead_name, ");
        sql.append("u_creator.full_name AS creator_name, u_owner.full_name AS assignee_name ");

        sql.append("FROM activities a ");
        sql.append("LEFT JOIN customers c ON a.customer_id = c.id ");
        sql.append("LEFT JOIN leads l ON a.lead_id = l.id ");
        sql.append("LEFT JOIN users u_creator ON a.created_by = u_creator.id ");

        // Join để lấy Owner (Người thực hiện)
        sql.append("LEFT JOIN activity_participants ap_owner ON a.id = ap_owner.activity_id AND ap_owner.role = 'Owner' ");
        sql.append("LEFT JOIN users u_owner ON ap_owner.user_id = u_owner.id ");

        // Join để lọc quyền xem
        sql.append("LEFT JOIN activity_participants ap_filter ON a.id = ap_filter.activity_id ");

        sql.append("WHERE 1=1 ");

        // --- CÁC ĐIỀU KIỆN LỌC (Copy y hệt hàm count) ---
        if (userId != null) {
            sql.append("AND (ap_filter.user_id = ").append(userId).append(" OR a.created_by = ").append(userId).append(") ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (a.title LIKE ? OR c.full_name LIKE ? OR l.full_name LIKE ?) ");
        }
        if (type != null && !type.equals("All") && !type.isEmpty()) {
            sql.append("AND a.type = ? ");
        }
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append("AND a.due_date >= ? ");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append("AND a.due_date <= ? ");
        }

        // --- PHÂN TRANG ---
        sql.append("ORDER BY a.created_at DESC ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            int index = 1;
            // Set tham số lọc
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim() + "%";
                ps.setString(index++, searchPattern);
                ps.setString(index++, searchPattern);
                ps.setString(index++, searchPattern);
            }
            if (type != null && !type.equals("All") && !type.isEmpty()) {
                ps.setString(index++, type);
            }
            if (fromDate != null && !fromDate.isEmpty()) {
                ps.setString(index++, fromDate);
            }
            if (toDate != null && !toDate.isEmpty()) {
                ps.setString(index++, toDate + " 23:59:59");
            }

            // Set tham số phân trang
            int offset = (pageIndex - 1) * pageSize;
            ps.setInt(index++, offset);
            ps.setInt(index++, pageSize);

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
                    act.setCreatedAt(rs.getTimestamp("created_at"));
                    act.setCreatedBy(rs.getInt("created_by"));
                    act.setCustomerName(rs.getString("customer_name"));
                    act.setLeadName(rs.getString("lead_name"));
                    act.setCreatorName(rs.getString("creator_name"));
                    act.setAssigneeName(rs.getString("assignee_name"));
                    list.add(act);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. Hàm lấy thống kê nhanh (Cập nhật: 5 chỉ số)
    public int[] getActivityStats(Integer userId) {
        // QUAN TRỌNG: Khai báo mảng có 5 phần tử
        // [0]=Total, [1]=Planned, [2]=InProgress, [3]=Completed, [4]=Overdue
        int[] stats = {0, 0, 0, 0, 0};

        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append("COUNT(*) AS total, ");

        // 1. Planned (Chỉ đếm những cái CÒN HẠN)
        sql.append("SUM(CASE WHEN status = 'Planned' AND due_date >= GETDATE() THEN 1 ELSE 0 END) AS planned, ");

        // 2. In Progress (Chỉ đếm những cái CÒN HẠN)
        sql.append("SUM(CASE WHEN status = 'In Progress' AND due_date >= GETDATE() THEN 1 ELSE 0 END) AS in_progress, ");

        // 3. Completed (Đã xong)
        sql.append("SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) AS completed, ");

        // 4. Overdue (Chưa xong & Đã QUÁ HẠN - Trừ Cancelled ra)
        sql.append("SUM(CASE WHEN status NOT IN ('Completed', 'Cancelled') AND due_date < GETDATE() THEN 1 ELSE 0 END) AS overdue ");

        sql.append("FROM activities a ");
        sql.append("LEFT JOIN activity_participants ap ON a.id = ap.activity_id ");
        sql.append("WHERE 1=1 ");

        if (userId != null) {
            sql.append("AND (ap.user_id = ? OR a.created_by = ?) ");
        }

        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            if (userId != null) {
                ps.setInt(1, userId);
                ps.setInt(2, userId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats[0] = rs.getInt("total");
                    stats[1] = rs.getInt("planned");
                    stats[2] = rs.getInt("in_progress");
                    stats[3] = rs.getInt("completed");
                    stats[4] = rs.getInt("overdue"); // Dòng này sẽ không lỗi nữa
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    public boolean isUserPIC(int activityId, int userId) {
        String sql = "SELECT COUNT(*) FROM activity_participants WHERE activity_id = ? AND user_id = ? AND role = 'Owner'";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, activityId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public void updateActivityParticipants(int activityId, List<Integer> participantIds) {
        String deleteSql = "DELETE FROM activity_participants WHERE activity_id = ?";
        String insertSql = "INSERT INTO activity_participants (activity_id, user_id, role) VALUES (?, ?, ?)";
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, activityId);
                psDelete.executeUpdate();
            }

            if (participantIds != null && !participantIds.isEmpty()) {
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    for (int i = 0; i < participantIds.size(); i++) {
                        psInsert.setInt(1, activityId);
                        psInsert.setInt(2, participantIds.get(i));
                        psInsert.setString(3, (i == 0) ? "Owner" : "Participant");
                        psInsert.addBatch();
                    }
                    psInsert.executeBatch();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Xóa Activity và các dữ liệu liên quan (Participants, Attachments)
    public boolean deleteActivity(int activityId) {
        String sqlParticipants = "DELETE FROM activity_participants WHERE activity_id = ?";
        String sqlAttachments = "DELETE FROM activity_attachments WHERE activity_id = ?";
        String sqlActivity = "DELETE FROM activities WHERE id = ?";

        Connection conn = null;
        PreparedStatement psPart = null;
        PreparedStatement psAtt = null;
        PreparedStatement psAct = null;
        boolean success = false;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Xóa người tham gia
            psPart = conn.prepareStatement(sqlParticipants);
            psPart.setInt(1, activityId);
            psPart.executeUpdate();

            // 2. Xóa đính kèm
            psAtt = conn.prepareStatement(sqlAttachments);
            psAtt.setInt(1, activityId);
            psAtt.executeUpdate();

            // 3. Xóa Activity chính
            psAct = conn.prepareStatement(sqlActivity);
            psAct.setInt(1, activityId);
            int rowsDeleted = psAct.executeUpdate();

            if (rowsDeleted > 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa Activity: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Lỗi rollback: " + ex.getMessage());
            }
        } finally {
            try {
                if (psPart != null) psPart.close();
                if (psAtt != null) psAtt.close();
                if (psAct != null) psAct.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }
}
