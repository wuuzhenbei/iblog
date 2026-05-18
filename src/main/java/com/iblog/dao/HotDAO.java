package com.iblog.dao;

import com.iblog.model.HotTrend;
import com.iblog.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotDAO {

    public List<HotTrend> findActive(int limit) {
        String sql = "SELECT * FROM hot_trends WHERE status = 'active' ORDER BY heat DESC LIMIT ?";
        List<HotTrend> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapHot(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int insert(HotTrend h) {
        String sql = "INSERT INTO hot_trends (keyword, heat, related_blogs, is_manual) VALUES (?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, h.getKeyword());
            ps.setInt(2, h.getHeat());
            ps.setString(3, h.getRelatedBlogs());
            ps.setInt(4, h.getIsManual());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE hot_trends SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void incrementHeat(int id) {
        String sql = "UPDATE hot_trends SET heat = heat + 1 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<HotTrend> findAll(int page, int size) {
        String sql = "SELECT * FROM hot_trends ORDER BY created_at DESC LIMIT ? OFFSET ?";
        List<HotTrend> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapHot(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private HotTrend mapHot(ResultSet rs) throws SQLException {
        HotTrend h = new HotTrend();
        h.setId(rs.getInt("id"));
        h.setKeyword(rs.getString("keyword"));
        h.setHeat(rs.getInt("heat"));
        h.setRelatedBlogs(rs.getString("related_blogs"));
        h.setIsManual(rs.getInt("is_manual"));
        h.setStatus(rs.getString("status"));
        h.setCreatedAt(rs.getTimestamp("created_at"));
        return h;
    }
}
