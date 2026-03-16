package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TierDAO extends DBContext {

    public String getTierNameById(int tierId) {

        String sql = "SELECT tier_name FROM tiers WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql);) {

            ps.setInt(1, tierId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("tier_name");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
