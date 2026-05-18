package com.iblog.servlet.demo;

import com.iblog.util.MailUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JavaMailDemoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/knowledge/javamail.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String to = req.getParameter("to");
        String subject = req.getParameter("subject");
        String content = req.getParameter("content");

        // 参数校验
        if (to == null || to.trim().isEmpty()) {
            req.setAttribute("error", "请输入收件人邮箱地址");
            forward(req, resp);
            return;
        }
        if (subject == null || subject.trim().isEmpty()) {
            req.setAttribute("error", "请输入邮件主题");
            forward(req, resp);
            return;
        }
        if (content == null || content.trim().isEmpty()) {
            req.setAttribute("error", "请输入邮件正文");
            forward(req, resp);
            return;
        }

        try {
            // 同步发送邮件（演示用）
            MailUtil.sendMail(to.trim(), subject.trim(), content.trim());

            req.setAttribute("success", true);
            req.setAttribute("message", "邮件发送成功！");
            req.setAttribute("sentTo", to.trim());
            req.setAttribute("sentSubject", subject.trim());

        } catch (Exception e) {
            req.setAttribute("error", "邮件发送失败：" + e.getMessage());
            req.setAttribute("mailDebugInfo", "请检查 MailUtil.java 中的 SMTP 配置（host/user/password）是否正确");
        }

        forward(req, resp);
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/knowledge/javamail.jsp");
        dispatcher.forward(req, resp);
    }
}
