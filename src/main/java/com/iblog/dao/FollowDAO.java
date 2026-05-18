package com.iblog.dao;

import com.iblog.model.Follow;
import com.iblog.model.User;
import com.iblog.model.UserProfile;
import com.iblog.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowDAO {

    public void follow(int followerId, int followingId) {
        String sql = "INSERT INTO follows (follower_id, following_id, status) VALUES (?, ?, 'following') ON DUPLICATE KEY UPDATE status = 'following'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        updateMutual(followerId, followingId);
    }

    public void unfollow(int followerId, int followingId) {
        String sql = "DELETE FROM follows WHERE follower_id = ? AND following_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        updateMutual(followerId, followingId);
    }

    public void block(int blockerId, int blockedId) {
        String sql = "INSERT INTO follows (follower_id, following_id, status) VALUES (?, ?, 'blocked') ON DUPLICATE KEY UPDATE status = 'blocked'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blockerId);
            ps.setInt(2, blockedId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public String getStatus(int followerId, int followingId) {
        String sql = "SELECT status FROM follows WHERE follower_id = ? AND following_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("status");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean isFollowing(int followerId, int followingId) {
        return "following".equals(getStatus(followerId, followingId)) || "mutual".equals(getStatus(followerId, followingId));
    }

    public List<UserProfile> getFollowers(int userId, int page, int size) {
        String sql = "SELECT p.* FROM follows f JOIN user_profiles p ON f.follower_id = p.user_id WHERE f.following_id = ? AND f.status IN ('following','mutual') ORDER BY f.created_at DESC LIMIT ? OFFSET ?";
        return queryProfiles(sql, userId, page, size);
    }

    public List<UserProfile> getFollowing(int userId, int page, int size) {
        String sql = "SELECT p.* FROM follows f JOIN user_profiles p ON f.following_id = p.user_id WHERE f.follower_id = ? AND f.status IN ('following','mutual') ORDER BY f.created_at DESC LIMIT ? OFFSET ?";
        return queryProfiles(sql, userId, page, size);
    }

    public int countFollowers(int userId) {
        return countByField("following_id", userId);
    }

    public int countFollowing(int userId) {
        return countByField("follower_id", userId);
    }

    private int countByField(String field, int userId) {
        String sql = "SELECT COUNT(*) FROM follows WHERE " + field + " = ? AND status IN ('following','mutual')";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private void updateMutual(int userId1, int userId2) {
        boolean aFollowsB = isFollowing(userId1, userId2);
        boolean bFollowsA = isFollowing(userId2, userId1);
        String newStatus = (aFollowsB && bFollowsA) ? "mutual" : "following";
        String sql = "UPDATE follows SET status = ? WHERE ((follower_id = ? AND following_id = ?) OR (follower_id = ? AND following_id = ?)) AND status != 'blocked'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, userId1);
            ps.setInt(3, userId2);
            ps.setInt(4, userId2);
            ps.setInt(5, userId1);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private List<UserProfile> queryProfiles(String sql, int userId, int page, int size) {
        List<UserProfile> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, size);
            ps.setInt(3, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserProfile p = new UserProfile();
                p.setUserId(rs.getInt("user_id"));
                p.setNickname(rs.getString("nickname"));
                p.setAvatarUrl(rs.getString("avatar_url"));
                p.setSignature(rs.getString("signature"));
                p.setLevel(rs.getInt("level"));
                list.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
