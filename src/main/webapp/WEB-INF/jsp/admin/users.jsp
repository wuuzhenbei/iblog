<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="用户管理" />
</jsp:include>
<jsp:include page="/WEB-INF/jsp/common/nav.jsp" />
<div class="container">
    <h2>用户管理</h2>

    <div class="card">
        <c:if test="${empty pageBean.data}">
            <p style="text-align:center;color:#999;padding:40px 0;">暂无用户</p>
        </c:if>

        <c:if test="${not empty pageBean.data}">
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
                    <c:forEach items="${pageBean.data}" var="user">
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

            <!-- 分页导航 -->
            <div class="pagination">
                <c:if test="${pageBean.hasPrevious()}">
                    <c:url value="/admin/users" var="prevUrl">
                        <c:param name="page" value="${pageBean.curPage - 1}" />
                    </c:url>
                    <a href="${prevUrl}">上一页</a>
                </c:if>

                <c:forEach begin="1" end="${pageBean.totalPages}" var="i">
                    <c:if test="${i == pageBean.curPage}">
                        <span class="current">${i}</span>
                    </c:if>
                    <c:if test="${i != pageBean.curPage}">
                        <c:url value="/admin/users" var="pageUrl">
                            <c:param name="page" value="${i}" />
                        </c:url>
                        <a href="${pageUrl}">${i}</a>
                    </c:if>
                </c:forEach>

                <c:if test="${pageBean.hasNext()}">
                    <c:url value="/admin/users" var="nextUrl">
                        <c:param name="page" value="${pageBean.curPage + 1}" />
                    </c:url>
                    <a href="${nextUrl}">下一页</a>
                </c:if>
            </div>
        </c:if>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
