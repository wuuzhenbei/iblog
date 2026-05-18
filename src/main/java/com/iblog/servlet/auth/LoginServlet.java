package com.iblog.servlet.auth;

import com.iblog.dao.UserDAO;
import com.iblog.model.User;
import com.iblog.util.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class LoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = readBody(req);
        Map<String, String> params = JsonUtil.getMapper().readValue(body, Map.class);

        String account = params.get("account");
        String password = params.get("password");
        String remember = params.get("remember");

        if (account == null || password == null) {
            JsonUtil.sendError(resp, 400, "账号和密码不能为空");
            return;
        }

        User user = userDAO.findByUsername(account);
        if (user == null) user = userDAO.findByPhone(account);
        if (user == null || !AuthUtil.verifyPassword(password, user.getPasswordHash())) {
            JsonUtil.sendError(resp, 401, "账号或密码错误");
            return;
        }

        if (!"active".equals(user.getStatus())) {
            JsonUtil.sendError(resp, 403, "账号已被冻结或注销");
            return;
        }

        SessionUtil.setCurrentUser(req, user);
        userDAO.updateLastLoginIp(user.getId(), req.getRemoteAddr());

        if ("true".equals(remember)) {
            String token = UUID.randomUUID().toString();
            userDAO.updateRememberToken(user.getId(), token);
            Cookie cookie = new Cookie("remember_token", token);
            cookie.setMaxAge(30 * 24 * 60 * 60);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            resp.addCookie(cookie);
        }

        Map<String, Object> data = Map.of(
            "success", true,
            "user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole()
            )
        );
        JsonUtil.sendJson(resp, data);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("remember_token".equals(c.getName())) {
                    User user = userDAO.findByRememberToken(c.getValue());
                    if (user != null && "active".equals(user.getStatus())) {
                        SessionUtil.setCurrentUser(req, user);
                        JsonUtil.sendSuccess(resp, "user", Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole()));
                        return;
                    }
                }
            }
        }
        JsonUtil.sendError(resp, 401, "未登录");
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
