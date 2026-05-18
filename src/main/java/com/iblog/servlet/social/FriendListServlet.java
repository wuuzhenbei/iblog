package com.iblog.servlet.social;

import com.iblog.dao.FollowDAO;
import com.iblog.model.UserProfile;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FriendListServlet extends HttpServlet {
    private final FollowDAO followDAO = new FollowDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        String type = req.getParameter("type");
        int page = getIntParam(req, "page", 1);
        int size = getIntParam(req, "size", 20);

        List<UserProfile> list;
        if ("followers".equals(type)) {
            list = followDAO.getFollowers(userId, page, size);
        } else {
            list = followDAO.getFollowing(userId, page, size);
        }

        JsonUtil.sendJson(resp, Map.of("success", true, "data", list));
    }

    private int getIntParam(HttpServletRequest req, String name, int defaultVal) {
        String val = req.getParameter(name);
        if (val == null || val.isEmpty()) return defaultVal;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return defaultVal; }
    }
}
