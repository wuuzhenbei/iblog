package com.iblog.servlet.blog;

import com.iblog.dao.BlogDAO;
import com.iblog.dao.CommentDAO;
import com.iblog.model.Comment;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class CommentServlet extends HttpServlet {
    private final CommentDAO commentDAO = new CommentDAO();
    private final BlogDAO blogDAO = new BlogDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) { String line; while ((line = reader.readLine()) != null) sb.append(line); }
        Map<String, String> params = JsonUtil.getMapper().readValue(sb.toString(), Map.class);

        int blogId = Integer.parseInt(params.get("blogId"));
        String content = params.get("content");
        if (content == null || content.trim().isEmpty()) {
            JsonUtil.sendError(resp, 400, "评论不能为空");
            return;
        }

        Comment comment = new Comment();
        comment.setBlogId(blogId);
        comment.setUserId(userId);
        comment.setContent(content);

        if (params.containsKey("parentId") && !params.get("parentId").isEmpty()) {
            comment.setParentId(Integer.parseInt(params.get("parentId")));
        }
        if (params.containsKey("replyToUserId") && !params.get("replyToUserId").isEmpty()) {
            comment.setReplyToUserId(Integer.parseInt(params.get("replyToUserId")));
        }

        int commentId = commentDAO.insert(comment);
        if (commentId > 0) {
            blogDAO.updateCommentCount(blogId, 1);
            JsonUtil.sendSuccess(resp, "commentId", commentId);
        } else {
            JsonUtil.sendError(resp, 500, "评论失败");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        String idStr = req.getParameter("id");
        if (idStr == null) { JsonUtil.sendError(resp, 400, "缺少ID"); return; }

        Comment comment = commentDAO.findById(Integer.parseInt(idStr));
        if (comment == null || comment.getUserId() != userId) {
            JsonUtil.sendError(resp, 403, "无权删除");
            return;
        }

        commentDAO.delete(comment.getId(), userId);
        blogDAO.updateCommentCount(comment.getBlogId(), -1);
        JsonUtil.sendSuccess(resp);
    }
}
