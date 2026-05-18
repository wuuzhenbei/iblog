package com.iblog.servlet.admin;

import com.iblog.dao.BlogDAO;
import com.iblog.dao.UserDAO;
import com.iblog.util.PageBean;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminDashboardServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private BlogDAO blogDAO = new BlogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 查询统计数据
        int totalUsers = userDAO.countAll();
        int totalBlogs = blogDAO.countByStatus("published");
        int todayBlogs = blogDAO.countTodayPublished();
        // 获取最近注册的10个用户
        PageBean recentUsersPage = userDAO.findAllWithPage(1, 10);

        // 设置 request 属性
        req.setAttribute("totalUsers", totalUsers);
        req.setAttribute("totalBlogs", totalBlogs);
        req.setAttribute("todayBlogs", todayBlogs);
        req.setAttribute("recentUsers", recentUsersPage.getData());

        // 转发到 JSP
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp");
        dispatcher.forward(req, resp);
    }
}
