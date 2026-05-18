package com.iblog.servlet.blog;

import com.iblog.util.FileUploadUtil;
import com.iblog.util.JsonUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 5 * 1024 * 1024,   // 5MB
    maxRequestSize = 10 * 1024 * 1024 // 10MB
)
public class BlogImageUploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查登录状态
        if (!com.iblog.util.SessionUtil.isLoggedIn(req)) {
            JsonUtil.sendError(resp, 401, "请先登录");
            return;
        }

        try {
            Part part = req.getPart("image");
            if (part == null || part.getSize() == 0) {
                JsonUtil.sendError(resp, 400, "请选择图片文件");
                return;
            }

            // 保存文件
            String url = FileUploadUtil.saveImage(part, "images", getServletContext());

            JsonUtil.sendSuccess(resp, "url", url);
        } catch (ServletException e) {
            JsonUtil.sendError(resp, 400, "上传失败: " + e.getMessage());
        } catch (IOException e) {
            JsonUtil.sendError(resp, 400, "上传失败: " + e.getMessage());
        }
    }
}
