package com.sep.sep_backend.user.entity;

/**
 * Category of an achievement.
 * Helps us later to know if it comes from XP, streaks, level, etc.
 */
public enum AchievementType {
    XP_MILESTONE,      // e.g. reach 100 XP, 500 XP
    STREAK,            // e.g. 7-day streak, 30-day streak
    LESSON_COMPLETION, // e.g. finish first lesson, finish 10 lessons
    LEVEL_REACHED,     // e.g. reach A2, B1, etc.
    OTHER              // fallback / custom
}
