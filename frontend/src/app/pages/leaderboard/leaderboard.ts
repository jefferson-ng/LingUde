import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslocoDirective } from '@jsverse/transloco';
import { LucideAngularModule, Crown, Flame, Zap, Target, Globe, Users } from 'lucide-angular';
import { LeaderboardService } from '../../services/leaderboard.service';
import { UserLearningService } from '../../services/user-learning.service';
import { LeaderboardEntry } from '../../models/leaderboard.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-leaderboard',
  imports: [CommonModule, TranslocoDirective, LucideAngularModule],
  templateUrl: './leaderboard.html',
  styleUrl: './leaderboard.css'
})
export class Leaderboard implements OnInit {
  // Lucide Icons
  readonly CrownIcon = Crown;
  readonly FlameIcon = Flame;
  readonly ZapIcon = Zap;
  readonly TargetIcon = Target;
  readonly GlobeIcon = Globe;
  readonly UsersIcon = Users;

  // Signals for reactive state
  selectedLeaderboardType = signal<'friends' | 'global'>('global');
  friendsLeaderboardData = signal<LeaderboardEntry[]>([]);
  globalLeaderboardData = signal<LeaderboardEntry[]>([]);
  currentUserId = signal<string | null>(null);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);

  // Computed: Active leaderboard based on selection
  activeLeaderboard = computed(() => {
    return this.selectedLeaderboardType() === 'friends'
      ? this.friendsLeaderboardData()
      : this.globalLeaderboardData();
  });

  // Computed properties
  topThree = computed(() => this.activeLeaderboard().slice(0, 3));

  currentUserEntry = computed(() => {
    const userId = this.currentUserId();
    return this.activeLeaderboard().find(entry => entry.userId === userId);
  });

  // Top achievers (for sidebar)
  topStreakUser = computed(() => {
    const sorted = [...this.activeLeaderboard()].sort((a, b) =>
      b.streakCount - a.streakCount
    );
    return sorted[0];
  });

  constructor(
    private leaderboardService: LeaderboardService,
    private userLearningService: UserLearningService
  ) {}

  ngOnInit(): void {
    this.loadLeaderboard();
    this.loadCurrentUser();
  }

  /**
   * Load both leaderboards (friends and global) in parallel
   */
  loadLeaderboard(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    forkJoin({
      friends: this.leaderboardService.getFriendsLeaderboard(),
      global: this.leaderboardService.getGlobalLeaderboard()
    }).subscribe({
      next: (data) => {
        this.friendsLeaderboardData.set(data.friends);
        this.globalLeaderboardData.set(data.global);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error loading leaderboards:', err);
        this.errorMessage.set('Failed to load leaderboards');
        this.isLoading.set(false);
      }
    });
  }

  /**
   * Load current user data to identify them in the leaderboard
   */
  loadCurrentUser(): void {
    this.userLearningService.getUserLearning().subscribe({
      next: (data) => {
        this.currentUserId.set(data.userId);
      },
      error: (err) => {
        console.error('Error loading user data:', err);
      }
    });
  }

  /**
   * Get initials from user's display name or username
   */
  getInitials(entry: LeaderboardEntry): string {
    if (entry.displayName) {
      return entry.displayName
        .split(' ')
        .map(n => n[0])
        .join('')
        .toUpperCase()
        .slice(0, 2);
    }
    return entry.username.slice(0, 2).toUpperCase();
  }

  /**
   * Check if entry is the current user
   */
  isCurrentUser(entry: LeaderboardEntry): boolean {
    return entry.userId === this.currentUserId();
  }

  /**
   * Calculate XP needed to reach next rank
   */
  getXpToNextRank(): number {
    const current = this.currentUserEntry();
    if (!current || current.rank === 1) return 0;

    const userAbove = this.activeLeaderboard()[current.rank - 2];
    return userAbove ? userAbove.xp - current.xp : 0;
  }
}
