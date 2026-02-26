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

// Hàm lấy danh sách Lead có phân quyền theo Sale
    public List<Lead> getLeadsBySaleId(Long saleId) {
        List<Lead> list = new ArrayList<>();

        // 1. SELECT thêm assigned_sales_id theo ý bạn
        String sql = "SELECT id, full_name, assigned_sales_id FROM leads WHERE status != 'converted'";

        // 2. Nếu truyền vào saleId (Tức là nhân viên thường) -> Cấp thêm điều kiện lọc
        if (saleId != null) {
            sql += " AND assigned_sales_id = ?";
        }

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            // Set tham số nếu có
            if (saleId != null) {
                ps.setLong(1, saleId);
            }

            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                Lead l = new Lead();
                l.setId(rs.getLong("id"));
                l.setFullName(rs.getString("full_name"));

                // 3. Lấy thêm assigned_sales_id gán vào model (Như bạn suy luận)
                // Dùng getObject để tránh lỗi ClassCastException nếu nhỡ may bị NULL ở DB
                Object assignedObj = rs.getObject("assigned_sales_id");
                if (assignedObj != null) {
                    l.setAssignedSalesId(((Number) assignedObj).longValue());
                }

                list.add(l);
                count++;
            }
            System.out.println("[LeadDAO] Found " + count + " leads");

            rs.close();
            ps.close();
        } catch (Exception e) {
            System.err.println("[LeadDAO] Error fetching leads: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public void updateStatus(long leadId, String status) {

        String sql
                = "UPDATE leads SET status=? WHERE id=?";

        try {

            PreparedStatement ps
                    = connection.prepareStatement(sql);

            ps.setString(1, status);

            ps.setLong(2, leadId);

            ps.executeUpdate();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
public void updateLead(Lead lead) {

    String sql =
            "UPDATE leads SET "
            + "full_name = ?, "
            + "phone = ?, "
            + "email = ?, "
            + "address = ?, "
            + "product_interest = ?, "
            + "source = ?, "
            + "campaign_id = ?, "
            + "updated_at = GETDATE() "
            + "WHERE id = ?";

    try {

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1, lead.getFullName());

        ps.setString(2, lead.getPhone());

        ps.setString(3, lead.getEmail());

        ps.setString(4, lead.getAddress());

        ps.setString(5, lead.getProductInterest());

        ps.setString(6, lead.getSource());

        if (lead.getCampaignId() != null) {

            ps.setLong(7, lead.getCampaignId());

        } else {

            ps.setNull(7, java.sql.Types.BIGINT);

        }

        ps.setLong(8, lead.getId());

        ps.executeUpdate();

    } catch (Exception e) {

        e.printStackTrace();

    }

}

    public Lead getLeadById(long id) {

        Lead lead = null;

        String sql = "SELECT * FROM leads WHERE id = ?";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                lead = new Lead();

                lead.setId(rs.getLong("id"));

                lead.setFullName(rs.getString("full_name"));

                lead.setPhone(rs.getString("phone"));

                lead.setEmail(rs.getString("email"));

                lead.setAddress(rs.getString("address"));

                lead.setProductInterest(rs.getString("product_interest"));

                lead.setSource(rs.getString("source"));

                lead.setStatus(rs.getString("status"));

                lead.setCampaignId(rs.getObject("campaign_id") != null
                        ? rs.getLong("campaign_id")
                        : null);

                lead.setCreatedBy(rs.getLong("created_by"));

                lead.setCreatedAt(rs.getTimestamp("created_at"));

                lead.setUpdatedAt(rs.getTimestamp("updated_at"));

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return lead;
    }
     public List<Lead> getQualifiedLeadsFull() {

    List<Lead> list = new ArrayList<>();

    String sql =
    "SELECT l.*, u.full_name AS sale_name "
  + "FROM leads l "
  + "LEFT JOIN users u ON l.assigned_sales_id = u.id "
  + "WHERE l.status IN ('qualified','assigned') "
  + "ORDER BY l.created_at DESC";

    try {

        PreparedStatement ps = connection.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            Lead lead = new Lead();

            lead.setId(rs.getLong("id"));

            lead.setFullName(rs.getString("full_name"));

            lead.setPhone(rs.getString("phone"));

            lead.setEmail(rs.getString("email"));

            lead.setAddress(rs.getString("address"));

            lead.setProductInterest(
                    rs.getString("product_interest"));

            lead.setSource(rs.getString("source"));

            lead.setStatus(rs.getString("status"));

            lead.setCampaignId(
                    (Long) rs.getObject("campaign_id"));

            lead.setAssignedSalesId(
                    (Long) rs.getObject("assigned_sales_id"));

            lead.setCreatedBy(
                    (Long) rs.getObject("created_by"));

            lead.setCreatedAt(
                    rs.getTimestamp("created_at"));

            lead.setUpdatedAt(
                    rs.getTimestamp("updated_at"));

            // quan trọng
            lead.setSaleName(
                    rs.getString("sale_name"));

            list.add(lead);
        }

    } catch (Exception e) {

        e.printStackTrace();
    }

    return list;
}
 public void assignLeadToSale(long leadId, long saleId) {

    String sql =
            "UPDATE leads SET "
            + "assigned_sales_id = ?, "
            + "status = 'assigned' "
            + "WHERE id = ?";

    try {

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setLong(1, saleId);

        ps.setLong(2, leadId);

        ps.executeUpdate();

    } catch (Exception e) {

        e.printStackTrace();

    }

}
   public Lead getById(long id) {
        String sql = "SELECT l.*, c.name AS campaign_name FROM leads l "
                   + "LEFT JOIN campaigns c ON l.campaign_id = c.id "
                   + "WHERE l.id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Lead l = new Lead();
                l.setId(rs.getLong("id"));
                l.setFullName(rs.getString("full_name"));
                l.setPhone(rs.getString("phone"));
                l.setEmail(rs.getString("email"));
                l.setAddress(rs.getString("address"));
                l.setProductInterest(rs.getString("product_interest"));
                l.setSource(rs.getString("source"));
                l.setStatus(rs.getString("status"));
                try { l.setCampaignId(rs.getLong("campaign_id")); } catch (Exception ignored) {}
                return l;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
