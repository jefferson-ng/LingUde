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

/**
 * Get the XP required to complete a specific level (go from level to level+1)
 * @param level The current level (1-based)
 * @returns XP required to complete this level
 */
export function getXpRequiredForLevel(level: number): number {
  // Level 1 requires 30 XP, Level 2 requires 35 XP, etc.
  return 25 + (5 * level);
}

/**
 * Get the total XP needed to reach a specific level from level 1
 * @param level The target level (1-based)
 * @returns Total XP accumulated to reach this level
 */
export function getTotalXpForLevel(level: number): number {
  if (level <= 1) return 0;

  // Sum of XP for levels 1 to (level-1)
  // Formula: sum from i=1 to n-1 of (25 + 5i) = 25(n-1) + 5 * (n-1)*n/2
  const n = level - 1;
  return 25 * n + (5 * n * (n + 1)) / 2;
}

/**
 * Calculate the user's level based on total accumulated XP
 * XP resets to 0 when leveling up, so this calculates which level the user is at
 * @param totalXp The user's total XP
 * @returns The user's current level (1-based)
 */
export function calculateLevel(totalXp: number): number {
  if (totalXp <= 0) return 1;

  let level = 1;
  let xpNeeded = getXpRequiredForLevel(level); // 30 XP for level 1
  let cumulativeXp = 0;

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
 * @returns Object with current level info
 */
export function calculateLevelProgress(totalXp: number): {
  level: number;
  xpInCurrentLevel: number;
  xpRequiredForNextLevel: number;
  progressPercent: number;
} {
  if (totalXp <= 0) {
    return {
      level: 1,
      xpInCurrentLevel: 0,
      xpRequiredForNextLevel: getXpRequiredForLevel(1), // 30 XP
      progressPercent: 0
    };
  }

  const level = calculateLevel(totalXp);
  const totalXpForCurrentLevel = getTotalXpForLevel(level);
  const xpInCurrentLevel = totalXp - totalXpForCurrentLevel;
  const xpRequiredForNextLevel = getXpRequiredForLevel(level);
  const progressPercent = (xpInCurrentLevel / xpRequiredForNextLevel) * 100;

  return {
    level,
    xpInCurrentLevel,
    xpRequiredForNextLevel,
    progressPercent: Math.min(progressPercent, 100)
  };
}

/**
 * Get a summary of XP requirements for display purposes
 * Useful for debugging or showing level progression info
 */
export function getLevelSummary(maxLevel: number = 20): Array<{
  level: number;
  xpToComplete: number;
  totalXpAtLevel: number;
}> {
  const summary = [];
  for (let level = 1; level <= maxLevel; level++) {
    summary.push({
      level,
      xpToComplete: getXpRequiredForLevel(level),
      totalXpAtLevel: getTotalXpForLevel(level)
    });
  }
  return summary;
}
