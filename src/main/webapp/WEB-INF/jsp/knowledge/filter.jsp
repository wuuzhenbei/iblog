<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Filter 知识点 - iBlog</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f5f5f5; color: #333; line-height: 1.6; }
        .container { max-width: 1000px; margin: 0 auto; padding: 20px; }
        .card { background: #fff; border-radius: 8px; padding: 24px; margin-bottom: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        h1 { font-size: 28px; margin-bottom: 8px; }
        h2 { font-size: 20px; margin-bottom: 16px; padding-bottom: 8px; border-bottom: 2px solid #1890ff; }
        h3 { font-size: 16px; margin: 16px 0 8px; }
        .subtitle { color: #999; margin-bottom: 20px; }

        /* 流程图样式 */
        .flow-diagram { background: #fafafa; border: 1px solid #e8e8e8; border-radius: 8px; padding: 24px; overflow-x: auto; font-family: 'Courier New', monospace; font-size: 13px; line-height: 1.8; }
        .flow-diagram .highlight { color: #1890ff; font-weight: bold; }
        .flow-diagram .filter-node { color: #ff4d4f; font-weight: bold; }
        .flow-diagram .comment { color: #52c41a; font-style: italic; }
        .flow-diagram .arrow { color: #999; }

        /* 表格样式 */
        table { width: 100%; border-collapse: collapse; margin: 12px 0; }
        th, td { padding: 10px 12px; text-align: left; border-bottom: 1px solid #f0f0f0; }
        th { background: #fafafa; font-weight: 600; }
        .tag { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
        .tag-blue { background: #e6f7ff; color: #1890ff; border: 1px solid #91d5ff; }
        .tag-green { background: #f6ffed; color: #52c41a; border: 1px solid #b7eb8f; }
        .tag-red { background: #fff2f0; color: #ff4d4f; border: 1px solid #ffccc7; }
        .tag-orange { background: #fff7e6; color: #fa8c16; border: 1px solid #ffd591; }

        /* 代码块 */
        .code-block { background: #1e1e1e; color: #d4d4d4; border-radius: 6px; padding: 16px; font-family: 'Courier New', monospace; font-size: 13px; overflow-x: auto; margin: 12px 0; }
        .code-block .keyword { color: #569cd6; }
        .code-block .string { color: #ce9178; }
        .code-block .comment { color: #6a9955; }
        .code-block .annotation { color: #dcdcaa; }

        /* 生命周期图 */
        .lifecycle { display: flex; align-items: center; justify-content: center; gap: 16px; flex-wrap: wrap; margin: 20px 0; }
        .lifecycle .phase { background: #1890ff; color: #fff; padding: 12px 24px; border-radius: 20px; font-weight: bold; }
        .lifecycle .arrow-icon { font-size: 24px; color: #999; }

        .info-box { background: #e6f7ff; border: 1px solid #91d5ff; border-radius: 6px; padding: 12px 16px; margin: 12px 0; }
        .info-box strong { color: #1890ff; }
    </style>
</head>
<body>

<div class="container">

    <div class="card">
        <h1>🔧 Filter 过滤器</h1>
        <p class="subtitle">Filter 是 JavaWeb 三大组件之一（Servlet、Filter、Listener），用于对请求进行前置/后置处理。</p>
    </div>

    <!-- ==================== 1. Filter 工作原理 ==================== -->
    <div class="card">
        <h2>1. Filter 工作原理</h2>
        <p>Filter 以"过滤器链"（FilterChain）的形式串联执行。每个 Filter 在 <code>chain.doFilter()</code> 调用前进行前置处理，调用后进行后置处理。</p>

        <div class="flow-diagram">
            <span class="comment">┌─────────────────────────────────────────────────────────────┐</span><br>
            <span class="comment">│                    Filter 链式执行流程                    │</span><br>
            <span class="comment">└─────────────────────────────────────────────────────────────┘</span><br><br>

            <span class="highlight">Client</span> (浏览器)<br>
            <span class="arrow">  │</span><br>
            <span class="arrow">  │</span> 请求<br>
            <span class="arrow">  ▼</span><br>
            ┌─ <span class="filter-node">[Filter 1: EncodingFilter]</span> ──┐<br>
            <span class="arrow">  │</span>  ├─ 前置: req.setCharacterEncoding("UTF-8")<br>
            <span class="arrow">  │</span>  ├─ <span class="highlight">chain.doFilter()</span> → 传递给下一个<br>
            <span class="arrow">  │</span>  └─ 后置: (无)<br>
            └────────────────────────────┘<br>
            <span class="arrow">  │</span><br>
            <span class="arrow">  ▼</span><br>
            ┌─ <span class="filter-node">[Filter 2: AuthFilter]</span> ──┐<br>
            <span class="arrow">  │</span>  ├─ 前置: 检查用户是否登录<br>
            <span class="arrow">  │</span>  ├─ <span class="highlight">chain.doFilter()</span> → 已登录则放行<br>
            <span class="arrow">  │</span>  └─ 未登录: 返回 401 JSON<br>
            └────────────────────────────┘<br>
            <span class="arrow">  │</span><br>
            <span class="arrow">  ▼</span><br>
            ┌─ <span class="filter-node">[Filter 3: AdminFilter]</span> ──┐<br>
            <span class="arrow">  │</span>  ├─ 前置: 检查角色是否为 super_admin<br>
            <span class="arrow">  │</span>  ├─ <span class="highlight">chain.doFilter()</span> → 管理员则放行<br>
            <span class="arrow">  │</span>  └─ 非管理员: 返回 403 JSON<br>
            └────────────────────────────┘<br>
            <span class="arrow">  │</span><br>
            <span class="arrow">  ▼</span><br>
            <span class="highlight">Target Servlet</span> (目标资源处理请求)<br>
            <span class="arrow">  │</span><br>
            <span class="arrow">  │</span> 响应<br>
            <span class="arrow">  ▼</span><br>
            <span class="highlight">Client</span> (浏览器收到响应)
        </div>

        <div class="info-box">
            <strong>核心概念：</strong>Filter 链是"剥洋葱"式的——请求从外到内经过每个 Filter 的前置逻辑，到达 Servlet；响应从内到外经过每个 Filter 的后置逻辑，返回客户端。
        </div>
    </div>

    <!-- ==================== 2. Filter 生命周期 ==================== -->
    <div class="card">
        <h2>2. Filter 生命周期</h2>
        <p>Filter 与 Servlet 类似，由 Web 容器（Tomcat）管理生命周期，包含三个阶段：</p>

        <div class="lifecycle">
            <div class="phase">① init()</div>
            <span class="arrow-icon">→</span>
            <div class="phase">② doFilter()</div>
            <span class="arrow-icon">→</span>
            <div class="phase">③ destroy()</div>
        </div>

        <table>
            <tr>
                <th>阶段</th>
                <th>调用时机</th>
                <th>调用次数</th>
                <th>说明</th>
            </tr>
            <tr>
                <td><span class="tag tag-blue">init(FilterConfig)</span></td>
                <td>Web 应用启动时</td>
                <td>1 次</td>
                <td>初始化过滤器，可读取 web.xml 中的 init-param</td>
            </tr>
            <tr>
                <td><span class="tag tag-green">doFilter(ServletRequest, ServletResponse, FilterChain)</span></td>
                <td>每次匹配的请求到达时</td>
                <td>N 次</td>
                <td>执行过滤逻辑，必须调用 chain.doFilter() 放行</td>
            </tr>
            <tr>
                <td><span class="tag tag-red">destroy()</span></td>
                <td>Web 应用关闭时</td>
                <td>1 次</td>
                <td>释放资源（关闭连接、线程池等）</td>
            </tr>
        </table>

        <div class="info-box">
            <strong>演示：</strong>在服务器控制台查看 LoggingFilter 的输出，可以看到 init → doFilter (每次请求) → destroy 的完整生命周期。
        </div>

        <h3>LoggingFilter 源码（新增）</h3>
        <div class="code-block">
            <span class="annotation">@WebFilter</span>(urlPatterns = <span class="string">"/*"</span>)<br>
            <span class="keyword">public class</span> LoggingFilter <span class="keyword">implements</span> Filter {<br>
            <br>
            &nbsp;&nbsp;&nbsp;&nbsp;<span class="keyword">public void</span> init(FilterConfig config) {<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(<span class="string">"[LoggingFilter] init()"</span>);<br>
            &nbsp;&nbsp;&nbsp;&nbsp;}<br>
            <br>
            &nbsp;&nbsp;&nbsp;&nbsp;<span class="keyword">public void</span> doFilter(ServletRequest req, ServletResponse resp,<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FilterChain chain) {<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(<span class="string">"[LoggingFilter] doFilter() — "</span><br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+ method + <span class="string">" "</span> + uri);<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;chain.doFilter(req, resp); <span class="comment">// 放行</span><br>
            &nbsp;&nbsp;&nbsp;&nbsp;}<br>
            <br>
            &nbsp;&nbsp;&nbsp;&nbsp;<span class="keyword">public void</span> destroy() {<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(<span class="string">"[LoggingFilter] destroy()"</span>);<br>
            &nbsp;&nbsp;&nbsp;&nbsp;}<br>
            }
        </div>
    </div>

    <!-- ==================== 3. 项目中的三个 Filter ==================== -->
    <div class="card">
        <h2>3. iBlog 项目中的 Filter</h2>

        <table>
            <tr>
                <th>Filter 名称</th>
                <th>类名</th>
                <th>URL 模式</th>
                <th>职责</th>
            </tr>
            <tr>
                <td><span class="tag tag-blue">EncodingFilter</span></td>
                <td><code>com.iblog.filter.EncodingFilter</code></td>
                <td><code>/*</code></td>
                <td>统一设置请求/响应编码为 UTF-8，解决中文乱码</td>
            </tr>
            <tr>
                <td><span class="tag tag-orange">AuthFilter</span></td>
                <td><code>com.iblog.filter.AuthFilter</code></td>
                <td><code>/api/user/*</code>, <code>/api/blogs</code> 等</td>
                <td>检查用户登录状态，未登录返回 401（GET 放行）</td>
            </tr>
            <tr>
                <td><span class="tag tag-red">AdminFilter</span></td>
                <td><code>com.iblog.filter.AdminFilter</code></td>
                <td><code>/api/admin/*</code></td>
                <td>检查管理员角色（super_admin），非管理员返回 403</td>
            </tr>
        </table>

        <h3>EncodingFilter — 编码过滤器</h3>
        <div class="code-block">
            <span class="keyword">public class</span> EncodingFilter <span class="keyword">implements</span> Filter {<br>
            &nbsp;&nbsp;&nbsp;&nbsp;<span class="keyword">public void</span> doFilter(ServletRequest req, ServletResponse resp,<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FilterChain chain) {<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;req.setCharacterEncoding(<span class="string">"UTF-8"</span>);<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;resp.setCharacterEncoding(<span class="string">"UTF-8"</span>);<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;chain.doFilter(req, resp);<br>
            &nbsp;&nbsp;&nbsp;&nbsp;}<br>
            }
        </div>

        <h3>AuthFilter — 认证过滤器</h3>
        <div class="code-block">
            <span class="keyword">public class</span> AuthFilter <span class="keyword">implements</span> Filter {<br>
            &nbsp;&nbsp;&nbsp;&nbsp;<span class="keyword">public void</span> doFilter(ServletRequest req, ServletResponse resp,<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FilterChain chain) {<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="keyword">if</span> (<span class="string">"GET"</span>.equalsIgnoreCase(request.getMethod())) {<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;chain.doFilter(req, resp); <span class="comment">// GET 放行</span><br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="keyword">return</span>;<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="keyword">if</span> (!SessionUtil.isLoggedIn(request)) {<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;JsonUtil.sendError(response, <span class="string">401</span>, <span class="string">"请先登录"</span>);<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="keyword">return</span>;<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;chain.doFilter(req, resp);<br>
            &nbsp;&nbsp;&nbsp;&nbsp;}<br>
            }
        </div>

        <h3>AdminFilter — 管理员过滤器</h3>
        <div class="code-block">
            <span class="keyword">public class</span> AdminFilter <span class="keyword">implements</span> Filter {<br>
            &nbsp;&nbsp;&nbsp;&nbsp;<span class="keyword">public void</span> doFilter(ServletRequest req, ServletResponse resp,<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FilterChain chain) {<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User user = SessionUtil.getCurrentUser(request);<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="keyword">if</span> (user == <span class="keyword">null</span>) {<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;JsonUtil.sendError(response, <span class="string">401</span>, <span class="string">"请先登录"</span>);<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="keyword">return</span>;<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="keyword">if</span> (!<span class="string">"super_admin"</span>.equals(user.getRole())) {<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;JsonUtil.sendError(response, <span class="string">403</span>, <span class="string">"无管理员权限"</span>);<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="keyword">return</span>;<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;chain.doFilter(req, resp);<br>
            &nbsp;&nbsp;&nbsp;&nbsp;}<br>
            }
        </div>
    </div>

    <!-- ==================== 4. Filter 配置方式 ==================== -->
    <div class="card">
        <h2>4. Filter 配置方式</h2>
        <p>Filter 支持两种配置方式：<strong>web.xml 声明式</strong> 和 <strong>@WebFilter 注解式</strong>（Servlet 3.0+）。</p>

        <h3>方式一：web.xml 声明式配置</h3>
        <p>适合集中管理所有 Filter，便于运维人员查看和修改。</p>
        <div class="code-block">
            <span class="comment">&lt;!-- web.xml — 声明 Filter --&gt;</span><br>
            &lt;<span class="keyword">filter</span>&gt;<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&lt;<span class="keyword">filter-name</span>&gt;EncodingFilter&lt;/<span class="keyword">filter-name</span>&gt;<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&lt;<span class="keyword">filter-class</span>&gt;com.iblog.filter.EncodingFilter&lt;/<span class="keyword">filter-class</span>&gt;<br>
            &lt;/<span class="keyword">filter</span>&gt;<br>
            &lt;<span class="keyword">filter-mapping</span>&gt;<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&lt;<span class="keyword">filter-name</span>&gt;EncodingFilter&lt;/<span class="keyword">filter-name</span>&gt;<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&lt;<span class="keyword">url-pattern</span>&gt;/*&lt;/<span class="keyword">url-pattern</span>&gt;<br>
            &lt;/<span class="keyword">filter-mapping</span>&gt;
        </div>

        <h3>方式二：@WebFilter 注解式配置</h3>
        <p>适合快速开发，注解直接在 Filter 类上声明映射关系。</p>
        <div class="code-block">
            <span class="annotation">@WebFilter</span>(urlPatterns = <span class="string">"/*"</span>)<br>
            <span class="keyword">public class</span> LoggingFilter <span class="keyword">implements</span> Filter {<br>
            &nbsp;&nbsp;&nbsp;&nbsp;<span class="comment">// ...</span><br>
            }
        </div>

        <h3>两种方式对比</h3>
        <table>
            <tr>
                <th>对比项</th>
                <th>web.xml 声明式</th>
                <th>@WebFilter 注解式</th>
            </tr>
            <tr>
                <td>配置位置</td>
                <td>集中在一个 web.xml</td>
                <td>分散在各 Filter 类上</td>
            </tr>
            <tr>
                <td>修改灵活性</td>
                <td>修改 URL 模式不需要重新编译</td>
                <td>修改需要重新编译</td>
            </tr>
            <tr>
                <td>初始化参数</td>
                <td>支持 &lt;init-param&gt;</td>
                <td>支持 @WebFilter(initParams=...)</td>
            </tr>
            <tr>
                <td>排序控制</td>
                <td>按 web.xml 声明顺序执行</td>
                <td>按类名顺序执行（不保证）</td>
            </tr>
            <tr>
                <td>推荐场景</td>
                <td>生产项目、多人协作</td>
                <td>快速原型、小型项目</td>
            </tr>
        </table>

        <div class="info-box">
            <strong>注意：</strong>iBlog 项目使用 web.xml 声明式配置（<code>web-app_4_0.xsd</code>），同时 LoggingFilter 使用 <code>@WebFilter</code> 注解演示了第二种方式。两种方式不能混用同一个 Filter，否则会导致重复执行。
        </div>
    </div>

    <!-- ==================== 5. Filter 执行顺序 ==================== -->
    <div class="card">
        <h2>5. Filter 链执行顺序验证</h2>
        <p>在 LoggingFilter 中，每次请求都会输出日志。结合 EncodingFilter（编码）→ AuthFilter（认证）→ AdminFilter（权限）的链式执行，可以清晰地观察到 Filter 的执行顺序。</p>

        <h3>请求 /api/admin/users 的完整执行流程：</h3>
        <div class="code-block">
            <span class="comment">// 1. EncodingFilter 设置 UTF-8 编码</span><br>
            [EncodingFilter] req.setCharacterEncoding(<span class="string">"UTF-8"</span>)<br>
            [EncodingFilter] chain.doFilter() <span class="comment">→ 放行</span><br><br>

            <span class="comment">// 2. AuthFilter 检查登录（POST 需要，GET 放行）</span><br>
            [AuthFilter] isLoggedIn? → <span class="keyword">true</span><br>
            [AuthFilter] chain.doFilter() <span class="comment">→ 放行</span><br><br>

            <span class="comment">// 3. AdminFilter 检查角色</span><br>
            [AdminFilter] role == super_admin? → <span class="keyword">true</span><br>
            [AdminFilter] chain.doFilter() <span class="comment">→ 放行</span><br><br>

            <span class="comment">// 4. 目标 Servlet 处理请求</span><br>
            [AdminUserServlet] 处理 /api/admin/users<br><br>

            <span class="comment">// 5. 响应逆向返回（后置处理）</span><br>
            [EncodingFilter] 响应已编码 → <span class="highlight">Client</span>
        </div>

        <div class="info-box">
            <strong>关键理解：</strong>Filter 链的执行是"先进先出"的——先配置的 Filter 先执行前置逻辑，但后执行后置逻辑。这就像一叠盘子，先放进去的最后拿出来。
        </div>
    </div>

    <div class="card" style="text-align: center; color: #999;">
        <p>iBlog 智能社交博客平台 - JavaWeb 知识点展示 - Filter（知识点 3）</p>
        <p>通过 <code>EL 表达式</code> 展示当前时间：
        <c:set var="now" value="<%= new java.util.Date() %>" />
        <fmt:formatDate value="${now}" pattern="yyyy-MM-dd HH:mm:ss" />
        </p>
    </div>

</div>
</body>
</html>
