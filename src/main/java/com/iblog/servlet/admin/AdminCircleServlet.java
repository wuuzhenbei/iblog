package com.iblog.servlet.admin;

import com.iblog.dao.CircleDAO;
import com.iblog.util.JsonUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class AdminCircleServlet extends HttpServlet {
    private final CircleDAO circleDAO = new CircleDAO();

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        int circleId = Integer.parseInt(params.get("id"));
        String status = params.get("status");
        circleDAO.updateStatus(circleId, status);
        JsonUtil.sendSuccess(resp);
    }
}
