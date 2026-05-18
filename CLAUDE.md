# CLAUDE.md — iBlog

## Project overview

iBlog（智能社交博客平台）是一个 Java Servlet + JSP + Vue 3 的全栈社交博客应用。无 Spring，无框架——后端是纯 Servlet + JSP，前端是 Vite + Vue 3 + Pinia。项目同时作为 JavaWeb 实习知识点综合实践，涵盖 14 个核心知识点。

## Architecture

```
iblog/
├── pom.xml                    # Maven WAR (Java 11, Tomcat 8.5)
├── sql/init.sql               # MySQL 8.0+ 初始化脚本（15 张表 + 默认管理员）
├── src/main/
│   ├── java/com/iblog/
│   │   ├── filter/            # EncodingFilter, AuthFilter, AdminFilter, LoggingFilter
│   │   ├── servlet/
│   │   │   ├── auth/          # 注册/登录/登出/验证码/密码重置
│   │   │   ├── user/          # 用户资料/隐私/状态/头像上传
│   │   │   ├── blog/          # 博文CRUD/评论/互动/草稿/图片上传/JSP列表
│   │   │   ├── social/        # 关注/好友/访客
│   │   │   ├── circle/        # 圈子管理/成员/帖子
│   │   │   ├── chat/          # 私信/会话
│   │   │   ├── ai/            # AI文案/润色/评论/情感分析
│   │   │   ├── search/        # 搜索
│   │   │   ├── hot/           # 热搜
│   │   │   ├── activity/      # 签到/反馈
│   │   │   ├── admin/         # 管理后台（JSON API + JSP 页面）
│   │   │   └── demo/          # 知识点演示（KnowledgeServlet/FileUploadDemo/JavaMailDemo）
│   │   ├── dao/               # 数据访问层（直接 JDBC + HikariCP）
│   │   ├── model/             # POJO 实体
│   │   ├── util/              # DBUtil, JsonUtil, AuthUtil, SessionUtil, CaptchaUtil, AiUtil,
│   │   │                      # PageBean, FileUploadUtil, MailUtil
│   │   └── exception/         # AppException
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── web.xml        # 所有 Servlet/Filter 映射的唯一配置文件
│       │   └── jsp/           # JSP 页面（WEB-INF 下不可直接访问，必须通过 Servlet forward）
│       │       ├── common/    # header.jsp, nav.jsp, footer.jsp（公共组件）
│       │       ├── admin/     # dashboard.jsp, blogs.jsp, users.jsp
│       │       ├── blog/      # list.jsp
│       │       └── knowledge/ # index.jsp, el-jstl.jsp, session-cookie.jsp, mvc.jsp,
│       │                      # scope.jsp, javabean.jsp, fileupload.jsp, javamail.jsp, filter.jsp
│       └── uploads/           # 用户上传文件（头像/博文图片），运行时自动创建
└── iblog-frontend/            # Vue 3 SPA（Vite 构建）
    ├── src/api/               # Axios 封装，按模块拆分
    ├── src/stores/            # Pinia stores (user, unread, theme)
    ├── src/views/             # 页面组件
    └── src/router/            # Vue Router，含 auth/admin 路由守卫
```

## Environment — 硬编码路径

| 组件 | 路径 |
|------|------|
| **项目根目录** | `C:\Users\liunx\IdeaProjects\iblog` |
| **JDK** | `C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot` |
| **Maven** | `C:\Users\liunx\.m2\wrapper\dists\apache-maven-3.6.3-bin\...\bin\mvn.cmd`（不在 PATH，用全路径调用） |
| **Tomcat** | `D:\liunx\Downloads\Documents\apache-tomcat-8.5.87-windows-x64\apache-tomcat-8.5.87` |
| **MySQL** | `C:\Program Files\MySQL\MySQL Server 8.0`（端口 3306） |

## Key commands

```bash
# 后端构建（Maven 不在 PATH，用全路径）
& "C:\Users\liunx\.m2\wrapper\dists\apache-maven-3.6.3-bin\1iopthnavndlasol9gbrbg6bf2\apache-maven-3.6.3\bin\mvn.cmd" -f pom.xml clean package

# 前端开发（需要 --host 0.0.0.0 才能被浏览器访问，否则只监听 IPv6）
cd iblog-frontend
npm install --include=dev   # vite 是 devDependency，必须加 --include=dev
node node_modules\vite\bin\vite.js --host 0.0.0.0

# 前端生产构建
node node_modules\.bin\vite.cmd build
# 产出到 src/main/webapp/static/

# 部署到 Tomcat
$env:CATALINA_HOME="D:\liunx\Downloads\Documents\apache-tomcat-8.5.87-windows-x64\apache-tomcat-8.5.87"
# 手动解压 WAR（Tomcat 自动解压有时卡住）：
Expand-Archive -Path target\iblog.war -DestinationPath "$env:CATALINA_HOME\webapps\iblog" -Force
& "$env:CATALINA_HOME\bin\startup.bat"

# 初始化数据库
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p123456 -e "CREATE DATABASE IF NOT EXISTS iblog_db DEFAULT CHARACTER SET utf8mb4;"
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p123456 iblog_db -e "source sql\init.sql"
```

## Database

- **数据库**: `iblog_db`，MySQL 8.0+，utf8mb4 字符集
- **凭据**: `root` / `123456`（硬编码在 `DBUtil.java`）
- **JDBC URL**: `jdbc:mysql://localhost:3306/iblog_db?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true`
- **连接池**: HikariCP 4.0.3，最大 20 连接
- **默认管理员**: `admin` / `admin123`（BCrypt 哈希预置在 init.sql）
- **无 `.env` 文件机制**: 数据库凭据修改需直接编辑 `DBUtil.java`

## URL routing — 双前端共存

项目同时存在 Vue SPA 和 JSP 页面，路由按 URL 前缀隔离：

| URL 前缀 | 处理方式 | 说明 |
|----------|----------|------|
| `/api/*` | Servlet → JSON | Vue SPA 的 API 后端 |
| `/admin/*` | Servlet → forward → JSP | 管理后台 JSP 版（AdminFilter 保护） |
| `/blog/list` | Servlet → forward → JSP | 博文列表 JSP 版（公开） |
| `/knowledge/*` | Servlet → forward → JSP | 知识点演示页面（公开） |
| `/uploads/*` | 静态文件 | 用户上传的头像和图片 |

**不会冲突**：Vue Router 使用 history 模式但只处理前端路由，`/api`、`/admin`、`/blog`、`/knowledge` 由 Tomcat 直接处理。

### Vite 代理配置

前端 `/api` 请求通过 Vite 代理转发到后端。**代理 target 必须包含 context path**：

```js
// vite.config.js
proxy: {
  '/api': {
    target: 'http://localhost:8080/iblog',  // ← 必须加 /iblog
    changeOrigin: true
  }
}
```

## Architecture decisions

### Auth 机制
- 基于 HttpSession 的会话认证，有效期 7 天
- AuthFilter 对 GET 请求放行，POST 请求需登录
- AdminFilter 检查 `role == "super_admin"`，保护 `/api/admin/*` 和 `/admin/*`
- 记忆登录: HttpOnly Cookie `remember_token`（30 天）

### Servlet 模式
- **JSON API Servlet**：手动解析 JSON body（`request.getReader()` + Jackson），返回 `{ "success": true/false, "data"|"error": ... }` 通过 `JsonUtil`
- **JSP Servlet**：使用 `request.setAttribute()` 设置数据，`request.getRequestDispatcher("/WEB-INF/jsp/xxx.jsp").forward()` 转发
- **所有映射在 `web.xml` 中声明**，不要同时使用 `@WebServlet` 注解（Tomcat 8.5 会报重复映射错误）

### JSP 规范
- JSP 放在 `WEB-INF/jsp/` 下，不能直接访问，必须通过 Servlet forward
- 所有 JSP 使用 `<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>` 声明编码
- 使用 JSTL `<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>` 和 `<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>`
- 公共组件通过 `<jsp:include page="/WEB-INF/jsp/common/header.jsp">` 包含（支持传参）
- EL 表达式访问 Servlet 设置的属性：`${pageBean.data}`、`${sessionScope.currentUser}`

### 文件上传
- 使用 Servlet 3.0 Part API（`@MultipartConfig` + `request.getPart()`）
- `FileUploadUtil.saveImage(part, subDir, servletContext)` 保存文件，返回相对路径
- 文件保存到 `servletContext.getRealPath("/uploads/" + subDir)`，不要硬编码路径
- 支持类型：jpg/png/gif/webp，最大 5MB

### 分页
- `PageBean` 封装分页数据：curPage/pageSize/totalRows/data
- DAO 方法返回 PageBean：`BlogDAO.findFeedWithPage(curPage, pageSize)`
- SQL 使用 `LIMIT offset, count`，注意参数顺序：offset 在前，count 在后

### 前端 API 调用
- Axios 实例 baseURL 为 `/api`，`withCredentials: true`
- 401 响应自动清除本地存储并跳转登录页
- API 模块按功能拆分: `auth.js`, `blog.js`, `social.js`, `circle.js`, `chat.js`, `ai.js`, `search.js`, `admin.js`

### 前端路由
- Vue Router history 模式，路由守卫检查 `meta.requiresAuth` 和 `meta.requiresAdmin`
- `/admin` 路由要求 `role === 'super_admin'`

## Gotchas — 踩坑记录

### 🔴 致命问题（会导致 500 / 部署失败）

- **@WebServlet 与 web.xml 冲突**: Tomcat 8.5 不允许同一个 Servlet 同时有 `@WebServlet` 注解和 `web.xml` 映射。**只用 web.xml**，不要加 `@WebServlet` 注解
- **JDBC characterEncoding**: 必须是 `utf8` 或 `UTF-8`，**不能是 `utf8mb4`**（Java 不认识这个编码名）
- **JDBC allowPublicKeyRetrieval**: MySQL 8.0 默认使用 `caching_sha2_password`，必须加 `allowPublicKeyRetrieval=true`
- **BCrypt 哈希**: init.sql 中的哈希必须与实际密码匹配。生成方式: `python -c "import bcrypt; print(bcrypt.hashpw(b'admin123', bcrypt.gensalt(10)).decode())"`
- **数据库必须先初始化**: 运行 `sql/init.sql` 创建数据库和表，否则所有 DAO 操作失败

### 🟡 常见问题

- **JSP 中 `${}` 空 EL 表达式**: 如果文本内容需要显示 `${}` 字面量，必须转义为 `$\{\}`，否则 EL 解析器报错
- **Tomcat WAR 自动解压卡住**: 手动用 `Expand-Archive` 解压 WAR 更可靠
- **前端只监听 IPv6**: `npm run dev` 默认监听 `[::1]:5173`，Chrome 无法访问。加 `--host 0.0.0.0`
- **npm install 缺少 devDependencies**: `vite` 是 devDependency，如果 `NODE_ENV=production` 会跳过。用 `npm install --include=dev`
- **Maven 不在 PATH**: 系统未安装独立 Maven，需要使用 `.m2/wrapper` 中的全路径
- **Tomcat 重启才能刷新 DBUtil**: `DBUtil` 的 static 块在类加载时执行，修改 JDBC 配置后必须重启 Tomcat
- **AuthFilter 只拦截 POST**: GET 请求对受保护路径也是放行的，这是有意设计（用于读取公开数据）
- **AdminFilter 覆盖两个前缀**: `/api/admin/*`（JSON API）和 `/admin/*`（JSP 页面），新增管理页面时确认 URL 在保护范围内
- **LIMIT 参数顺序**: MySQL `LIMIT offset, count`，DAO 中 `ps.setInt(1, offset); ps.setInt(2, pageSize);`，颠倒会导致分页数据错乱

### 🟢 注意事项

- **无测试框架**: 项目没有单元测试或集成测试
- **无 ORM**: 数据访问是纯 JDBC，SQL 写在 DAO 类中
- **Servlet 没有统一的 body 解析**: 每个 Servlet 各自用 `readBody()` 方法，添加新 Servlet 时需复制此模式
- **multipart-config location 硬编码**: web.xml 中 `<location>C:\Users\liunx\AppData\Local\Temp</location>` 是 Windows 路径
- **前端构建产物在 webapp 下**: `npm run build` 会清空 `src/main/webapp/static/`，这是设计如此
