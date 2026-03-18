package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Customer;
import model.Order;

public class OrderDAO extends DBContext {

    public List<Order> getOrderByCustomerId(int id) {
        List<Order> ls = new ArrayList<>();
        String sql = "SELECT id, quotation_id, order_number, total_amount "
                + ", status, created_at FROM orders WHERE customer_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Order d = new Order();

                d.setId(rs.getInt("id"));
                d.setQuotationId(rs.getInt("quotation_id"));
                d.setOrderNumber(rs.getString("order_number"));
                d.setTotalAmount(rs.getBigDecimal("total_amount"));
                d.setStatus(rs.getString("status"));
                d.setCreatedAt(rs.getTimestamp("created_at"));

                ls.add(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ls;
    }
}
