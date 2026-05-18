package com.iblog.dao;

import com.iblog.model.User;
import com.iblog.model.UserProfile;
import com.iblog.util.DBUtil;
import com.iblog.util.PageBean;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public User findByPhone(String phone) {
        String sql = "SELECT * FROM users WHERE phone = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public User findByRememberToken(String token) {
        String sql = "SELECT * FROM users WHERE remember_token = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public int insert(User user) {
        String sql = "INSERT INTO users (username, phone, password_hash, role, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole() != null ? user.getRole() : "normal");
            ps.setString(5, "active");
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public void updateEmail(int userId, String email) {
        String sql = "UPDATE users SET email = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateResetToken(int userId, String token, java.sql.Timestamp expire) {
        String sql = "UPDATE users SET reset_token = ?, reset_token_expire = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setTimestamp(2, expire);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void updateRememberToken(int userId, String token) {
        String sql = "UPDATE users SET remember_token = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateLastLoginIp(int userId, String ip) {
        String sql = "UPDATE users SET last_login_ip = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ip);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updatePassword(int userId, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public UserProfile getProfile(int userId) {
        String sql = "SELECT * FROM user_profiles WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapProfile(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void updateProfile(UserProfile p) {
        String sql = "UPDATE user_profiles SET nickname=?, avatar_url=?, gender=?, birthday=?, region=?, signature=?, interests=?, bg_image_url=?, motto=? WHERE user_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNickname());
            ps.setString(2, p.getAvatarUrl());
            ps.setString(3, p.getGender());
            ps.setDate(4, p.getBirthday());
            ps.setString(5, p.getRegion());
            ps.setString(6, p.getSignature());
            ps.setString(7, p.getInterests());
            ps.setString(8, p.getBgImageUrl());
            ps.setString(9, p.getMotto());
            ps.setInt(10, p.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void insertProfile(int userId, String nickname) {
        String sql = "INSERT INTO user_profiles (user_id, nickname) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, nickname);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void insertPrivacy(int userId) {
        String sql = "INSERT INTO privacy_settings (user_id) VALUES (?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<User> search(String keyword, int page, int size) {
        String sql = "SELECT u.*, p.nickname, p.avatar_url FROM users u JOIN user_profiles p ON u.id = p.user_id WHERE u.status='active' AND (u.username LIKE ? OR p.nickname LIKE ?) LIMIT ? OFFSET ?";
        List<User> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setInt(3, size);
            ps.setInt(4, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = mapUser(rs);
                // store nickname in a temp way
                list.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM users WHERE status != 'deleted'";
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public List<User> findAll(int page, int size) {
        String sql = "SELECT * FROM users WHERE status != 'deleted' ORDER BY created_at DESC LIMIT ? OFFSET ?";
        List<User> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ========== 分页查询方法（T06） ==========

    public PageBean findAllWithPage(int curPage, int pageSize) {
        int totalRows = countAll();
        List<User> list = findAll(curPage, pageSize);
        return new PageBean(curPage, pageSize, totalRows, list);
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPhone(rs.getString("phone"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getString("status"));
        u.setRememberToken(rs.getString("remember_token"));
        u.setLastLoginIp(rs.getString("last_login_ip"));
        u.setEmail(rs.getString("email"));
        u.setResetToken(rs.getString("reset_token"));
        u.setResetTokenExpire(rs.getTimestamp("reset_token_expire"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }

    private UserProfile mapProfile(ResultSet rs) throws SQLException {
        UserProfile p = new UserProfile();
        p.setUserId(rs.getInt("user_id"));
        p.setNickname(rs.getString("nickname"));
        p.setAvatarUrl(rs.getString("avatar_url"));
        p.setGender(rs.getString("gender"));
        p.setBirthday(rs.getDate("birthday"));
        p.setRegion(rs.getString("region"));
        p.setSignature(rs.getString("signature"));
        p.setInterests(rs.getString("interests"));
        p.setBgImageUrl(rs.getString("bg_image_url"));
        p.setMotto(rs.getString("motto"));
        p.setLevel(rs.getInt("level"));
        p.setPoints(rs.getInt("points"));
        return p;
    }
}
