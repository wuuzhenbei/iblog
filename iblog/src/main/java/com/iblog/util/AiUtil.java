package com.iblog.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AiUtil {
    private static final String AI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = System.getenv("AI_API_KEY");
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String chat(String systemPrompt, String userInput) {
        try {
            URL url = new URL(AI_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            String body = String.format(
                "{\"model\":\"gpt-3.5-turbo\",\"messages\":[{\"role\":\"system\",\"content\":\"%s\"},{\"role\":\"user\",\"content\":\"%s\"}]}",
                escapeJson(systemPrompt), escapeJson(userInput)
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() != 200) {
                return "AI 服务暂时不可用";
            }

            try (InputStream is = conn.getInputStream()) {
                JsonNode root = mapper.readTree(is);
                return root.at("/choices/0/message/content").asText("AI 响应异常");
            }
        } catch (Exception e) {
            return "AI 服务暂时不可用";
        }
    }

    public static String generateCopywriting(String style, String topic) {
        String prompt = "请生成一段" + style + "风格的文案，主题：" + topic + "，150字以内";
        return chat("你是一个专业的文案创作助手，擅长各种风格的文案写作", prompt);
    }

    public static String polishText(String text, String action) {
        return chat("你是一个文字润色专家，对用户文字进行" + action + "，保持原意不变", text);
    }

    public static String generateComment(String blogContent) {
        String truncated = blogContent.length() > 500 ? blogContent.substring(0, 500) : blogContent;
        return chat("针对以下博文，生成一条友善、有深度的评论，50字以内", truncated);
    }

    public static String analyzeEmotion(String text) {
        return chat("分析这段文字的情绪，只回复一个词：开心/伤心/愤怒/焦虑/平淡/感动/期待", text);
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
