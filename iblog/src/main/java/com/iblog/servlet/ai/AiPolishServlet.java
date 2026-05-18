package com.iblog.servlet.ai;

import com.iblog.util.AiUtil;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class AiPolishServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        String text = params.get("text");
        String action = params.getOrDefault("action", "润色");
        if (text == null || text.trim().isEmpty()) {
            JsonUtil.sendError(resp, 400, "请输入文字");
            return;
        }

        String result = AiUtil.polishText(text, action);
        JsonUtil.sendJson(resp, Map.of("success", true, "data", result));
    }
}
