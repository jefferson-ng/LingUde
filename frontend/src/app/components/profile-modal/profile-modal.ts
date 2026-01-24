import { Component, signal, output, inject, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslocoDirective, TranslocoService } from '@jsverse/transloco';
import { LucideAngularModule, LogOut, X, Trophy, Flame, Star, Lock, Zap, TrendingUp, Target, Award, BookOpen, GraduationCap, Medal, LucideIconData } from 'lucide-angular';
import { ProfileService, AchievementWithStatus } from '../../services/profile.service';
import { AuthService } from '../../services/auth.service';
import { UserLearningService } from '../../services/user-learning.service';
import { calculateLevelProgress } from '../../utils/level.utils';
import { ShareButtonComponent } from '../share-button/share-button';

interface GroupedAchievement {
  code: string;
  title: string;
  description: string;
  type: string;
  isGroup: boolean;
  milestones?: AchievementWithStatus[];
  currentProgress?: number;
  targetProgress?: number;
  progressPercent?: number;
  unlocked: boolean;
  mainAchievement?: AchievementWithStatus;
}

// XP Milestone requirements
const XP_MILESTONES = [
  { code: 'XP_100', required: 100 },
  { code: 'XP_600', required: 600 }
];

// Streak Milestone requirements
const STREAK_MILESTONES = [
  { code: 'STREAK_3', required: 3 },
  { code: 'STREAK_7', required: 7 }
];

@Component({
  selector: 'app-profile-modal',
  imports: [CommonModule, TranslocoDirective, LucideAngularModule, ShareButtonComponent],
  templateUrl: './profile-modal.html',
  styleUrl: './profile-modal.css',
  standalone: true
})
export class ProfileModalComponent implements OnInit {
  private profileService = inject(ProfileService);
  private authService = inject(AuthService);
  private userLearningService = inject(UserLearningService);
  private transloco = inject(TranslocoService);

  // Icons
  readonly LogOutIcon = LogOut;
  readonly CloseIcon = X;
  readonly TrophyIcon = Trophy;
  readonly FlameIcon = Flame;
  readonly StarIcon = Star;
  readonly LockIcon = Lock;
  readonly ZapIcon = Zap;
  readonly TrendingUpIcon = TrendingUp;
  readonly TargetIcon = Target;
  readonly AwardIcon = Award;
  readonly BookOpenIcon = BookOpen;
  readonly GraduationCapIcon = GraduationCap;
  readonly MedalIcon = Medal;

  // Outputs
  close = output<void>();
  logout = output<void>();

  // State
  protected readonly isLoading = signal(true);
  protected readonly userName = signal('');
  protected readonly userEmail = signal('');
  protected readonly userLevel = signal(1);
  protected readonly userXP = signal(0);
  protected readonly userStreak = signal(0);
  protected readonly xpInCurrentLevel = signal(0);
  protected readonly xpForNextLevel = signal(100);
  protected readonly xpProgressPercent = signal(0);
  protected readonly achievements = signal<AchievementWithStatus[]>([]);
  protected readonly isClosing = signal(false);
  protected readonly isStreakActiveToday = signal(false);

  // Grouped achievements for display
  protected readonly groupedAchievements = signal<GroupedAchievement[]>([]);

  ngOnInit(): void {
    this.loadUserData();
    this.loadAchievements();
  }

  private loadUserData(): void {
    // Get user info from auth service
    this.authService.user$.subscribe(user => {
      if (user) {
        this.userName.set(user.username);
        this.userEmail.set(user.email);
      }
    });

    // Get XP and streak from user learning service
    this.userLearningService.userLearning$.subscribe(data => {
      if (data) {
        this.userXP.set(data.xp);
        this.userStreak.set(data.streakCount);
        const progress = calculateLevelProgress(data.xp);
        this.userLevel.set(progress.level);
        this.xpInCurrentLevel.set(progress.xpInCurrentLevel);
        this.xpForNextLevel.set(progress.xpRequiredForNextLevel);
        this.xpProgressPercent.set(progress.progressPercent);
        
        // Check if streak is active today
        const today = new Date().toISOString().split('T')[0];
        const lastActivityDate = data.lastActivityDate?.split('T')[0];
        this.isStreakActiveToday.set(lastActivityDate === today && data.streakCount > 0);
      }
    });
  }

  private loadAchievements(): void {
    this.profileService.getAllAchievements().subscribe({
      next: (achievements) => {
        this.achievements.set(achievements);
        this.groupAchievements(achievements);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load achievements:', err);
        this.isLoading.set(false);
      }
    });
  }

  onClose(): void {
    this.isClosing.set(true);
    setTimeout(() => this.close.emit(), 300);
  }

  onLogout(): void {
    this.logout.emit();
    this.onClose();
  }

  @HostListener('document:keydown.escape')
  onEscapeKey(): void {
    this.onClose();
  }

  onOverlayClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('profile-modal-overlay')) {
      this.onClose();
    }
  }

  getUserInitial(): string {
    return this.userName() ? this.userName().charAt(0).toUpperCase() : '?';
  }

  getUnlockedCount(): number {
    return this.achievements().filter(a => a.unlocked).length;
  }

  getTotalCount(): number {
    return this.achievements().length;
  }

  /**
   * Returns the appropriate icon for an achievement based on its type
   */
  getAchievementIcon(achievement: AchievementWithStatus): LucideIconData {
    switch (achievement.type) {
      case 'XP_MILESTONE':
        return this.ZapIcon;          // ⚡ Lightning bolt for XP
      case 'STREAK':
        return this.FlameIcon;        // 🔥 Flame for streaks
      case 'LESSON_COMPLETION':
        return this.BookOpenIcon;     // 📖 Book for lessons
      case 'LEVEL_REACHED':
        return this.GraduationCapIcon; // 🎓 Graduation cap for levels
      default:
        return this.TrophyIcon;       // 🏆 Trophy as default
    }
  }

  /**
   * Groups achievements and calculates progress
   */
  private groupAchievements(achievements: AchievementWithStatus[]): void {
    const grouped: GroupedAchievement[] = [];
    
    // Group XP achievements
    const xpAchievements = achievements.filter(a => a.type === 'XP_MILESTONE').sort((a, b) => {
      const aReq = XP_MILESTONES.find(m => m.code === a.code)?.required || 0;
      const bReq = XP_MILESTONES.find(m => m.code === b.code)?.required || 0;
      return aReq - bReq;
    });

    if (xpAchievements.length > 0) {
      // Find the next uncompleted achievement or use the highest if all completed
      let targetMilestone = xpAchievements.find(a => !a.unlocked);
      if (!targetMilestone) {
        // All completed, use the highest milestone
        targetMilestone = xpAchievements[xpAchievements.length - 1];
      }
      
      const targetMilestoneData = XP_MILESTONES.find(m => m.code === targetMilestone.code);
      const targetXP = targetMilestoneData?.required || 0;
      
      // Calculate base XP (from previous milestone)
      const previousIndex = xpAchievements.indexOf(targetMilestone) - 1;
      const baseXP = previousIndex >= 0 ? 
        (XP_MILESTONES.find(m => m.code === xpAchievements[previousIndex].code)?.required || 0) : 0;
      
      const currentXP = this.userXP();
      const relativeProgress = currentXP - baseXP;
      const relativeTarget = targetXP - baseXP;
      const allUnlocked = xpAchievements.every(a => a.unlocked);
      
      grouped.push({
        code: 'XP_GROUP',
        title: this.transloco.translate('profile.achievementGroups.xpCollector'),
        description: this.transloco.translate('profile.achievementGroups.xpCollectorDesc'),
        type: 'XP_MILESTONE',
        isGroup: true,
        milestones: xpAchievements,
        currentProgress: currentXP,
        targetProgress: targetXP,
        progressPercent: Math.min((relativeProgress / relativeTarget) * 100, 100),
        unlocked: allUnlocked,
        mainAchievement: xpAchievements[0]
      });
    }

    // Group STREAK achievements
    const streakAchievements = achievements.filter(a => a.type === 'STREAK').sort((a, b) => {
      const aReq = STREAK_MILESTONES.find(m => m.code === a.code)?.required || 0;
      const bReq = STREAK_MILESTONES.find(m => m.code === b.code)?.required || 0;
      return aReq - bReq;
    });

    if (streakAchievements.length > 0) {
      // Find the next uncompleted achievement or use the highest if all completed
      let targetMilestone = streakAchievements.find(a => !a.unlocked);
      if (!targetMilestone) {
        // All completed, use the highest milestone
        targetMilestone = streakAchievements[streakAchievements.length - 1];
      }
      
      const targetMilestoneData = STREAK_MILESTONES.find(m => m.code === targetMilestone.code);
      const targetStreak = targetMilestoneData?.required || 0;
      
      // Calculate base streak (from previous milestone)
      const previousIndex = streakAchievements.indexOf(targetMilestone) - 1;
      const baseStreak = previousIndex >= 0 ? 
        (STREAK_MILESTONES.find(m => m.code === streakAchievements[previousIndex].code)?.required || 0) : 0;
      
      const currentStreak = this.userStreak();
      const relativeProgress = currentStreak - baseStreak;
      const relativeTarget = targetStreak - baseStreak;
      const allUnlocked = streakAchievements.every(a => a.unlocked);
      
      grouped.push({
        code: 'STREAK_GROUP',
        title: this.transloco.translate('profile.achievementGroups.streakMaster'),
        description: this.transloco.translate('profile.achievementGroups.streakMasterDesc'),
        type: 'STREAK',
        isGroup: true,
        milestones: streakAchievements,
        currentProgress: currentStreak,
        targetProgress: targetStreak,
        progressPercent: Math.min((relativeProgress / relativeTarget) * 100, 100),
        unlocked: allUnlocked,
        mainAchievement: streakAchievements[0]
      });
    }

    // Add other achievements individually with progress
    achievements.filter(a => a.type !== 'XP_MILESTONE' && a.type !== 'STREAK').forEach(achievement => {
      const progress = this.calculateAchievementProgress(achievement);
      grouped.push({
        ...achievement,
        isGroup: false,
        currentProgress: progress.current,
        targetProgress: progress.target,
        progressPercent: progress.percent,
        mainAchievement: achievement
      });
    });

    this.groupedAchievements.set(grouped);
  }

  /**
   * Calculates progress for individual achievements
   */
  private calculateAchievementProgress(achievement: AchievementWithStatus): { current: number, target: number, percent: number } {
    // STREAK achievements
    if (achievement.code === 'STREAK_3') {
      const current = this.userStreak();
      const target = 3;
      return {
        current,
        target,
        percent: Math.min((current / target) * 100, 100)
      };
    }
    
    if (achievement.code === 'STREAK_7') {
      const current = this.userStreak();
      const target = 7;
      return {
        current,
        target,
        percent: Math.min((current / target) * 100, 100)
      };
    }

    // Default: no progress
    return { current: 0, target: 1, percent: achievement.unlocked ? 100 : 0 };
  }
}
