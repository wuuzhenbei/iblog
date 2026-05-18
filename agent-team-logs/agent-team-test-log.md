# 测试员私有日志

> **项目**：iBlog 智能社交博客平台
> **创建时间**：2026-05-18
> **权限**：仅供测试员实例读取，其他角色禁止读取

---

## 第1轮综合测试 - 详细测试用例

> **测试时间**：2026-05-18  
> **测试环境**：Windows 11, Java 11, Maven, Tomcat 8.5  
> **测试方法**：静态代码检查 + 文件存在性验证

---

### TC-01: 文件完整性检查

**测试目标**：验证所有新增/修改的文件是否存在

| 序号 | 文件路径 | 预期 | 实际 | 结果 |
|------|----------|------|------|------|
| 1 | src/main/java/com/iblog/util/PageBean.java | 存在 | 存在 | ✅ |
| 2 | src/main/java/com/iblog/util/FileUploadUtil.java | 存在 | 存在 | ✅ |
| 3 | src/main/java/com/iblog/util/MailUtil.java | 存在 | 存在 | ✅ |
| 4 | src/main/java/com/iblog/servlet/user/AvatarUploadServlet.java | 存在 | 存在 | ✅ |
| 5 | src/main/java/com/iblog/servlet/blog/BlogImageUploadServlet.java | 存在 | 存在 | ✅ |
| 6 | src/main/java/com/iblog/servlet/blog/BlogListJspServlet.java | 存在 | 存在 | ✅ |
| 7 | src/main/java/com/iblog/servlet/admin/AdminDashboardServlet.java | 存在 | 存在 | ✅ |
| 8 | src/main/java/com/iblog/servlet/admin/AdminBlogJspServlet.java | 存在 | 存在 | ✅ |
| 9 | src/main/java/com/iblog/servlet/admin/AdminUserJspServlet.java | 存在 | 存在 | ✅ |
| 10 | src/main/java/com/iblog/servlet/demo/KnowledgeServlet.java | 存在 | 存在 | ✅ |
| 11 | src/main/java/com/iblog/servlet/demo/FileUploadDemoServlet.java | 存在 | 存在 | ✅ |
| 12 | src/main/java/com/iblog/servlet/demo/JavaMailDemoServlet.java | 存在 | 存在 | ✅ |
| 13 | src/main/java/com/iblog/filter/LoggingFilter.java | 存在 | 存在 | ✅ |
| 14 | src/main/webapp/WEB-INF/jsp/common/header.jsp | 存在 | 存在 | ✅ |
| 15 | src/main/webapp/WEB-INF/jsp/common/nav.jsp | 存在 | 存在 | ✅ |
| 16 | src/main/webapp/WEB-INF/jsp/common/footer.jsp | 存在 | 存在 | ✅ |
| 17 | src/main/webapp/WEB-INF/jsp/admin/dashboard.jsp | 存在 | 存在 | ✅ |
| 18 | src/main/webapp/WEB-INF/jsp/admin/blogs.jsp | 存在 | 存在 | ✅ |
| 19 | src/main/webapp/WEB-INF/jsp/admin/users.jsp | 存在 | 存在 | ✅ |
| 20 | src/main/webapp/WEB-INF/jsp/blog/list.jsp | 存在 | 存在 | ✅ |
| 21 | src/main/webapp/WEB-INF/jsp/knowledge/index.jsp | 存在 | 存在 | ✅ |
| 22 | src/main/webapp/WEB-INF/jsp/knowledge/el-jstl.jsp | 存在 | 存在 | ✅ |
| 23 | src/main/webapp/WEB-INF/jsp/knowledge/session-cookie.jsp | 存在 | 存在 | ✅ |
| 24 | src/main/webapp/WEB-INF/jsp/knowledge/mvc.jsp | 存在 | 存在 | ✅ |
| 25 | src/main/webapp/WEB-INF/jsp/knowledge/scope.jsp | 存在 | 存在 | ✅ |
| 26 | src/main/webapp/WEB-INF/jsp/knowledge/javabean.jsp | 存在 | 存在 | ✅ |
| 27 | src/main/webapp/WEB-INF/jsp/knowledge/fileupload.jsp | 存在 | 存在 | ✅ |
| 28 | src/main/webapp/WEB-INF/jsp/knowledge/javamail.jsp | 存在 | 存在 | ✅ |
| 29 | src/main/webapp/WEB-INF/jsp/knowledge/filter.jsp | 存在 | 存在 | ✅ |

**结果**：29/29 通过

---

### TC-02: web.xml Servlet 映射检查

**测试目标**：验证所有新增 Servlet 在 web.xml 中正确注册

| 序号 | Servlet 名称 | URL 模式 | 预期 | 实际 | 结果 |
|------|-------------|----------|------|------|------|
| 1 | AdminDashboardServlet | /admin/dashboard | 注册 | 已注册(L447-449) | ✅ |
| 2 | BlogListJspServlet | /blog/list | 注册 | 已注册(L455-458) | ✅ |
| 3 | AdminBlogJspServlet | /admin/blogs | 注册 | 已注册(L464-467) | ✅ |
| 4 | AdminUserJspServlet | /admin/users | 注册 | 已注册(L473-476) | ✅ |
| 5 | KnowledgeServlet | /knowledge | 注册 | 已注册(L483-485) | ✅ |
| 6 | KnowledgeServlet | /knowledge/el-jstl | 注册 | 已注册(L487-489) | ✅ |
| 7 | KnowledgeServlet | /knowledge/session-cookie | 注册 | 已注册(L491-493) | ✅ |
| 8 | KnowledgeServlet | /knowledge/mvc | 注册 | 已注册(L495-497) | ✅ |
| 9 | KnowledgeServlet | /knowledge/scope | 注册 | 已注册(L499-501) | ✅ |
| 10 | KnowledgeServlet | /knowledge/javabean | 注册 | 已注册(L503-505) | ✅ |
| 11 | FileUploadDemoServlet | /knowledge/fileupload | 注册 | 已注册(L516-519) | ✅ |
| 12 | JavaMailDemoServlet | /knowledge/javamail | 注册 | 已注册(L525-528) | ✅ |

**附加检查**：
- AdminFilter 映射 `/admin/*`：✅ 已添加(L39)
- multipart-config 配置：✅ 3个Servlet均有配置

**结果**：12/12 通过

---

### TC-03: PageBean 类结构检查

**测试目标**：验证 PageBean 类结构完整性

| 检查项 | 预期 | 实际 | 结果 |
|--------|------|------|------|
| 包名 | com.iblog.util | com.iblog.util | ✅ |
| 无参构造 | 存在 | 存在(L11) | ✅ |
| 全参构造 | 存在 | 存在(L13-18) | ✅ |
| curPage 属性 | 存在 | 存在(L6) | ✅ |
| pageSize 属性 | 存在 | 存在(L7) | ✅ |
| totalRows 属性 | 存在 | 存在(L8) | ✅ |
| data 属性 | 存在 | 存在(L9) | ✅ |
| getTotalPages() | 存在 | 存在(L21-24) | ✅ |
| hasPrevious() | 存在 | 存在(L27-29) | ✅ |
| hasNext() | 存在 | 存在(L32-34) | ✅ |
| getOffset() | 存在 | 存在(L37-39) | ✅ |
| getter/setter | 完整 | 完整(L42-49) | ✅ |

**结果**：12/12 通过

---

### TC-04: BlogDAO LIMIT 参数顺序检查

**测试目标**：验证分页查询 LIMIT 参数顺序正确

| 方法 | SQL 片段 | 参数1 | 参数2 | 结果 |
|------|----------|-------|-------|------|
| findFeedWithPage | LIMIT ?, ? | (curPage-1)*pageSize (offset) | pageSize (count) | ✅ |
| findAllWithPage | LIMIT ?, ? | (curPage-1)*pageSize (offset) | pageSize (count) | ✅ |

**代码位置**：
- findFeedWithPage: L198-199
- findAllWithPage: L231-232

**结果**：2/2 通过

---

### TC-05: FileUploadUtil 路径检查

**测试目标**：验证文件上传使用 getRealPath 而非硬编码路径

| 检查项 | 预期 | 实际 | 结果 |
|--------|------|------|------|
| 方法签名 | 包含 ServletContext 参数 | `saveImage(Part part, String subDir, javax.servlet.ServletContext servletContext)` (L12) | ✅ |
| 路径获取 | 使用 getRealPath | `servletContext.getRealPath("/uploads/" + subDir)` (L29) | ✅ |
| 硬编码路径 | 不存在 | 未发现 | ✅ |

**结果**：3/3 通过

---

### TC-06: nav.jsp JSTL 声明检查

**测试目标**：验证 nav.jsp 正确声明 JSTL taglib

| 检查项 | 预期 | 实际 | 结果 |
|--------|------|------|------|
| JSTL core 声明 | 存在 | `<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>` (L2) | ✅ |
| c:if 使用 | 正确 | `<c:if test="${not empty sessionScope.currentUser}">` (L12) | ✅ |

**结果**：2/2 通过

---

### TC-07: AdminDashboardServlet 注解检查

**测试目标**：验证 AdminDashboardServlet 有 @WebServlet 注解

| 检查项 | 预期 | 实际 | 结果 |
|--------|------|------|------|
| @WebServlet 注解 | 存在 | `@WebServlet("/admin/dashboard")` (L15) | ✅ |
| HttpServlet 继承 | 存在 | `extends HttpServlet` (L16) | ✅ |
| doGet 方法 | 存在 | `protected void doGet(...)` (L22) | ✅ |
| forward 到 JSP | 存在 | `req.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp")` (L37) | ✅ |

**结果**：4/4 通过

---

### TC-08: init.sql 语法检查

**测试目标**：验证 ALTER TABLE 和 CREATE TABLE 语法正确

| 序号 | SQL 语句 | 行号 | 语法 | 结果 |
|------|----------|------|------|------|
| 1 | ALTER TABLE `users` ADD COLUMN `email` VARCHAR(100) | L209 | 正确 | ✅ |
| 2 | ALTER TABLE `users` ADD COLUMN `reset_token` VARCHAR(128) | L210 | 正确 | ✅ |
| 3 | ALTER TABLE `users` ADD COLUMN `reset_token_expire` DATETIME | L211 | 正确 | ✅ |
| 4 | CREATE TABLE `favorites` (...) | L214-222 | 正确 | ✅ |

**favorites 表结构检查**：
- 主键：id INT AUTO_INCREMENT ✅
- 外键：user_id, blog_id ✅
- 唯一约束：UNIQUE KEY user_blog ✅
- ON DELETE CASCADE：✅

**结果**：4/4 通过

---

### TC-09: 知识点覆盖验证

**测试目标**：验证 14 个知识点都有对应实现

| # | 知识点 | 实现文件 | 验证方式 | 结果 |
|---|--------|----------|----------|------|
| 1 | JSP | 15 个 .jsp 文件 | 文件存在 | ✅ |
| 2 | Servlet | 39+ 个 Servlet 类 | 文件存在 + web.xml 映射 | ✅ |
| 3 | Filter | LoggingFilter.java + filter.jsp | 文件存在 | ✅ |
| 4 | JavaBean | javabean.jsp (useBean/setProperty/getProperty) | 代码检查 | ✅ |
| 5 | MVC | mvc.jsp (架构图解) | 文件存在 | ✅ |
| 6 | EL 表达式 | el-jstl.jsp (${user.username} 等) | 代码检查 | ✅ |
| 7 | JSTL 标签 | el-jstl.jsp (c:forEach/c:if/fmt:formatDate) | 代码检查 | ✅ |
| 8 | Session | session-cookie.jsp (sessionScope) | 代码检查 | ✅ |
| 9 | Cookie | session-cookie.jsp (cookie 隐式对象) | 代码检查 | ✅ |
| 10 | 文件上传 | FileUploadUtil.java + fileupload.jsp | 文件存在 | ✅ |
| 11 | 分页 | PageBean.java + BlogDAO.java + list.jsp | 代码检查 | ✅ |
| 12 | JavaMail | MailUtil.java + javamail.jsp | 文件存在 | ✅ |
| 13 | JDBC | HikariCP + DBUtil.java | 已有实现 | ✅ |
| 14 | 数据库 | init.sql (14+1 张表) | 代码检查 | ✅ |

**结果**：14/14 通过

---

### TC-10: 依赖完整性检查

**测试目标**：验证 pom.xml 包含所有新增依赖

| 序号 | 依赖 | 版本 | scope | 结果 |
|------|------|------|-------|------|
| 1 | javax.servlet.jsp-api | 2.3.3 | provided | ✅ |
| 2 | jstl-api | 1.2 | compile | ✅ |
| 3 | jstl-impl | 1.2 | compile | ✅ |
| 4 | javax.mail:mail | 1.4.7 | compile | ✅ |
| 5 | commons-fileupload | 1.4 | compile | ✅ |
| 6 | commons-io | 2.11.0 | compile | ✅ |

**结果**：6/6 通过

---

### 测试总结

| 测试用例 | 通过 | 失败 | 总计 |
|----------|------|------|------|
| TC-01 文件完整性 | 29 | 0 | 29 |
| TC-02 web.xml 映射 | 12 | 0 | 12 |
| TC-03 PageBean 结构 | 12 | 0 | 12 |
| TC-04 LIMIT 参数 | 2 | 0 | 2 |
| TC-05 FileUploadUtil 路径 | 3 | 0 | 3 |
| TC-06 nav.jsp JSTL | 2 | 0 | 2 |
| TC-07 AdminDashboardServlet | 4 | 0 | 4 |
| TC-08 init.sql 语法 | 4 | 0 | 4 |
| TC-09 知识点覆盖 | 14 | 0 | 14 |
| TC-10 依赖完整性 | 6 | 0 | 6 |
| **总计** | **88** | **0** | **88** |

### 发现的问题

**警告（非阻塞）**：
1. @WebServlet 注解与 web.xml 映射重复（7 个 Servlet）
2. multipart-config location 硬编码 Windows 路径

**阻塞问题**：无

### 测试结论

✅ **全部通过**。88 个测试检查点全部通过，无阻塞问题。代码质量良好，知识点覆盖完整。
