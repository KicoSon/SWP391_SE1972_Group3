package dal;

import model.Staff;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DBContext {

    // Lấy tất cả user
    public List<User> getAllUsers() {

        List<User> list = new ArrayList<>();

        String sql = """
            SELECT 
                id,
                username,
                password_hash,
                full_name,
                email,
                role_id,
                department,
                is_active,
                created_at
            FROM users
        """;

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                User u = new User();

                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setRoleId(rs.getInt("role_id"));
                u.setDepartment(rs.getString("department"));
                u.setActive(rs.getBoolean("is_active"));

                if (rs.getTimestamp("created_at") != null) {
                    u.setCreatedAt(
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }

                list.add(u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<Staff> getSalesList() {

        List<Staff> list = new ArrayList<>();

        String sql =
                "SELECT u.id, u.full_name, u.email " +
                "FROM users u " +
                "JOIN roles r ON u.role_id = r.id " +
                "WHERE r.name = 'sales'";

        try {

            PreparedStatement ps =
                    connection.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Staff s = new Staff();

                s.setId(rs.getInt("id"));

                s.setFullName(rs.getString("full_name"));

                s.setEmail(rs.getString("email"));

                list.add(s);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return list;

    }

    public static void main(String[] args) {

        UserDAO dao = new UserDAO();

        List<User> list = dao.getAllUsers();

        for (User u : list) {
            System.out.println(u.getId() + " | " + u.getFullName());
        }
    }
}
