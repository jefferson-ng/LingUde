import { Component, signal, inject, OnInit, computed } from '@angular/core';
import { TranslocoDirective } from '@jsverse/transloco';
import { CommonModule } from '@angular/common';
import { UserLearningService } from '../../services/user-learning.service';
import { LeaderboardService } from '../../services/leaderboard.service';
import { ExerciseService } from '../../services/exercise.service';
import { LeaderboardEntry } from '../../models/leaderboard.model';
import { LucideAngularModule, Flame } from 'lucide-angular';

@Component({
  selector: 'app-dashboard',
  imports: [TranslocoDirective, CommonModule, LucideAngularModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  private userLearningService = inject(UserLearningService);
  private leaderboardService = inject(LeaderboardService);
  private exerciseService = inject(ExerciseService);

  // Icons
  readonly FlameIcon = Flame;

  // User progress data
  protected userXP = signal(0);
  protected userStreak = signal(0);
  protected isStreakActiveToday = signal(false);
  protected userLevel = signal(1);
  protected xpForCurrentLevel = signal(0);
  protected xpForNextLevel = signal(100);
  protected progressPercent = signal(0);

  // Dashboard stats (real data)
  protected completedLevelsCount = signal(0);
  protected exercisesToReviewCount = signal(0);
  protected userRank = signal<number | null>(null);

  // User learning preferences
  protected learningLanguage = signal<string>('');
  protected currentLevel = signal<string>('');
  protected targetLevel = signal<string>('');

  // Leaderboard data
  protected friendsLeaderboardData = signal<LeaderboardEntry[]>([]);
  protected globalLeaderboardData = signal<LeaderboardEntry[]>([]);
  protected currentUserId = signal<string | null>(null);

  // Top 3 friends for dashboard widget
  protected topThree = computed(() => this.friendsLeaderboardData().slice(0, 3));

  ngOnInit(): void {
    this.loadUserData();
    this.loadLeaderboardData();
    this.loadExercisesToReview();

    // Subscribe to XP updates
    this.userLearningService.userLearning$.subscribe(data => {
      if (data) {
        this.currentUserId.set(data.userId);
        this.updateUserProgress(data.xp, data.streakCount);
        // Update streak active status - nur aktiv wenn heute UND streakCount > 0
        const today = new Date().toISOString().split('T')[0];
        const lastActivityDate = data.lastActivityDate?.split('T')[0];
        this.isStreakActiveToday.set(lastActivityDate === today && data.streakCount > 0);
        this.updateLearningInfo(data.learningLanguage, data.currentLevel, data.targetLevel);
        // Update completed levels count from user data
        this.updateCompletedLevelsCount(data.completedLevels);
        // Update user rank from global leaderboard
        this.updateUserRank(this.globalLeaderboardData());
      }
    });
  }

  private loadLeaderboardData(): void {
    // Load friends leaderboard for the big display
    this.leaderboardService.getFriendsLeaderboard().subscribe({
      next: (data) => {
        this.friendsLeaderboardData.set(data);
      },
      error: (err) => {
        console.error('Error loading friends leaderboard for dashboard:', err);
      }
    });

    // Load global leaderboard for user rank
    this.leaderboardService.getGlobalLeaderboard().subscribe({
      next: (data) => {
        this.globalLeaderboardData.set(data);
        // Find current user's global rank
        this.updateUserRank(data);
      },
      error: (err) => {
        console.error('Error loading global leaderboard for dashboard:', err);
      }
    });
  }

  private updateUserRank(leaderboard: LeaderboardEntry[]): void {
    const userId = this.currentUserId();
    if (userId) {
      const userEntry = leaderboard.find(entry => entry.userId === userId);
      this.userRank.set(userEntry?.rank ?? null);
    }
  }

  protected isCurrentUser(entry: LeaderboardEntry): boolean {
    return entry.userId === this.currentUserId();
  }

  private loadUserData(): void {
    this.userLearningService.getUserLearning().subscribe({
      next: (data) => {
        this.updateUserProgress(data.xp, data.streakCount);
        // Update streak active status - nur aktiv wenn heute UND streakCount > 0
        const today = new Date().toISOString().split('T')[0];
        const lastActivityDate = data.lastActivityDate?.split('T')[0];
        this.isStreakActiveToday.set(lastActivityDate === today && data.streakCount > 0);
        this.updateLearningInfo(data.learningLanguage, data.currentLevel, data.targetLevel);
      },
      error: (error) => {
        console.error('Error loading user data:', error);
      }
    });
  }

  private updateUserProgress(xp: number, streak: number): void {
    this.userXP.set(xp);
    this.userStreak.set(streak);

    // Calculate level (100 XP per level)
    const level = Math.floor(xp / 100) + 1;
    this.userLevel.set(level);

    // Calculate XP progress within current level
    const xpInCurrentLevel = xp % 100;
    this.xpForCurrentLevel.set(xpInCurrentLevel);
    this.xpForNextLevel.set(100 - xpInCurrentLevel);

    // Calculate progress percentage
    const percent = (xpInCurrentLevel / 100) * 100;
    this.progressPercent.set(percent);
  }

  private updateLearningInfo(language: string | null, current: string, target: string): void {
    this.learningLanguage.set(this.getLanguageName(language));
    this.currentLevel.set(current);
    this.targetLevel.set(target);
  }

  private getLanguageName(code: string | null): string {
    const languageMap: Record<string, string> = {
      'DE': 'German',
      'EN': 'English',
      'FR': 'French',
      'ES': 'Spanish',
      'IT': 'Italian'
    };
    return code ? languageMap[code] || code : 'Not selected';
  }

  /**
   * Load exercises that need review from backend
   */
  private loadExercisesToReview(): void {
    this.exerciseService.getIncorrectExercises().subscribe({
      next: (exercises) => {
        this.exercisesToReviewCount.set(exercises.length);
      },
      error: (err) => {
        console.error('Error loading exercises to review:', err);
        this.exercisesToReviewCount.set(0);
      }
    });
  }

  /**
   * Parse completedLevels string and count total completed levels
   * Format: "DE-A1:1,2;EN-A1:1,3;DE-B1:1"
   */
  private updateCompletedLevelsCount(completedLevels: string | undefined): void {
    if (!completedLevels) {
      this.completedLevelsCount.set(0);
      return;
    }

    let totalCount = 0;
    const difficultyGroups = completedLevels.split(';');
    for (const group of difficultyGroups) {
      if (group.trim()) {
        const [, levelsStr] = group.split(':');
        if (levelsStr) {
          const levels = levelsStr.split(',').filter(l => l.trim());
          totalCount += levels.length;
        }
      }
    }
    this.completedLevelsCount.set(totalCount);
  }
}
