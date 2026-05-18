<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="JavaBean 演示" />
</jsp:include>
<jsp:include page="/WEB-INF/jsp/common/nav.jsp" />
<div class="container">
    <h2>JavaBean 演示</h2>

    <div class="card">
        <h3>什么是 JavaBean？</h3>
        <p>JavaBean 是一种遵循特定规范的 Java 类：</p>
        <ul style="margin:12px 0;padding-left:20px;line-height:1.8;">
            <li>有无参构造器（空构造方法）</li>
            <li>属性有 getter / setter 方法</li>
            <li>可序列化（implements Serializable，可选）</li>
        </ul>
        <p>iBlog 中的 JavaBean 示例：User.java、Blog.java、UserProfile.java（共 9 个 model 类）</p>
    </div>

    <div class="card">
        <h3>&lt;jsp:useBean&gt; 示例</h3>
        <p>创建或获取一个 JavaBean 实例：</p>
        <pre style="background:#f5f5f5;padding:12px;border-radius:4px;font-size:13px;">
&lt;jsp:useBean id="now" class="java.util.Date" scope="page" /&gt;
&lt;jsp:useBean id="user" class="com.iblog.model.User" scope="session" /&gt;</pre>

        <jsp:useBean id="now" class="java.util.Date" scope="page" />
        <p>当前时间（通过 jsp:useBean 创建的 Date 实例）：${now}</p>

        <c:if test="${empty sessionScope.currentUser}">
            <p style="color:#999;">Session 中无 User 对象（未登录）</p>
        </c:if>
        <c:if test="${not empty sessionScope.currentUser}">
            <p>当前用户（通过 jsp:useBean 读取的 Session 中的 User）：${sessionScope.currentUser.username}</p>
        </c:if>
    </div>

    <div class="card">
        <h3>&lt;jsp:setProperty&gt; 示例</h3>
        <p>设置 JavaBean 属性（可自动从请求参数填充）：</p>
        <pre style="background:#f5f5f5;padding:12px;border-radius:4px;font-size:13px;">
&lt;%-- 自动匹配请求参数填充属性 --%&gt;
&lt;jsp:setProperty name="user" property="*" /&gt;

&lt;%-- 手动设置属性 --%&gt;
&lt;jsp:setProperty name="user" property="username" value="test_user" /&gt;</pre>

        <p style="color:#999;">property="*" 会自动将请求参数名与 JavaBean 属性名匹配并赋值。</p>
    </div>

    <div class="card">
        <h3>&lt;jsp:getProperty&gt; 示例</h3>
        <p>读取 JavaBean 属性并输出：</p>
        <pre style="background:#f5f5f5;padding:12px;border-radius:4px;font-size:13px;">
&lt;jsp:getProperty name="now" property="time" /&gt;
&lt;jsp:getProperty name="user" property="username" /&gt;</pre>

        <p>now.time = <jsp:getProperty name="now" property="time" /></p>

        <c:if test="${not empty sessionScope.currentUser}">
            <p>user.username = ${sessionScope.currentUser.username}</p>
        </c:if>
    </div>

    <div class="card">
        <h3>JavaBean 规范对照表</h3>
        <table>
            <thead>
                <tr>
                    <th>规范</th>
                    <th>说明</th>
                    <th>iBlog 示例</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>无参构造</td>
                    <td>必须有一个 public 无参构造器</td>
                    <td>User.java、Blog.java 等均有默认构造器</td>
                </tr>
                <tr>
                    <td>属性封装</td>
                    <td>属性 private，通过 getter/setter 访问</td>
                    <td>如 User.getId() / User.setId()</td>
                </tr>
                <tr>
                    <td>命名规范</td>
                    <td>boolean 属性用 isXxx()，其他用 getXxx()</td>
                    <td>Blog.isLiked() / Blog.setLiked()</td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
