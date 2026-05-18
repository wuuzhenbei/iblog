# JavaWeb 实验报告 —— 第二天

**姓名：** ______________  
**学号：** ______________  
**日期：** 2026 年 5 月 16 日  

---

## 实验 3：静态网页部署练习
**原理：** Tomcat 的 DefaultServlet 直接返回静态资源，`web.xml` 配置 `<welcome-file-list>` 可指定默认首页。
**步骤：**
1. 在 `exp3/` 下创建 `index.html`，编写图书管理表格。
2. 在 `web.xml` 中配置欢迎页为 `exp3/index.html`。
3. 启动 Tomcat 访问根路径，验证页面自动跳转。
**【图 3-1】web.xml 欢迎页配置**
> *(插入配置截图)*
**【图 3-2】浏览器显示图书管理系统首页**
> *(插入页面效果截图)*

---

## 实验 4：`<jsp:include>`、`<jsp:param>` 与 `<jsp:forward>`
**原理：** `<jsp:include>` 运行时动态包含页面；`<jsp:param>` 传递参数；`<jsp:forward>` 服务器端转发请求，地址栏不变。
**步骤：**
1. `include.jsp` 使用 `<jsp:include page="box.jsp">` 并传参 `color`、`title`。
2. `box.jsp` 通过 `request.getParameter()` 接收参数并渲染表格。
3. `input.jsp` 提交年龄到 `forward.jsp`，判断权限后转发至 `next.jsp`，地址栏保持为 `forward.jsp`。
**【图 4-1】include.jsp 动态包含传参效果**
> *(插入绿色背景表格截图)*
**【图 4-2】forward.jsp 转发后 next.jsp 显示结果**
> *(插入权限显示截图)*

---

## 实验 5：out 对象的使用
**原理：** `out` 是 `JspWriter` 对象，内容先写入缓冲区，满或 flush 时发送给客户端。
**步骤：**
1. 设置 `buffer="2kb"`、`autoFlush="false"`。
2. 调用 `out.println()` 输出内容，`out.clear()` 清空缓冲区。
3. 使用 `getBufferSize()` 查看大小，`flush()` 手动刷新，循环输出测试边界。
**【图 5-1】out.jsp 缓冲区操作结果**
> *(插入显示缓冲区大小及剩余空间的截图)*

---

## 实验 6：request 对象的使用
**原理：** `request` 封装 HTTP 请求，`getParameter()` 获取单值，`getParameterValues()` 获取多值（复选框），`setCharacterEncoding()` 解决 POST 乱码。
**步骤：**
1. `reg.html` 设计注册表单含文本框、单选、复选框等。
2. `reg.jsp` 使用 `request.setCharacterEncoding("UTF-8")` 设置编码。
3. 遍历输出 `getParameterValues("fav")` 获取的兴趣爱好数组。
**【图 6-1】reg.html 注册表单页面**
> *(插入表单截图)*
**【图 6-2】reg.jsp 显示提交结果**
> *(插入参数显示截图)*

---

## 实验总结
本日掌握了静态资源部署、JSP 动作标签传参与转发、out 缓冲区机制及 request 表单处理。重点理解了动态包含与静态包含的区别，以及 POST 请求中文乱码的解决方法，为后续 session 和数据库实验打下基础。
