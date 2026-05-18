# 审查员私有日志

> **项目**：iBlog 智能社交博客平台
> **创建时间**：2026-05-18
> **权限**：仅供审查员实例读取，其他角色禁止读取

---

## 第 1 轮审查详细笔记

### 审查时间
2026-05-18

### 审查范围
- T01：pom.xml 添加 6 个依赖
- T19：sql/init.sql 数据库表结构扩展

---

### T01 — pom.xml 依赖审查详情

**依赖清单验证：**
1. `javax.servlet.jsp-api` 2.3.3 (provided) ✅
   - 这是 Servlet 3.1 对应的 JSP API 版本
   - scope=provided 正确，因为 Tomcat 自带 JSP 引擎
   
2. `jstl-api` 1.2 (compile) ✅
   - JSTL 标准 API，1.2 是稳定版本
   
3. `jstl-impl` 1.2 (compile) ✅
   - GlassFish 的 JSTL 实现，与 API 版本匹配
   - 避免了 Jakarta 版本的兼容性问题
   
4. `javax.mail:mail` 1.4.7 (compile) ✅
   - 标准 JavaMail API
   - 1.4.7 较旧但稳定，满足项目需求
   
5. `commons-fileupload` 1.4 (compile) ✅
   - Apache 文件上传库
   - 作为 Servlet 3.0 Part API 的备选方案
   
6. `commons-io` 2.11.0 (compile) ✅
   - commons-fileupload 的依赖
   - 2.11.0 是较新版本

**XML 格式检查：**
- 缩进一致：4 空格 ✅
- 注释清晰：每个依赖都有中文注释 ✅
- 标签闭合正确 ✅

---

### T19 — 数据库表结构审查详情

**ALTER TABLE 语句：**
1. `ALTER TABLE users ADD COLUMN email VARCHAR(100) DEFAULT NULL AFTER phone;`
   - 语法正确 ✅
   - 位置合理（phone 后面）✅
   - DEFAULT NULL 允许空值 ✅
   
2. `ALTER TABLE users ADD COLUMN reset_token VARCHAR(128) DEFAULT NULL AFTER remember_token;`
   - 语法正确 ✅
   - 128 字符足够存储 UUID 或 hash ✅
   
3. `ALTER TABLE users ADD COLUMN reset_token_expire DATETIME DEFAULT NULL AFTER reset_token;`
   - 语法正确 ✅
   - DATETIME 类型适合存储过期时间 ✅

**CREATE TABLE favorites：**
```sql
CREATE TABLE `favorites` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `blog_id` INT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `user_blog` (`user_id`, `blog_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`blog_id`) REFERENCES `blogs`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

检查项：
- 主键：AUTO_INCREMENT ✅
- 外键：两个外键都设置了 ON DELETE CASCADE ✅
- 唯一约束：UNIQUE KEY user_blog 防止重复收藏 ✅
- 字符集：utf8mb4 与现有表一致 ✅
- 引擎：InnoDB 支持事务和外键 ✅

**建议改进（非阻塞）：**
1. email 字段建议添加 UNIQUE 约束
   - 原因：防止同一邮箱注册多个账号
   - 修改：`email VARCHAR(100) UNIQUE DEFAULT NULL`
   
2. 可考虑为 favorites 表添加索引
   - `INDEX idx_user (user_id)` 用于查询用户的收藏列表
   - 但 UNIQUE KEY 已经包含 user_id，可能足够

---

### T02 — PageBean.java 审查详情

**类结构检查：**
1. 包名：`com.iblog.util` ✅
2. 无参构造函数：`public PageBean() {}` ✅
3. 全参构造函数：包含 curPage, pageSize, totalRows, data ✅
4. getter/setter：完整覆盖所有字段 ✅

**方法逻辑验证：**
1. `getTotalPages()`：
   - 条件：`if (totalRows <= 0) return 0;` ✅
   - 计算：`(totalRows + pageSize - 1) / pageSize` ✅
   - 测试：totalRows=10, pageSize=3 → 4 页 ✅
   
2. `hasPrevious()`：
   - 逻辑：`return curPage > 1;` ✅
   - 测试：curPage=1 → false, curPage=2 → true ✅
   
3. `hasNext()`：
   - 逻辑：`return curPage < getTotalPages();` ✅
   - 测试：curPage=4, totalPages=4 → false ✅
   
4. `getOffset()`：
   - 逻辑：`return (curPage - 1) * pageSize;` ✅
   - 测试：curPage=2, pageSize=10 → 10 ✅

**潜在问题：**
- 无参数边界检查（curPage可能小于1，pageSize可能为0）
- 但作为JavaBean，通常由调用方保证参数有效性

**结论：✅ 通过**

---

### T03 — FileUploadUtil.java 审查详情

**文件类型校验：**
- 检查 `part.getContentType()` ✅
- 允许类型：image/jpeg, image/png, image/gif, image/webp ✅
- 潜在风险：contentType 可被伪造，但作为基本校验可接受

**文件大小限制：**
- 常量：`MAX_SIZE = 5 * 1024 * 1024` (5MB) ✅
- 检查：`part.getSize() > MAX_SIZE` ✅

**UUID 文件名生成：**
- 使用 `UUID.randomUUID().toString().replace("-", "")` ✅
- 获取原始扩展名：`getExtension(part.getSubmittedFileName())` ✅
- 拼接：UUID + 扩展名 ✅

**目录创建逻辑：**
- 路径：**硬编码绝对路径** ❌
  ```java
  String dir = "C:\\Users\\liunx\\IdeaProjects\\iblog\\src\\main\\webapp\\uploads\\" + subDir;
  ```
- 风险：生产环境路径错误，无法部署
- 应使用：`getServletContext().getRealPath("/uploads/")`
- 目录创建：`dirFile.mkdirs()` ✅

**文件保存：**
- 使用 `part.write(fullPath)` ✅
- 返回相对路径：`/uploads/subDir/fileName` ✅

**辅助方法：**
- `getExtension()`：正确获取扩展名 ✅
- `getSubmittedFileName()`：从 content-disposition 头提取文件名 ✅

**结论：❌ 阻塞问题（硬编码路径）**

---

### T04 — MailUtil.java 审查详情

**javax.mail 使用：**
- Properties 配置：host, port, auth, starttls ✅
- Session 创建：`Session.getInstance(props, authenticator)` ✅
- 没有使用 `tran.connect()` 单独传参 ✅

**Authenticator 模式：**
- 匿名内部类重写 `getPasswordAuthentication()` ✅
- 返回 `new PasswordAuthentication(SMTP_USER, SMTP_PASS)` ✅

**异步发送：**
- ExecutorService：`Executors.newFixedThreadPool(2)` ✅
- 异步方法：`sendAsync()` 提交任务到线程池 ✅
- 错误处理：捕获异常并打印到 stderr ✅

**HTML 邮件模板：**
- `buildConfirmationHtml()`：注册确认模板 ✅
- `buildResetHtml()`：密码重置模板 ✅
- 包含样式、按钮、备用链接 ✅

**SMTP 配置：**
- 硬编码为 `smtp.qq.com:587` ✅
- 有 TODO 提示修改凭据 ✅
- 生产环境应改为从配置读取

**潜在改进：**
- 提供关闭 ExecutorService 的方法（destroy 方法）
- 考虑使用 try-with-resources 或更完善的资源管理

**结论：✅ 通过**

---

### T11 — JSP 公共组件审查详情

#### header.jsp
- **编码声明**：`<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>` ✅
- **JSTL taglib 声明**：
  - `prefix="c" uri="http://java.sun.com/jsp/jstl/core"` ✅
  - `prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"` ✅
- **CSS 样式**：
  - 全局重置：`* { margin: 0; padding: 0; box-sizing: border-box; }` ✅
  - 按钮样式：`.btn-primary`, `.btn-danger`, `.btn-success` ✅
  - 表格样式：`table`, `th`, `td` ✅
  - 标签样式：`.tag-green`, `.tag-gray`, `.tag-red`, `.tag-blue` ✅
  - 分页样式：`.pagination` ✅
  - 统计卡片：`.stat-cards`, `.stat-card` ✅
- **HTML 结构**：DOCTYPE, lang="zh-CN", meta 标签 ✅
- **标题**：`${param.title} - iBlog 管理后台` ✅

#### nav.jsp
- **编码声明**：有 ✅
- **EL 表达式**：
  - `${pageContext.request.contextPath}` ✅
  - `${sessionScope.currentUser.username}` ✅
  - `${sessionScope.currentUser.role}` ✅
- **导航链接**：
  - 仪表盘：`/admin/dashboard` ✅
  - 博文管理：`/admin/blogs` ✅
  - 用户管理：`/admin/users` ✅
  - 知识点展示：`/knowledge` ✅
- **条件显示**：`<c:if test="${not empty sessionScope.currentUser}">` ✅
- **问题**：**缺少 JSTL taglib 声明** ❌
  - 使用了 `<c:if>` 但没有声明 JSTL
  - 会导致 JSP 编译失败

#### footer.jsp
- **编码声明**：有 ✅
- **HTML 闭合**：`</body></html>` ✅
- **内容**：版权信息 ✅

**编码一致性：**
- 所有 JSP 都使用 UTF-8 编码 ✅
- 多个 page 指令不冲突 ✅

**结论：❌ 阻塞问题（nav.jsp 缺少 JSTL 声明）**

---

### 审查结论

**通过标准：**
- [x] 代码符合计划规范（除阻塞问题外）
- [x] 语法正确
- [x] 版本/类型选择合理
- [x] 编码一致
- [ ] 无安全风险（硬编码路径是部署风险）

**阻塞问题：**
1. FileUploadUtil.java:29 — 硬编码绝对路径
2. nav.jsp — 缺少 JSTL taglib 声明

**最终结论：⚠️ 有条件通过**

需修复 2 个阻塞问题后才能提交。

---

## 第 1 轮 Wave 3 审查详细笔记

### 审查时间
2026-05-18

### 审查范围
- T05：BlogDAO 分页方法
- T06：UserDAO 分页方法
- T07：AvatarUploadServlet
- T08：BlogImageUploadServlet
- T09：RegisterServlet 修改
- T10：PasswordResetServlet 修改
- T20：filter.jsp + LoggingFilter

---

### T05 — BlogDAO 分页方法审查详情

#### findFeedWithPage 方法

**方法签名检查：**
```java
public PageBean findFeedWithPage(int curPage, int pageSize)
```
- 返回类型：PageBean ✅
- 参数：curPage, pageSize ✅

**SQL 语句检查：**
```java
String sql = "SELECT b.*, p.nickname, p.avatar_url FROM blogs b JOIN user_profiles p ON b.user_id = p.user_id WHERE b.status = 'published' AND b.visibility = 'public' ORDER BY b.is_pinned DESC, b.created_at DESC LIMIT ?, ?";
```
- JOIN 语法正确 ✅
- WHERE 条件合理 ✅
- ORDER BY 正确（置顶优先，时间倒序）✅

**❌ 阻塞问题：LIMIT 参数顺序错误**
```java
// 当前代码（第197-198行）：
ps.setInt(1, pageSize);              // 设置第1个参数为 pageSize
ps.setInt(2, (curPage - 1) * pageSize); // 设置第2个参数为 offset

// MySQL LIMIT 语法：LIMIT offset, count
// 正确写法应该是：
ps.setInt(1, (curPage - 1) * pageSize); // offset
ps.setInt(2, pageSize);                  // count
```

**问题分析：**
- MySQL 的 `LIMIT ?, ?` 语法是 `LIMIT offset, count`
- 当前代码将 offset 和 count 颠倒了
- 后果：第 1 页显示正确（offset=0），第 2 页开始数据错乱

**PageBean 构造检查：**
```java
return new PageBean(curPage, pageSize, totalRows, list);
```
- 参数顺序：curPage, pageSize, totalRows, data ✅
- 与 PageBean 构造函数匹配 ✅

#### countByStatus 方法

```java
public int countByStatus(String status) {
    String sql = "SELECT COUNT(*) FROM blogs WHERE status = ?";
    // ...
}
```
- SQL 语法正确 ✅
- 参数化查询防 SQL 注入 ✅
- 异常处理：返回 0 ✅

**结论：❌ 阻塞问题（LIMIT 参数顺序错误）**

---

### T06 — UserDAO 分页方法审查详情

#### findAllWithPage 方法

```java
public PageBean findAllWithPage(int curPage, int pageSize) {
    int totalRows = countAll();
    List<User> list = findAll(curPage, pageSize);
    return new PageBean(curPage, pageSize, totalRows, list);
}
```
- 调用 countAll() 获取总数 ✅
- 调用 findAll(curPage, pageSize) 获取数据 ✅
- PageBean 构造正确 ✅

#### countAll 方法

```java
public int countAll() {
    String sql = "SELECT COUNT(*) FROM users WHERE status != 'deleted'";
    // ...
}
```
- 排除 deleted 状态用户 ✅
- 使用 Statement 而非 PreparedStatement（无参数，可接受）✅

#### findAll 方法（已存在）

```java
public List<User> findAll(int page, int size) {
    String sql = "SELECT * FROM users WHERE status != 'deleted' ORDER BY created_at DESC LIMIT ? OFFSET ?";
    // ...
    ps.setInt(1, size);
    ps.setInt(2, (page - 1) * size);
    // ...
}
```
- 使用 `LIMIT ? OFFSET ?` 语法（更清晰）✅
- 参数顺序正确：size 在前，offset 在后 ✅

#### mapUser 新增字段

```java
u.setEmail(rs.getString("email"));
u.setResetToken(rs.getString("reset_token"));
u.setResetTokenExpire(rs.getTimestamp("reset_token_expire"));
```
- 与数据库新增字段匹配 ✅
- 类型正确：String, String, Timestamp ✅

**结论：✅ 通过**

---

### T07 — AvatarUploadServlet 审查详情

#### @MultipartConfig 注解

```java
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 5 * 1024 * 1024,   // 5MB
    maxRequestSize = 10 * 1024 * 1024 // 10MB
)
```
- fileSizeThreshold=1MB：超过此大小写入临时文件 ✅
- maxFileSize=5MB：单文件限制 ✅
- maxRequestSize=10MB：整个请求限制 ✅

#### Session 检查

```java
if (!SessionUtil.isLoggedIn(req)) {
    JsonUtil.sendError(resp, 401, "请先登录");
    return;
}
```
- 使用 SessionUtil.isLoggedIn() ✅
- 返回 401 错误 ✅

#### getPart 使用

```java
Part part = req.getPart("avatar");
if (part == null || part.getSize() == 0) {
    JsonUtil.sendError(resp, 400, "请选择头像文件");
    return;
}
```
- 正确获取 "avatar" 字段 ✅
- 检查 null 和空文件 ✅

#### FileUploadUtil.saveImage 调用

```java
String avatarUrl = FileUploadUtil.saveImage(part, "avatars", getServletContext());
```
- 3 参数调用：part, subDir, servletContext ✅
- 与 Wave 2 修复后的方法签名匹配 ✅

#### 数据库更新

```java
int userId = SessionUtil.getCurrentUserId(req);
com.iblog.model.UserProfile profile = userDAO.getProfile(userId);
if (profile != null) {
    profile.setAvatarUrl(avatarUrl);
    userDAO.updateProfile(profile);
}
```
- 获取当前用户 ID ✅
- 获取用户资料 ✅
- 更新 avatar_url ✅

#### 错误处理

```java
} catch (ServletException e) {
    JsonUtil.sendError(resp, 400, "上传失败: " + e.getMessage());
} catch (IOException e) {
    JsonUtil.sendError(resp, 400, "上传失败: " + e.getMessage());
}
```
- 捕获 ServletException（getPart 可能抛出）✅
- 捕获 IOException（文件写入可能抛出）✅
- 返回 400 错误 ✅

**结论：✅ 通过**

---

### T08 — BlogImageUploadServlet 审查详情

#### @MultipartConfig 注解
与 AvatarUploadServlet 相同配置 ✅

#### getPart 使用

```java
Part part = req.getPart("image");
```
- 使用 "image" 而非 "avatar"，符合博文图片场景 ✅

#### FileUploadUtil.saveImage 调用

```java
String url = FileUploadUtil.saveImage(part, "images", getServletContext());
```
- subDir 使用 "images" ✅

#### ❌ 阻塞问题：缺少登录检查

**问题**：doPost 方法没有检查用户登录状态

**对比 AvatarUploadServlet：**
```java
// AvatarUploadServlet 有：
if (!SessionUtil.isLoggedIn(req)) {
    JsonUtil.sendError(resp, 401, "请先登录");
    return;
}

// BlogImageUploadServlet 缺少此检查
```

**风险**：
- 匿名用户可上传大量图片
- 服务器空间被恶意占用
- 无审计追踪（不知道谁上传的）

**修复建议**：在 doPost 开头添加与 AvatarUploadServlet 相同的 Session 检查

**结论：❌ 阻塞问题（缺少登录检查）**

---

### T09 — RegisterServlet 修改审查详情

#### email 字段接收

```java
String email = params.get("email");
```
- 从请求参数获取 email ✅
- 可选字段，允许为 null ✅

#### 邮件发送逻辑

```java
if (email != null && !email.trim().isEmpty()) {
    userDAO.updateEmail(userId, email);
    user.setEmail(email);
    String confirmUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort()
            + req.getContextPath() + "/api/auth/confirm?token=" + userId;
    MailUtil.sendConfirmationMail(email, username, confirmUrl);
}
```
- null 安全检查 ✅
- 保存邮箱到数据库 ✅
- 异步发送邮件（MailUtil 内部使用 ExecutorService）✅

#### 🟡 警告：confirmUrl 使用 userId 作为 token

**问题**：`"/api/auth/confirm?token=" + userId`

**风险**：
- userId 是递增整数，容易被猜测
- 攻击者可遍历 userId 确认任意用户邮箱

**建议**：使用 UUID 或随机令牌

**结论：✅ 通过（有警告）**

---

### T10 — PasswordResetServlet 修改审查详情

#### UUID 令牌生成

```java
String resetToken = UUID.randomUUID().toString().replace("-", "");
```
- 使用 UUID.randomUUID() 生成随机令牌 ✅
- 移除连字符，得到 32 位十六进制字符串 ✅

#### 令牌过期时间

```java
Timestamp expire = new Timestamp(System.currentTimeMillis() + 3600 * 1000);
```
- 1 小时有效期 ✅
- 使用 Timestamp 类型 ✅

#### 数据库操作

```java
userDAO.updateResetToken(user.getId(), resetToken, expire);
```
- 调用 UserDAO 方法存储令牌 ✅

#### 令牌验证逻辑（doGet）

```java
User user = findByResetToken(token);
if (user == null) {
    JsonUtil.sendError(resp, 400, "重置令牌无效");
    return;
}
Timestamp now = new Timestamp(System.currentTimeMillis());
if (user.getResetTokenExpire() == null || user.getResetTokenExpire().before(now)) {
    JsonUtil.sendError(resp, 400, "重置令牌已过期");
    return;
}
```
- 检查令牌存在性 ✅
- 检查过期时间 ✅

#### 安全考虑

```java
User user = userDAO.findByEmail(email);
if (user == null) {
    // 为安全考虑，不暴露邮箱是否注册
    JsonUtil.sendSuccess(resp);
    return;
}
```
- 邮箱不存在时返回成功，不暴露信息 ✅

#### 🟡 警告：findByResetToken 直接写 SQL

```java
private User findByResetToken(String token) {
    String sql = "SELECT * FROM users WHERE reset_token = ?";
    // 直接在 Servlet 中写 SQL
}
```
- 违反 DAO/Servlet 分层原则
- 建议移到 UserDAO

**结论：✅ 通过（有警告）**

---

### T20 — filter.jsp + LoggingFilter 审查详情

#### filter.jsp 页面内容

**知识点覆盖：**
1. Filter 工作原理（流程图）✅
2. Filter 生命周期（init → doFilter → destroy）✅
3. iBlog 项目中的 Filter（EncodingFilter/AuthFilter/AdminFilter）✅
4. Filter 配置方式（web.xml vs @WebFilter）✅
5. Filter 链执行顺序验证 ✅

**EL/JSTL 使用：**
```jsp
<c:set var="now" value="<%= new java.util.Date() %>" />
<fmt:formatDate value="${now}" pattern="yyyy-MM-dd HH:mm:ss" />
```
- 正确使用 JSTL 标签 ✅

**代码示例：**
- 展示了 EncodingFilter/AuthFilter/AdminFilter 源码 ✅
- 对比了 web.xml 和 @WebFilter 两种配置方式 ✅

#### LoggingFilter 实现

```java
public class LoggingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) { ... }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) { ... }

    @Override
    public void destroy() { ... }
}
```
- 正确实现 Filter 接口 ✅
- 实现所有三个方法 ✅

**doFilter 逻辑：**
```java
HttpServletRequest request = (HttpServletRequest) req;
String method = request.getMethod();
String uri = request.getRequestURI();
String ip = request.getRemoteAddr();

System.out.println("[LoggingFilter] doFilter() — " + sdf.format(new Date())
        + " | " + method + " " + uri + " | IP: " + ip);

long start = System.currentTimeMillis();
chain.doFilter(req, resp);
long cost = System.currentTimeMillis() - start;

System.out.println("[LoggingFilter] doFilter() — 响应完成，耗时: " + cost + "ms");
```
- 记录请求方法、URI、IP ✅
- 记录响应耗时 ✅
- 正确调用 chain.doFilter() ✅

#### web.xml 注册

```xml
<filter>
    <filter-name>LoggingFilter</filter-name>
    <filter-class>com.iblog.filter.LoggingFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>LoggingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```
- 注册正确 ✅
- URL 模式 /* 匹配所有请求 ✅

**结论：✅ 通过**

---

### web.xml 变更审查详情

#### LoggingFilter 映射
```xml
<filter>
    <filter-name>LoggingFilter</filter-name>
    <filter-class>com.iblog.filter.LoggingFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>LoggingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```
- 位置：在 AuthFilter/AdminFilter 之后 ✅
- URL 模式：/* 匹配所有请求 ✅

#### AvatarUploadServlet 映射
```xml
<servlet>
    <servlet-name>AvatarUploadServlet</servlet-name>
    <servlet-class>com.iblog.servlet.user.AvatarUploadServlet</servlet-class>
    <multipart-config>
        <location>C:\Users\liunx\AppData\Local\Temp</location>
        <max-file-size>5242880</max-file-size>
        <max-request-size>10485760</max-request-size>
    </multipart-config>
</servlet>
<servlet-mapping>
    <servlet-name>AvatarUploadServlet</servlet-name>
    <url-pattern>/api/user/avatar</url-pattern>
</servlet-mapping>
```
- URL 模式：/api/user/avatar ✅
- 无冲突 ✅
- **🟡 警告**：location 硬编码 Windows 路径

#### BlogImageUploadServlet 映射
```xml
<servlet>
    <servlet-name>BlogImageUploadServlet</servlet-name>
    <servlet-class>com.iblog.servlet.blog.BlogImageUploadServlet</servlet-class>
    <multipart-config>
        <location>C:\Users\liunx\AppData\Local\Temp</location>
        <max-file-size>5242880</max-file-size>
        <max-request-size>10485760</max-request-size>
    </multipart-config>
</servlet>
<servlet-mapping>
    <servlet-name>BlogImageUploadServlet</servlet-name>
    <url-pattern>/api/blogs/upload</url-pattern>
</servlet-mapping>
```
- URL 模式：/api/blogs/upload ✅
- 无冲突 ✅
- **🟡 警告**：location 硬编码 Windows 路径

**结论：✅ 通过（有警告）**

---

### Wave 3 审查总结

**阻塞问题（2个）：**
1. BlogDAO.java:197-198 — LIMIT 参数顺序错误
2. BlogImageUploadServlet.java — 缺少登录检查

**警告（3个）：**
1. RegisterServlet.java:56 — userId 作为确认令牌不安全
2. PasswordResetServlet.java:131-150 — findByResetToken 应移到 UserDAO
3. web.xml:107/203 — multipart-config location 硬编码 Windows 路径

**通过项（7个）：**
- T06 UserDAO 分页方法 ✅
- T07 AvatarUploadServlet ✅
- T09 RegisterServlet 邮件发送 ✅
- T10 PasswordResetServlet 令牌生成 ✅
- T20 filter.jsp + LoggingFilter ✅
- User.java 模型扩展 ✅
- web.xml 映射配置 ✅

**最终结论：❌ 打回**

需修复 2 个阻塞问题后重新审查。

---

## 第 1 轮 Wave 3 复审记录

### 复审时间
2026-05-18

### 复审背景
Wave 3 首次审查发现 2 个阻塞问题，开发者已修复，Committer 执行复审验证。

---

### 阻塞问题修复验证

#### 问题 1：BlogDAO.java LIMIT 参数顺序 ✅ 已修复

**修复前代码（错误）：**
```java
ps.setInt(1, pageSize);              // 错误：第1个参数应该是 offset
ps.setInt(2, (curPage - 1) * pageSize); // 错误：第2个参数应该是 count
```

**修复后代码（正确）：**
```java
ps.setInt(1, (curPage - 1) * pageSize); // 正确：offset
ps.setInt(2, pageSize);                  // 正确：count
```

**验证方法：**
- 读取 BlogDAO.java 第 198-199 行
- 确认 `ps.setInt(1, (curPage - 1) * pageSize)` 为 offset
- 确认 `ps.setInt(2, pageSize)` 为 count
- 符合 MySQL `LIMIT offset, count` 语法

**结论：✅ 修复正确**

---

#### 问题 2：BlogImageUploadServlet.java 登录检查 ✅ 已修复

**修复前代码（缺失）：**
- doPost 方法开头没有 SessionUtil.isLoggedIn(req) 检查

**修复后代码（正确）：**
```java
// 检查登录状态
if (!com.iblog.util.SessionUtil.isLoggedIn(req)) {
    JsonUtil.sendError(resp, 401, "请先登录");
    return;
}
```

**验证方法：**
- 读取 BlogImageUploadServlet.java 第 22-26 行
- 确认登录检查在 doPost 入口处
- 确认返回 401 状态码
- 与 AvatarUploadServlet 保持一致

**结论：✅ 修复正确**

---

### 其他 Wave 3 文件快速验证

| 文件 | 检查项 | 结果 |
|------|--------|------|
| UserDAO.java | findAllWithPage + countAll + mapUser 新增字段 | ✅ |
| User.java | email/resetToken/resetTokenExpire 字段及 getter/setter | ✅ |
| AvatarUploadServlet.java | @MultipartConfig + SessionUtil 检查 + FileUploadUtil 3参数调用 | ✅ |
| RegisterServlet.java | email 字段接收 + MailUtil.sendConfirmationMail 异步调用 | ✅ |
| PasswordResetServlet.java | UUID 令牌 + 1h 过期 + findByResetToken + 三模式 doPost | ✅ |
| LoggingFilter.java | Filter 接口实现 + init/doFilter/destroy + 耗时统计 | ✅ |
| filter.jsp | Filter 知识点展示 + JSTL/EL 使用 | ✅ |
| web.xml | LoggingFilter /* + AvatarUpload + BlogImageUpload 映射 | ✅ |

---

### 提交信息

- **commit sha**：`7364917`
- **commit message**：`feat(round-1): 添加分页DAO、文件上传Servlet、邮件功能、Filter展示`
- **文件数量**：10 个文件（6 个修改 + 4 个新增）
- **代码行数**：+753 行, -3 行

---

### 复审结论

✅ **复审通过**

2 个阻塞问题均已正确修复，Wave 3 全部代码已提交。

---

## 第 1 轮 Wave 4 审查详细笔记

### 审查时间
2026-05-18

### 审查范围
- T12：AdminDashboardServlet + dashboard.jsp
- T13：BlogListJspServlet + list.jsp
- T14：AdminBlogJspServlet + blogs.jsp
- T15：AdminUserJspServlet + users.jsp
- T16：KnowledgeServlet + 6 个知识 JSP
- T17：FileUploadDemoServlet + fileupload.jsp
- T18：JavaMailDemoServlet + javamail.jsp

---

### AdminDashboardServlet 审查详情

**@WebServlet 注解：**
```java
@WebServlet("/admin/dashboard")
```
- URL 映射正确 ✅
- 与 web.xml 重复（web.xml 也有 `/admin/dashboard` 映射）⚠️

**Session 检查：❌ 缺失**
- 没有 `SessionUtil.isLoggedIn(req)` 检查
- 没有角色检查（super_admin）
- AdminFilter 只映射 `/api/admin/*`，不覆盖 `/admin/*`

**数据查询：**
```java
int totalUsers = userDAO.countAll();
int totalBlogs = blogDAO.countByStatus("published");
int todayBlogs = blogDAO.countTodayPublished();
PageBean recentUsersPage = userDAO.findAllWithPage(1, 10);
```
- countAll() — UserDAO 已有方法 ✅
- countByStatus("published") — BlogDAO 已有方法 ✅
- countTodayPublished() — Wave 4 新增方法 ✅
- findAllWithPage(1, 10) — UserDAO 已有方法 ✅

**request.setAttribute：**
```java
req.setAttribute("totalUsers", totalUsers);
req.setAttribute("totalBlogs", totalBlogs);
req.setAttribute("todayBlogs", todayBlogs);
req.setAttribute("recentUsers", recentUsersPage.getData());
```
- 属性名与 JSP 中 EL 引用匹配 ✅

**forward 路径：**
```java
req.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp")
```
- 路径正确，以 /WEB-INF/jsp/ 开头 ✅

**结论：❌ 阻塞问题（缺少权限保护）**

---

### BlogListJspServlet 审查详情

**@WebServlet 注解：**
```java
@WebServlet("/blog/list")
```
- URL 映射正确 ✅
- 与 web.xml 重复 ⚠️

**分页参数处理：**
```java
int page = 1;
String pageStr = req.getParameter("page");
if (pageStr != null && !pageStr.isEmpty()) {
    try {
        page = Integer.parseInt(pageStr);
        if (page < 1) page = 1;
    } catch (NumberFormatException e) {
        page = 1;
    }
}
int pageSize = 10;
```
- 默认值 page=1 ✅
- 边界检查 page < 1 ✅
- NumberFormatException 处理 ✅
- pageSize=10 硬编码，可接受 ✅

**数据查询：**
```java
PageBean pageBean = blogDAO.findFeedWithPage(page, pageSize);
```
- 调用 findFeedWithPage（已发布博文分页查询）✅
- LIMIT 参数已在 Wave 3 修复 ✅

**request 属性：**
```java
req.setAttribute("pageBean", pageBean);
```
- JSP 中 `${pageBean.data}` / `${pageBean.hasPrevious()}` / `${pageBean.hasNext()}` 匹配 ✅

**结论：✅ 通过**

---

### AdminBlogJspServlet 审查详情

**与 BlogListJspServlet 对比：**
- 分页参数处理逻辑完全相同 ✅
- 调用 `blogDAO.findAllWithPage(page, pageSize)` 而非 `findFeedWithPage`
  - findAllWithPage 是 Wave 4 新增，不过滤状态（管理后台需要看所有博文）✅

**Session 检查：❌ 缺失**（同 AdminDashboardServlet）

**结论：❌ 阻塞问题（缺少权限保护）**

---

### AdminUserJspServlet 审查详情

**分页参数处理：** 与上述相同 ✅

**数据查询：**
```java
PageBean pageBean = userDAO.findAllWithPage(page, pageSize);
```
- UserDAO.findAllWithPage 已在 Wave 2 审查通过 ✅

**Session 检查：❌ 缺失**（同 AdminDashboardServlet）

**结论：❌ 阻塞问题（缺少权限保护）**

---

### KnowledgeServlet 审查详情

**@WebServlet 注解：**
```java
@WebServlet({"/knowledge", "/knowledge/el-jstl", "/knowledge/session-cookie", "/knowledge/mvc", "/knowledge/scope", "/knowledge/javabean"})
```
- 6 个 URL 映射 ✅
- 与 web.xml 重复 ⚠️

**switch-case 路由：**
```java
String path = req.getServletPath();
switch (path) {
    case "/knowledge/el-jstl": ...
    case "/knowledge/session-cookie": ...
    case "/knowledge/mvc": ...
    case "/knowledge/scope": ...
    case "/knowledge/javabean": ...
    default: // /knowledge
}
```
- 使用 getServletPath() 获取路径 ✅
- 每个 case 正确 forward 到对应 JSP ✅
- default 处理 /knowledge 导航页 ✅

**el-jstl 数据设置：**
```java
req.setAttribute("user", createDemoUser());
req.setAttribute("list", Arrays.asList("Java", "Servlet", "JSP", "JSTL"));
req.setAttribute("now", new Date());
req.setAttribute("price", 128.5);
```
- JSP 中 `${user.username}` / `${list}` / `${now}` / `${price}` 匹配 ✅

**createDemoUser() 方法：**
```java
Map<String, Object> user = new HashMap<>();
user.put("username", "demo_user");
user.put("role", "admin");
user.put("email", "demo@iblog.com");
```
- 返回 Map 而非 User 对象
- JSP 中 `${user.username}` 访问 Map 的 key ✅
- EL 表达式对 Map 和 JavaBean 都支持 `.key` 语法 ✅

**结论：✅ 通过**

---

### FileUploadDemoServlet 审查详情

**@WebServlet + @MultipartConfig：**
```java
@WebServlet("/knowledge/fileupload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 10 * 1024 * 1024
)
```
- 注解正确 ✅
- 与 web.xml 重复配置 ⚠️

**doGet：**
```java
RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/knowledge/fileupload.jsp");
dispatcher.forward(req, resp);
```
- 直接转发到表单页面 ✅

**doPost：**
```java
Part filePart = req.getPart("file");
if (filePart == null || filePart.getSize() == 0) {
    req.setAttribute("error", "请选择要上传的文件");
    forward(req, resp);
    return;
}
String originalFileName = FileUploadUtil.getSubmittedFileName(filePart);
long fileSize = filePart.getSize();
String contentType = filePart.getContentType();
String savedPath = FileUploadUtil.saveImage(filePart, "demo", getServletContext());
```
- getPart("file") 与 JSP 表单 `<input type="file" name="file">` 匹配 ✅
- 空文件检查 ✅
- FileUploadUtil.saveImage 3 参数调用 ✅
- 设置展示信息到 request ✅

**错误处理：**
```java
} catch (Exception e) {
    req.setAttribute("error", "上传失败：" + e.getMessage());
    forward(req, resp);
}
```
- 捕获异常并设置错误信息 ✅

**结论：✅ 通过**

---

### JavaMailDemoServlet 审查详情

**@WebServlet：**
```java
@WebServlet("/knowledge/javamail")
```
- URL 映射正确 ✅

**doGet：** 直接转发到表单页面 ✅

**doPost 参数校验：**
```java
if (to == null || to.trim().isEmpty()) {
    req.setAttribute("error", "请输入收件人邮箱地址");
    forward(req, resp);
    return;
}
```
- 三个参数（to/subject/content）都有非空校验 ✅
- 校验失败设置 error 并 forward 回表单 ✅

**邮件发送：**
```java
MailUtil.sendMail(to.trim(), subject.trim(), content.trim());
```
- 调用 MailUtil.sendMail 方法 ✅
- MailUtil 中已有此方法（第 45 行）✅

**成功/失败处理：**
```java
req.setAttribute("success", true);
req.setAttribute("message", "邮件发送成功！");
req.setAttribute("sentTo", to.trim());
req.setAttribute("sentSubject", subject.trim());
```
- JSP 中 `${success}` / `${message}` / `${sentTo}` / `${sentSubject}` 匹配 ✅

**结论：✅ 通过**

---

### JSP 页面审查详情

#### dashboard.jsp

**EL 属性引用：**
- `${totalUsers}` ← AdminDashboardServlet setAttribute("totalUsers") ✅
- `${totalBlogs}` ← setAttribute("totalBlogs") ✅
- `${todayBlogs}` ← setAttribute("todayBlogs") ✅
- `${recentUsers}` ← setAttribute("recentUsers", recentUsersPage.getData()) ✅

**JSTL 使用：**
- `<c:forEach items="${recentUsers}" var="user">` ✅
- `<c:choose>/<c:when>/<c:otherwise>` 角色和状态标签 ✅
- `<fmt:formatDate value="${user.createdAt}" pattern="yyyy-MM-dd HH:mm">` ✅
  - User.createdAt 是 Timestamp 类型，fmt:formatDate 支持 ✅

**include 路径：**
- `<jsp:include page="/WEB-INF/jsp/common/header.jsp">` ✅
- `<jsp:include page="/WEB-INF/jsp/common/nav.jsp">` ✅
- `<jsp:include page="/WEB-INF/jsp/common/footer.jsp">` ✅

**结论：✅ 通过**

#### blogs.jsp / users.jsp / list.jsp

**分页导航逻辑：**
```jsp
<c:if test="${pageBean.hasPrevious()}">
    <c:url value="/admin/blogs" var="prevUrl">
        <c:param name="page" value="${pageBean.curPage - 1}" />
    </c:url>
    <a href="${prevUrl}">上一页</a>
</c:if>
```
- hasPrevious() 调用正确 ✅
- c:url + c:param 构建 URL 正确 ✅
- curPage - 1 计算正确 ✅

```jsp
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
```
- 页码循环 begin=1, end=totalPages ✅
- 当前页高亮显示 ✅
- 非当前页可点击 ✅

```jsp
<c:if test="${pageBean.hasNext()}">
    <c:url value="/admin/blogs" var="nextUrl">
        <c:param name="page" value="${pageBean.curPage + 1}" />
    </c:url>
    <a href="${nextUrl}">下一页</a>
</c:if>
```
- hasNext() 调用正确 ✅
- curPage + 1 计算正确 ✅

**空状态处理：**
```jsp
<c:if test="${empty pageBean.data}">
    <p style="text-align:center;color:#999;padding:40px 0;">暂无博文</p>
</c:if>
```
- 正确处理空数据 ✅

**结论：✅ 通过**

#### el-jstl.jsp

**EL 表达式演示：**
- `${1 + 1}` 算术运算 ✅
- `${empty list}` 空值判断 ✅
- `${param.page}` 请求参数 ✅
- `${cookie.remember_token.name}` Cookie 读取 ✅

**JSTL 标签演示：**
- `<c:set var="greeting" value="Hello, iBlog!" scope="page">` ✅
- `<c:if test="${score >= 60}">` ✅
- `<c:choose>/<c:when>/<c:otherwise>` ✅
- `<c:forEach items="${list}" var="item" varStatus="vs">` ✅
- `<c:url value="/knowledge" var="knowledgeUrl">` ✅
- `<c:catch var="error">` ✅

**fmt 标签演示：**
- `<fmt:formatDate value="${now}" pattern="yyyy-MM-dd HH:mm:ss">` ✅
- `<fmt:formatNumber value="${price}" type="currency">` ✅

**结论：✅ 通过**

#### session-cookie.jsp

**Session 演示：**
- `${pageContext.session.id}` ✅
- `${sessionScope.currentUser.username}` ✅
- `${sessionScope.currentUser.role}` ✅

**Cookie 演示：**
- `${cookie.remember_token.name}` / `${cookie.remember_token.value}` ✅
- `<c:forEach items="${cookie}" var="c">` 遍历所有 Cookie ✅
- `${c.key}` / `${c.value.value}` 正确访问 Map 结构 ✅

**结论：✅ 通过**

#### mvc.jsp

**内容检查：**
- MVC 三层架构说明 ✅
- iBlog 中的 MVC 实现对照表 ✅
- 请求处理流程图（ASCII）✅
- 典型请求示例（/admin/dashboard）✅

**结论：✅ 通过**

#### scope.jsp

**四大作用域演示：**
- `<c:set var="x" value="..." scope="page/request/session/application">` ✅
- `${pageScope.x}` / `${requestScope.x}` / `${sessionScope.x}` / `${applicationScope.x}` ✅
- EL 隐式对象说明表 ✅
- 不指定 scope 的查找顺序说明 ✅

**结论：✅ 通过**

#### javabean.jsp

**JavaBean 演示：**
- `<jsp:useBean id="now" class="java.util.Date" scope="page">` ✅
- `<jsp:getProperty name="now" property="time">` ✅
- Session 中的 User 对象读取 ✅
- JavaBean 规范对照表 ✅

**结论：✅ 通过**

#### fileupload.jsp

**上传表单：**
- `<form method="post" enctype="multipart/form-data">` ✅
- `<input type="file" name="file" required>` ✅
- name="file" 与 Servlet getPart("file") 匹配 ✅

**成功展示：**
- `${originalFileName}` / `${fileSize}` / `${contentType}` / `${savedPath}` ✅
- `<fmt:formatNumber value="${fileSize / 1048576}" pattern="#.##">` 文件大小格式化 ✅
- `<c:if test="${contentType.startsWith('image/')}">` 图片预览判断 ✅
- `<img src="${pageContext.request.contextPath}${savedPath}">` 图片路径正确 ✅

**错误展示：**
- `${error}` ✅

**结论：✅ 通过**

#### javamail.jsp

**邮件表单：**
- `<form method="post">` ✅
- `<input type="email" name="to" required>` ✅
- `<input type="text" name="subject" required>` ✅
- `<textarea name="content" required>` ✅

**成功/失败展示：**
- `${success}` / `${message}` / `${sentTo}` / `${sentSubject}` ✅
- `${error}` / `${mailDebugInfo}` ✅

**javax.mail 核心类说明表** ✅
**代码示例** ✅

**结论：✅ 通过**

---

### Wave 4 审查总结

**阻塞问题（1 个，涉及 3 个 Servlet）：**
1. AdminDashboardServlet / AdminBlogJspServlet / AdminUserJspServlet — 缺少权限保护
   - AdminFilter 映射 `/api/admin/*` 不覆盖 `/admin/*`
   - Servlet 内部也没有 Session + 角色检查

**警告（3 个）：**
1. 7 个 Servlet 的 @WebServlet 注解与 web.xml 映射重复
2. multipart-config location 硬编码 Windows 路径
3. FileUploadDemoServlet @MultipartConfig 与 web.xml 重复

**通过项：**
- 4 个 Admin/Blog Servlet 的数据查询、forward 路径、分页参数处理 ✅
- KnowledgeServlet 路由逻辑、数据设置 ✅
- FileUploadDemoServlet 文件上传逻辑 ✅
- JavaMailDemoServlet 邮件发送逻辑 ✅
- 12 个 JSP 页面的 taglib、EL、JSTL、分页导航 ✅
- web.xml 7 个 Servlet 映射完整性 ✅
- 3 个新增 DAO 方法 ✅

**最终结论：✅ 通过（阻塞问题已修复）**

AdminFilter 映射已增加 `/admin/*`，管理后台页面现在受 AdminFilter 保护。

---

### Wave 4 提交信息

- **commit sha**：`5f363ac`
- **commit message**：`feat(round-1): 添加管理后台和知识点展示JSP页面`
- **文件数量**：22 个文件（19 个新增 + 3 个修改）
- **代码行数**：+1588 行
