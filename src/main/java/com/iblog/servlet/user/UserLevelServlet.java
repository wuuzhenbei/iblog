package com.iblog.servlet.user;

import com.iblog.dao.UserDAO;
import com.iblog.model.UserProfile;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

public class UserLevelServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        UserProfile profile = userDAO.getProfile(userId);
        if (profile == null) { JsonUtil.sendError(resp, 404, "用户不存在"); return; }

        int level = profile.getLevel();
        int points = profile.getPoints();
        int nextLevelPoints = level * 100;

        JsonUtil.sendJson(resp, Map.of("success", true, "data", Map.of(
            "level", level,
            "points", points,
            "nextLevelPoints", nextLevelPoints
        )));
    }
}
