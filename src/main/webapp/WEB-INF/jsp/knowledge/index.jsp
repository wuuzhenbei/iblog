<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="知识点导航" />
</jsp:include>
<jsp:include page="/WEB-INF/jsp/common/nav.jsp" />
<div class="container">
    <h2>JavaWeb 知识点导航</h2>
    <p style="color:#666;margin-bottom:20px;">本模块展示 iBlog 项目中涉及的 14 个 JavaWeb 核心知识点，点击链接进入对应演示页面。</p>

    <div style="display:grid;grid-template-columns:repeat(2,1fr);gap:16px;">
        <!-- 知识点卡片 -->
        <div class="card" style="cursor:pointer;" onclick="location.href='${pageContext.request.contextPath}/knowledge/el-jstl'">
            <h3>#1 JSP & #6 EL & #7 JSTL</h3>
            <p>JSP 页面、EL 表达式（${}）、JSTL 标签（c:forEach / c:if / fmt:formatDate）</p>
            <span style="color:#1890ff;">查看演示 →</span>
        </div>

        <div class="card" style="cursor:pointer;" onclick="location.href='${pageContext.request.contextPath}/knowledge/session-cookie'">
            <h3>#8 Session & #9 Cookie</h3>
            <p>HttpSession 会话管理、Cookie 读写操作、sessionScope 与 cookie 隐式对象</p>
            <span style="color:#1890ff;">查看演示 →</span>
        </div>

        <div class="card" style="cursor:pointer;" onclick="location.href='${pageContext.request.contextPath}/knowledge/mvc'">
            <h3>#5 MVC 架构</h3>
            <p>Servlet(Controller) → DAO(Model) → JSP(View) 的分层架构说明</p>
            <span style="color:#1890ff;">查看演示 →</span>
        </div>

        <div class="card" style="cursor:pointer;" onclick="location.href='${pageContext.request.contextPath}/knowledge/scope'">
            <h3>#6 EL 四大作用域</h3>
            <p>pageScope / requestScope / sessionScope / applicationScope 演示</p>
            <span style="color:#1890ff;">查看演示 →</span>
        </div>

        <div class="card" style="cursor:pointer;" onclick="location.href='${pageContext.request.contextPath}/knowledge/javabean'">
            <h3>#4 JavaBean</h3>
            <p>jsp:useBean / jsp:setProperty / jsp:getProperty 标准动作演示</p>
            <span style="color:#1890ff;">查看演示 →</span>
        </div>

        <div class="card" style="cursor:pointer;" onclick="location.href='${pageContext.request.contextPath}/knowledge/fileupload'">
            <h3>#10 文件上传</h3>
            <p>Servlet 3.0 Part API + commons-fileupload 文件上传演示</p>
            <span style="color:#1890ff;">查看演示 →</span>
        </div>

        <div class="card" style="cursor:pointer;" onclick="location.href='${pageContext.request.contextPath}/knowledge/javamail'">
            <h3>#12 JavaMail</h3>
            <p>javax.mail 发送 HTML 邮件演示（注册确认 / 密码重置）</p>
            <span style="color:#1890ff;">查看演示 →</span>
        </div>

        <div class="card" style="cursor:pointer;" onclick="location.href='${pageContext.request.contextPath}/knowledge/filter'">
            <h3>#3 Filter</h3>
            <p>过滤器工作原理、生命周期、EncodingFilter/AuthFilter/AdminFilter 链式执行</p>
            <span style="color:#1890ff;">查看演示 →</span>
        </div>

        <div class="card" style="cursor:pointer;" onclick="location.href='${pageContext.request.contextPath}/blog/list'">
            <h3>#11 分页</h3>
            <p>PageBean + BlogDAO 分页查询 + JSP JSTL 分页导航演示</p>
            <span style="color:#1890ff;">查看演示 →</span>
        </div>

        <div class="card">
            <h3>#2 Servlet & #13 JDBC & #14 数据库</h3>
            <p>39 个 Servlet（@WebServlet + web.xml）、HikariCP + 纯 JDBC、MySQL 14+1 张表</p>
            <span style="color:#999;">已有实现，请在源代码中查看</span>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
