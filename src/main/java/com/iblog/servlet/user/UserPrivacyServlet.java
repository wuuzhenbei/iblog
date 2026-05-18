package com.iblog.servlet.user;

import com.iblog.util.DBUtil;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class UserPrivacyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM privacy_settings WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                JsonUtil.sendJson(resp, Map.of("success", true, "data", Map.of(
                    "hideOnline", rs.getInt("hide_online"),
                    "visitPermission", rs.getString("visit_permission"),
                    "dynamicScope", rs.getString("dynamic_scope")
                )));
            } else {
                JsonUtil.sendError(resp, 404, "未找到隐私设置");
            }
        } catch (SQLException e) {
            JsonUtil.sendError(resp, 500, "服务器错误");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE privacy_settings SET hide_online=?, visit_permission=?, dynamic_scope=? WHERE user_id=?")) {
            ps.setInt(1, "true".equals(params.get("hideOnline")) ? 1 : 0);
            ps.setString(2, params.getOrDefault("visitPermission", "all"));
            ps.setString(3, params.getOrDefault("dynamicScope", "all"));
            ps.setInt(4, userId);
            ps.executeUpdate();
            JsonUtil.sendSuccess(resp);
        } catch (SQLException e) {
            JsonUtil.sendError(resp, 500, "服务器错误");
        }
    }
}
