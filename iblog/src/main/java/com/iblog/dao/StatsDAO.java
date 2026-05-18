package com.iblog.dao;

import com.iblog.util.DBUtil;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StatsDAO {

    public Map<String, Object> getOverview() {
        Map<String, Object> stats = new HashMap<>();
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users WHERE status != 'deleted'");
            if (rs.next()) stats.put("totalUsers", rs.getInt(1));

            rs = st.executeQuery("SELECT COUNT(*) FROM users WHERE status = 'active' AND DATE(created_at) = CURDATE()");
            if (rs.next()) stats.put("todayNewUsers", rs.getInt(1));

            rs = st.executeQuery("SELECT COUNT(*) FROM blogs WHERE status = 'published'");
            if (rs.next()) stats.put("totalBlogs", rs.getInt(1));

            rs = st.executeQuery("SELECT COUNT(*) FROM blogs WHERE status = 'published' AND DATE(created_at) = CURDATE()");
            if (rs.next()) stats.put("todayNewBlogs", rs.getInt(1));

            rs = st.executeQuery("SELECT COUNT(*) FROM comments WHERE status = 'normal'");
            if (rs.next()) stats.put("totalComments", rs.getInt(1));

            rs = st.executeQuery("SELECT COUNT(*) FROM circles WHERE status = 'active'");
            if (rs.next()) stats.put("totalCircles", rs.getInt(1));

            rs = st.executeQuery("SELECT COUNT(*) FROM reports WHERE status = 'pending'");
            if (rs.next()) stats.put("pendingReports", rs.getInt(1));

        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }
}
