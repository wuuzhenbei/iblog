package com.iblog.servlet.blog;

import com.iblog.dao.BlogDAO;
import com.iblog.model.Blog;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class BlogListServlet extends HttpServlet {
    private final BlogDAO blogDAO = new BlogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int page = getIntParam(req, "page", 1);
        int size = getIntParam(req, "size", 10);
        String userIdStr = req.getParameter("userId");
        String circleIdStr = req.getParameter("circleId");

        List<Blog> blogs;
        if (userIdStr != null && !userIdStr.isEmpty()) {
            blogs = blogDAO.findByUserId(Integer.parseInt(userIdStr), page, size);
        } else {
            blogs = blogDAO.findFeed(page, size);
        }

        int currentUserId = SessionUtil.getCurrentUserId(req);
        // TODO: mark liked/favorited status for current user

        JsonUtil.sendJson(resp, Map.of("success", true, "data", blogs));
    }

    private int getIntParam(HttpServletRequest req, String name, int defaultVal) {
        String val = req.getParameter(name);
        if (val == null || val.isEmpty()) return defaultVal;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return defaultVal; }
    }
}
