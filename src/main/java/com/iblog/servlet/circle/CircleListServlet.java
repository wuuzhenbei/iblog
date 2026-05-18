package com.iblog.servlet.circle;

import com.iblog.dao.CircleDAO;
import com.iblog.model.Circle;
import com.iblog.util.JsonUtil;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CircleListServlet extends HttpServlet {
    private final CircleDAO circleDAO = new CircleDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int page = getIntParam(req, "page", 1);
        int size = getIntParam(req, "size", 20);
        String category = req.getParameter("category");

        List<Circle> circles;
        if (category != null && !category.isEmpty()) {
            circles = circleDAO.findByCategory(category, page, size);
        } else {
            circles = circleDAO.findAll(page, size);
        }

        JsonUtil.sendJson(resp, Map.of("success", true, "data", circles));
    }

    private int getIntParam(HttpServletRequest req, String name, int defaultVal) {
        String val = req.getParameter(name);
        if (val == null || val.isEmpty()) return defaultVal;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return defaultVal; }
    }
}
