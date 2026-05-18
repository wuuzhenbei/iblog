package com.iblog.dao;

import com.iblog.model.Circle;
import com.iblog.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CircleDAO {

    public int insert(Circle c) {
        String sql = "INSERT INTO circles (name, description, category, creator_id, is_official) VALUES (?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getDescription());
            ps.setString(3, c.getCategory());
            ps.setObject(4, c.getCreatorId());
            ps.setInt(5, c.getIsOfficial());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public Circle findById(int id) {
        String sql = "SELECT c.*, p.nickname AS creator_nickname FROM circles c LEFT JOIN user_profiles p ON c.creator_id = p.user_id WHERE c.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapCircle(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Circle> findAll(int page, int size) {
        String sql = "SELECT c.*, p.nickname AS creator_nickname FROM circles c LEFT JOIN user_profiles p ON c.creator_id = p.user_id WHERE c.status = 'active' ORDER BY c.member_count DESC LIMIT ? OFFSET ?";
        return queryList(sql, page, size);
    }

    public List<Circle> findByCategory(String category, int page, int size) {
        String sql = "SELECT c.*, p.nickname AS creator_nickname FROM circles c LEFT JOIN user_profiles p ON c.creator_id = p.user_id WHERE c.status = 'active' AND c.category = ? ORDER BY c.member_count DESC LIMIT ? OFFSET ?";
        List<Circle> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            ps.setInt(2, size);
            ps.setInt(3, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapCircle(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void join(int circleId, int userId) {
        String sql = "INSERT IGNORE INTO circle_members (circle_id, user_id, role) VALUES (?, ?, 'member')";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, circleId);
            ps.setInt(2, userId);
            if (ps.executeUpdate() > 0) {
                updateMemberCount(circleId, 1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void leave(int circleId, int userId) {
        String sql = "DELETE FROM circle_members WHERE circle_id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, circleId);
            ps.setInt(2, userId);
            if (ps.executeUpdate() > 0) {
                updateMemberCount(circleId, -1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean isMember(int circleId, int userId) {
        String sql = "SELECT 1 FROM circle_members WHERE circle_id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, circleId);
            ps.setInt(2, userId);
            return ps.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public void updateMemberCount(int circleId, int delta) {
        String sql = "UPDATE circles SET member_count = member_count + ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, circleId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateStatus(int circleId, String status) {
        String sql = "UPDATE circles SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, circleId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM circles WHERE status = 'active'";
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private List<Circle> queryList(String sql, int page, int size) {
        List<Circle> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapCircle(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Circle mapCircle(ResultSet rs) throws SQLException {
        Circle c = new Circle();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setCategory(rs.getString("category"));
        c.setCreatorId(rs.getInt("creator_id"));
        c.setIsOfficial(rs.getInt("is_official"));
        c.setMemberCount(rs.getInt("member_count"));
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setCreatorNickname(rs.getString("creator_nickname"));
        return c;
    }
}
