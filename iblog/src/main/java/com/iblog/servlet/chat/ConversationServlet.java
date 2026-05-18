package com.iblog.servlet.chat;

import com.iblog.dao.MessageDAO;
import com.iblog.model.Message;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConversationServlet extends HttpServlet {
    private final MessageDAO messageDAO = new MessageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        List<Message> conversations = messageDAO.getConversations(userId);
        int unread = messageDAO.countUnread(userId);
        JsonUtil.sendJson(resp, Map.of("success", true, "data", conversations, "unreadCount", unread));
    }
}
