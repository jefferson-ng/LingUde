import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface AchievementNotification {
  code: string;
  title: string;
  description: string;
  type: string;
}

@Injectable({
  providedIn: 'root'
})
export class AchievementNotificationService {
  // Observable for achievement unlocks
  private achievementUnlocked$ = new Subject<AchievementNotification>();

  // Public observable
  public readonly onAchievementUnlocked = this.achievementUnlocked$.asObservable();

  // Track shown achievements to avoid duplicates
  private shownAchievements = new Set<string>();

  // Queue for pending achievements
  private notificationQueue: AchievementNotification[] = [];
  private isShowingNotification = false;

  /**
   * Show an achievement notification (queued)
   */
  showAchievement(achievement: AchievementNotification): void {
    // Avoid showing the same achievement multiple times
    if (this.shownAchievements.has(achievement.code)) {
      return;
    }

    this.shownAchievements.add(achievement.code);
    this.notificationQueue.push(achievement);

    // Start showing if not already showing
    if (!this.isShowingNotification) {
      this.showNextNotification();
    }
  }

  /**
   * Called when a notification animation completes.
   * Shows the next notification in the queue if any.
   */
  notificationComplete(): void {
    this.isShowingNotification = false;
    this.showNextNotification();
  }

  /**
   * Shows the next notification from the queue
   */
  private showNextNotification(): void {
    if (this.notificationQueue.length === 0) {
      return;
    }

    this.isShowingNotification = true;
    const nextAchievement = this.notificationQueue.shift()!;
    this.achievementUnlocked$.next(nextAchievement);
  }

  /**
   * Reset shown achievements (e.g., on logout)
   */
  reset(): void {
    this.shownAchievements.clear();
    this.notificationQueue = [];
    this.isShowingNotification = false;
  }

  /**
   * Check if an achievement has been shown
   */
  hasBeenShown(code: string): boolean {
    return this.shownAchievements.has(code);
  }
}
