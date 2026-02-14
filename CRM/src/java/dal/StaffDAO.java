package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Staff;

public class StaffDAO extends DBContext {

    // Lấy tất cả nhân viên đang hoạt động (Active)
    public List<Staff> getAllActiveStaff() {
        List<Staff> list = new ArrayList<>();
        // Query lấy các cột cần thiết từ bảng users
        // Giả sử bảng tên là [users] như trong script SQL bạn gửi
        String sql = "SELECT id, full_name, email, role_id, department FROM users WHERE is_active = 1";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Staff s = new Staff();
                // Map dữ liệu từ SQL vào Object Staff
                s.setId(rs.getInt("id"));
                s.setFullName(rs.getString("full_name"));
                s.setEmail(rs.getString("email"));
                s.setRoleId(rs.getInt("role_id"));
                s.setDepartment(rs.getString("department"));
                s.setActive(true);
                
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // (Tùy chọn) Hàm lấy Staff theo ID - dùng khi cần hiển thị chi tiết
    public Staff getStaffById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Staff s = new Staff();
                s.setId(rs.getInt("id"));
                s.setFullName(rs.getString("full_name"));
                s.setEmail(rs.getString("email"));
                s.setRoleId(rs.getInt("role_id"));
                s.setDepartment(rs.getString("department"));
                s.setActive(rs.getBoolean("is_active"));
                return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}