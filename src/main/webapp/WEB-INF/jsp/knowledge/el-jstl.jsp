<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="EL + JSTL 演示" />
</jsp:include>
<jsp:include page="/WEB-INF/jsp/common/nav.jsp" />
<div class="container">
    <h2>EL 表达式 + JSTL 标签演示</h2>

    <!-- EL 算术运算 -->
    <div class="card">
        <h3>EL 算术运算</h3>
        <table>
            <tr><td>\${1 + 1}</td><td>=</td><td>${1 + 1}</td></tr>
            <tr><td>\${10 - 3}</td><td>=</td><td>${10 - 3}</td></tr>
            <tr><td>\${6 * 7}</td><td>=</td><td>${6 * 7}</td></tr>
            <tr><td>\${20 / 4}</td><td>=</td><td>${20 / 4}</td></tr>
            <tr><td>\${10 % 3}</td><td>=</td><td>${10 % 3}</td></tr>
        </table>
    </div>

    <!-- EL 空值判断 -->
    <div class="card">
        <h3>EL empty 运算符</h3>
        <c:set var="demoList" value="${list}" />
        <p>\${empty list} = ${empty list}（list 不为空所以是 false）</p>
        <p>\${empty nullValue} = ${empty null}（null 为空所以是 true）</p>
        <p>\${empty ""} = ${empty ""}（空字符串为空所以是 true）</p>
    </div>

    <!-- EL 请求参数 -->
    <div class="card">
        <h3>EL 隐式对象 - param</h3>
        <p>\${param.page} = ${param.page}</p>
        <p>提示：在 URL 后加 ?page=2 可看到效果</p>
    </div>

    <!-- EL Cookie -->
    <div class="card">
        <h3>EL 隐式对象 - cookie</h3>
        <p>\${cookie.remember_token.name} = ${cookie.remember_token.name}</p>
        <p>\${cookie.remember_token.value} = ${cookie.remember_token.value}</p>
        <c:if test="${empty cookie.remember_token}">
            <p style="color:#999;">（当前无 remember_token Cookie）</p>
        </c:if>
    </div>

    <!-- JSTL c:set -->
    <div class="card">
        <h3>JSTL &lt;c:set&gt;</h3>
        <c:set var="greeting" value="Hello, iBlog!" scope="page" />
        <p>greeting = ${greeting}</p>
        <c:set var="score" value="85" />
        <p>score = ${score}</p>
    </div>

    <!-- JSTL c:if -->
    <div class="card">
        <h3>JSTL &lt;c:if&gt;</h3>
        <c:if test="${score >= 60}">
            <p style="color:#52c41a;">✓ 成绩及格（score = ${score} >= 60）</p>
        </c:if>
        <c:if test="${score < 60}">
            <p style="color:#ff4d4f;">✗ 成绩不及格</p>
        </c:if>
    </div>

    <!-- JSTL c:choose -->
    <div class="card">
        <h3>JSTL &lt;c:choose&gt;</h3>
        <c:set var="level" value="B" />
        <c:choose>
            <c:when test="${level == 'A'}">优秀</c:when>
            <c:when test="${level == 'B'}">良好</c:when>
            <c:when test="${level == 'C'}">及格</c:when>
            <c:otherwise>不及格</c:otherwise>
        </c:choose>
    </div>

    <!-- JSTL c:forEach -->
    <div class="card">
        <h3>JSTL &lt;c:forEach&gt;</h3>
        <ul>
            <c:forEach items="${list}" var="item" varStatus="vs">
                <li>${vs.index}. ${item}</li>
            </c:forEach>
        </ul>
    </div>

    <!-- JSTL c:url -->
    <div class="card">
        <h3>JSTL &lt;c:url&gt;</h3>
        <c:url value="/knowledge" var="knowledgeUrl" />
        <p>URL 重写结果：<a href="${knowledgeUrl}">${knowledgeUrl}</a></p>
        <c:url value="/knowledge/el-jstl" var="elUrl">
            <c:param name="page" value="2" />
        </c:url>
        <p>带参数的 URL：<a href="${elUrl}">${elUrl}</a></p>
    </div>

    <!-- JSTL c:catch -->
    <div class="card">
        <h3>JSTL &lt;c:catch&gt;</h3>
        <c:catch var="error">
            <%-- 故意触发异常 --%>
            <c:set var="result" value="${1 / 0}" />
        </c:catch>
        <c:if test="${not empty error}">
            <p style="color:#ff4d4f;">捕获到异常：${error.message}</p>
        </c:if>
    </div>

    <!-- fmt:formatDate -->
    <div class="card">
        <h3>&lt;fmt:formatDate&gt;</h3>
        <c:set var="now" value="<%= new java.util.Date() %>" />
        <p>默认格式：<fmt:formatDate value="${now}" /></p>
        <p>完整格式：<fmt:formatDate value="${now}" pattern="yyyy-MM-dd HH:mm:ss" /></p>
        <p>日期格式：<fmt:formatDate value="${now}" pattern="yyyy年MM月dd日" /></p>
    </div>

    <!-- fmt:formatNumber -->
    <div class="card">
        <h3>&lt;fmt:formatNumber&gt;</h3>
        <c:set var="price" value="1234.5678" />
        <p>货币格式：<fmt:formatNumber value="${price}" type="currency" /></p>
        <p>百分比格式：<fmt:formatNumber value="0.856" type="percent" /></p>
        <p>数字格式（2位小数）：<fmt:formatNumber value="${price}" pattern="#,##0.00" /></p>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
