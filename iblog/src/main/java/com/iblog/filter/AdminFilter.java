package com.iblog.filter;

import com.iblog.model.User;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        User user = SessionUtil.getCurrentUser(request);
        if (user == null) {
            JsonUtil.sendError(response, 401, "请先登录");
            return;
        }

        if (!"super_admin".equals(user.getRole())) {
            JsonUtil.sendError(response, 403, "无管理员权限");
            return;
        }

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {}
}
