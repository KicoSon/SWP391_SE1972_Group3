package dal;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO extends DBContext {

    public List<Customer> filterCustomers(String search, String statusFilter) {

    List<Customer> list = new ArrayList<>();

    StringBuilder sql = new StringBuilder(
        "SELECT id, full_name, email, phone, profile_pic_url, created_at, status, tier_id " +
        "FROM customers WHERE 1=1 "
    );

    if (search != null && !search.trim().isEmpty()) {
        sql.append(" AND (full_name LIKE ? OR email LIKE ?) ");
    }

    if (statusFilter != null && !statusFilter.trim().isEmpty()) {
        sql.append(" AND status = ? ");
    }

    sql.append(" ORDER BY id DESC ");

    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

        int index = 1;

        if (search != null && !search.trim().isEmpty()) {
            ps.setString(index++, "%" + search + "%");
            ps.setString(index++, "%" + search + "%");
        }

        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            ps.setString(index++, statusFilter);
        }

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            Customer c = new Customer();

            c.setId(rs.getInt("id"));
            c.setFullName(rs.getString("full_name"));
            c.setEmail(rs.getString("email"));
            c.setPhone(rs.getString("phone"));
            c.setProfileURL(rs.getString("profile_pic_url"));
            c.setCreateAt(rs.getString("created_at"));
            c.setStatus(rs.getString("status"));
            c.setTier(rs.getInt("tier_id"));

            list.add(c);
        }

        System.out.println("Loaded customers: " + list.size());

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
}
