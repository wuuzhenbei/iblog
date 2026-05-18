package com.iblog.servlet.demo;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class KnowledgeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        switch (path) {
            case "/knowledge/el-jstl":
                // 设置 EL + JSTL 演示所需的数据
                req.setAttribute("user", createDemoUser());
                req.setAttribute("list", Arrays.asList("Java", "Servlet", "JSP", "JSTL"));
                req.setAttribute("now", new Date());
                req.setAttribute("price", 128.5);
                forward(req, resp, "/WEB-INF/jsp/knowledge/el-jstl.jsp");
                break;

            case "/knowledge/session-cookie":
                // Session/Cookie 数据已由 EL 直接从 sessionScope/cookie 读取
                forward(req, resp, "/WEB-INF/jsp/knowledge/session-cookie.jsp");
                break;

            case "/knowledge/mvc":
                forward(req, resp, "/WEB-INF/jsp/knowledge/mvc.jsp");
                break;

            case "/knowledge/scope":
                forward(req, resp, "/WEB-INF/jsp/knowledge/scope.jsp");
                break;

            case "/knowledge/javabean":
                forward(req, resp, "/WEB-INF/jsp/knowledge/javabean.jsp");
                break;

            default: // /knowledge
                forward(req, resp, "/WEB-INF/jsp/knowledge/index.jsp");
                break;
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String jspPath) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher(jspPath);
        dispatcher.forward(req, resp);
    }

    /**
     * 创建一个演示用的 User JavaBean 对象<br>
     * 用于 EL 表达式演示（${user.username}）和 JSTL 演示
     */
    public static Map<String, Object> createDemoUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("username", "demo_user");
        user.put("role", "admin");
        user.put("email", "demo@iblog.com");
        return user;
    }
}
