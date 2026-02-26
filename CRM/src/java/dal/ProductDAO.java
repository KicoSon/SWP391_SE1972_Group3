package dal;

import model.sales.Product;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class ProductDAO extends DBContext {

    // ============================================================
    // GET ALL ACTIVE PRODUCTS
    // ============================================================
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_active=1 ORDER BY category, name";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ============================================================
    // GET BY CATEGORY
    // ============================================================
    public List<Product> getByCategory(String category) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_active=1 AND category=? ORDER BY name";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ============================================================
    // GET BY ID
    // ============================================================
    public Product getById(int id) {
        String sql = "SELECT * FROM products WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ============================================================
    // APPLY PRICING RULE
    // ============================================================
    public BigDecimal applyPricingRule(int productId, int quantity, Integer customerId) {
        Product p = getById(productId);
        if (p == null) return BigDecimal.ZERO;

        BigDecimal basePrice = p.getBasePrice();
        // Check active pricing rules for this date
        String sql = "SELECT TOP 1 * FROM pricing_rules " +
                     "WHERE is_active=1 AND (start_date IS NULL OR start_date <= GETDATE()) " +
                     "AND (end_date IS NULL OR end_date >= GETDATE()) ORDER BY id DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BigDecimal value = rs.getBigDecimal("value");
                boolean isPercent = rs.getBoolean("is_percent");
                if (isPercent) {
                    BigDecimal discount = basePrice.multiply(value).divide(new BigDecimal(100));
                    return basePrice.subtract(discount);
                } else {
                    return basePrice.subtract(value).max(BigDecimal.ZERO);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return basePrice;
    }

    // ============================================================
    // SEARCH PRODUCTS (for AJAX)
    // ============================================================
    public List<Product> search(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_active=1 AND (name LIKE ? OR sku LIKE ?) ORDER BY name";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ============================================================
    // HELPER
    // ============================================================
    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setCategory(rs.getString("category"));
        p.setSku(rs.getString("sku"));
        p.setBasePrice(rs.getBigDecimal("base_price"));
        p.setActive(rs.getBoolean("is_active"));
        p.setDescription(rs.getString("description"));
        p.setImageUrl(rs.getString("image_url"));
        return p;
    }
}
