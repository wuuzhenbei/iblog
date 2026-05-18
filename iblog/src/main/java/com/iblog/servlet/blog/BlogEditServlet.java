package com.iblog.servlet.blog;

import com.iblog.dao.BlogDAO;
import com.iblog.model.Blog;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class BlogEditServlet extends HttpServlet {
    private final BlogDAO blogDAO = new BlogDAO();

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        int blogId = Integer.parseInt(params.get("id"));
        Blog blog = blogDAO.findById(blogId);
        if (blog == null || blog.getUserId() != userId) {
            JsonUtil.sendError(resp, 403, "无权编辑");
            return;
        }

        blog.setContent(params.getOrDefault("content", blog.getContent()));
        blog.setImages(params.get("images"));
        blog.setMoodTag(params.get("moodTag"));
        blog.setSceneTag(params.get("sceneTag"));
        blog.setLocation(params.get("location"));
        blog.setVisibility(params.getOrDefault("visibility", blog.getVisibility()));

        blogDAO.update(blog);
        JsonUtil.sendSuccess(resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        String idStr = req.getParameter("id");
        if (idStr == null) { JsonUtil.sendError(resp, 400, "缺少ID"); return; }

        Blog blog = blogDAO.findById(Integer.parseInt(idStr));
        if (blog == null || blog.getUserId() != userId) {
            JsonUtil.sendError(resp, 403, "无权删除");
            return;
        }

        blogDAO.delete(blog.getId(), userId);
        JsonUtil.sendSuccess(resp);
    }
}
