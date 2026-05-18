<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="MVC 架构说明" />
</jsp:include>
<jsp:include page="/WEB-INF/jsp/common/nav.jsp" />
<div class="container">
    <h2>MVC 架构模式说明</h2>

    <div class="card">
        <h3>什么是 MVC？</h3>
        <p>MVC（Model-View-Controller）是一种软件架构模式，将应用程序分为三个核心组件：</p>
        <ul style="margin:16px 0;padding-left:20px;line-height:2;">
            <li><strong>Model（模型）</strong> — 数据和业务逻辑（JavaBean / DAO）</li>
            <li><strong>View（视图）</strong> — 用户界面（JSP）</li>
            <li><strong>Controller（控制器）</strong> — 请求分发和流程控制（Servlet）</li>
        </ul>
    </div>

    <div class="card">
        <h3>iBlog 中的 MVC 实现</h3>
        <table>
            <thead>
                <tr>
                    <th>层次</th>
                    <th>技术</th>
                    <th>职责</th>
                    <th>示例类</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>Controller</strong></td>
                    <td>Servlet</td>
                    <td>接收请求、解析参数、调用 Model、转发到 View</td>
                    <td>LoginServlet, BlogListJspServlet, AdminDashboardServlet</td>
                </tr>
                <tr>
                    <td><strong>Model</strong></td>
                    <td>JavaBean + DAO + JDBC</td>
                    <td>封装数据、业务逻辑、数据库访问</td>
                    <td>User.java, UserDAO.java, BlogDAO.java</td>
                </tr>
                <tr>
                    <td><strong>View</strong></td>
                    <td>JSP + EL + JSTL</td>
                    <td>展示数据、渲染 HTML 页面</td>
                    <td>dashboard.jsp, list.jsp, el-jstl.jsp</td>
                </tr>
            </tbody>
        </table>
    </div>

    <div class="card">
        <h3>请求处理流程图</h3>
        <pre style="background:#f5f5f5;padding:20px;border-radius:4px;overflow-x:auto;font-size:13px;line-height:1.8;">
┌──────────┐     HTTP 请求      ┌────────────┐     调用      ┌───────────┐
│  浏览器   │ ────────────────→  │ Controller  │ ──────────→  │   Model    │
│  Client   │                    │  (Servlet)  │              │  (JavaBean)│
└──────────┘                    └────────────┘              └─────┬─────┘
      ↑                              │                            │
      │                              │ 设置 Model 数据到          │ 数据库查询
      │                              │ request.setAttribute()      │
      │                              ↓                            ↓
      │                         ┌────────────┐              ┌──────────┐
      └──  HTML 响应 ──────────  │    View    │  forward     │ Database │
                                 │   (JSP)    │ ←──────────  │ (MySQL)  │
                                 └────────────┘              └──────────┘
        </pre>
        <p><strong>流程说明：</strong></p>
        <ol style="margin:12px 0;padding-left:20px;line-height:1.8;">
            <li>浏览器发送 HTTP 请求到服务器</li>
            <li>Servlet（Controller）接收请求，解析参数</li>
            <li>Servlet 调用 DAO（Model）查询数据库</li>
            <li>DAO 将查询结果封装为 JavaBean 对象返回</li>
            <li>Servlet 将 JavaBean 通过 setAttribute() 存入 request 作用域</li>
            <li>Servlet 通过 forward() 将请求转发到 JSP（View）</li>
            <li>JSP 使用 EL + JSTL 读取数据，生成 HTML</li>
            <li>HTML 响应返回给浏览器</li>
        </ol>
    </div>

    <div class="card">
        <h3>iBlog 典型请求示例</h3>
        <p><strong>场景：</strong>访问管理后台仪表盘 <code>GET /admin/dashboard</code></p>
        <pre style="background:#f5f5f5;padding:16px;border-radius:4px;font-size:13px;line-height:1.6;">
1. 浏览器请求 GET /admin/dashboard
2. EncodingFilter → LoggingFilter 处理（Filter 链）
3. AdminDashboardServlet.doGet() 接收请求（Controller）
4. 调用 UserDAO.countAll() + BlogDAO.countByStatus()（Model）
5. 结果存入 request.setAttribute("totalUsers", ...) 
6. forward 到 /WEB-INF/jsp/admin/dashboard.jsp（View）
7. JSP 使用 ${totalUsers} 展示数据
8. 返回 HTML 给浏览器
        </pre>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
