package com.iblog.servlet.blog;

import com.iblog.dao.BlogDAO;
import com.iblog.dao.CommentDAO;
import com.iblog.model.Blog;
import com.iblog.model.Comment;
import com.iblog.util.JsonUtil;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class BlogDetailServlet extends HttpServlet {
    private final BlogDAO blogDAO = new BlogDAO();
    private final CommentDAO commentDAO = new CommentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            JsonUtil.sendError(resp, 400, "缺少博文ID");
            return;
        }

        int blogId = Integer.parseInt(idStr);
        Blog blog = blogDAO.findById(blogId);
        if (blog == null) {
            JsonUtil.sendError(resp, 404, "博文不存在");
            return;
        }

        blogDAO.incrementView(blogId);
        List<Comment> comments = commentDAO.findByBlogId(blogId);

        JsonUtil.sendJson(resp, Map.of("success", true, "data", Map.of("blog", blog, "comments", comments)));
    }
}
