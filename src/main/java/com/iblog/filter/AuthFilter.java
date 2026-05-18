package com.iblog.filter;

import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(req, resp);
            return;
        }

        if (!SessionUtil.isLoggedIn(request)) {
            JsonUtil.sendError(response, 401, "请先登录");
            return;
        }

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {}
}
