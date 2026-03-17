package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Role;

public class RoleDAO extends DBContext {

    public List<Role> getAllDepartments() {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT id, name FROM roles";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {

            while (rs.next()) {
                Role d = new Role();
                d.setId(rs.getInt("id"));
                d.setName(rs.getString("name"));

                list.add(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public String getRoleNameById(int roleId) {
        String sql = "SELECT name FROM roles WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
