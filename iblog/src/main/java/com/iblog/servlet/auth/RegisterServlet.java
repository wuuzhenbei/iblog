package com.iblog.servlet.auth;

import com.iblog.dao.UserDAO;
import com.iblog.model.User;
import com.iblog.util.AuthUtil;
import com.iblog.util.JsonUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class RegisterServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = readBody(req);
        Map<String, String> params = JsonUtil.getMapper().readValue(body, Map.class);

        String username = params.get("username");
        String phone = params.get("phone");
        String password = params.get("password");

        if (username == null || username.trim().isEmpty()) {
            JsonUtil.sendError(resp, 400, "用户名不能为空");
            return;
        }
        if (password == null || password.length() < 6) {
            JsonUtil.sendError(resp, 400, "密码至少6位");
            return;
        }
        if (userDAO.findByUsername(username) != null) {
            JsonUtil.sendError(resp, 400, "用户名已存在");
            return;
        }
        if (phone != null && !phone.isEmpty() && userDAO.findByPhone(phone) != null) {
            JsonUtil.sendError(resp, 400, "手机号已注册");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPasswordHash(AuthUtil.hashPassword(password));

        int userId = userDAO.insert(user);
        if (userId > 0) {
            user.setId(userId);
            userDAO.insertProfile(userId, username);
            userDAO.insertPrivacy(userId);
            JsonUtil.sendSuccess(resp, "userId", userId);
        } else {
            JsonUtil.sendError(resp, 500, "注册失败");
        }
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
