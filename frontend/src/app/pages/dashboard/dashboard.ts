import { Component, signal, inject, OnInit, computed } from '@angular/core';
import { TranslocoDirective } from '@jsverse/transloco';
import { CommonModule } from '@angular/common';
import { UserLearningService } from '../../services/user-learning.service';
import { LeaderboardService } from '../../services/leaderboard.service';
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

  // User learning preferences
  protected learningLanguage = signal<string>('');
  protected currentLevel = signal<string>('');
  protected targetLevel = signal<string>('');

  // Leaderboard data
  protected leaderboardData = signal<LeaderboardEntry[]>([]);
  protected currentUserId = signal<string | null>(null);

  // Top 3 for dashboard widget
  protected topThree = computed(() => this.leaderboardData().slice(0, 3));

  ngOnInit(): void {
    this.loadUserData();
    this.loadLeaderboardData();

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
      }
    });
  }

  private loadLeaderboardData(): void {
    this.leaderboardService.getFriendsLeaderboard().subscribe({
      next: (data) => {
        this.leaderboardData.set(data);
      },
      error: (err) => {
        console.error('Error loading leaderboard for dashboard:', err);
      }
    });
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
}
