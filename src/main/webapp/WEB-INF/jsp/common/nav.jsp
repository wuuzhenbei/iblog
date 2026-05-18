<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav style="background:#001529;padding:0 20px;display:flex;align-items:center;height:48px;">
    <a href="${pageContext.request.contextPath}/admin/dashboard" style="color:#fff;text-decoration:none;font-size:18px;font-weight:bold;margin-right:40px;">iBlog 管理后台</a>
    <div style="display:flex;gap:20px;">
        <a href="${pageContext.request.contextPath}/admin/dashboard" style="color:rgba(255,255,255,0.85);text-decoration:none;">仪表盘</a>
        <a href="${pageContext.request.contextPath}/admin/blogs" style="color:rgba(255,255,255,0.85);text-decoration:none;">博文管理</a>
        <a href="${pageContext.request.contextPath}/admin/users" style="color:rgba(255,255,255,0.85);text-decoration:none;">用户管理</a>
        <a href="${pageContext.request.contextPath}/knowledge" style="color:rgba(255,255,255,0.85);text-decoration:none;">知识点展示</a>
    </div>
    <div style="margin-left:auto;color:rgba(255,255,255,0.65);">
        <c:if test="${not empty sessionScope.currentUser}">
            ${sessionScope.currentUser.username} (${sessionScope.currentUser.role})
        </c:if>
    </div>
</nav>
