package com.iblog.dao;

import com.iblog.model.Blog;
import com.iblog.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlogDAO {

    public int insert(Blog b) {
        String sql = "INSERT INTO blogs (user_id, content, images, mood_tag, scene_tag, location, visibility, status, schedule_time) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getUserId());
            ps.setString(2, b.getContent());
            ps.setString(3, b.getImages());
            ps.setString(4, b.getMoodTag());
            ps.setString(5, b.getSceneTag());
            ps.setString(6, b.getLocation());
            ps.setString(7, b.getVisibility());
            ps.setString(8, b.getStatus());
            ps.setTimestamp(9, b.getScheduleTime());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public Blog findById(int id) {
        String sql = "SELECT b.*, p.nickname, p.avatar_url FROM blogs b JOIN user_profiles p ON b.user_id = p.user_id WHERE b.id = ? AND b.status != 'deleted'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapBlog(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Blog> findFeed(int page, int size) {
        String sql = "SELECT b.*, p.nickname, p.avatar_url FROM blogs b JOIN user_profiles p ON b.user_id = p.user_id WHERE b.status = 'published' AND b.visibility = 'public' ORDER BY b.is_pinned DESC, b.created_at DESC LIMIT ? OFFSET ?";
        return queryList(sql, page, size);
    }

    public List<Blog> findByUserId(int userId, int page, int size) {
        String sql = "SELECT b.*, p.nickname, p.avatar_url FROM blogs b JOIN user_profiles p ON b.user_id = p.user_id WHERE b.user_id = ? AND b.status = 'published' ORDER BY b.is_pinned DESC, b.created_at DESC LIMIT ? OFFSET ?";
        List<Blog> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, size);
            ps.setInt(3, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapBlog(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Blog> findByCircleId(int circleId, int page, int size) {
        String sql = "SELECT b.*, p.nickname, p.avatar_url FROM blogs b JOIN user_profiles p ON b.user_id = p.user_id WHERE b.status = 'published' AND b.visibility = 'public' ORDER BY b.created_at DESC LIMIT ? OFFSET ?";
        // Note: circle_id field can be added to blogs table if needed
        return queryList(sql, page, size);
    }

    public List<Blog> findDrafts(int userId) {
        String sql = "SELECT b.*, p.nickname, p.avatar_url FROM blogs b JOIN user_profiles p ON b.user_id = p.user_id WHERE b.user_id = ? AND b.status = 'draft' ORDER BY b.updated_at DESC";
        List<Blog> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapBlog(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void update(Blog b) {
        String sql = "UPDATE blogs SET content=?, images=?, mood_tag=?, scene_tag=?, location=?, visibility=?, status=?, schedule_time=? WHERE id=? AND user_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getContent());
            ps.setString(2, b.getImages());
            ps.setString(3, b.getMoodTag());
            ps.setString(4, b.getSceneTag());
            ps.setString(5, b.getLocation());
            ps.setString(6, b.getVisibility());
            ps.setString(7, b.getStatus());
            ps.setTimestamp(8, b.getScheduleTime());
            ps.setInt(9, b.getId());
            ps.setInt(10, b.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int id, int userId) {
        String sql = "UPDATE blogs SET status = 'deleted' WHERE id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void adminDelete(int id) {
        String sql = "UPDATE blogs SET status = 'deleted' WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void incrementView(int id) {
        String sql = "UPDATE blogs SET view_count = view_count + 1 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateLikeCount(int blogId, int delta) {
        String sql = "UPDATE blogs SET like_count = like_count + ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, blogId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateCommentCount(int blogId, int delta) {
        String sql = "UPDATE blogs SET comment_count = comment_count + ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, blogId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateForwardCount(int blogId) {
        String sql = "UPDATE blogs SET forward_count = forward_count + 1 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Blog> search(String keyword, int page, int size) {
        String sql = "SELECT b.*, p.nickname, p.avatar_url FROM blogs b JOIN user_profiles p ON b.user_id = p.user_id WHERE b.status='published' AND b.content LIKE ? ORDER BY b.created_at DESC LIMIT ? OFFSET ?";
        List<Blog> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setInt(2, size);
            ps.setInt(3, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapBlog(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM blogs WHERE status = 'published'";
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int countByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM blogs WHERE user_id = ? AND status = 'published'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private List<Blog> queryList(String sql, int page, int size) {
        List<Blog> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapBlog(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Blog mapBlog(ResultSet rs) throws SQLException {
        Blog b = new Blog();
        b.setId(rs.getInt("id"));
        b.setUserId(rs.getInt("user_id"));
        b.setContent(rs.getString("content"));
        b.setImages(rs.getString("images"));
        b.setMoodTag(rs.getString("mood_tag"));
        b.setSceneTag(rs.getString("scene_tag"));
        b.setLocation(rs.getString("location"));
        b.setVisibility(rs.getString("visibility"));
        b.setIsPinned(rs.getInt("is_pinned"));
        b.setStatus(rs.getString("status"));
        b.setScheduleTime(rs.getTimestamp("schedule_time"));
        b.setViewCount(rs.getInt("view_count"));
        b.setLikeCount(rs.getInt("like_count"));
        b.setCommentCount(rs.getInt("comment_count"));
        b.setForwardCount(rs.getInt("forward_count"));
        b.setCreatedAt(rs.getTimestamp("created_at"));
        b.setUpdatedAt(rs.getTimestamp("updated_at"));
        b.setNickname(rs.getString("nickname"));
        b.setAvatarUrl(rs.getString("avatar_url"));
        return b;
    }
}
