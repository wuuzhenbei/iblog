package com.iblog.model;

import java.sql.Timestamp;
import java.util.List;

public class Comment {
    private int id;
    private int blogId;
    private int userId;
    private Integer parentId;
    private Integer replyToUserId;
    private String content;
    private int likeCount;
    private String status;
    private Timestamp createdAt;

    private String nickname;
    private String avatarUrl;
    private String replyToNickname;
    private List<Comment> children;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBlogId() { return blogId; }
    public void setBlogId(int blogId) { this.blogId = blogId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
    public Integer getReplyToUserId() { return replyToUserId; }
    public void setReplyToUserId(Integer replyToUserId) { this.replyToUserId = replyToUserId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getReplyToNickname() { return replyToNickname; }
    public void setReplyToNickname(String replyToNickname) { this.replyToNickname = replyToNickname; }
    public List<Comment> getChildren() { return children; }
    public void setChildren(List<Comment> children) { this.children = children; }
}
