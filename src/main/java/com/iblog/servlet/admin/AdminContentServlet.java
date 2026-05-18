package com.iblog.servlet.admin;

import com.iblog.dao.BlogDAO;
import com.iblog.util.JsonUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class AdminContentServlet extends HttpServlet {
    private final BlogDAO blogDAO = new BlogDAO();

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        int blogId = Integer.parseInt(params.get("id"));
        blogDAO.adminDelete(blogId);
        JsonUtil.sendSuccess(resp);
    }
}
