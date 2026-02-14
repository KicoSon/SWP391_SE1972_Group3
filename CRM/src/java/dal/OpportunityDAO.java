package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.activity.Opportunity;

public class OpportunityDAO extends DBContext {

    // Lấy danh sách Cơ hội
    public List<Opportunity> getAllOpportunities() {
        List<Opportunity> list = new ArrayList<>();
        // 1. Phải SELECT thêm customer_id
        String sql = "SELECT id, title, customer_id FROM opportunities";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Opportunity o = new Opportunity();
                o.setId(rs.getInt("id"));
                o.setTitle(rs.getString("title"));

                // 2. QUAN TRỌNG: Phải set giá trị này!
                o.setCustomerId(rs.getInt("customer_id"));

                list.add(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Bạn làm tương tự hàm getAllLeads() cho bảng leads nhé!
}
