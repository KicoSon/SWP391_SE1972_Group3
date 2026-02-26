package dal;

import model.Staff;
import java.sql.*;
import java.util.*;

public class UserDao extends DBContext {

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

}