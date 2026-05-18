package com.iblog.servlet.circle;

import com.iblog.dao.BlogDAO;
import com.iblog.model.Blog;
import com.iblog.util.JsonUtil;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CirclePostServlet extends HttpServlet {
    private final BlogDAO blogDAO = new BlogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String circleIdStr = req.getParameter("circleId");
        if (circleIdStr == null) { JsonUtil.sendError(resp, 400, "缺少圈子ID"); return; }

        int page = getIntParam(req, "page", 1);
        int size = getIntParam(req, "size", 10);

        List<Blog> blogs = blogDAO.findByCircleId(Integer.parseInt(circleIdStr), page, size);
        JsonUtil.sendJson(resp, Map.of("success", true, "data", blogs));
    }

    private int getIntParam(HttpServletRequest req, String name, int defaultVal) {
        String val = req.getParameter(name);
        if (val == null || val.isEmpty()) return defaultVal;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return defaultVal; }
    }
}
