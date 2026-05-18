package com.iblog.servlet.blog;

import com.iblog.dao.BlogDAO;
import com.iblog.model.Blog;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

public class BlogPublishServlet extends HttpServlet {
    private final BlogDAO blogDAO = new BlogDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        String content = params.get("content");
        if (content == null || content.trim().isEmpty()) {
            JsonUtil.sendError(resp, 400, "内容不能为空");
            return;
        }

        Blog blog = new Blog();
        blog.setUserId(userId);
        blog.setContent(content);
        blog.setImages(params.get("images"));
        blog.setMoodTag(params.get("moodTag"));
        blog.setSceneTag(params.get("sceneTag"));
        blog.setLocation(params.get("location"));
        blog.setVisibility(params.getOrDefault("visibility", "public"));

        String scheduleTime = params.get("scheduleTime");
        if (scheduleTime != null && !scheduleTime.isEmpty()) {
            blog.setScheduleTime(Timestamp.valueOf(scheduleTime));
            blog.setStatus("scheduled");
        } else {
            blog.setStatus("published");
        }

        int blogId = blogDAO.insert(blog);
        if (blogId > 0) {
            JsonUtil.sendSuccess(resp, "blogId", blogId);
        } else {
            JsonUtil.sendError(resp, 500, "发布失败");
        }
    }
}
