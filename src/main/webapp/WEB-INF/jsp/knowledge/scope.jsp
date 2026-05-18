<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="四大作用域演示" />
</jsp:include>
<jsp:include page="/WEB-INF/jsp/common/nav.jsp" />
<div class="container">
    <h2>JSP 四大作用域演示</h2>

    <div class="card">
        <h3>设置不同作用域的变量</h3>
        <c:set var="x" value="page 作用域值" scope="page" />
        <c:set var="x" value="request 作用域值" scope="request" />
        <c:set var="x" value="session 作用域值" scope="session" />
        <c:set var="x" value="application 作用域值" scope="application" />
        <p style="color:#999;">已通过 &lt;c:set&gt; 在四个作用域设置了同名变量 "x"。</p>
    </div>

    <div class="card">
        <h3>各作用域读取结果</h3>
        <table>
            <thead>
                <tr>
                    <th>作用域</th>
                    <th>EL 表达式</th>
                    <th>值</th>
                    <th>说明</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Page</td>
                    <td>\${pageScope.x}</td>
                    <td>${pageScope.x}</td>
                    <td>仅当前 JSP 页面有效</td>
                </tr>
                <tr>
                    <td>Request</td>
                    <td>\${requestScope.x}</td>
                    <td>${requestScope.x}</td>
                    <td>同一次请求有效（含 forward）</td>
                </tr>
                <tr>
                    <td>Session</td>
                    <td>\${sessionScope.x}</td>
                    <td>${sessionScope.x}</td>
                    <td>同一次会话有效（7 天）</td>
                </tr>
                <tr>
                    <td>Application</td>
                    <td>\${applicationScope.x}</td>
                    <td>${applicationScope.x}</td>
                    <td>整个 Web 应用生命周期</td>
                </tr>
            </tbody>
        </table>
    </div>

    <div class="card">
        <h3>EL 隐式对象说明</h3>
        <table>
            <thead>
                <tr>
                    <th>EL 隐式对象</th>
                    <th>对应 Java 类型</th>
                    <th>作用域范围</th>
                </tr>
            </thead>
            <tbody>
                <tr><td>pageScope</td><td>PageContext</td><td>当前 JSP 页面</td></tr>
                <tr><td>requestScope</td><td>HttpServletRequest</td><td>当前请求</td></tr>
                <tr><td>sessionScope</td><td>HttpSession</td><td>当前会话</td></tr>
                <tr><td>applicationScope</td><td>ServletContext</td><td>整个 Web 应用</td></tr>
            </tbody>
        </table>
    </div>

    <div class="card">
        <h3>不指定 scope 的 EL 查找顺序</h3>
        <p>使用 \${x} 而不指定作用域时，EL 按以下顺序查找：</p>
        <ol style="margin:12px 0;padding-left:20px;line-height:1.8;">
            <li><strong>pageScope</strong> → 找到 "page 作用域值" 则返回</li>
            <li><strong>requestScope</strong> → 找到则返回</li>
            <li><strong>sessionScope</strong> → 找到则返回</li>
            <li><strong>applicationScope</strong> → 找到则返回</li>
        </ol>
        <p><strong>\${x} = ${x}</strong>（按顺序找到的第一个值）</p>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
