export interface AdminUserSummary {
  userId: string;
  username: string;
  email: string;
  role: string;
  totalXp: number;
  streak: number;
}

export interface AdminUserAchievement {
  code: string;
  title: string;
  description: string;
  earnedAt: string;
}

export interface AdminUserDetail {
  userId: string;
  username: string;
  email: string;
  role: string;
  displayName: string | null;
  avatarUrl: string | null;
  bio: string | null;
  city: string | null;
  country: string | null;
  learningLanguage: string | null;
  currentLevel: string | null;
  targetLevel: string | null;
  xp: number;
  streakCount: number;
  lastActivityDate: string | null;
  achievements: AdminUserAchievement[];
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}