package com.iblog.model;

import java.sql.Timestamp;

public class Blog {
    private int id;
    private int userId;
    private String content;
    private String images;
    private String moodTag;
    private String sceneTag;
    private String location;
    private String visibility;
    private int isPinned;
    private String status;
    private Timestamp scheduleTime;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private int forwardCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Joined fields
    private String nickname;
    private String avatarUrl;
    private boolean liked;
    private boolean favorited;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public String getMoodTag() { return moodTag; }
    public void setMoodTag(String moodTag) { this.moodTag = moodTag; }
    public String getSceneTag() { return sceneTag; }
    public void setSceneTag(String sceneTag) { this.sceneTag = sceneTag; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public int getIsPinned() { return isPinned; }
    public void setIsPinned(int isPinned) { this.isPinned = isPinned; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getScheduleTime() { return scheduleTime; }
    public void setScheduleTime(Timestamp scheduleTime) { this.scheduleTime = scheduleTime; }
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    public int getForwardCount() { return forwardCount; }
    public void setForwardCount(int forwardCount) { this.forwardCount = forwardCount; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }
    public boolean isFavorited() { return favorited; }
    public void setFavorited(boolean favorited) { this.favorited = favorited; }
}
