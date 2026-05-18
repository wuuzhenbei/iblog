package com.iblog.servlet.user;

import com.iblog.dao.FollowDAO;
import com.iblog.dao.UserDAO;
import com.iblog.model.User;
import com.iblog.model.UserProfile;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserProfileServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final FollowDAO followDAO = new FollowDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userIdStr = req.getParameter("userId");
        int userId;
        if (userIdStr != null && !userIdStr.isEmpty()) {
            userId = Integer.parseInt(userIdStr);
        } else {
            userId = SessionUtil.getCurrentUserId(req);
        }
        if (userId == 0) {
            JsonUtil.sendError(resp, 400, "用户不存在");
            return;
        }

        User user = userDAO.findById(userId);
        UserProfile profile = userDAO.getProfile(userId);
        if (user == null || profile == null) {
            JsonUtil.sendError(resp, 404, "用户不存在");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", profile.getNickname());
        data.put("avatarUrl", profile.getAvatarUrl());
        data.put("gender", profile.getGender());
        data.put("birthday", profile.getBirthday());
        data.put("region", profile.getRegion());
        data.put("signature", profile.getSignature());
        data.put("interests", profile.getInterests());
        data.put("bgImageUrl", profile.getBgImageUrl());
        data.put("motto", profile.getMotto());
        data.put("level", profile.getLevel());
        data.put("points", profile.getPoints());
        data.put("followerCount", followDAO.countFollowers(userId));
        data.put("followingCount", followDAO.countFollowing(userId));

        int currentUserId = SessionUtil.getCurrentUserId(req);
        if (currentUserId > 0 && currentUserId != userId) {
            data.put("isFollowing", followDAO.isFollowing(currentUserId, userId));
        }

        JsonUtil.sendJson(resp, Map.of("success", true, "data", data));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) {
            JsonUtil.sendError(resp, 401, "请先登录");
            return;
        }

        String body = readBody(req);
        Map<String, String> params = JsonUtil.getMapper().readValue(body, Map.class);

        UserProfile profile = userDAO.getProfile(userId);
        if (profile == null) {
            JsonUtil.sendError(resp, 404, "用户资料不存在");
            return;
        }

        if (params.containsKey("nickname")) profile.setNickname(params.get("nickname"));
        if (params.containsKey("avatarUrl")) profile.setAvatarUrl(params.get("avatarUrl"));
        if (params.containsKey("gender")) profile.setGender(params.get("gender"));
        if (params.containsKey("birthday")) profile.setBirthday(java.sql.Date.valueOf(params.get("birthday")));
        if (params.containsKey("region")) profile.setRegion(params.get("region"));
        if (params.containsKey("signature")) profile.setSignature(params.get("signature"));
        if (params.containsKey("interests")) profile.setInterests(params.get("interests"));
        if (params.containsKey("bgImageUrl")) profile.setBgImageUrl(params.get("bgImageUrl"));
        if (params.containsKey("motto")) profile.setMotto(params.get("motto"));

        userDAO.updateProfile(profile);
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
