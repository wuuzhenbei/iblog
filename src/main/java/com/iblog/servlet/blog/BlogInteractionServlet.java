package com.iblog.servlet.blog;

import com.iblog.dao.BlogDAO;
import com.iblog.util.DBUtil;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class BlogInteractionServlet extends HttpServlet {
    private final BlogDAO blogDAO = new BlogDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        int blogId = Integer.parseInt(params.get("blogId"));
        String type = params.get("type"); // like, dislike, favorite, forward

        if (!"like".equals(type) && !"dislike".equals(type) && !"favorite".equals(type) && !"forward".equals(type)) {
            JsonUtil.sendError(resp, 400, "无效的互动类型");
            return;
        }

        boolean exists = checkExists(userId, blogId, type);
        if (exists) {
            removeInteraction(userId, blogId, type);
            if ("like".equals(type)) blogDAO.updateLikeCount(blogId, -1);
            JsonUtil.sendJson(resp, Map.of("success", true, "action", "removed"));
        } else {
            addInteraction(userId, blogId, type);
            if ("like".equals(type)) blogDAO.updateLikeCount(blogId, 1);
            if ("forward".equals(type)) blogDAO.updateForwardCount(blogId);
            JsonUtil.sendJson(resp, Map.of("success", true, "action", "added"));
        }
    }

    private boolean checkExists(int userId, int blogId, String type) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM interactions WHERE user_id=? AND blog_id=? AND type=?")) {
            ps.setInt(1, userId);
            ps.setInt(2, blogId);
            ps.setString(3, type);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    private void addInteraction(int userId, int blogId, String type) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT IGNORE INTO interactions (user_id, blog_id, type) VALUES (?,?,?)")) {
            ps.setInt(1, userId);
            ps.setInt(2, blogId);
            ps.setString(3, type);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void removeInteraction(int userId, int blogId, String type) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM interactions WHERE user_id=? AND blog_id=? AND type=?")) {
            ps.setInt(1, userId);
            ps.setInt(2, blogId);
            ps.setString(3, type);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
