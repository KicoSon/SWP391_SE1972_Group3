package dal;

import model.sales.Pipeline;
import model.sales.PipelineStage;
import model.sales.LostReason;
import java.sql.*;
import java.util.*;

public class PipelineDAO extends DBContext {

    // ============================================================
    // PIPELINE
    // ============================================================
    /** Trả về 1 pipeline giả vì schema không có bảng pipelines riêng */
    public List<Pipeline> getAll() {
        List<Pipeline> list = new ArrayList<>();
        Pipeline p = new Pipeline();
        p.setId(1);
        p.setName("Sales Pipeline");
        p.setDefault(true);
        list.add(p);
        return list;
    }

    public Pipeline getById(int id) {
        Pipeline p = new Pipeline();
        p.setId(1);
        p.setName("Sales Pipeline");
        p.setDefault(true);
        return p;
    }

    public Pipeline getDefault() {
        return getById(1);
    }

    public boolean insert(Pipeline p) { return false; }

    public boolean update(Pipeline p) { return false; }

    // ============================================================
    // PIPELINE STAGES
    // ============================================================
    public List<PipelineStage> getStagesByPipelineId(int pipelineId) {
        List<PipelineStage> list = new ArrayList<>();
        String sql = "SELECT * FROM pipeline_stages ORDER BY sort_order";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapStage(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertStage(PipelineStage s) {
        String sql = "INSERT INTO pipeline_stages (name, sort_order, color, is_won, is_lost) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, s.getStageName());
            ps.setInt(2, s.getOrderIndex());
            ps.setString(3, s.getColor() != null ? s.getColor() : "#6c757d");
            ps.setBoolean(4, s.isWon());
            ps.setBoolean(5, s.isLost());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean updateStage(PipelineStage s) {
        String sql = "UPDATE pipeline_stages SET name=?, sort_order=?, color=?, is_won=?, is_lost=? WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, s.getStageName());
            ps.setInt(2, s.getOrderIndex());
            ps.setString(3, s.getColor());
            ps.setBoolean(4, s.isWon());
            ps.setBoolean(5, s.isLost());
            ps.setInt(6, s.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean deleteStage(int stageId) {
        String sql = "DELETE FROM pipeline_stages WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, stageId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean reorderStages(List<Integer> stageIds) {
        String sql = "UPDATE pipeline_stages SET sort_order=? WHERE id=?";
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(sql);
            for (int i = 0; i < stageIds.size(); i++) {
                ps.setInt(1, i + 1);
                ps.setInt(2, stageIds.get(i));
                ps.addBatch();
            }
            ps.executeBatch();
            connection.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try { connection.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    // ============================================================
    // LOST REASONS
    // ============================================================
    public List<LostReason> getAllLostReasons() {
        List<LostReason> list = new ArrayList<>();
        String sql = "SELECT * FROM lost_reasons ORDER BY id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LostReason lr = new LostReason();
                lr.setId(rs.getInt("id"));
                lr.setReason(rs.getString("reason"));
                lr.setActive(rs.getBoolean("is_active"));
                list.add(lr);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean insertLostReason(LostReason lr) {
        String sql = "INSERT INTO lost_reasons (reason, is_active) VALUES (?,1)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, lr.getReason());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean toggleLostReason(int id, boolean isActive) {
        String sql = "UPDATE lost_reasons SET is_active=? WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBoolean(1, isActive);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private Pipeline mapPipeline(ResultSet rs) throws SQLException {
        Pipeline p = new Pipeline();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setDefault(rs.getBoolean("is_default"));
        p.setCreatedBy(rs.getInt("created_by"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }

    private PipelineStage mapStage(ResultSet rs) throws SQLException {
        PipelineStage s = new PipelineStage();
        s.setId(rs.getInt("id"));
        s.setPipelineId(1);
        s.setStageName(rs.getString("name"));
        s.setOrderIndex(rs.getInt("sort_order"));
        s.setColor(rs.getString("color"));
        s.setWon(rs.getBoolean("is_won"));
        s.setLost(rs.getBoolean("is_lost"));
        return s;
    }
}
