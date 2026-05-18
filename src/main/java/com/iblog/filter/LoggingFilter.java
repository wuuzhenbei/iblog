package com.iblog.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志过滤器 — 演示 Filter 生命周期（init → doFilter → destroy）
 *
 * 配置方式：在 web.xml 中声明（与 @WebFilter 注解式配置对比演示）
 */
public class LoggingFilter implements Filter {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("[LoggingFilter] init() — 过滤器初始化，时间: " + sdf.format(new Date()));
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
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
    }

    @Override
    public void destroy() {
        System.out.println("[LoggingFilter] destroy() — 过滤器销毁，时间: " + sdf.format(new Date()));
    }
}
