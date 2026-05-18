# JavaWeb 实验报告 —— 第一天

**姓名：** ______________  
**学号：** ______________  
**日期：** 2026 年 5 月 15 日  

---

## 实验 1：JDK、Tomcat、IDEA 安装
**原理：** JDK 提供 Java 编译运行环境，Tomcat 是轻量级 Web 容器解析 Servlet/JSP，IDEA 提供集成开发支持。环境变量 `JAVA_HOME` 和 `Path` 用于系统全局识别命令。
**步骤：**
1. 下载 JDK 17 安装包，配置 `JAVA_HOME` 和 `Path`，命令行验证 `java -version`。
2. 解压 Tomcat 8.5，无需安装，双击 `bin/startup.bat` 启动，访问 `localhost:8080`。
3. 安装 IDEA，配置 JDK 路径并安装 Smart Tomcat 插件。
**【图 1-1】系统环境变量配置界面**
> *(插入环境变量截图)*
**【图 1-2】Tomcat 启动成功及欢迎页**
> *(插入控制台和浏览器截图)*
**【图 1-3】IDEA 项目配置界面**
> *(插入 IDEA 启动截图)*

---

## 实验 2：Tomcat 部署项目（startup 版 & IDEA 版）
**原理：** Maven 将项目打包为 WAR，Tomcat 启动时自动扫描 `webapps` 并解压；IDEA 通过插件直接关联源码目录实现热部署。`provided` 作用域避免依赖冲突。
**步骤：**
1. **手动部署：** 执行 `mvn clean package`，将 WAR 复制到 Tomcat `webapps`，启动后访问 `/demo1-1.0-SNAPSHOT/hello.jsp`。
2. **IDEA 部署：** 导入 `pom.xml`，配置 Smart Tomcat 路径和 Context Path，一键启动访问。
3. 验证 `web.xml` 欢迎页配置及 JSP 动态时间输出。
**【图 2-1】Maven 打包成功控制台**
> *(插入 BUILD SUCCESS 截图)*
**【图 2-2】webapps 目录 WAR 包及解压文件夹**
> *(插入文件管理器截图)*
**【图 2-3】IDEA Tomcat 运行配置**
> *(插入 Run Configurations 截图)*
**【图 2-4】浏览器显示 hello.jsp 动态时间**
> *(插入页面效果截图)*

---

## 实验总结
本日完成了 Java Web 基础环境搭建，掌握了 JDK、Tomcat 和 IDEA 的安装配置。通过手动打包和 IDE 集成两种方式部署 `demo1` 项目，理解了 WAR 包结构、依赖作用域及 JSP 运行机制，为后续实验奠定基础。
