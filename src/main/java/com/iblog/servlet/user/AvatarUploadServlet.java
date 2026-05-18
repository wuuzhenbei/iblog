package com.iblog.servlet.user;

import com.iblog.dao.UserDAO;
import com.iblog.util.FileUploadUtil;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
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
public class AvatarUploadServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查登录状态
        if (!SessionUtil.isLoggedIn(req)) {
            JsonUtil.sendError(resp, 401, "请先登录");
            return;
        }

        try {
            Part part = req.getPart("avatar");
            if (part == null || part.getSize() == 0) {
                JsonUtil.sendError(resp, 400, "请选择头像文件");
                return;
            }

            // 保存文件
            String avatarUrl = FileUploadUtil.saveImage(part, "avatars", getServletContext());

            // 更新数据库
            int userId = SessionUtil.getCurrentUserId(req);
            com.iblog.model.UserProfile profile = userDAO.getProfile(userId);
            if (profile != null) {
                profile.setAvatarUrl(avatarUrl);
                userDAO.updateProfile(profile);
            }

            JsonUtil.sendSuccess(resp, "avatarUrl", avatarUrl);
        } catch (ServletException e) {
            JsonUtil.sendError(resp, 400, "上传失败: " + e.getMessage());
        } catch (IOException e) {
            JsonUtil.sendError(resp, 400, "上传失败: " + e.getMessage());
        }
    }
}
