package com.iblog.model;

import java.sql.Date;

public class UserProfile {
    private int userId;
    private String nickname;
    private String avatarUrl;
    private String gender;
    private Date birthday;
    private String region;
    private String signature;
    private String interests;
    private String bgImageUrl;
    private String motto;
    private int level;
    private int points;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }
    public String getBgImageUrl() { return bgImageUrl; }
    public void setBgImageUrl(String bgImageUrl) { this.bgImageUrl = bgImageUrl; }
    public String getMotto() { return motto; }
    public void setMotto(String motto) { this.motto = motto; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}
