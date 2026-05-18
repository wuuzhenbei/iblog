package com.iblog.servlet.admin;

import com.iblog.dao.UserDAO;
import com.iblog.util.PageBean;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/users")
public class AdminUserJspServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

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
        PageBean pageBean = userDAO.findAllWithPage(page, pageSize);

        req.setAttribute("pageBean", pageBean);

        // 转发到 JSP
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp");
        dispatcher.forward(req, resp);
    }
}
