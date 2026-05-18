package com.iblog.servlet.activity;

import com.iblog.util.DBUtil;
import com.iblog.util.JsonUtil;
import com.iblog.util.SessionUtil;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Map;

public class SignInServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        LocalDate today = LocalDate.now();
        try (Connection conn = DBUtil.getConnection()) {
            // Check if already signed
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM sign_records WHERE user_id = ? AND sign_date = ?");
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(today));
            if (ps.executeQuery().next()) {
                JsonUtil.sendError(resp, 400, "今日已签到");
                return;
            }

            // Calculate consecutive days
            int consecutive = 0;
            LocalDate checkDate = today.minusDays(1);
            while (true) {
                ps = conn.prepareStatement("SELECT 1 FROM sign_records WHERE user_id = ? AND sign_date = ?");
                ps.setInt(1, userId);
                ps.setDate(2, Date.valueOf(checkDate));
                if (ps.executeQuery().next()) {
                    consecutive++;
                    checkDate = checkDate.minusDays(1);
                } else {
                    break;
                }
            }
            consecutive++; // today

            int points = Math.min(consecutive * 5, 50); // max 50 points per day
            ps = conn.prepareStatement("INSERT INTO sign_records (user_id, sign_date, points_earned) VALUES (?, ?, ?)");
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(today));
            ps.setInt(3, points);
            ps.executeUpdate();

            // Update user points
            ps = conn.prepareStatement("UPDATE user_profiles SET points = points + ? WHERE user_id = ?");
            ps.setInt(1, points);
            ps.setInt(2, userId);
            ps.executeUpdate();

            JsonUtil.sendJson(resp, Map.of("success", true, "data", Map.of(
                "points", points,
                "consecutive", consecutive
            )));
        } catch (SQLException e) {
            JsonUtil.sendError(resp, 500, "签到失败");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = SessionUtil.getCurrentUserId(req);
        if (userId == 0) { JsonUtil.sendError(resp, 401, "请先登录"); return; }

        LocalDate today = LocalDate.now();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM sign_records WHERE user_id = ? AND sign_date = ?")) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(today));
            boolean signed = ps.executeQuery().next();
            JsonUtil.sendJson(resp, Map.of("success", true, "signed", signed));
        } catch (SQLException e) {
            JsonUtil.sendError(resp, 500, "查询失败");
        }
    }
}
