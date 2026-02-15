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

    public boolean insertCampaign(Campaign c) {

        String sql = "INSERT INTO campaigns "
                + "(name, description, banner_url, start_date, end_date, status, created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, c.getName());

            ps.setString(2, c.getDescription());

            ps.setString(3, c.getBannerUrl());

            ps.setDate(4, new java.sql.Date(c.getStartDate().getTime()));

            ps.setDate(5, new java.sql.Date(c.getEndDate().getTime()));

            ps.setString(6, c.getStatus());

            ps.setLong(7, c.getCreatedBy());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return false;

    }

    public boolean isCampaignNameExist(String name) {

        String sql = "SELECT COUNT(*) FROM campaigns WHERE name = ?";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return rs.getInt(1) > 0;

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return false;
    }

    public Campaign getCampaignById(long id) {

        Campaign c = null;

        String sql = "SELECT * FROM campaigns WHERE id = ?";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                c = new Campaign();

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

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return c;
    }

    public void updateCampaign(Campaign c) {

        String sql
                = "UPDATE campaigns SET "
                + "name = ?, "
                + "description = ?, "
                + "banner_url = ?, "
                + "start_date = ?, "
                + "end_date = ?, "
                + "status = ?, "
                + "updated_at = GETDATE() "
                + "WHERE id = ?";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, c.getName());

            ps.setString(2, c.getDescription());

            ps.setString(3, c.getBannerUrl());

            ps.setDate(4,
                    c.getStartDate() == null
                    ? null
                    : new java.sql.Date(c.getStartDate().getTime())
            );

            ps.setDate(5,
                    c.getEndDate() == null
                    ? null
                    : new java.sql.Date(c.getEndDate().getTime())
            );

            ps.setString(6, c.getStatus());

            ps.setLong(7, c.getId());

            ps.executeUpdate();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}
