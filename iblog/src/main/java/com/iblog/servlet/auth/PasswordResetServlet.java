package com.iblog.servlet.auth;

import com.iblog.dao.UserDAO;
import com.iblog.model.User;
import com.iblog.util.AuthUtil;
import com.iblog.util.JsonUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class PasswordResetServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = readBody(req);
        Map<String, String> params = JsonUtil.getMapper().readValue(body, Map.class);

        String account = params.get("account");
        String phone = params.get("phone");
        String newPassword = params.get("newPassword");

        if (account == null || phone == null || newPassword == null) {
            JsonUtil.sendError(resp, 400, "参数不完整");
            return;
        }

        User user = userDAO.findByUsername(account);
        if (user == null) user = userDAO.findByPhone(account);
        if (user == null) {
            JsonUtil.sendError(resp, 404, "用户不存在");
            return;
        }

        if (!phone.equals(user.getPhone())) {
            JsonUtil.sendError(resp, 400, "手机号不匹配");
            return;
        }

        userDAO.updatePassword(user.getId(), AuthUtil.hashPassword(newPassword));
        JsonUtil.sendSuccess(resp);
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
}
