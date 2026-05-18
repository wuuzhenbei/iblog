<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="JavaMail 演示" />
</jsp:include>
<jsp:include page="/WEB-INF/jsp/common/nav.jsp" />
<div class="container">
    <h2>JavaMail 邮件发送演示</h2>

    <div class="card">
        <h3>JavaMail API 核心类</h3>
        <table>
            <thead>
                <tr>
                    <th>类名</th>
                    <th>说明</th>
                </tr>
            </thead>
            <tbody>
                <tr><td>javax.mail.Session</td><td>邮件会话，保存 SMTP 服务器配置（host/port/auth）</td></tr>
                <tr><td>javax.mail.Transport</td><td>邮件传输协议，负责发送邮件</td></tr>
                <tr><td>javax.mail.internet.MimeMessage</td><td>MIME 格式邮件消息，设置发件人/收件人/主题/正文</td></tr>
                <tr><td>javax.mail.Authenticator</td><td>认证器，提供 SMTP 登录的用户名和密码</td></tr>
            </tbody>
        </table>
    </div>

    <div class="card">
        <h3>发送测试邮件</h3>
        <p style="color:#999;margin-bottom:12px;">
            提示：需要先在 MailUtil.java 中配置 SMTP 信息（host/user/password）才能正常发送。
        </p>

        <form method="post">
            <div style="margin-bottom:12px;">
                <label><strong>收件人：</strong></label>
                <input type="email" name="to" required placeholder="recipient@example.com"
                       style="width:100%;padding:8px;border:1px solid #d9d9d9;border-radius:4px;margin-top:4px;">
            </div>
            <div style="margin-bottom:12px;">
                <label><strong>主题：</strong></label>
                <input type="text" name="subject" required placeholder="邮件主题"
                       style="width:100%;padding:8px;border:1px solid #d9d9d9;border-radius:4px;margin-top:4px;">
            </div>
            <div style="margin-bottom:12px;">
                <label><strong>正文（支持 HTML）：</strong></label>
                <textarea name="content" required placeholder="邮件正文内容"
                          style="width:100%;padding:8px;border:1px solid #d9d9d9;border-radius:4px;margin-top:4px;min-height:120px;"></textarea>
            </div>
            <div>
                <button type="submit" class="btn btn-primary">发送邮件</button>
            </div>
        </form>
    </div>

    <!-- 成功提示 -->
    <c:if test="${success}">
        <div class="card" style="border:1px solid #b7eb8f;background:#f6ffed;">
            <h3 style="color:#52c41a;">${message}</h3>
            <table>
                <tr><td><strong>收件人：</strong></td><td>${sentTo}</td></tr>
                <tr><td><strong>主题：</strong></td><td>${sentSubject}</td></tr>
            </table>
        </div>
    </c:if>

    <!-- 错误提示 -->
    <c:if test="${not empty error}">
        <div class="card" style="border:1px solid #ffccc7;background:#fff2f0;">
            <h3 style="color:#ff4d4f;">发送失败</h3>
            <p>${error}</p>
            <c:if test="${not empty mailDebugInfo}">
                <p style="color:#999;margin-top:8px;">${mailDebugInfo}</p>
            </c:if>
        </div>
    </c:if>

    <div class="card">
        <h3>核心代码示例</h3>
        <pre style="background:#f5f5f5;padding:16px;border-radius:4px;font-size:13px;overflow-x:auto;line-height:1.6;">
// 1. 创建 Session（配置 SMTP）
Properties props = new Properties();
props.put("mail.smtp.host", "smtp.qq.com");
props.put("mail.smtp.port", "587");
props.put("mail.smtp.auth", "true");
props.put("mail.smtp.starttls.enable", "true");

Session session = Session.getInstance(props, new Authenticator() {
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("user@qq.com", "授权码");
    }
});

// 2. 创建 MimeMessage
Message message = new MimeMessage(session);
message.setFrom(new InternetAddress("user@qq.com"));
message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
message.setSubject(subject);
message.setContent(content, "text/html;charset=UTF-8");

// 3. 发送
Transport.send(message);</pre>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
