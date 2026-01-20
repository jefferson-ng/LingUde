package com.sep.sep_backend.util;

/**
 * XP and Level calculation utilities
 *
 * Exponential leveling system:
 * - Level 1 → 2: 30 XP
 * - Level 2 → 3: 35 XP
 * - Level 3 → 4: 40 XP
 * - Each subsequent level requires 5 more XP than the previous
 *
 * Formula for XP required to go from level n to n+1: 25 + (5 * n)
 * Total XP needed to reach level n: sum of all previous level requirements
 */
public class LevelUtils {

    /**
     * Get the XP required to complete a specific level (go from level to level+1)
     * @param level The current level (1-based)
     * @return XP required to complete this level
     */
    public static int getXpRequiredForLevel(int level) {
        // Level 1 requires 30 XP, Level 2 requires 35 XP, etc.
        return 25 + (5 * level);
    }

    /**
     * Get the total XP needed to reach a specific level from level 1
     * @param level The target level (1-based)
     * @return Total XP accumulated to reach this level
     */
    public static int getTotalXpForLevel(int level) {
        if (level <= 1) return 0;

        // Sum of XP for levels 1 to (level-1)
        // Formula: sum from i=1 to n-1 of (25 + 5i) = 25(n-1) + 5 * (n-1)*n/2
        int n = level - 1;
        return 25 * n + (5 * n * (n + 1)) / 2;
    }

    /**
     * Calculate the user's level based on total accumulated XP
     * @param totalXp The user's total XP
     * @return The user's current level (1-based)
     */
    public static int calculateLevel(int totalXp) {
        if (totalXp <= 0) return 1;

        int level = 1;
        int xpNeeded = getXpRequiredForLevel(level); // 30 XP for level 1
        int cumulativeXp = 0;

        while (cumulativeXp + xpNeeded <= totalXp) {
            cumulativeXp += xpNeeded;
            level++;
            xpNeeded = getXpRequiredForLevel(level);
        }

        return level;
    }

    /**
     * Calculate XP progress within the current level
     * @param totalXp The user's total XP
     * @return XP accumulated within the current level
     */
    public static int getXpInCurrentLevel(int totalXp) {
        if (totalXp <= 0) return 0;
        int level = calculateLevel(totalXp);
        int totalXpForCurrentLevel = getTotalXpForLevel(level);
        return totalXp - totalXpForCurrentLevel;
    }

    /**
     * Calculate progress percentage within the current level
     * @param totalXp The user's total XP
     * @return Progress percentage (0-100)
     */
    public static double getProgressPercent(int totalXp) {
        if (totalXp <= 0) return 0;
        int level = calculateLevel(totalXp);
        int xpInCurrentLevel = getXpInCurrentLevel(totalXp);
        int xpRequiredForNextLevel = getXpRequiredForLevel(level);
        return Math.min((double) xpInCurrentLevel / xpRequiredForNextLevel * 100, 100);
    }
}
