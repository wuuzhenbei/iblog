package com.iblog.servlet.blog;

import com.iblog.dao.BlogDAO;
import com.iblog.util.PageBean;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/blog/list")
public class BlogListJspServlet extends HttpServlet {

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

        // 查询分页数据
        PageBean pageBean = blogDAO.findFeedWithPage(page, pageSize);

        // 设置 request 属性
        req.setAttribute("pageBean", pageBean);

        // 转发到 JSP
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/blog/list.jsp");
        dispatcher.forward(req, resp);
    }
}
