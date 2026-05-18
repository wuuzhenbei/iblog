package com.iblog.servlet.admin;

import com.iblog.dao.HotDAO;
import com.iblog.model.HotTrend;
import com.iblog.util.JsonUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AdminHotServlet extends HttpServlet {
    private final HotDAO hotDAO = new HotDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int page = getIntParam(req, "page", 1);
        int size = getIntParam(req, "size", 20);
        List<HotTrend> trends = hotDAO.findAll(page, size);
        JsonUtil.sendJson(resp, Map.of("success", true, "data", trends));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        HotTrend hot = new HotTrend();
        hot.setKeyword(params.get("keyword"));
        hot.setHeat(Integer.parseInt(params.getOrDefault("heat", "100")));
        hot.setIsManual(1);

        int id = hotDAO.insert(hot);
        JsonUtil.sendSuccess(resp, "id", id);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        int id = Integer.parseInt(params.get("id"));
        hotDAO.updateStatus(id, "removed");
        JsonUtil.sendSuccess(resp);
    }

    private int getIntParam(HttpServletRequest req, String name, int defaultVal) {
        String val = req.getParameter(name);
        if (val == null || val.isEmpty()) return defaultVal;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return defaultVal; }
    }
}
