package com.iblog.servlet.activity;

import com.iblog.util.DBUtil;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class FeedbackServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        String content = params.get("content");
        if (content == null || content.trim().isEmpty()) {
            JsonUtil.sendError(resp, 400, "反馈内容不能为空");
            return;
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO reports (reporter_id, target_type, target_id, reason) VALUES (?, 'user', ?, ?)")) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setString(3, "[反馈] " + content);
            ps.executeUpdate();
            JsonUtil.sendSuccess(resp);
        } catch (SQLException e) {
            JsonUtil.sendError(resp, 500, "提交失败");
        }
    }
}
