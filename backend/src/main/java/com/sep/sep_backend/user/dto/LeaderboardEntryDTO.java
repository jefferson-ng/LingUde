package com.sep.sep_backend.user.dto;

import com.sep.sep_backend.user.entity.UserLearning;
import com.sep.sep_backend.user.entity.UserProfile;
import com.sep.sep_backend.util.LevelUtils;

/**
 * DTO representing a single entry in the leaderboard
 * Contains user info, rank, XP, level, and streak data
 */
public class LeaderboardEntryDTO {

    private Integer rank;
    private String userId;
    private String username;
    private String displayName;
    private String avatarUrl;
    private Integer xp;
    private Integer level;
    private Integer streakCount;

    // Default constructor
    public LeaderboardEntryDTO() {
    }

    /**
     * Constructor that builds DTO from UserLearning and UserProfile entities
     * @param rank The user's rank in the leaderboard (1-based)
     * @param userLearning User's learning data (xp, streak, etc.)
     * @param profile User's profile data (displayName, avatarUrl) - can be null
     */
    public LeaderboardEntryDTO(Integer rank, UserLearning userLearning, UserProfile profile) {
        this.rank = rank;
        this.userId = userLearning.getUser().getId().toString();
        this.username = userLearning.getUser().getUsername();
        this.displayName = profile != null ? profile.getDisplayName() : null;
        this.avatarUrl = profile != null ? profile.getAvatarUrl() : null;
        this.xp = userLearning.getXp();
        // Level calculation using exponential leveling system
        this.level = LevelUtils.calculateLevel(userLearning.getXp());
        this.streakCount = userLearning.getStreakCount();
    }

    // Getters and Setters
    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(Integer streakCount) {
        this.streakCount = streakCount;
    }
}
