package com.iblog.util;

import javax.servlet.http.Part;
import java.io.IOException;
import java.util.UUID;

public class FileUploadUtil {
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};

    // 保存上传的图片，返回相对路径
    public static String saveImage(Part part, String subDir, javax.servlet.ServletContext servletContext) throws IOException {
        // 校验文件类型
        String contentType = part.getContentType();
        boolean allowed = false;
        for (String type : ALLOWED_TYPES) {
            if (type.equals(contentType)) { allowed = true; break; }
        }
        if (!allowed) throw new IOException("不支持的文件类型: " + contentType);

        // 校验文件大小
        if (part.getSize() > MAX_SIZE) throw new IOException("文件大小超过5MB限制");

        // 生成唯一文件名
        String ext = getExtension(part.getSubmittedFileName());
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        // 使用 getRealPath 获取真实路径
        String uploadDir = servletContext.getRealPath("/uploads/" + subDir);
        java.io.File dirFile = new java.io.File(uploadDir);
        if (!dirFile.exists()) dirFile.mkdirs();

        // 保存文件
        String fullPath = uploadDir + java.io.File.separator + fileName;
        part.write(fullPath);

        // 返回相对路径
        return "/uploads/" + subDir + "/" + fileName;
    }

    // 获取文件扩展名
    private static String getExtension(String fileName) {
        if (fileName == null) return "";
        int lastDot = fileName.lastIndexOf('.');
        return lastDot >= 0 ? fileName.substring(lastDot) : "";
    }

    // 从 Part 中提取原始文件名
    public static String getSubmittedFileName(Part part) {
        String cd = part.getHeader("content-disposition");
        if (cd == null) return null;
        for (String token : cd.split(";")) {
            token = token.trim();
            if (token.startsWith("filename=")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
