# iBlog - 智能社交博客平台

一个功能完整的社交博客平台，支持博文发布、社交互动、私信聊天、AI 文案生成、圈子系统、热搜榜单等核心功能。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 11 + Servlet 4.0 + HikariCP |
| 前端 | Vue 3 + Pinia + Vue Router + Axios |
| 数据库 | MySQL 8.0 (utf8mb4) |
| 构建 | Maven 3.6+ / Vite 5 |
| 容器 | Tomcat 9+ |

## 功能模块

### 用户系统
- 注册/登录（账号+手机号）/ 退出
- 图形验证码
- 记住登录（Cookie Token，30天有效）
- 找回密码（手机号验证）
- 个人资料编辑（昵称/头像/性别/生日/地区/签名/兴趣/座右铭）
- 隐私设置（隐藏在线/访客权限/动态范围）
- 账号冻结/注销
- 成长体系（等级/积分）

### 博文系统
- 发布博文（文字+图片+心情/场景标签+位置）
- 可见范围控制（公开/仅粉丝/仅好友/仅自己）
- 定时发布
- 草稿箱
- 编辑/删除博文
- 置顶博文
- 信息流分页加载
- 个人主页博文列表

### 互动系统
- 点赞/取消点赞
- 踩
- 收藏
- 转发
- 楼中楼评论（一级评论 + 回复评论）

### 社交系统
- 关注/取关
- 互相关注自动标记为好友
- 拉黑用户
- 粉丝列表/关注列表
- 访客记录
- 好友分组

### 私信系统
- 一对一私信
- 会话列表
- 消息已读标记
- 消息撤回
- 未读消息计数

### 圈子系统
- 创建圈子（名称/分类/简介）
- 圈子分类（娱乐/情感/游戏/美食/旅行/职场/其他）
- 加入/退出圈子
- 圈内博文聚合
- 成员管理

### AI 工具箱
- AI 文案生成（治愈/文艺/搞笑/伤感/励志风格）
- AI 润色/扩写/缩写
- AI 评论话术生成
- AI 情绪分析
- AI 内容安全审核
- 管理员可全局开关 AI 功能

### 热搜系统
- 热搜榜单（按热度排序）
- 管理员手动添加/删除热搜
- 热度自动衰减

### 搜索系统
- 全站搜索（博文+用户）
- 按类型筛选（全部/博文/用户）

### 运营功能
- 每日签到（连续签到递增积分，每日上限50分）
- 意见反馈
- 举报系统

### 管理后台
- 数据概览（总用户/今日新增/总博文/待处理举报等）
- 用户管理（冻结/激活/删除）
- 内容审核（下架博文）
- 圈子管理（冻结/解冻）
- 热搜管理
- AI 功能开关

## 项目结构

```
iblog/
├── pom.xml                                    # Maven 配置
├── .gitignore
├── README.md
├── sql/
│   └── init.sql                               # 数据库建表脚本（14张表）
├── src/main/
│   ├── java/com/iblog/
│   │   ├── model/                             # 数据模型（9个）
│   │   │   ├── User.java
│   │   │   ├── UserProfile.java
│   │   │   ├── Blog.java
│   │   │   ├── Comment.java
│   │   │   ├── Follow.java
│   │   │   ├── Message.java
│   │   │   ├── Circle.java
│   │   │   ├── HotTrend.java
│   │   │   └── SignRecord.java
│   │   ├── dao/                               # 数据库操作（8个）
│   │   │   ├── UserDAO.java
│   │   │   ├── BlogDAO.java
│   │   │   ├── CommentDAO.java
│   │   │   ├── FollowDAO.java
│   │   │   ├── CircleDAO.java
│   │   │   ├── MessageDAO.java
│   │   │   ├── HotDAO.java
│   │   │   └── StatsDAO.java
│   │   ├── util/                              # 工具类（6个）
│   │   │   ├── DBUtil.java                    # HikariCP 连接池
│   │   │   ├── AuthUtil.java                  # BCrypt 密码哈希
│   │   │   ├── SessionUtil.java               # Session 管理
│   │   │   ├── JsonUtil.java                  # JSON 响应工具
│   │   │   ├── CaptchaUtil.java               # 图形验证码
│   │   │   └── AiUtil.java                    # AI 接口封装
│   │   ├── filter/                            # 过滤器（3个）
│   │   │   ├── EncodingFilter.java            # UTF-8 编码
│   │   │   ├── AuthFilter.java                # 登录校验
│   │   │   └── AdminFilter.java               # 管理员权限
│   │   ├── servlet/                           # Servlet 控制器（26个）
│   │   │   ├── auth/                          # 认证模块
│   │   │   ├── user/                          # 用户模块
│   │   │   ├── blog/                          # 博文模块
│   │   │   ├── social/                        # 社交模块
│   │   │   ├── circle/                        # 圈子模块
│   │   │   ├── chat/                          # 私信模块
│   │   │   ├── ai/                            # AI 模块
│   │   │   ├── search/                        # 搜索模块
│   │   │   ├── hot/                           # 热搜模块
│   │   │   ├── activity/                      # 活动模块
│   │   │   └── admin/                         # 管理后台
│   │   └── exception/
│   │       └── AppException.java
│   └── webapp/WEB-INF/
│       └── web.xml                            # Servlet 映射配置
└── iblog-frontend/                            # Vue 3 前端
    ├── package.json
    ├── vite.config.js
    ├── index.html
    └── src/
        ├── main.js
        ├── App.vue
        ├── router/index.js                    # 路由配置（20+页面）
        ├── stores/                            # Pinia 状态管理
        │   ├── user.js
        │   ├── theme.js
        │   └── unread.js
        ├── api/                               # Axios 接口封装（8个模块）
        ├── views/                             # 页面组件（20个）
        │   ├── Home.vue                       # 首页信息流
        │   ├── Login.vue                      # 登录
        │   ├── Register.vue                   # 注册
        │   ├── Profile.vue                    # 个人主页
        │   ├── BlogDetail.vue                 # 博文详情+评论
        │   ├── BlogEditor.vue                 # 发布/编辑博文
        │   ├── Circles.vue                    # 圈子列表
        │   ├── Messages.vue                   # 私信列表
        │   ├── ChatWindow.vue                 # 聊天窗口
        │   ├── HotTrends.vue                  # 热搜榜
        │   ├── Search.vue                     # 搜索结果
        │   ├── AiTools.vue                    # AI 工具箱
        │   ├── Settings.vue                   # 个人设置
        │   └── admin/                         # 管理后台（4个页面）
        ├── components/                        # 公共组件
        │   ├── NavBar.vue                     # 顶部导航
        │   └── BlogCard.vue                   # 博文卡片
        └── assets/css/main.css                # 全局样式（支持深色模式）
```

## 数据库设计（14张表）

| 表名 | 说明 | 核心字段 |
|------|------|----------|
| users | 用户表 | username, phone, password_hash, role, status |
| user_profiles | 用户资料 | nickname, avatar, gender, birthday, level, points |
| privacy_settings | 隐私设置 | hide_online, visit_permission, dynamic_scope |
| blogs | 博文表 | content, images, mood_tag, visibility, status |
| comments | 评论表 | parent_id, reply_to_user_id（楼中楼） |
| interactions | 互动表 | type: like/dislike/favorite/forward |
| follows | 关注关系 | status: following/mutual/blocked |
| visit_records | 访客记录 | visitor_id, visited_user_id |
| messages | 私信表 | is_read, is_recalled |
| circles | 圈子表 | category, member_count, is_official |
| circle_members | 圈子成员 | role: member/admin |
| hot_trends | 热搜表 | keyword, heat, is_manual |
| sign_records | 签到表 | sign_date, points_earned |
| reports | 举报表 | target_type, reason, status |

## 快速开始

### 环境要求
- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Node.js 18+
- Tomcat 9+

### 1. 初始化数据库

```bash
mysql -u root -p < sql/init.sql
```

默认管理员账号：`admin` / `admin123`

### 2. 配置数据库连接

编辑 `src/main/java/com/iblog/util/DBUtil.java`：

```java
config.setUsername("root");
config.setPassword("123456");  // 改成你的密码
```

### 3. 启动后端

```bash
mvn clean package
# 将 target/iblog.war 复制到 Tomcat 的 webapps 目录
# 启动 Tomcat
```

### 4. 启动前端

```bash
cd iblog-frontend
npm install
npm run dev
```

浏览器访问 http://localhost:5173

### 5. 生产部署

```bash
cd iblog-frontend
npm run build
# 打包产物在 src/main/webapp/static/，随 war 包一起部署
```

## API 端点

### 认证模块
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/auth/register | 注册 | 否 |
| POST | /api/auth/login | 登录 | 否 |
| POST | /api/auth/logout | 退出 | 是 |
| GET | /api/auth/captcha | 获取验证码 | 否 |
| POST | /api/auth/reset-password | 找回密码 | 否 |

### 用户模块
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/user/profile | 查看资料 | 否 |
| PUT | /api/user/profile | 修改资料 | 是 |
| PUT | /api/user/privacy | 隐私设置 | 是 |
| PUT | /api/user/status | 冻结/注销 | 是 |
| GET | /api/user/level | 等级信息 | 是 |

### 博文模块
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/blogs | 发布博文 | 是 |
| GET | /api/blogs/list | 信息流 | 否 |
| GET | /api/blogs/detail | 博文详情 | 否 |
| PUT | /api/blogs/edit | 编辑博文 | 是 |
| DELETE | /api/blogs/edit | 删除博文 | 是 |
| POST | /api/blogs/interact | 点赞/收藏 | 是 |
| GET | /api/blogs/drafts | 草稿箱 | 是 |
| POST | /api/blogs/drafts | 保存草稿 | 是 |
| POST | /api/comments | 发表评论 | 是 |
| DELETE | /api/comments | 删除评论 | 是 |

### 社交模块
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/social/follow | 关注/取关/拉黑 | 是 |
| GET | /api/social/list | 粉丝/关注列表 | 是 |
| GET | /api/social/visitors | 访客记录 | 是 |

### 私信模块
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/messages/conversations | 会话列表 | 是 |
| GET | /api/messages | 聊天记录 | 是 |
| POST | /api/messages | 发送私信 | 是 |
| DELETE | /api/messages | 撤回消息 | 是 |

### 圈子模块
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/circles | 圈子列表 | 否 |
| POST | /api/circles/manage | 创建圈子 | 是 |
| GET | /api/circles/posts | 圈内博文 | 否 |
| POST | /api/circles/members | 加入/退出 | 是 |

### AI 模块
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/ai/copywriting | 文案生成 | 是 |
| POST | /api/ai/polish | 润色/扩写/缩写 | 是 |
| POST | /api/ai/comment | 评论话术 | 是 |
| POST | /api/ai/emotion | 情绪分析 | 是 |

### 搜索与热搜
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/search | 全站搜索 | 否 |
| GET | /api/hot/trends | 热搜榜单 | 否 |

### 活动模块
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/sign | 每日签到 | 是 |
| GET | /api/sign | 签到状态 | 是 |
| POST | /api/feedback | 意见反馈 | 是 |

### 管理后台
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/admin/stats | 数据统计 | 管理员 |
| GET | /api/admin/users | 用户列表 | 管理员 |
| PUT | /api/admin/users | 管理用户 | 管理员 |
| DELETE | /api/admin/content | 下架博文 | 管理员 |
| GET | /api/admin/hot | 热搜管理 | 管理员 |
| POST | /api/admin/hot | 添加热搜 | 管理员 |
| DELETE | /api/admin/hot | 删除热搜 | 管理员 |
| GET | /api/admin/ai | AI 开关状态 | 管理员 |
| PUT | /api/admin/ai | 切换 AI 开关 | 管理员 |

## 协作开发

### 添加协作者
1. 打开仓库 Settings > Collaborators
2. 添加对方 GitHub 用户名
3. 对方接受邀请后即可 push

### 分支规范
```bash
# 每人在自己的分支开发
git checkout -b feature/你的功能名

# 开发完成后推送
git push origin feature/你的功能名

# 在 GitHub 提 Pull Request，审核后合并到 main
```

### 模块分工建议
| 模块 | 文件 |
|------|------|
| 认证模块 | servlet/auth/*, views/Login.vue, views/Register.vue |
| 博文模块 | servlet/blog/*, views/BlogDetail.vue, views/BlogEditor.vue |
| 社交模块 | servlet/social/*, views/UserPage.vue |
| 私信模块 | servlet/chat/*, views/Messages.vue, views/ChatWindow.vue |
| 圈子模块 | servlet/circle/*, views/Circles.vue, views/CircleDetail.vue |
| AI 模块 | servlet/ai/*, views/AiTools.vue |
| 管理后台 | servlet/admin/*, views/admin/* |

## 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| AI_API_KEY | AI 接口密钥 | 需自行配置 |
| AI_API_URL | AI 接口地址 | https://api.openai.com/v1/chat/completions |

## License

MIT
