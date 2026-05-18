package com.iblog.servlet.auth;

import com.iblog.util.CaptchaUtil;
import javax.servlet.http.*;
import java.io.IOException;

public class CaptchaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = CaptchaUtil.generate(resp);
        req.getSession(true).setAttribute("captcha", code.toUpperCase());
    }
}
