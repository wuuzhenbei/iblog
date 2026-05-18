<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Session + Cookie 演示" />
</jsp:include>
<jsp:include page="/WEB-INF/jsp/common/nav.jsp" />
<div class="container">
    <h2>Session + Cookie 演示</h2>

    <!-- Session 演示 -->
    <div class="card">
        <h3>Session（HttpSession）</h3>
        <p><strong>Session ID：</strong>${pageContext.session.id}</p>
        <p><strong>当前登录用户：</strong>${sessionScope.currentUser.username}（${sessionScope.currentUser.role}）</p>

        <h4 style="margin-top:20px;">Session 属性操作示例</h4>
        <c:if test="${not empty sessionScope.currentUser}">
            <p>✓ 用户已登录，username = ${sessionScope.currentUser.username}</p>
            <p>✓ role = ${sessionScope.currentUser.role}</p>
        </c:if>
        <c:if test="${empty sessionScope.currentUser}">
            <p style="color:#999;">当前未登录，Session 中无 currentUser 属性</p>
        </c:if>

        <h4 style="margin-top:20px;">Session 作用域读取演示</h4>
        <p>通过 \${sessionScope.currentUser} 直接读取 Session 中的属性。</p>
        <p>也可通过 \${sessionScope.getAttribute("currentUser")} 方式读取。</p>
    </div>

    <!-- Cookie 演示 -->
    <div class="card">
        <h3>Cookie 操作演示</h3>
        <p><strong>remember_token Cookie 名称：</strong>${cookie.remember_token.name}</p>
        <p><strong>remember_token Cookie 值：</strong>${cookie.remember_token.value}</p>

        <c:if test="${empty cookie.remember_token}">
            <p style="color:#999;">当前无 remember_token Cookie</p>
        </c:if>

        <h4 style="margin-top:20px;">所有 Cookie 列表</h4>
        <table>
            <thead>
                <tr>
                    <th>Cookie 名称</th>
                    <th>Cookie 值</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${cookie}" var="c">
                    <tr>
                        <td>${c.key}</td>
                        <td>${c.value.value}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty cookie}">
                    <tr><td colspan="2" style="text-align:center;color:#999;">无 Cookie</td></tr>
                </c:if>
            </tbody>
        </table>

        <h4 style="margin-top:20px;">EL 读取 Cookie 示例</h4>
        <p>\${cookie.remember_token.name} = ${cookie.remember_token.name}</p>
        <p>\${cookie.remember_token.value} = ${cookie.remember_token.value}</p>
        <p>\${cookie.JSESSIONID.name} = ${cookie.JSESSIONID.name}</p>
        <p>\${cookie.JSESSIONID.value} = ${cookie.JSESSIONID.value}</p>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
