/**
 * Represents a single entry in the leaderboard
 * Contains user info, rank, XP, level, and streak data
 */
export interface LeaderboardEntry {
  rank: number;
  userId: string;
  username: string;
  displayName: string | null;
  avatarUrl: string | null;
  xp: number;
  level: number;
  streakCount: number;
}
