package com.iblog.servlet.demo;

import com.iblog.util.FileUploadUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;


@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 10 * 1024 * 1024
)
public class FileUploadDemoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/knowledge/fileupload.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // 获取上传的文件 Part
            Part filePart = req.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                req.setAttribute("error", "请选择要上传的文件");
                forward(req, resp);
                return;
            }

            // 获取原始文件名
            String originalFileName = FileUploadUtil.getSubmittedFileName(filePart);
            // 获取文件大小
            long fileSize = filePart.getSize();
            // 获取文件类型
            String contentType = filePart.getContentType();
            // 保存文件（使用 Servlet 3.0 Part API）
            String savedPath = FileUploadUtil.saveImage(filePart, "demo", getServletContext());

            // 设置展示信息
            req.setAttribute("originalFileName", originalFileName);
            req.setAttribute("fileSize", fileSize);
            req.setAttribute("contentType", contentType);
            req.setAttribute("savedPath", savedPath);
            req.setAttribute("uploadSuccess", true);

            forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "上传失败：" + e.getMessage());
            forward(req, resp);
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/knowledge/fileupload.jsp");
        dispatcher.forward(req, resp);
    }
}
