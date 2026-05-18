# AGENTS.md — iBlog

## Project overview

iBlog（智能社交博客平台）是一个 Java Servlet + Vue 3 的全栈社交博客应用。无 Spring，无框架——后端是纯 Servlet，前端是 Vite + Vue 3 + Pinia。

## Architecture

```
iblog/
├── pom.xml                    # Maven WAR (Java 11, Tomcat 8.5)
├── sql/init.sql               # MySQL 8.0+ 初始化脚本（14 张表 + 默认管理员）
├── src/main/
│   ├── java/com/iblog/
│   │   ├── filter/            # EncodingFilter, AuthFilter, AdminFilter
│   │   ├── servlet/           # 按功能分包: auth, user, blog, social, circle, chat, ai, search, hot, activity, admin
│   │   ├── dao/               # 数据访问层（直接 JDBC + HikariCP）
│   │   ├── model/             # POJO 实体
│   │   ├── util/              # DBUtil, JsonUtil, AuthUtil, SessionUtil, CaptchaUtil, AiUtil
│   │   └── exception/         # AppException
│   └── webapp/WEB-INF/web.xml # 所有 Servlet/Filter 映射的唯一配置文件
└── iblog-frontend/            # Vue 3 SPA（Vite 构建）
    ├── src/api/               # Axios 封装，按模块拆分
    ├── src/stores/            # Pinia stores (user, unread, theme)
    ├── src/views/             # 页面组件
    └── src/router/            # Vue Router，含 auth/admin 路由守卫
```

## Key commands

```bash
# 后端构建（生成 WAR）
mvn clean package
# 产出: target/iblog.war → 部署到 Tomcat webapps/

# 前端开发
cd iblog-frontend
npm install
npm run dev        # localhost:5173，/api 代理到 localhost:8080

# 前端生产构建（输出到 src/main/webapp/static/，随 WAR 一起打包）
npm run build
```

## Deployment

- 后端 WAR 部署到 Tomcat 8.5（`CATALINA_HOME` 参见用户级 CLAUDE.md）
- 前端 `npm run build` 产物输出到 `src/main/webapp/static/`，由 Tomcat 直接服务
- 数据库: MySQL 8.0+，初始化脚本 `sql/init.sql`
- 默认管理员: `admin` / `admin123`（BCrypt 哈希已预置在 init.sql）

## Environment

- `AI_API_KEY` — OpenAI API 密钥，AI 功能（文案生成、润色、评论、情感分析）必需
- 数据库连接硬编码在 `DBUtil.java`：`localhost:3306/iblog_db`，用户 `root`/`root`
- 无 `.env` 文件机制，数据库凭据修改需直接编辑 `DBUtil.java`

## Architecture decisions

### Auth 机制
- 基于 HttpSession 的会话认证，有效期 7 天
- AuthFilter 对 GET 请求放行，POST 请求需登录
- AdminFilter 检查 `role == "super_admin"`
- 记忆登录: HttpOnly Cookie `remember_token`（30 天）

### Servlet 模式
- 每个 Servlet 手动解析 JSON body（`request.getReader()` + Jackson）
- 统一响应格式: `{ "success": true/false, "data"|"error": ... }` 通过 `JsonUtil`
- 无注解路由，所有映射在 `web.xml` 中声明

### 前端 API 调用
- Axios 实例 baseURL 为 `/api`，`withCredentials: true`
- 401 响应自动清除本地存储并跳转登录页
- API 模块按功能拆分: `auth.js`, `blog.js`, `social.js`, `circle.js`, `chat.js`, `ai.js`, `search.js`, `admin.js`

### 前端路由
- Vue Router history 模式，路由守卫检查 `meta.requiresAuth` 和 `meta.requiresAdmin`
- `/admin` 路由要求 `role === 'super_admin'`

## Gotchas

- **无测试框架**: 项目没有单元测试或集成测试，没有 test runner 配置
- **无 ORM**: 数据访问是纯 JDBC，SQL 写在 DAO 类中
- **DB 凭据硬编码**: `DBUtil.java` 中 `root`/`root`，不是从环境变量或配置文件读取
- **web.xml 是真相来源**: 新增 Servlet 必须在 `web.xml` 中注册，否则不会被路由
- **前端构建产物在 webapp 下**: `npm run build` 会清空 `src/main/webapp/static/`，这是设计如此，前端和后端一起打包进 WAR
- **Servlet 没有统一的 body 解析**: 每个 Servlet 各自用 `readBody()` 方法，添加新 Servlet 时需复制此模式
- **AuthFilter 只拦截 POST**: GET 请求对受保护路径也是放行的，这是有意设计（用于读取公开数据）
