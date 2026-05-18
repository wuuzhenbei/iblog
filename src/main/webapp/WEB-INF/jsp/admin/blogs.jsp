<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="博文管理" />
</jsp:include>
<jsp:include page="/WEB-INF/jsp/common/nav.jsp" />
<div class="container">
    <h2>博文管理</h2>

    <div class="card">
        <c:if test="${empty pageBean.data}">
            <p style="text-align:center;color:#999;padding:40px 0;">暂无博文</p>
        </c:if>

        <c:if test="${not empty pageBean.data}">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>内容</th>
                        <th>作者</th>
                        <th>状态</th>
                        <th>点赞</th>
                        <th>评论</th>
                        <th>发布时间</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${pageBean.data}" var="blog">
                        <tr>
                            <td>${blog.id}</td>
                            <td>${blog.content}</td>
                            <td>${blog.nickname}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${blog.status == 'published'}">
                                        <span class="tag tag-green">已发布</span>
                                    </c:when>
                                    <c:when test="${blog.status == 'draft'}">
                                        <span class="tag tag-gray">草稿</span>
                                    </c:when>
                                    <c:when test="${blog.status == 'deleted'}">
                                        <span class="tag tag-red">已删除</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="tag tag-blue">${blog.status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${blog.likeCount}</td>
                            <td>${blog.commentCount}</td>
                            <td><fmt:formatDate value="${blog.createdAt}" pattern="yyyy-MM-dd HH:mm" /></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <!-- 分页导航 -->
            <div class="pagination">
                <c:if test="${pageBean.hasPrevious()}">
                    <c:url value="/admin/blogs" var="prevUrl">
                        <c:param name="page" value="${pageBean.curPage - 1}" />
                    </c:url>
                    <a href="${prevUrl}">上一页</a>
                </c:if>

                <c:forEach begin="1" end="${pageBean.totalPages}" var="i">
                    <c:if test="${i == pageBean.curPage}">
                        <span class="current">${i}</span>
                    </c:if>
                    <c:if test="${i != pageBean.curPage}">
                        <c:url value="/admin/blogs" var="pageUrl">
                            <c:param name="page" value="${i}" />
                        </c:url>
                        <a href="${pageUrl}">${i}</a>
                    </c:if>
                </c:forEach>

                <c:if test="${pageBean.hasNext()}">
                    <c:url value="/admin/blogs" var="nextUrl">
                        <c:param name="page" value="${pageBean.curPage + 1}" />
                    </c:url>
                    <a href="${nextUrl}">下一页</a>
                </c:if>
            </div>
        </c:if>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
