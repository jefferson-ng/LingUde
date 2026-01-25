import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AchievementNotificationService } from './achievement-notification.service';

/**
 * Achievement with unlock status for the profile modal
 */
export interface AchievementWithStatus {
  code: string;
  title: string;
  description: string;
  iconUrl: string | null;
  type: 'XP_MILESTONE' | 'STREAK' | 'LESSON_COMPLETION' | 'LEVEL_REACHED' | 'OTHER';
  unlocked: boolean;
  earnedAt: string | null;
}

/**
 * User profile response from the API
 */
export interface UserProfileResponse {
  username: string;
  displayName: string | null;
  avatarUrl: string | null;
  xp: number;
  currentLevel: string;
  achievements: AchievementWithStatus[];
}

/**
 * Service for fetching profile-related data including achievements.
 */
@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private readonly apiUrl = '/api/profile';
  private http = inject(HttpClient);
  private achievementNotificationService = inject(AchievementNotificationService);

  // Track previously unlocked achievements
  private previouslyUnlocked = new Set<string>();
  // Track if initial load has completed
  private isInitialized = false;

  constructor() {}

  /**
   * Fetches all achievements with their unlock status for the current user.
   * Returns both locked (grayed out) and unlocked achievements.
   *
   * @returns Observable of AchievementWithStatus array
   */
  getAllAchievements(): Observable<AchievementWithStatus[]> {
    return this.http.get<AchievementWithStatus[]>(`${this.apiUrl}/achievements/all`).pipe(
      tap(achievements => {
        // Check for newly unlocked achievements
        achievements.forEach(achievement => {
          if (achievement.unlocked && !this.previouslyUnlocked.has(achievement.code)) {
            // New achievement found that we haven't seen before
            // Show notification if:
            // 1. We've already done the initial load (isInitialized), OR
            // 2. The achievement was earned recently (within last 60 seconds)
            const isRecentlyEarned = this.isRecentlyEarned(achievement.earnedAt);

            if (this.isInitialized || isRecentlyEarned) {
              this.achievementNotificationService.showAchievement({
                code: achievement.code,
                title: achievement.title,
                description: achievement.description,
                type: achievement.type
              });
            }
            this.previouslyUnlocked.add(achievement.code);
          }
        });

        // Mark as initialized after first load
        this.isInitialized = true;
      })
    );
  }

  /**
   * Checks if an achievement was earned within the last 60 seconds.
   */
  private isRecentlyEarned(earnedAt: string | null): boolean {
    if (!earnedAt) return false;

    const earnedDate = new Date(earnedAt);
    const now = new Date();
    const diffInSeconds = (now.getTime() - earnedDate.getTime()) / 1000;

    // Use 60 seconds to account for timezone differences and processing delays
    return diffInSeconds <= 60;
  }

  /**
   * Reset the previously unlocked achievements tracker.
   * Should be called on logout.
   */
  reset(): void {
    this.previouslyUnlocked.clear();
    this.isInitialized = false;
  }

  /**
   * Fetches the current user's profile data.
   *
   * @returns Observable of UserProfileResponse
   */
  getUserProfile(): Observable<UserProfileResponse> {
    return this.http.get<UserProfileResponse>(`${this.apiUrl}/me`);
  }

  /**
   * Updates the avatar for the current user.
   *
   * @param avatarUrl the new avatar URL or null to remove
   * @returns Observable that completes when the update is done
   */
  updateAvatar(avatarUrl: string | null): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/avatar`, { avatarUrl });
  }
}
