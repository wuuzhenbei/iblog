package com.iblog.dao;

import com.iblog.model.Message;
import com.iblog.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public int insert(Message m) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, content, content_type) VALUES (?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getSenderId());
            ps.setInt(2, m.getReceiverId());
            ps.setString(3, m.getContent());
            ps.setString(4, m.getContentType() != null ? m.getContentType() : "text");
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public List<Message> getConversation(int userId1, int userId2, int page, int size) {
        String sql = "SELECT m.*, sp.nickname AS sender_nickname, sp.avatar_url AS sender_avatar FROM messages m JOIN user_profiles sp ON m.sender_id = sp.user_id WHERE ((m.sender_id=? AND m.receiver_id=?) OR (m.sender_id=? AND m.receiver_id=?)) AND m.is_recalled = 0 ORDER BY m.created_at DESC LIMIT ? OFFSET ?";
        List<Message> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId1);
            ps.setInt(2, userId2);
            ps.setInt(3, userId2);
            ps.setInt(4, userId1);
            ps.setInt(5, size);
            ps.setInt(6, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapMessage(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Message> getConversations(int userId) {
        String sql = "SELECT m.*, sp.nickname AS sender_nickname, sp.avatar_url AS sender_avatar, rp.nickname AS receiver_nickname FROM messages m JOIN user_profiles sp ON m.sender_id = sp.user_id JOIN user_profiles rp ON m.receiver_id = rp.user_id WHERE m.id IN (SELECT MAX(id) FROM messages WHERE (sender_id = ? OR receiver_id = ?) AND is_recalled = 0 GROUP BY LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id)) ORDER BY m.created_at DESC";
        List<Message> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapMessage(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void markAsRead(int senderId, int receiverId) {
        String sql = "UPDATE messages SET is_read = 1 WHERE sender_id = ? AND receiver_id = ? AND is_read = 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void recall(int messageId, int userId) {
        String sql = "UPDATE messages SET is_recalled = 1 WHERE id = ? AND sender_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int countUnread(int userId) {
        String sql = "SELECT COUNT(*) FROM messages WHERE receiver_id = ? AND is_read = 0 AND is_recalled = 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Message mapMessage(ResultSet rs) throws SQLException {
        Message m = new Message();
        m.setId(rs.getInt("id"));
        m.setSenderId(rs.getInt("sender_id"));
        m.setReceiverId(rs.getInt("receiver_id"));
        m.setContent(rs.getString("content"));
        m.setContentType(rs.getString("content_type"));
        m.setIsRead(rs.getInt("is_read"));
        m.setIsRecalled(rs.getInt("is_recalled"));
        m.setCreatedAt(rs.getTimestamp("created_at"));
        m.setSenderNickname(rs.getString("sender_nickname"));
        m.setSenderAvatar(rs.getString("sender_avatar"));
        try { m.setReceiverNickname(rs.getString("receiver_nickname")); } catch (SQLException ignored) {}
        return m;
    }
}
