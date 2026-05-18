package com.iblog.servlet.circle;

import com.iblog.dao.CircleDAO;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class CircleMemberServlet extends HttpServlet {
    private final CircleDAO circleDAO = new CircleDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        int circleId = Integer.parseInt(params.get("circleId"));
        String action = params.get("action");

        if ("join".equals(action)) {
            circleDAO.join(circleId, userId);
            JsonUtil.sendSuccess(resp);
        } else if ("leave".equals(action)) {
            circleDAO.leave(circleId, userId);
            JsonUtil.sendSuccess(resp);
        } else {
            JsonUtil.sendError(resp, 400, "无效操作");
        }
    }
}
