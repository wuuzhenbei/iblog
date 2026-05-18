package com.iblog.servlet.user;

import com.iblog.dao.UserDAO;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class UserStatusServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        String action = params.get("action");
        if ("freeze".equals(action)) {
            userDAO.updateStatus(userId, "frozen");
            SessionUtil.invalidate(req);
            JsonUtil.sendSuccess(resp);
        } else if ("delete".equals(action)) {
            userDAO.updateStatus(userId, "deleted");
            SessionUtil.invalidate(req);
            JsonUtil.sendSuccess(resp);
        } else {
            JsonUtil.sendError(resp, 400, "无效操作");
        }
    }
}
