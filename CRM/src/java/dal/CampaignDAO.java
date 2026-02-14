package dal;

import model.Campaign;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CampaignDAO extends DBContext {

    // =========================
    // GET ALL CAMPAIGNS
    // =========================
    public List<Campaign> filterCampaigns(String search, String status) {

    List<Campaign> list = new ArrayList<>();

    StringBuilder sql = new StringBuilder(
        "SELECT * FROM campaigns WHERE 1=1"
    );

    if (search != null && !search.trim().isEmpty()) {
        sql.append(" AND name LIKE ?");
    }

    if (status != null && !status.trim().isEmpty()) {
        sql.append(" AND status = ?");
    }

    sql.append(" ORDER BY created_at DESC");

    try {

        PreparedStatement ps = connection.prepareStatement(sql.toString());

        int index = 1;

        if (search != null && !search.trim().isEmpty()) {
            ps.setString(index++, "%" + search + "%");
        }

        if (status != null && !status.trim().isEmpty()) {
            ps.setString(index++, status);
        }

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            Campaign c = new Campaign();

            c.setId(rs.getLong("id"));
            c.setName(rs.getString("name"));
            c.setDescription(rs.getString("description"));
            c.setBannerUrl(rs.getString("banner_url"));
            c.setStartDate(rs.getDate("start_date"));
            c.setEndDate(rs.getDate("end_date"));
            c.setStatus(rs.getString("status"));
            c.setCreatedBy(rs.getLong("created_by"));
            c.setCreatedAt(rs.getTimestamp("created_at"));
            c.setUpdatedAt(rs.getTimestamp("updated_at"));

            list.add(c);

        }

    } catch (Exception e) {

        e.printStackTrace();

    }

    return list;

}



}
