<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="文件上传演示" />
</jsp:include>
<jsp:include page="/WEB-INF/jsp/common/nav.jsp" />
<div class="container">
    <h2>文件上传演示</h2>

    <div class="card">
        <h3>Servlet 3.0 Part API — 文件上传</h3>
        <p>使用 <code>@MultipartConfig</code> 注解 + <code>request.getPart("file")</code> API</p>

        <form method="post" enctype="multipart/form-data" style="margin-top:16px;">
            <div style="margin-bottom:12px;">
                <label><strong>选择文件：</strong></label>
                <input type="file" name="file" required style="margin-left:8px;">
            </div>
            <div>
                <button type="submit" class="btn btn-primary">上传文件</button>
            </div>
        </form>
    </div>

    <!-- 错误提示 -->
    <c:if test="${not empty error}">
        <div class="card" style="border:1px solid #ffccc7;background:#fff2f0;">
            <h3 style="color:#ff4d4f;">上传失败</h3>
            <p>${error}</p>
        </div>
    </c:if>

    <!-- 上传成功展示 -->
    <c:if test="${uploadSuccess}">
        <div class="card" style="border:1px solid #b7eb8f;background:#f6ffed;">
            <h3 style="color:#52c41a;">上传成功</h3>
            <table>
                <tr>
                    <td><strong>原始文件名：</strong></td>
                    <td>${originalFileName}</td>
                </tr>
                <tr>
                    <td><strong>文件大小：</strong></td>
                    <td>
                        <c:choose>
                            <c:when test="${fileSize >= 1048576}">
                                <fmt:formatNumber value="${fileSize / 1048576}" pattern="#.##" /> MB
                            </c:when>
                            <c:when test="${fileSize >= 1024}">
                                <fmt:formatNumber value="${fileSize / 1024}" pattern="#.##" /> KB
                            </c:when>
                            <c:otherwise>
                                ${fileSize} 字节
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <td><strong>文件类型：</strong></td>
                    <td>${contentType}</td>
                </tr>
                <tr>
                    <td><strong>保存路径：</strong></td>
                    <td><code>${savedPath}</code></td>
                </tr>
            </table>

            <c:if test="${contentType.startsWith('image/')}">
                <div style="margin-top:12px;">
                    <p><strong>图片预览：</strong></p>
                    <img src="${pageContext.request.contextPath}${savedPath}" alt="上传的图片" style="max-width:400px;max-height:300px;border:1px solid #d9d9d9;border-radius:4px;">
                </div>
            </c:if>
        </div>
    </c:if>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
