package com.iblog.servlet.admin;

import com.iblog.dao.BlogDAO;
import com.iblog.util.PageBean;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminBlogJspServlet extends HttpServlet {

    private BlogDAO blogDAO = new BlogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取分页参数
        int page = 1;
        String pageStr = req.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int pageSize = 10;

        // 查询所有博文（不限状态）
        PageBean pageBean = blogDAO.findAllWithPage(page, pageSize);

        req.setAttribute("pageBean", pageBean);

        // 转发到 JSP
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/admin/blogs.jsp");
        dispatcher.forward(req, resp);
    }
}
