package com.iblog.model;

import java.sql.Timestamp;

public class HotTrend {
    private int id;
    private String keyword;
    private int heat;
    private String relatedBlogs;
    private int isManual;
    private String status;
    private Timestamp createdAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public int getHeat() { return heat; }
    public void setHeat(int heat) { this.heat = heat; }
    public String getRelatedBlogs() { return relatedBlogs; }
    public void setRelatedBlogs(String relatedBlogs) { this.relatedBlogs = relatedBlogs; }
    public int getIsManual() { return isManual; }
    public void setIsManual(int isManual) { this.isManual = isManual; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
