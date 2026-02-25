package dal;

import model.Lead;
import java.sql.*;
import java.util.*;

public class LeadDAO extends DBContext {

    // =============================
    // FILTER LEADS
    // =============================
    public List<Lead> filterLeads(String search, String status) {

        List<Lead> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT l.*, c.name AS campaign_name "
                + "FROM leads l "
                + "LEFT JOIN campaigns c ON l.campaign_id = c.id "
                + "WHERE 1=1"
        );

        if (search != null && !search.trim().isEmpty()) {

            sql.append(" AND l.full_name LIKE ?");

        }

        if (status != null && !status.trim().isEmpty()) {

            sql.append(" AND l.status = ?");

        }

        sql.append(" ORDER BY l.created_at DESC");

        try {

            PreparedStatement ps
                    = connection.prepareStatement(sql.toString());

            int index = 1;

            if (search != null && !search.trim().isEmpty()) {

                ps.setString(index++, "%" + search + "%");

            }

            if (status != null && !status.trim().isEmpty()) {

                ps.setString(index++, status);

            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Lead l = new Lead();

                l.setId(rs.getLong("id"));

                l.setFullName(rs.getString("full_name"));

                l.setPhone(rs.getString("phone"));

                l.setEmail(rs.getString("email"));

                l.setAddress(rs.getString("address"));

                l.setProductInterest(
                        rs.getString("product_interest")
                );

                l.setSource(rs.getString("source"));

                l.setStatus(rs.getString("status"));

                l.setCampaignId(
                        (Long) rs.getObject("campaign_id")
                );

                l.setAssignedSalesId(
                        (Long) rs.getObject("assigned_sales_id")
                );

                l.setCreatedBy(
                        (Long) rs.getObject("created_by")
                );

                l.setCreatedAt(
                        rs.getTimestamp("created_at")
                );

                l.setUpdatedAt(
                        rs.getTimestamp("updated_at")
                );

                l.setCampaignName(
                        rs.getString("campaign_name")
                );

                list.add(l);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return list;

    }

    // =============================
    // INSERT LEAD
    // =============================
    public boolean insertLead(Lead l) {

        String sql
                = "INSERT INTO leads "
                + "(full_name, phone, email, address, "
                + "product_interest, source, status, campaign_id, created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {

            PreparedStatement ps
                    = connection.prepareStatement(sql);

            ps.setString(1, l.getFullName());

            ps.setString(2, l.getPhone());

            ps.setString(3, l.getEmail());

            ps.setString(4, l.getAddress());

            ps.setString(5, l.getProductInterest());

            ps.setString(6, l.getSource());

            ps.setString(7, l.getStatus());

            if (l.getCampaignId() != null) {
                ps.setLong(8, l.getCampaignId());
            } else {
                ps.setNull(8, Types.BIGINT);
            }

            if (l.getCreatedBy() != null) {
                ps.setLong(9, l.getCreatedBy());
            } else {
                ps.setNull(9, Types.BIGINT);
            }

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return false;

    }

    public boolean isEmailExist(String email) {

        String sql
                = "SELECT COUNT(*) FROM leads WHERE email=?";

        try {

            PreparedStatement ps
                    = connection.prepareStatement(sql);

            ps.setString(1, email);

            ResultSet rs
                    = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return false;

    }

}
