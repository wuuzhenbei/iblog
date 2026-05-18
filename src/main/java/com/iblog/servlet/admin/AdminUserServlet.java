package com.iblog.servlet.admin;

import com.iblog.dao.UserDAO;
import com.iblog.model.User;
import com.iblog.util.JsonUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AdminUserServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int page = getIntParam(req, "page", 1);
        int size = getIntParam(req, "size", 20);

        List<User> users = userDAO.findAll(page, size);
        int total = userDAO.countAll();
        JsonUtil.sendJson(resp, Map.of("success", true, "data", users, "total", total));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        int userId = Integer.parseInt(params.get("id"));
        String action = params.get("action");

        if ("freeze".equals(action)) {
            userDAO.updateStatus(userId, "frozen");
        } else if ("activate".equals(action)) {
            userDAO.updateStatus(userId, "active");
        } else if ("delete".equals(action)) {
            userDAO.updateStatus(userId, "deleted");
        } else {
            JsonUtil.sendError(resp, 400, "无效操作");
            return;
        }
        JsonUtil.sendSuccess(resp);
    }

    private int getIntParam(HttpServletRequest req, String name, int defaultVal) {
        String val = req.getParameter(name);
        if (val == null || val.isEmpty()) return defaultVal;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return defaultVal; }
    }
}
