package com.iblog.servlet.chat;

import com.iblog.dao.MessageDAO;
import com.iblog.model.Message;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MessageServlet extends HttpServlet {
    private final MessageDAO messageDAO = new MessageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        String targetIdStr = req.getParameter("targetId");
        if (targetIdStr == null) { JsonUtil.sendError(resp, 400, "缺少目标用户ID"); return; }

        int targetId = Integer.parseInt(targetIdStr);
        int page = getIntParam(req, "page", 1);
        int size = getIntParam(req, "size", 20);

        messageDAO.markAsRead(targetId, userId);
        List<Message> messages = messageDAO.getConversation(userId, targetId, page, size);
        JsonUtil.sendJson(resp, Map.of("success", true, "data", messages));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        int receiverId = Integer.parseInt(params.get("receiverId"));
        String content = params.get("content");
        if (content == null || content.trim().isEmpty()) {
            JsonUtil.sendError(resp, 400, "消息不能为空");
            return;
        }

        Message msg = new Message();
        msg.setSenderId(userId);
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        msg.setContentType(params.getOrDefault("contentType", "text"));

        int msgId = messageDAO.insert(msg);
        JsonUtil.sendSuccess(resp, "messageId", msgId);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        String idStr = req.getParameter("id");
        if (idStr == null) { JsonUtil.sendError(resp, 400, "缺少ID"); return; }
        messageDAO.recall(Integer.parseInt(idStr), userId);
        JsonUtil.sendSuccess(resp);
    }

    private int getIntParam(HttpServletRequest req, String name, int defaultVal) {
        String val = req.getParameter(name);
        if (val == null || val.isEmpty()) return defaultVal;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return defaultVal; }
    }
}
