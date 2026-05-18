package com.iblog.servlet.social;

import com.iblog.util.DBUtil;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VisitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        String sql = "SELECT v.visited_at, p.user_id, p.nickname, p.avatar_url FROM visit_records v JOIN user_profiles p ON v.visitor_id = p.user_id WHERE v.visited_user_id = ? ORDER BY v.visited_at DESC LIMIT 20";
        List<Map<String, Object>> visitors = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                visitors.add(Map.of(
                    "userId", rs.getInt("user_id"),
                    "nickname", rs.getString("nickname"),
                    "avatarUrl", rs.getString("avatar_url") != null ? rs.getString("avatar_url") : "",
                    "visitedAt", rs.getTimestamp("visited_at").toString()
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        JsonUtil.sendJson(resp, Map.of("success", true, "data", visitors));
    }

    public static void recordVisit(int visitorId, int visitedUserId) {
        if (visitorId == visitedUserId) return;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO visit_records (visitor_id, visited_user_id) VALUES (?,?)")) {
            ps.setInt(1, visitorId);
            ps.setInt(2, visitedUserId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
