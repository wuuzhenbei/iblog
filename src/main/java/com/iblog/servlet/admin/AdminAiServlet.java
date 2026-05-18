package com.iblog.servlet.admin;

import com.iblog.util.JsonUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdminAiServlet extends HttpServlet {
    private static final AtomicBoolean aiEnabled = new AtomicBoolean(true);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonUtil.sendJson(resp, Map.of("success", true, "enabled", aiEnabled.get()));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        boolean enabled = "true".equals(params.get("enabled"));
        aiEnabled.set(enabled);
        JsonUtil.sendJson(resp, Map.of("success", true, "enabled", enabled));
    }

    public static boolean isAiEnabled() {
        return aiEnabled.get();
    }
}
