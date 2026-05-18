package com.iblog.servlet.admin;

import com.iblog.dao.StatsDAO;
import com.iblog.util.JsonUtil;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

public class AdminStatsServlet extends HttpServlet {
    private final StatsDAO statsDAO = new StatsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> stats = statsDAO.getOverview();
        JsonUtil.sendJson(resp, Map.of("success", true, "data", stats));
    }
}
