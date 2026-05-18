# 开发者私有日志

> **项目**：iBlog 智能社交博客平台
> **创建时间**：2026-05-18
> **权限**：仅供开发者实例读取，其他角色禁止读取

---

## Wave 1 — 2026-05-18

### T01 — pom.xml 添加依赖

**影响文件**：`pom.xml`

**操作**：在 `<dependencies>` 末尾（BCrypt 依赖之后）插入了 6 个新依赖。

**依赖清单**：

| 依赖 | 版本 | Scope | 用途 |
|------|------|-------|------|
| javax.servlet.jsp:javax.servlet.jsp-api | 2.3.3 | provided | JSP 编译支持（Tomcat 8.5 自带运行环境） |
| javax.servlet.jsp.jstl:jstl-api | 1.2 | compile | JSTL 标准标签库 API |
| org.glassfish.web:jstl-impl | 1.2 | compile | JSTL 参考实现（与 javax.servlet.jsp.jstl 配套） |
| javax.mail:mail | 1.4.7 | compile | 邮件发送（注册确认 + 密码重置） |
| commons-fileupload:commons-fileupload | 1.4 | compile | 文件上传备选方案（Part API 为主） |
| commons-io:commons-io | 2.11.0 | compile | commons-fileupload 的 IO 依赖 |

**决策依据**：
- JSTL 选择 `jstl-api 1.2` + `glassfish jstl-impl 1.2` 而非 Jakarta 版本，因为项目使用 javax.servlet 命名空间（Tomcat 8.5 + Java 11）
- JSP API 设为 `provided`，因为 Tomcat 8.5 自带了 JSP 运行环境
- commons-io 是 commons-fileupload 的传递依赖需求，显式声明版本避免冲突

**验收**：XML 语法完整，无缺失标签或属性错误。

---

### T19 — 数据库表结构扩展

**影响文件**：`sql/init.sql`

**操作**：在文件末尾追加了 3 条 ALTER TABLE 和 1 条 CREATE TABLE 语句。

**新增字段**：

| 表 | 字段 | 类型 | 位置 | 用途 |
|----|------|------|------|------|
| users | email | VARCHAR(100) | AFTER phone | 邮件地址，用于注册确认和密码重置 |
| users | reset_token | VARCHAR(128) | AFTER remember_token | 密码重置令牌（UUID） |
| users | reset_token_expire | DATETIME | AFTER reset_token | 令牌过期时间 |

**新建表**：

| 表名 | 用途 | 约束 |
|------|------|------|
| favorites | 用户收藏博文（Session 购物车类比） | UNIQUE(user_id, blog_id)，级联删除 |

**设计决策**：
- `email` 放在 `phone` 之后，与联系方式字段相邻，符合数据库设计规范
- `reset_token` 使用 `VARCHAR(128)` 以容纳 UUID（36）+ 可能的签名前缀
- `reset_token_expire` 使用 `DATETIME`，与 `schedule_time` 等时间字段保持一致
- `favorites` 表使用 `UNIQUE KEY (user_id, blog_id)` 防止重复收藏，与现有 `interactions` 表的 `user_blog_type` 唯一约束模式一致
- 外键均使用 `ON DELETE CASCADE`，与现有表的外键策略统一

**验收**：SQL 语法符合 MySQL 8.0+ 规范，ALTER TABLE 使用 `AFTER` 子句指定列位置，CREATE TABLE 包含完整的主键、唯一键和外键约束。

---

## Wave 2 — 2026-05-18

### T02 — PageBean 分页工具类

**影响文件**：`src/main/java/com/iblog/util/PageBean.java`（新建）

**属性设计**：
| 属性 | 类型 | 说明 |
|------|------|------|
| curPage | int | 当前页码（从1开始），与前端分页组件一致 |
| pageSize | int | 每页大小，默认由调用方设置 |
| totalRows | int | 总记录数，由 DAO 层的 count 查询填充 |
| data | List<?> | 当前页数据，泛型通配符保持通用性 |

**方法设计**：
- `getTotalPages()`：`(totalRows + pageSize - 1) / pageSize`，避免浮点数运算，处理 totalRows <= 0 的边界情况
- `hasPrevious()`：`curPage > 1`，第一页无上一页
- `hasNext()`：`curPage < getTotalPages()`，最后一页无下一页
- `getOffset()`：`(curPage - 1) * pageSize`，直接用于 SQL LIMIT 子句的 offset

**设计决策**：
- 使用 `List<?>` 而非泛型 `<T>`，因为 JSP EL 无法处理泛型类型擦除后的差异，`List<?>` 在 JSTL `<c:forEach>` 中可直接遍历
- 无参构造器保留（JavaBean 规范），供 JSP `<jsp:useBean>` 使用
- `getOffset()` 方法名清晰表达语义（SQL offset），而非 `getStartIndex()` 等模糊命名
- `totalRows <= 0` 时 `getTotalPages()` 返回 0，避免除以零和负数页数

**知识点覆盖**：分页（知识点11）✓

---

### T03 — FileUploadUtil 文件上传工具类

**影响文件**：`src/main/java/com/iblog/util/FileUploadUtil.java`（新建）

**核心方法**：
- `saveImage(Part part, String subDir)`：保存上传图片，返回相对路径
- `getExtension(String fileName)`：提取文件扩展名
- `getSubmittedFileName(Part part)`：从 content-disposition 头提取原始文件名

**校验流程**：
1. 内容类型（Content-Type）检查：只允许 image/jpeg, image/png, image/gif, image/webp
2. 文件大小检查：硬限制 5MB（`MAX_SIZE = 5 * 1024 * 1024`）

**文件命名策略**：
- UUID（去连字符）+ 原始扩展名，如 `a1b2c3d4e5f6a7b8c9d0e1f2.jpg`
- 避免中文/特殊字符导致 URL 编码问题，保证文件名唯一性

**路径处理**：
- 保存路径：`{project_root}/src/main/webapp/uploads/{subDir}/{uuid}.ext`
- 返回路径：`/uploads/{subDir}/{uuid}.ext`（Web 可访问的相对路径）
- 目录自动创建：`file.mkdirs()` 确保多级目录存在

**说明**：保存路径硬编码为项目绝对路径。更健壮的方式是用 `getServletContext().getRealPath("/uploads/")`，但工具类无 ServletContext 引用，后续 Servlet 层若需改进可传参覆盖。

**知识点覆盖**：文件上传（知识点10）✓

---

### T04 — MailUtil 邮件工具类

**影响文件**：`src/main/java/com/iblog/util/MailUtil.java`（新建）

**SMTP 配置**：
| 参数 | 值 | 说明 |
|------|-----|------|
| host | smtp.qq.com | QQ 邮箱 SMTP 服务器 |
| port | 587 | TLS 端口（STARTTLS） |
| user | your_email@qq.com | **占位符，用户需修改** |
| pass | your_smtp_authorization_code | **占位符，用户需修改** |

**公开方法**：
| 方法 | 用途 |
|------|------|
| `sendConfirmationMail(to, username, confirmUrl)` | 注册确认邮件 |
| `sendPasswordResetMail(to, username, resetUrl)` | 密码重置邮件 |
| `sendMail(to, subject, content)` | 同步发送（供演示使用） |

**异步机制**：
- 使用 `Executors.newFixedThreadPool(2)` 创建 2 线程池
- `sendConfirmationMail`/`sendPasswordResetMail` 提交到线程池后立即返回
- 发送失败在异步线程中打印错误日志，不抛出到调用方
- 线程池为静态 final，类加载时创建，应用生命周期内复用

**设计决策**：
- 选择 QQ 邮箱 SMTP 作为默认（国内用户普遍使用）
- 使用 STARTTLS（端口 587）而非 SSL（端口 465），更兼容
- 邮件模板使用内联 CSS，兼容主流邮件客户端
- TODO 标记醒目，提示用户配置实际凭据

**知识点覆盖**：JavaMail（知识点12）✓

---

### T11 — JSP 公共组件（header/footer/nav）

**影响文件**：
| 文件 | 说明 |
|------|------|
| `src/main/webapp/WEB-INF/jsp/common/header.jsp` | 公共页头 |
| `src/main/webapp/WEB-INF/jsp/common/nav.jsp` | 导航栏 |
| `src/main/webapp/WEB-INF/jsp/common/footer.jsp` | 公共页脚 |

**header.jsp** 设计：
- JSP page 指令：`contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"`，防止中文乱码
- JSTL taglib 声明：`c`（核心标签库）、`fmt`（格式化标签库）
- CSS 样式定义：
  - 管理后台 UI 色系：`#1890ff`（主色）、`#ff4d4f`（危险）、`#52c41a`（成功）
  - `.tag-*` 标签样式：绿色（published）、灰色（draft）、红色（deleted）、蓝色（info）
  - `.pagination` 分页栏：居中 flex 布局
  - `.stat-cards` 统计卡片：4 列 grid 布局
- `<title>` 使用 `${param.title}` 接收动态标题，各页面通过 `<jsp:include>` 传入 title 参数

**nav.jsp** 设计：
- 深色导航栏（`#001529` Ant Design 暗色主题色）
- 菜单项：仪表盘、博文管理、用户管理、知识点展示
- 所有链接使用 `${pageContext.request.contextPath}` 作为上下文前缀，确保部署到任意路径下都能正确跳转
- 右侧使用 EL 显示当前用户信息：`${sessionScope.currentUser.username} (${sessionScope.currentUser.role})`
- 使用 `<c:if>` 判断 sessionScope 中是否存在 currentUser

**footer.jsp** 设计：
- 简单页脚：居中、浅灰色文字
- 关闭 `<body>` 和 `<html>` 标签（header.jsp 中打开）

**使用方式**（供后续 JSP 页面引用）：
```jsp
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="仪表盘" />
</jsp:include>
<jsp:include page="/WEB-INF/jsp/common/nav.jsp" />
<!-- 页面主体内容 -->
<div class="container">...</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
```

**设计决策**：
- 使用 `<jsp:include>` 动作而非 `<%@ include %>` 指令，因指令在编译期合并（可能造成变量命名冲突），动作在运行时合并（独立编译、更灵活）
- header.jsp 中的样式使用全局 class 而非内联，便于后续 JSP 页面复用
- nav.jsp 放在独立文件，便于根据登录状态动态控制显隐
- 不引入外部 CSS 库（如 Bootstrap），保持轻量，独立管理知识点展示

**知识点覆盖**：JSP（知识点1）✓、EL（知识点6）✓、JSTL（知识点7）✓

---

## Wave 2 修复 — 2026-05-18

### 审查阻塞问题修复

**任务来源**：Reviewer Agent 的 Wave 2 审查报告指出 2 个阻塞问题需修复。

#### Fix 1 — FileUploadUtil.java 硬编码路径

**修改文件**：`src/main/java/com/iblog/util/FileUploadUtil.java`

**问题**：`saveImage` 方法第 29 行使用硬编码绝对路径 `"C:\\Users\\liunx\\IdeaProjects\\iblog\\src\\main\\webapp\\uploads\\"`，导致生产环境路径错误。

**修复方案**：
- 方法签名从 `saveImage(Part part, String subDir)` 改为 `saveImage(Part part, String subDir, javax.servlet.ServletContext servletContext)`
- 使用 `servletContext.getRealPath("/uploads/" + subDir)` 获取 Tomcat 运行时真实路径
- 路径兼容：`getRealPath()` 在开发环境和生产环境均返回正确路径

**调用方影响**：
- `AvatarUploadServlet`、`BlogImageUploadServlet`、`FileUploadDemoServlet` 等所有调用 `saveImage` 的地方需传入 `getServletContext()` 参数
- 这些 Servlet 继承自 `HttpServlet`，可通过 `this.getServletContext()` 或 `req.getServletContext()` 获取

**状态**：✅ 已修复

#### Fix 2 — nav.jsp 缺少 JSTL taglib 声明

**修改文件**：`src/main/webapp/WEB-INF/jsp/common/nav.jsp`

**问题**：第 11 行使用 `<c:if>` 标签，但整个文件未声明 JSTL 核心标签库，会导致 JSP 编译失败。

**修复方案**：
- 在 page 指令之后添加 `<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>`
- 确保 `<c:if>` 标签能被正确解析
- 与 header.jsp 中已在第 2 行声明的 taglib 保持一致

**状态**：✅ 已修复

---

## Wave 3 修复 — 2026-05-18

### 审查阻塞问题修复（Reviewer Agent 报告）

**任务来源**：Reviewer Agent 的 Wave 3 审查报告指出 2 个阻塞问题需修复。

#### Fix 1 — BlogDAO.java LIMIT 参数顺序错误

**修改文件**：`src/main/java/com/iblog/dao/BlogDAO.java`

**问题**：`findFeedWithPage` 方法第 198-199 行，LIMIT 子句的参数顺序颠倒：
```java
// 错误：ps.setInt(1, pageSize);      // 第1个参数传入 pageSize
//      ps.setInt(2, offset);          // 第2个参数传入 offset
// 正确：ps.setInt(1, offset);         // 第1个参数 = 偏移量
//      ps.setInt(2, pageSize);        // 第2个参数 = 每页大小
```

MySQL 的 `LIMIT ?, ?` 语法：第一个 `?` 是 offset（偏移量），第二个 `?` 是 count（数量）。
当前代码将两者颠倒，导致第 2 页起数据错乱。

**修复**：交换两个 `setInt()` 参数：
```java
ps.setInt(1, (curPage - 1) * pageSize);  // offset
ps.setInt(2, pageSize);                    // count
```

**影响范围**：仅 `findFeedWithPage` 方法，其他使用 `LIMIT ? OFFSET ?` 语法的 `findByUserId`、`search` 等方法不受影响。

**状态**：✅ 已修复

#### Fix 2 — BlogImageUploadServlet.java 缺少登录检查

**修改文件**：`src/main/java/com/iblog/servlet/blog/BlogImageUploadServlet.java`

**问题**：doPost 方法未检查用户登录状态，匿名用户可上传图片。

**风险**：恶意用户可匿名上传大量图片占用服务器磁盘空间。

**修复**：在 doPost 方法开头添加：
```java
if (!SessionUtil.isLoggedIn(req)) {
    JsonUtil.sendError(resp, 401, "请先登录");
    return;
}
```
使用完全限定名 `com.iblog.util.SessionUtil.isLoggedIn(req)`，不新增 import（JsonUtil 已导入）。

**与 AvatarUploadServlet 一致**：AvatarUploadServlet 已有相同的登录检查，此修复保持一致性。

**状态**：✅ 已修复

---

## Wave 3 原始实现（保留）

### T05 — BlogDAO 分页查询

**影响文件**：`src/main/java/com/iblog/dao/BlogDAO.java`

**新增方法**：
```java
public PageBean findFeedWithPage(int curPage, int pageSize)
public int countByStatus(String status)
```

**设计决策**：
- `findFeedWithPage` 先调 `countByStatus("published")` 拿总数，再执行分页 SQL 查当前页，最后构造 PageBean
- SQL 复用已有的 `findFeed()` 逻辑：`WHERE b.status = 'published' AND b.visibility = 'public' ORDER BY b.is_pinned DESC, b.created_at DESC LIMIT ?, ?`
- `LIMIT ?, ?` 语法：第一个参数是 pageSize，第二个是 offset = (curPage - 1) * pageSize

---

### T06 — UserDAO 分页查询

**影响文件**：`src/main/java/com/iblog/dao/UserDAO.java`

**新增方法**：
```java
public PageBean findAllWithPage(int curPage, int pageSize)
public void updateEmail(int userId, String email)
public void updateResetToken(int userId, String token, Timestamp expire)
public User findByEmail(String email)
```

**设计决策**：
- `findAllWithPage` 复用已有的 `countAll()` 和 `findAll()` 方法组装 PageBean
- `updateEmail` / `updateResetToken` / `findByEmail` 是为 T10（密码重置邮件）准备的基础方法
- `updateResetToken` 同时更新 `reset_token` 和 `reset_token_expire` 两个字段，传入 null 可清除令牌

**User 模型扩展**：
- 新增字段：`email` (String)、`resetToken` (String)、`resetTokenExpire` (Timestamp)
- 对应的 getter/setter 已添加
- `mapUser()` ResultSet mapping 已更新

---

### T07 — AvatarUploadServlet 头像上传

**新建文件**：`src/main/java/com/iblog/servlet/user/AvatarUploadServlet.java`

**注册映射**：web.xml — `/api/user/avatar` (POST)

**技术选型**：
- `@MultipartConfig` 注解声明（Tomcat 8.5 Servlet 3.1 兼容）
- web.xml 同时配置 `<multipart-config>`（Tomcat 部署需要）
- Part API：`request.getPart("avatar")` 获取上传文件
- `FileUploadUtil.saveImage(part, "avatars", getServletContext())` 保存文件
- 使用 `SessionUtil.isLoggedIn()` / `getCurrentUserId()` 获取当前用户

**数据流**：
1. 检查登录状态 → 未登录返回 401
2. 获取 Part → 空文件返回 400
3. 调用 FileUploadUtil.saveImage() 保存 → 返回相对路径 `/uploads/avatars/xxx.jpg`
4. 通过 UserDAO.getProfile() 获取 UserProfile → 更新 avatar_url → updateProfile()
5. 返回 JSON `{ "success": true, "avatarUrl": "/uploads/avatars/xxx.jpg" }`

**安全考量**：
- AuthFilter 已拦截 `/api/user/*`，POST 请求自动需要登录
- 额外在 Servlet 中再次检查 SessionUtil.isLoggedIn()（双重保险）
- 文件类型和大小在 FileUploadUtil 中校验

---

### T08 — BlogImageUploadServlet 博文图片上传

**新建文件**：`src/main/java/com/iblog/servlet/blog/BlogImageUploadServlet.java`

**注册映射**：web.xml — `/api/blogs/upload` (POST)

**设计**：
- 与 T07 类似，但不需要额外登录检查（AuthFilter 拦截 `/api/blogs`）
- 调用 `FileUploadUtil.saveImage(part, "images", getServletContext())`
- 返回 JSON `{ "success": true, "url": "/uploads/images/xxx.jpg" }`

---

### T09 — RegisterServlet 增加邮件确认

**影响文件**：`src/main/java/com/iblog/servlet/auth/RegisterServlet.java`

**改动**：
1. 从 JSON 请求体读取 `email` 字段（可选）
2. 注册成功后：如果 email 非空 → `userDAO.updateEmail(userId, email)` 保存到数据库 → 异步调用 `MailUtil.sendConfirmationMail()` 发送确认邮件
3. 邮件发送完全异步（MailUtil 内部使用 ExecutorService），失败不影响主流程

**设计决策**：
- email 为可选字段，不强制用户提供
- 确认 URL 构造：`{scheme}://{serverName}:{serverPort}/{contextPath}/api/auth/confirm?token={userId}`

---

### T10 — PasswordResetServlet 增加邮件重置

**影响文件**：`src/main/java/com/iblog/servlet/auth/PasswordResetServlet.java`

**重构方案**：完全重写，支持三种模式

**doGet**：验证重置令牌 `GET /api/auth/reset-password?token=xxx`
- 查询 users 表匹配 reset_token → 检查是否过期（1小时）→ 返回 username

**doPost 三种模式**：
| 模式 | 参数 | 流程 |
|------|------|------|
| 1. 发送重置邮件 | `{email: "xxx"}` | findByEmail → 生成UUID → saveResetToken → sendMail |
| 2. 令牌重置密码 | `{token, newPassword}` | 验证 token → 更新密码 → 清除 token |
| 3. 手机重置密码 | `{account, phone, newPassword}` | 保留原有逻辑 |

**安全设计**：
- UUID 无连字符 token（36 → 32 字符），1 小时过期
- 重置密码后清除 token 防止重放
- 邮箱不存在时返回 success（防止枚举攻击）

---

### T20 — Filter 知识点展示

**新建文件**：
1. `src/main/java/com/iblog/filter/LoggingFilter.java` — 日志过滤器（可选）
2. `src/main/webapp/WEB-INF/jsp/knowledge/filter.jsp` — Filter 知识点页面

**LoggingFilter**：
- 记录每次请求：时间戳、HTTP 方法、URI、客户端 IP、处理耗时
- 完整实现 Filter 生命周期三个方法：init → doFilter → destroy
- 在 web.xml 中注册（与 @WebFilter 注解对比演示）

**filter.jsp 页面**：
1. **Filter 工作原理** — ASCII 图解 EncodingFilter → AuthFilter → AdminFilter 链式执行
2. **Filter 生命周期** — init / doFilter / destroy 三阶段 + LoggingFilter 源码
3. **三个 Filter 源码展示** — EncodingFilter、AuthFilter、AdminFilter
4. **配置方式对比** — web.xml 声明式 vs @WebFilter 注解式
5. **执行顺序验证** — 请求 `/api/admin/users` 的完整链追踪
6. **知识点展示**：JSP(1)、EL(6)、JSTL(7) — `${param.title}` + `<c:set>` + `<fmt:formatDate>`

---

### 文件变更清单总览

| 操作 | 文件 |
|------|------|
| 新建 | `src/main/java/com/iblog/servlet/user/AvatarUploadServlet.java` |
| 新建 | `src/main/java/com/iblog/servlet/blog/BlogImageUploadServlet.java` |
| 新建 | `src/main/java/com/iblog/filter/LoggingFilter.java` |
| 新建 | `src/main/webapp/WEB-INF/jsp/knowledge/filter.jsp` |
| 修改 | `src/main/java/com/iblog/dao/BlogDAO.java` |
| 修改 | `src/main/java/com/iblog/dao/UserDAO.java` |
| 修改 | `src/main/java/com/iblog/model/User.java` |
| 修改 | `src/main/java/com/iblog/servlet/auth/RegisterServlet.java` |
| 修改 | `src/main/java/com/iblog/servlet/auth/PasswordResetServlet.java` |
| 修改 | `src/main/webapp/WEB-INF/web.xml` |
| 修改 | `agent-team-logs/agent-team-log.md` |

---

## Wave 4 — 2026-05-18

### 概述

Wave 4 实现全部 7 个任务（T12-T18），新增 7 个 Servlet 和 12 个 JSP 页面，修改 2 个 DAO 文件和 web.xml。

### T12 — AdminDashboardServlet + dashboard.jsp

**新建文件**：
- `src/main/java/com/iblog/servlet/admin/AdminDashboardServlet.java`
- `src/main/webapp/WEB-INF/jsp/admin/dashboard.jsp`

**Servlet 设计**：
- `doGet()` 方法查询 4 项统计数据：UserDAO.countAll()、BlogDAO.countByStatus("published")、BlogDAO.countTodayPublished()、UserDAO.findAllWithPage(1, 10)
- 使用 `request.setAttribute()` 设置数据
- 使用 `RequestDispatcher.forward()` 转发到 JSP

**JSP 设计**：
- 使用 `<jsp:include page="/WEB-INF/jsp/common/header.jsp">` 包含页头（<jsp:param name="title"> 传参）
- 导航栏顶部的深色导航 + 右侧用户信息
- 统计卡片：3 列 grid 布局（调用已有的 `.stat-cards` / `.stat-card` CSS 类）
- 最近用户列表：`<c:forEach items="${recentUsers}" var="user">` 遍历
- 角色标签：`<c:choose><c:when test="${user.role == 'super_admin'}">` 显示不同颜色

**标签颜色映射**：
| 条件 | class | 说明 |
|------|-------|------|
| role == 'super_admin' | tag tag-red | 红色—超级管理员 |
| role == 'admin' | tag tag-blue | 蓝色—管理员 |
| 其他 | tag tag-gray | 灰色—普通用户 |
| status == 'active' | tag tag-green | 绿色—正常 |
| status == 'banned' | tag tag-red | 红色—封禁 |

**知识点覆盖**：JSP(1) ✓、Servlet(2) ✓、MVC(5) ✓、EL(6) ✓、JSTL(7) ✓、Session(8) ✓

---

### T13 — BlogListJspServlet + blog/list.jsp

**新建文件**：
- `src/main/java/com/iblog/servlet/blog/BlogListJspServlet.java`
- `src/main/webapp/WEB-INF/jsp/blog/list.jsp`

**Servlet 设计**：
- 读取 `page` 请求参数，默认第 1 页
- 调用 `BlogDAO.findFeedWithPage(page, 10)` 获取分页数据
- 将 `PageBean` 存入 request，forward 到 JSP

**JSP 设计**：
- `<c:forEach items="${pageBean.data}" var="blog">` 遍历博文
- `${blog.content}` `${blog.nickname}` `${blog.likeCount}` `${blog.commentCount}` 展示
- 空状态：`<c:if test="${empty pageBean.data}">` 显示"暂无博文"
- 分页导航：
  - `<c:if test="${pageBean.hasPrevious()}">` — "上一页"链接
  - `<c:forEach begin="1" end="${pageBean.totalPages}" var="i">` — 页码
  - `<c:if test="${i == pageBean.curPage}">` — 当前页高亮（`.current` 类）
  - `<c:url value="/blog/list"><c:param name="page" value="${i}" />` — 构建分页 URL
  - `<c:if test="${pageBean.hasNext()}">` — "下一页"链接
- `<fmt:formatDate value="${blog.createdAt}" pattern="yyyy-MM-dd HH:mm" />` 格式化日期

**知识点覆盖**：JSP(1) ✓、EL(6) ✓、JSTL(7) ✓、分页(11) ✓、MVC(5) ✓

---

### T14 — AdminBlogJspServlet + admin/blogs.jsp

**新建文件**：
- `src/main/java/com/iblog/servlet/admin/AdminBlogJspServlet.java`
- `src/main/webapp/WEB-INF/jsp/admin/blogs.jsp`

**Servlet 设计**：
- 读取 `page` 参数，调用 `BlogDAO.findAllWithPage()`（新方法，不分状态）
- `findAllWithPage` 在 BlogDAO 中新增：使用 `SELECT COUNT(*) FROM blogs` 统计总数，`ORDER BY created_at DESC` 排序
- forward 到 blogs.jsp

**JSP 状态标签**：
```jsp
<c:choose>
    <c:when test="${blog.status == 'published'}"><span class="tag tag-green">已发布</span></c:when>
    <c:when test="${blog.status == 'draft'}"><span class="tag tag-gray">草稿</span></c:when>
    <c:when test="${blog.status == 'deleted'}"><span class="tag tag-red">已删除</span></c:when>
    <c:otherwise><span class="tag tag-blue">${blog.status}</span></c:otherwise>
</c:choose>
```

**知识点覆盖**：JSP(1) ✓、EL(6) ✓、JSTL(7) ✓、分页(11) ✓

---

### T15 — AdminUserJspServlet + admin/users.jsp

**新建文件**：
- `src/main/java/com/iblog/servlet/admin/AdminUserJspServlet.java`
- `src/main/webapp/WEB-INF/jsp/admin/users.jsp`

**设计**：与 T14 结构一致。分页查询用户（`UserDAO.findAllWithPage`），JSP 使用 `<c:choose>` 根据 role 显示标签。

**知识点覆盖**：JSP(1) ✓、EL(6) ✓、JSTL(7) ✓、分页(11) ✓

---

### T16 — KnowledgeServlet + 知识点展示页面

**新建文件**：
- `src/main/java/com/iblog/servlet/demo/KnowledgeServlet.java`
- `src/main/webapp/WEB-INF/jsp/knowledge/index.jsp`（导航页）
- `src/main/webapp/WEB-INF/jsp/knowledge/el-jstl.jsp`（EL + JSTL 演示）
- `src/main/webapp/WEB-INF/jsp/knowledge/session-cookie.jsp`（Session + Cookie 演示）
- `src/main/webapp/WEB-INF/jsp/knowledge/mvc.jsp`（MVC 架构说明）
- `src/main/webapp/WEB-INF/jsp/knowledge/scope.jsp`（四大作用域演示）
- `src/main/webapp/WEB-INF/jsp/knowledge/javabean.jsp`（JavaBean 演示）

**KnowledgeServlet 设计**：
- 使用 `@WebServlet` 配置 6 个 URL 映射：`/knowledge`, `/knowledge/el-jstl`, `/knowledge/session-cookie`, `/knowledge/mvc`, `/knowledge/scope`, `/knowledge/javabean`
- `doGet()` 根据 `req.getServletPath()` 匹配不同的 JSP 并 forward
- 提供 `createDemoUser()` 静态方法创建 EL 演示用的 Map 数据

**index.jsp 导航页**：
- 10 个卡片（2 列 grid），每个卡片链接到对应知识点页面
- 涵盖全部 14 个知识点的索引
- 使用 `onclick="location.href='${pageContext.request.contextPath}/knowledge/...'"` 跳转

**el-jstl.jsp** 知识点覆盖：
| 知识点 | 代码 | 说明 |
|--------|------|------|
| EL 算术 | `${1+1}` `${10-3}` `${6*7}` | 四则运算 + 取模 |
| EL empty | `${empty list}` `${empty null}` | 空值判断 |
| EL param | `${param.page}` | 请求参数读取 |
| EL cookie | `${cookie.remember_token.value}` | Cookie 读取 |
| c:set | `<c:set var="greeting" value="Hello"/>` | 变量设置 |
| c:if | `<c:if test="${score >= 60}">` | 条件判断 |
| c:choose | `<c:choose><c:when>...<c:otherwise>` | 多条件分支 |
| c:forEach | `<c:forEach items="${list}" var="item" varStatus="vs">` | 集合遍历 |
| c:url | `<c:url value="/knowledge"><c:param name="page"/></c:url>` | URL 重写 |
| c:catch | `<c:catch var="error">${1/0}</c:catch>` | 异常捕获 |
| fmt:formatDate | `<fmt:formatDate value="${now}" pattern="yyyy-MM-dd HH:mm:ss"/>` | 日期格式化 |
| fmt:formatNumber | `<fmt:formatNumber value="1234.5678" type="currency"/>` | 数字格式化 |

**session-cookie.jsp**：
- `pageContext.session.id` — Session ID
- `sessionScope.currentUser` — 当前登录用户
- `<c:forEach items="${cookie}" var="c">` — 遍历所有 Cookie
- `${cookie.remember_token.name}` `${cookie.remember_token.value}` — 按名称读取

**mvc.jsp**：
- MVC 图文说明：Servlet(Controller) → DAO(Model) → JSP(View)
- ASCII 流程图展示完整请求生命周期
- iBlog 典型请求示例（`/admin/dashboard`）

**scope.jsp**：
- `<c:set var="x" value="..." scope="page" />` — 四个作用域同名变量
- `pageScope.x` / `requestScope.x` / `sessionScope.x` / `applicationScope.x` — 分别读取
- EL 默认查找顺序说明：page → request → session → application

**javabean.jsp**：
- `<jsp:useBean id="now" class="java.util.Date" scope="page" />` — 创建实例
- `${sessionScope.currentUser}` — 读取 Session 中的 User
- `<jsp:getProperty name="now" property="time" />` — 读取属性
- JavaBean 规范对照表（无参构造 / 属性封装 / 命名规范）

---

### T17 — FileUploadDemoServlet + fileupload.jsp

**新建文件**：
- `src/main/java/com/iblog/servlet/demo/FileUploadDemoServlet.java`
- `src/main/webapp/WEB-INF/jsp/knowledge/fileupload.jsp`

**Servlet 设计**：
- `@WebServlet("/knowledge/fileupload")` + `@MultipartConfig` 注解
- `doGet()`：展示上传表单（forward 到 fileupload.jsp）
- `doPost()`：
  1. `request.getPart("file")` 获取上传文件
  2. `FileUploadUtil.getSubmittedFileName(part)` 获取原始文件名
  3. `FileUploadUtil.saveImage(part, "demo", getServletContext())` 保存文件
  4. 将文件名、大小、类型、保存路径存入 request，forward 展示

**JSP 设计**：
- 上传表单：`<form method="post" enctype="multipart/form-data">` + `<input type="file" name="file">`
- 上传结果：原始文件名、文件大小（自动识别 B/KB/MB）、文件类型、保存路径
- 图片预览：`<c:if test="${contentType.startsWith('image/')}">` 显示 `<img>` 标签
- 错误提示：红色卡片样式

---

### T18 — JavaMailDemoServlet + javamail.jsp

**新建文件**：
- `src/main/java/com/iblog/servlet/demo/JavaMailDemoServlet.java`
- `src/main/webapp/WEB-INF/jsp/knowledge/javamail.jsp`

**Servlet 设计**：
- `@WebServlet("/knowledge/javamail")`
- `doGet()`：展示邮件发送表单
- `doPost()`：接收 `to`、`subject`、`content` 参数 → 参数校验 → `MailUtil.sendMail()` 同步发送 → 结果提示
- 错误时附带调试信息（提示检查 SMTP 配置）

**JSP 设计**：
- javax.mail 四大核心类说明表：Session / Transport / MimeMessage / Authenticator
- 邮件发送表单：收件人(input type=email)、主题(input text)、正文(textarea)
- 成功提示 / 失败提示（不同颜色卡片）
- JavaMail 核心代码示例

---

### DAO 新增方法

**BlogDAO**（Wave 3 基础上追加）：
```java
public PageBean findAllWithPage(int curPage, int pageSize)  // 管理后台查询所有博文（不限状态）
public int countTodayPublished()                            // 统计今日发布博文数
```

**UserDAO**（Wave 3 基础上追加）：
```java
public List<User> findRecentUsers(int limit)               // 获取最近注册的 N 个用户
```

---

### web.xml 注册

新增 7 个 Servlet 映射：

| Servlet | URL 模式 | 备注 |
|---------|----------|------|
| AdminDashboardServlet | /admin/dashboard | GET |
| BlogListJspServlet | /blog/list | GET |
| AdminBlogJspServlet | /admin/blogs | GET |
| AdminUserJspServlet | /admin/users | GET |
| KnowledgeServlet | /knowledge + 5 个子路径 | 6 个 URL 映射 |
| FileUploadDemoServlet | /knowledge/fileupload | GET+POST，multipart-config |
| JavaMailDemoServlet | /knowledge/javamail | GET+POST |

---

### 文件变更清单总览

| 操作 | 文件 |
|------|------|
| 新建 | `src/main/java/com/iblog/servlet/admin/AdminDashboardServlet.java` |
| 新建 | `src/main/java/com/iblog/servlet/blog/BlogListJspServlet.java` |
| 新建 | `src/main/java/com/iblog/servlet/admin/AdminBlogJspServlet.java` |
| 新建 | `src/main/java/com/iblog/servlet/admin/AdminUserJspServlet.java` |
| 新建 | `src/main/java/com/iblog/servlet/demo/KnowledgeServlet.java` |
| 新建 | `src/main/java/com/iblog/servlet/demo/FileUploadDemoServlet.java` |
| 新建 | `src/main/java/com/iblog/servlet/demo/JavaMailDemoServlet.java` |
| 新建 | `src/main/webapp/WEB-INF/jsp/admin/dashboard.jsp` |
| 新建 | `src/main/webapp/WEB-INF/jsp/blog/list.jsp` |
| 新建 | `src/main/webapp/WEB-INF/jsp/admin/blogs.jsp` |
| 新建 | `src/main/webapp/WEB-INF/jsp/admin/users.jsp` |
| 新建 | `src/main/webapp/WEB-INF/jsp/knowledge/index.jsp` |
| 新建 | `src/main/webapp/WEB-INF/jsp/knowledge/el-jstl.jsp` |
| 新建 | `src/main/webapp/WEB-INF/jsp/knowledge/session-cookie.jsp` |
| 新建 | `src/main/webapp/WEB-INF/jsp/knowledge/mvc.jsp` |
| 新建 | `src/main/webapp/WEB-INF/jsp/knowledge/scope.jsp` |
| 新建 | `src/main/webapp/WEB-INF/jsp/knowledge/javabean.jsp` |
| 新建 | `src/main/webapp/WEB-INF/jsp/knowledge/fileupload.jsp` |
| 新建 | `src/main/webapp/WEB-INF/jsp/knowledge/javamail.jsp` |
| 修改 | `src/main/webapp/WEB-INF/web.xml` |
| 修改 | `src/main/java/com/iblog/dao/BlogDAO.java` |
| 修改 | `src/main/java/com/iblog/dao/UserDAO.java` |
| 修改 | `agent-team-logs/agent-team-log.md` |
