package com.iblog.servlet.social;

import com.iblog.dao.FollowDAO;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class FollowServlet extends HttpServlet {
    private final FollowDAO followDAO = new FollowDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        int targetId = Integer.parseInt(params.get("targetId"));
        String action = params.get("action");

        if (userId == targetId) {
            JsonUtil.sendError(resp, 400, "不能关注自己");
            return;
        }

        switch (action) {
            case "follow":
                followDAO.follow(userId, targetId);
                JsonUtil.sendJson(resp, Map.of("success", true, "action", "followed"));
                break;
            case "unfollow":
                followDAO.unfollow(userId, targetId);
                JsonUtil.sendJson(resp, Map.of("success", true, "action", "unfollowed"));
                break;
            case "block":
                followDAO.block(userId, targetId);
                JsonUtil.sendJson(resp, Map.of("success", true, "action", "blocked"));
                break;
            default:
                JsonUtil.sendError(resp, 400, "无效操作");
        }
    }
}
