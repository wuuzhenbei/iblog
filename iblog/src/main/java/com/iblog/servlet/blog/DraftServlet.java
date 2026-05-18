package com.iblog.servlet.blog;

import com.iblog.dao.BlogDAO;
import com.iblog.model.Blog;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DraftServlet extends HttpServlet {
    private final BlogDAO blogDAO = new BlogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        List<Blog> drafts = blogDAO.findDrafts(userId);
        JsonUtil.sendJson(resp, Map.of("success", true, "data", drafts));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        Blog blog = new Blog();
        blog.setUserId(userId);
        blog.setContent(params.get("content"));
        blog.setImages(params.get("images"));
        blog.setStatus("draft");

        int blogId = blogDAO.insert(blog);
        JsonUtil.sendSuccess(resp, "blogId", blogId);
    }
}
