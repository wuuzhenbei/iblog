package com.iblog.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailUtil {
    // TODO: 用户需要配置自己的 SMTP 信息
    private static final String SMTP_HOST = "smtp.qq.com";
    private static final int SMTP_PORT = 587;
    private static final String SMTP_USER = "your_email@qq.com";      // TODO: 修改
    private static final String SMTP_PASS = "your_smtp_authorization_code"; // TODO: 修改
    private static final String FROM_NAME = "iBlog 智能社交博客平台";

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    // 异步发送确认邮件
    public static void sendConfirmationMail(String to, String username, String confirmUrl) {
        String subject = "【iBlog】注册确认";
        String content = buildConfirmationHtml(username, confirmUrl);
        sendAsync(to, subject, content);
    }

    // 异步发送密码重置邮件
    public static void sendPasswordResetMail(String to, String username, String resetUrl) {
        String subject = "【iBlog】密码重置";
        String content = buildResetHtml(username, resetUrl);
        sendAsync(to, subject, content);
    }

    // 异步发送
    private static void sendAsync(String to, String subject, String content) {
        executor.submit(() -> {
            try {
                sendMail(to, subject, content);
            } catch (Exception e) {
                System.err.println("邮件发送失败: " + e.getMessage());
            }
        });
    }

    // 同步发送邮件
    public static void sendMail(String to, String subject, String content) throws MessagingException, java.io.UnsupportedEncodingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASS);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SMTP_USER, FROM_NAME, "UTF-8"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(content, "text/html;charset=UTF-8");

        Transport.send(message);
    }

    private static String buildConfirmationHtml(String username, String confirmUrl) {
        return "<div style='font-family:sans-serif;padding:20px;'>" +
               "<h2>欢迎注册 iBlog！</h2>" +
               "<p>亲爱的 " + username + "，请点击以下链接完成注册：</p>" +
               "<a href='" + confirmUrl + "' style='display:inline-block;padding:10px 20px;background:#1890ff;color:#fff;text-decoration:none;border-radius:4px;'>确认注册</a>" +
               "<p style='color:#999;margin-top:20px;'>如果链接无法点击，请复制以下地址到浏览器：" + confirmUrl + "</p>" +
               "</div>";
    }

    private static String buildResetHtml(String username, String resetUrl) {
        return "<div style='font-family:sans-serif;padding:20px;'>" +
               "<h2>密码重置请求</h2>" +
               "<p>亲爱的 " + username + "，请点击以下链接重置密码：</p>" +
               "<a href='" + resetUrl + "' style='display:inline-block;padding:10px 20px;background:#ff4d4f;color:#fff;text-decoration:none;border-radius:4px;'>重置密码</a>" +
               "<p style='color:#999;margin-top:20px;'>如果这不是您的操作，请忽略此邮件。</p>" +
               "</div>";
    }
}
