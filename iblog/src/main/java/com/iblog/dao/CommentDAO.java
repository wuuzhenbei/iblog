package com.iblog.dao;

import com.iblog.model.Comment;
import com.iblog.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    public int insert(Comment c) {
        String sql = "INSERT INTO comments (blog_id, user_id, parent_id, reply_to_user_id, content) VALUES (?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getBlogId());
            ps.setInt(2, c.getUserId());
            ps.setObject(3, c.getParentId());
            ps.setObject(4, c.getReplyToUserId());
            ps.setString(5, c.getContent());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public List<Comment> findByBlogId(int blogId) {
        String sql = "SELECT c.*, p.nickname, p.avatar_url FROM comments c JOIN user_profiles p ON c.user_id = p.user_id WHERE c.blog_id = ? AND c.status = 'normal' AND c.parent_id IS NULL ORDER BY c.created_at ASC";
        List<Comment> topComments = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Comment c = mapComment(rs);
                c.setChildren(findChildren(c.getId()));
                topComments.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return topComments;
    }

    private List<Comment> findChildren(int parentId) {
        String sql = "SELECT c.*, p.nickname, p.avatar_url, rp.nickname AS reply_to_nickname FROM comments c JOIN user_profiles p ON c.user_id = p.user_id LEFT JOIN user_profiles rp ON c.reply_to_user_id = rp.user_id WHERE c.parent_id = ? AND c.status = 'normal' ORDER BY c.created_at ASC";
        List<Comment> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Comment c = mapComment(rs);
                c.setReplyToNickname(rs.getString("reply_to_nickname"));
                list.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void delete(int id, int userId) {
        String sql = "UPDATE comments SET status = 'deleted' WHERE id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Comment findById(int id) {
        String sql = "SELECT c.*, p.nickname, p.avatar_url FROM comments c JOIN user_profiles p ON c.user_id = p.user_id WHERE c.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapComment(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private Comment mapComment(ResultSet rs) throws SQLException {
        Comment c = new Comment();
        c.setId(rs.getInt("id"));
        c.setBlogId(rs.getInt("blog_id"));
        c.setUserId(rs.getInt("user_id"));
        int parentId = rs.getInt("parent_id");
        c.setParentId(rs.wasNull() ? null : parentId);
        int replyTo = rs.getInt("reply_to_user_id");
        c.setReplyToUserId(rs.wasNull() ? null : replyTo);
        c.setContent(rs.getString("content"));
        c.setLikeCount(rs.getInt("like_count"));
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setNickname(rs.getString("nickname"));
        c.setAvatarUrl(rs.getString("avatar_url"));
        return c;
    }
}
