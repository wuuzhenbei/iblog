package com.iblog.servlet.search;

import com.iblog.dao.BlogDAO;
import com.iblog.dao.UserDAO;
import com.iblog.model.Blog;
import com.iblog.model.User;
import com.iblog.util.JsonUtil;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchServlet extends HttpServlet {
    private final BlogDAO blogDAO = new BlogDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String q = req.getParameter("q");
        String type = req.getParameter("type");
        int page = getIntParam(req, "page", 1);
        int size = getIntParam(req, "size", 10);

        if (q == null || q.trim().isEmpty()) {
            JsonUtil.sendError(resp, 400, "搜索关键词不能为空");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        if ("user".equals(type)) {
            data.put("users", userDAO.search(q, page, size));
        } else if ("blog".equals(type)) {
            data.put("blogs", blogDAO.search(q, page, size));
        } else {
            data.put("blogs", blogDAO.search(q, page, size));
            data.put("users", userDAO.search(q, page, size));
        }

        JsonUtil.sendJson(resp, Map.of("success", true, "data", data));
    }

    private int getIntParam(HttpServletRequest req, String name, int defaultVal) {
        String val = req.getParameter(name);
        if (val == null || val.isEmpty()) return defaultVal;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return defaultVal; }
    }
}
