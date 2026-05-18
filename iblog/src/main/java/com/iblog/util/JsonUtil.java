package com.iblog.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void sendJson(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(mapper.writeValueAsString(data));
        out.flush();
    }

    public static void sendSuccess(HttpServletResponse response) throws IOException {
        sendJson(response, Map.of("success", true));
    }

    public static void sendSuccess(HttpServletResponse response, String key, Object value) throws IOException {
        sendJson(response, Map.of("success", true, key, value));
    }

    public static void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        sendJson(response, Map.of("success", false, "error", message));
    }

    public static <T> T parseJson(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }
}
