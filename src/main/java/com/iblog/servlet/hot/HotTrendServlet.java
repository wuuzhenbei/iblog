package com.iblog.servlet.hot;

import com.iblog.dao.HotDAO;
import com.iblog.model.HotTrend;
import com.iblog.util.JsonUtil;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HotTrendServlet extends HttpServlet {
    private final HotDAO hotDAO = new HotDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String limitStr = req.getParameter("limit");
        int limit = 20;
        if (limitStr != null && !limitStr.isEmpty()) {
            try { limit = Integer.parseInt(limitStr); } catch (NumberFormatException ignored) {}
        }

        List<HotTrend> trends = hotDAO.findActive(limit);
        JsonUtil.sendJson(resp, Map.of("success", true, "data", trends));
    }
}
