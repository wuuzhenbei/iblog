package com.iblog.servlet.circle;

import com.iblog.dao.CircleDAO;
import com.iblog.model.Circle;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class CircleManageServlet extends HttpServlet {
    private final CircleDAO circleDAO = new CircleDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        String name = params.get("name");
        if (name == null || name.trim().isEmpty()) {
            JsonUtil.sendError(resp, 400, "圈子名称不能为空");
            return;
        }

        Circle circle = new Circle();
        circle.setName(name);
        circle.setDescription(params.get("description"));
        circle.setCategory(params.get("category"));
        circle.setCreatorId(userId);

        int circleId = circleDAO.insert(circle);
        if (circleId > 0) {
            circleDAO.join(circleId, userId);
            JsonUtil.sendSuccess(resp, "circleId", circleId);
        } else {
            JsonUtil.sendError(resp, 500, "创建失败");
        }
    }
}
