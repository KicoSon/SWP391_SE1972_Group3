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

        try (PreparedStatement ps = getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
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
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Staff> getAllSales() {

        List<Staff> list = new ArrayList<>();

        String sql = """
                 SELECT id, role_id, email, password_hash, full_name,
                        department, created_at, is_active
                 FROM users
                 WHERE role_id = 2 AND is_active = 1
                 """;

        try (
                PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Staff s = new Staff();

                s.setId(rs.getInt("id"));
                s.setRoleId(rs.getInt("role_id"));
                s.setEmail(rs.getString("email"));
                s.setPassword(rs.getString("password_hash"));
                s.setFullName(rs.getString("full_name"));
                s.setDepartment(rs.getString("department"));
                s.setCreatedAt(rs.getString("created_at"));
                s.setActive(rs.getBoolean("is_active"));

                list.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void updateStatus(int id, boolean active) {
        String sql = "UPDATE users SET is_active = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Staff> getAllStaff() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT id, full_name, email, role_id, department, is_active FROM users";

        try (PreparedStatement ps = getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Staff s = new Staff();
                s.setId(rs.getInt("id"));
                s.setFullName(rs.getString("full_name"));
                s.setEmail(rs.getString("email"));
                s.setRoleId(rs.getInt("role_id"));
                s.setDepartment(rs.getString("department"));
                s.setActive(rs.getInt("is_active") == 1 ? true : false);

                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isEmailExistExceptId(String email, int id) {

        String sql = "SELECT 1 FROM users WHERE email = ? AND id <> ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setInt(2, id);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateWithPassword(Staff s) {
        String sql = "UPDATE users SET full_name=?, email=?, password_hash=?, role_id=?, department=?, is_active=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            RoleDAO roleDao = new RoleDAO();
            String department = roleDao.getRoleNameById(s.getRoleId());

            ps.setString(1, s.getFullName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPassword());
            ps.setInt(4, s.getRoleId());
            ps.setString(5, department);
            ps.setBoolean(6, s.isActive());
            ps.setInt(7, s.getId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateWithoutPassword(Staff s) {
        String sql = "UPDATE users SET full_name=?, email=?, role_id=?, department=?, is_active=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            RoleDAO roleDao = new RoleDAO();
            String department = roleDao.getRoleNameById(s.getRoleId());

            ps.setString(1, s.getFullName());
            ps.setString(2, s.getEmail());
            ps.setInt(3, s.getRoleId());
            ps.setString(4, department); // 🔥 thêm
            ps.setBoolean(5, s.isActive());
            ps.setInt(6, s.getId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEmailExist(String email) {

        String sql = "SELECT 1 FROM users WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertStaff(Staff s) {
        String sql = "INSERT INTO users (username, password_hash, full_name, email, role_id, department, is_active, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String email = s.getEmail();
            String username = "";

            if (email != null && email.contains("@")) {
                username = email.substring(0, email.indexOf("@"));
            }
            RoleDAO rdao = new RoleDAO();
            String department = rdao.getRoleNameById(s.getRoleId());
            ps.setString(1, username);
            ps.setString(2, s.getPassword());
            ps.setString(3, s.getFullName());
            ps.setString(4, s.getEmail());
            ps.setInt(5, s.getRoleId());
            ps.setString(6, department);
            ps.setBoolean(7, s.isActive());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
