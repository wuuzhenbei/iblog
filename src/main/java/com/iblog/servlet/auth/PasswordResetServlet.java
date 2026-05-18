package com.iblog.servlet.auth;

import com.iblog.dao.UserDAO;
import com.iblog.model.User;
import com.iblog.util.AuthUtil;
import com.iblog.util.JsonUtil;
import com.iblog.util.MailUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

public class PasswordResetServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String token = req.getParameter("token");
        if (token == null || token.isEmpty()) {
            JsonUtil.sendError(resp, 400, "缺少重置令牌");
            return;
        }

        // 查找匹配的令牌
        // 由于没有按 token 查询的方法，我们遍历或使用已保存的 token 验证逻辑
        // 这里用一个简单的 SQL 查询替代
        // 实际可以用 userDAO 的通用查询
        User user = findByResetToken(token);
        if (user == null) {
            JsonUtil.sendError(resp, 400, "重置令牌无效");
            return;
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (user.getResetTokenExpire() == null || user.getResetTokenExpire().before(now)) {
            JsonUtil.sendError(resp, 400, "重置令牌已过期");
            return;
        }

        JsonUtil.sendSuccess(resp, "username", user.getUsername());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = readBody(req);
        Map<String, String> params = JsonUtil.getMapper().readValue(body, Map.class);

        // ===== 模式1：发送重置邮件 =====
        String email = params.get("email");
        if (email != null && !email.isEmpty()) {
            handleSendResetEmail(req, resp, email);
            return;
        }

        // ===== 模式2：通过令牌重置密码 =====
        String token = params.get("token");
        String newPassword = params.get("newPassword");
        if (token != null && newPassword != null) {
            handleResetWithToken(resp, token, newPassword);
            return;
        }

        // ===== 模式3：通过手机号重置密码（原有逻辑） =====
        String account = params.get("account");
        String phone = params.get("phone");
        if (account != null && phone != null && newPassword != null) {
            handlePhoneReset(resp, account, phone, newPassword);
            return;
        }

        JsonUtil.sendError(resp, 400, "参数不完整，需要 email 或 token+newPassword 或 account+phone+newPassword");
    }

    private void handleSendResetEmail(HttpServletRequest req, HttpServletResponse resp, String email) throws IOException {
        User user = userDAO.findByEmail(email);
        if (user == null) {
            // 为安全考虑，不暴露邮箱是否注册
            JsonUtil.sendSuccess(resp);
            return;
        }

        // 生成重置令牌（1小时有效）
        String resetToken = UUID.randomUUID().toString().replace("-", "");
        Timestamp expire = new Timestamp(System.currentTimeMillis() + 3600 * 1000);
        userDAO.updateResetToken(user.getId(), resetToken, expire);

        // 发送重置邮件
        String resetUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort()
                + req.getContextPath() + "/api/auth/reset-password?token=" + resetToken;
        MailUtil.sendPasswordResetMail(email, user.getUsername(), resetUrl);

        JsonUtil.sendSuccess(resp);
    }

    private void handleResetWithToken(HttpServletResponse resp, String token, String newPassword) throws IOException {
        User user = findByResetToken(token);
        if (user == null) {
            JsonUtil.sendError(resp, 400, "重置令牌无效");
            return;
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (user.getResetTokenExpire() == null || user.getResetTokenExpire().before(now)) {
            JsonUtil.sendError(resp, 400, "重置令牌已过期");
            return;
        }

        // 更新密码并清除令牌
        userDAO.updatePassword(user.getId(), AuthUtil.hashPassword(newPassword));
        userDAO.updateResetToken(user.getId(), null, null);

        JsonUtil.sendSuccess(resp);
    }

    private void handlePhoneReset(HttpServletResponse resp, String account, String phone, String newPassword) throws IOException {
        User user = userDAO.findByUsername(account);
        if (user == null) user = userDAO.findByPhone(account);
        if (user == null) {
            JsonUtil.sendError(resp, 404, "用户不存在");
            return;
        }

        if (!phone.equals(user.getPhone())) {
            JsonUtil.sendError(resp, 400, "手机号不匹配");
            return;
        }

        userDAO.updatePassword(user.getId(), AuthUtil.hashPassword(newPassword));
        JsonUtil.sendSuccess(resp);
    }

    private User findByResetToken(String token) {
        // 由于数据库中没有按 token 查询的专用方法，使用直接 SQL
        String sql = "SELECT * FROM users WHERE reset_token = ?";
        try (java.sql.Connection conn = com.iblog.util.DBUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setResetToken(rs.getString("reset_token"));
                u.setResetTokenExpire(rs.getTimestamp("reset_token_expire"));
                return u;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
}
