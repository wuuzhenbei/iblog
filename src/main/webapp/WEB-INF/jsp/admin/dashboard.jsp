<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="仪表盘" />
</jsp:include>
<jsp:include page="/WEB-INF/jsp/common/nav.jsp" />
<div class="container">
    <h2>仪表盘</h2>

    <!-- 统计卡片 -->
    <div class="stat-cards">
        <div class="stat-card">
            <div class="number">${totalUsers}</div>
            <div class="label">用户总数</div>
        </div>
        <div class="stat-card">
            <div class="number">${totalBlogs}</div>
            <div class="label">博文总数</div>
        </div>
        <div class="stat-card">
            <div class="number">${todayBlogs}</div>
            <div class="label">今日发布</div>
        </div>
    </div>

    <!-- 最近用户列表 -->
    <div class="card">
        <h3>最近注册用户</h3>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>用户名</th>
                    <th>角色</th>
                    <th>状态</th>
                    <th>注册时间</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${recentUsers}" var="user">
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>
                            <c:choose>
                                <c:when test="${user.role == 'super_admin'}">
                                    <span class="tag tag-red">超级管理员</span>
                                </c:when>
                                <c:when test="${user.role == 'admin'}">
                                    <span class="tag tag-blue">管理员</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="tag tag-gray">普通用户</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${user.status == 'active'}">
                                    <span class="tag tag-green">正常</span>
                                </c:when>
                                <c:when test="${user.status == 'banned'}">
                                    <span class="tag tag-red">封禁</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="tag tag-gray">${user.status}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td><fmt:formatDate value="${user.createdAt}" pattern="yyyy-MM-dd HH:mm" /></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
