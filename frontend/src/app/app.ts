import { Component, signal, inject, OnInit, HostListener } from '@angular/core';
import { AuthService, UserInfo } from './services/auth.service';
import { Router, RouterLink, RouterLinkActive, RouterOutlet, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TranslocoDirective } from '@jsverse/transloco';
import { UserLearningService } from './services/user-learning.service';
import { LucideAngularModule, Home, BookOpen, Target, Trophy, Users, Settings, GraduationCap, LogOut, Menu, X, Shield, MessageCircle, Mic } from 'lucide-angular';
import { filter, skip } from 'rxjs/operators';
import { calculateLevelProgress } from './utils/level.utils';
import { ProfileModalComponent } from './components/profile-modal/profile-modal';
import { AchievementNotificationComponent } from './components/achievement-notification/achievement-notification';
import { AchievementNotificationService, AchievementNotification } from './services/achievement-notification.service';
import { ProfileService } from './services/profile.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule, TranslocoDirective, LucideAngularModule, ProfileModalComponent, AchievementNotificationComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private userLearningService = inject(UserLearningService);
  private router = inject(Router);
  private achievementNotificationService = inject(AchievementNotificationService);
  private profileService = inject(ProfileService);

  // Lucide Icons
  readonly HomeIcon = Home;
  readonly BookOpenIcon = BookOpen;
  readonly TargetIcon = Target;
  readonly TrophyIcon = Trophy;
  readonly SettingsIcon = Settings;
  readonly GraduationCapIcon = GraduationCap;
  readonly LogOutIcon = LogOut;
  readonly MenuIcon = Menu;
  readonly XIcon = X;
  readonly UsersIcon = Users;
  readonly ShieldIcon = Shield;
  readonly MessageCircleIcon = MessageCircle;
  readonly MicIcon = Mic;

  protected readonly title = signal('LingUDE');
  protected readonly isAdmin = signal(false);
  protected readonly userLevel = signal(1);
  protected readonly Math = Math;
  protected readonly userXP = signal(0);
  protected readonly userStreak = signal(0);
  protected readonly isStreakActiveToday = signal(false);
  protected readonly xpInCurrentLevel = signal(0);
  protected readonly xpForNextLevel = signal(100);
  protected readonly xpProgressPercent = signal(0);
  protected readonly userName = signal('');
  protected readonly userEmail = signal('');
  protected readonly userAvatarUrl = signal<string | null>(null);
  protected readonly isLoggedIn = signal(false);
  private auth = inject(AuthService);
  protected readonly isUserDropdownOpen = signal(false);
  protected readonly isProfileModalOpen = signal(false);
  protected readonly isSidebarOpen = signal(false);
  protected readonly isOnLevelSelection = signal(false);
  protected readonly currentAchievementNotification = signal<AchievementNotification | null>(null);

  /**
   * Initialize component and load user XP data from backend
   */
  ngOnInit(): void {
      // Ensure user info is loaded from storage on app startup
      this.auth.loadUserFromStorage();

    // Subscribe to achievement notifications
    this.achievementNotificationService.onAchievementUnlocked.subscribe(achievement => {
      this.currentAchievementNotification.set(achievement);
    });

    // Track current route to hide sidebar on level-selection
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.isOnLevelSelection.set(event.url.includes('/level-selection'));
    });

    // Check initial route
    this.isOnLevelSelection.set(this.router.url.includes('/level-selection'));

    this.auth.user$.subscribe((user: UserInfo | null) => {
      if (user) {
        this.userName.set(user.username);
        this.userEmail.set(user.email);
        this.isLoggedIn.set(true);
        this.isAdmin.set(user.role === 'ADMIN');
      } else {
        this.userName.set('');
        this.userEmail.set('');
        this.isLoggedIn.set(false);
        this.isAdmin.set(false);
      }
    });
    this.loadUserXP();
    this.loadUserAvatar();
    this.userLearningService.userLearning$.subscribe(data => {
      if (data) {
        console.log('📢 Header received update from UserLearningService:', {xp: data.xp, streak: data.streakCount, lastActivity: data.lastActivityDate});
        this.updateXPAndLevel(data.xp);
        this.userStreak.set(data.streakCount);
        // Streak ist aktiv, wenn lastActivityDate heute ist UND streakCount > 0
        const today = new Date().toISOString().split('T')[0];
        // Handle both ISO datetime and date-only formats
        const lastActivityDate = data.lastActivityDate?.includes('T')
          ? data.lastActivityDate.split('T')[0]
          : data.lastActivityDate;
        const isActive = lastActivityDate === today && data.streakCount > 0;
        this.isStreakActiveToday.set(isActive);
        console.log('📢 Header updated: XP=' + data.xp + ', Streak=' + data.streakCount + ', LastActivity=' + lastActivityDate + ', Today=' + today + ', Active=' + isActive);
      }
    });

    // Check for new achievements after XP/streak updates
    // filter(data => !!data) ignores null emissions from BehaviorSubject
    // skip(1) skips the first real data load (handled by loadUserXP)
    this.userLearningService.userLearning$.pipe(
      filter(data => !!data),
      skip(1)
    ).subscribe(() => {
      this.checkForNewAchievements();
    });
  }

  /**
   * Check for newly unlocked achievements and show notifications
   */
  private checkForNewAchievements(): void {
    if (!this.isLoggedIn()) return;

    this.profileService.getAllAchievements().subscribe({
      next: () => {
        // The ProfileService internally handles detecting new achievements
        // and triggers the notification via AchievementNotificationService
      },
      error: (err) => console.error('Error checking achievements:', err)
    });
  }
  async onLogout(): Promise<void> {
    this.userLearningService.clearCache();
    this.achievementNotificationService.reset();
    this.profileService.reset();
    this.userXP.set(0);
    this.userStreak.set(0);
    this.userAvatarUrl.set(null);
    await this.auth.logout();
  }

  /**
   * Load user avatar from backend
   */
  private loadUserAvatar(): void {
    this.profileService.getUserProfile().subscribe({
      next: (profile) => {
        this.userAvatarUrl.set(profile.avatarUrl);
      },
      error: (err) => {
        console.error('Error loading user avatar:', err);
      }
    });
  }

  /**
   * Load user XP from backend
   */
  private loadUserXP(): void {
    this.userLearningService.getUserLearning().subscribe({
      next: (data) => {
        this.updateXPAndLevel(data.xp);
        this.userStreak.set(data.streakCount);
        // Streak ist aktiv, wenn lastActivityDate heute ist UND streakCount > 0
        const today = new Date().toISOString().split('T')[0];
        // Handle both ISO datetime and date-only formats
        const lastActivityDate = data.lastActivityDate?.includes('T')
          ? data.lastActivityDate.split('T')[0]
          : data.lastActivityDate;
        const isActive = lastActivityDate === today && data.streakCount > 0;
        this.isStreakActiveToday.set(isActive);
        console.log('Loaded user XP and Streak in app header:', data.xp, data.streakCount, 'LastActivity:', lastActivityDate, 'Active today:', isActive);

        // Initialize achievements (loads previously unlocked to detect new ones later)
        this.profileService.getAllAchievements().subscribe();
      },
      error: (error) => {
        console.error('Error loading user XP:', error);
        // Keep default value on error
      }
    });
  }

  /**
   * Update XP and calculate level progress using exponential leveling system
   */
  private updateXPAndLevel(xp: number): void {
    this.userXP.set(xp);

    // Calculate level progress using exponential formula
    const progress = calculateLevelProgress(xp);
    this.userLevel.set(progress.level);
    this.xpInCurrentLevel.set(progress.xpInCurrentLevel);
    this.xpForNextLevel.set(progress.xpRequiredForNextLevel);
    this.xpProgressPercent.set(progress.progressPercent);
  }

  /**
   * Toggle user dropdown visibility (now opens profile modal)
   */
  toggleUserDropdown(): void {
    if (this.isLoggedIn()) {
      this.isProfileModalOpen.set(true);
    }
  }

  /**
   * Close user dropdown (kept for compatibility)
   */
  closeUserDropdown(): void {
    this.isUserDropdownOpen.set(false);
  }

  /**
   * Close profile modal
   */
  closeProfileModal(): void {
    this.isProfileModalOpen.set(false);
    // Refresh avatar in case it was changed
    this.loadUserAvatar();
  }

  /**
   * Handle achievement notification animation completion
   */
  onAchievementNotificationComplete(): void {
    this.currentAchievementNotification.set(null);
    // Notify the service to show the next queued notification
    this.achievementNotificationService.notificationComplete();
  }

  /**
   * Toggle sidebar visibility on mobile
   */
  toggleSidebar(): void {
    this.isSidebarOpen.set(!this.isSidebarOpen());
  }

  /**
   * Close sidebar
   */
  closeSidebar(): void {
    this.isSidebarOpen.set(false);
  }

  /**
   * Close dropdown when clicking outside
   */
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;

    // Close user dropdown if clicking outside
    const clickedInsideUserProfile = target.closest('.user-profile');
    if (!clickedInsideUserProfile && this.isUserDropdownOpen()) {
      this.closeUserDropdown();
    }
  }

  navItems = [
    { path: '/dashboard', translationKey: 'nav.dashboard', icon: 'home' },
    { path: '/learning', translationKey: 'nav.learning', icon: 'graduation-cap' },
    { path: '/pronunciation', translationKey: 'nav.pronunciation', icon: 'mic' },
    { path: '/chat', translationKey: 'nav.chat', icon: 'message-circle' },
    { path: '/friends', translationKey: 'nav.friends', icon: 'users' },
    { path: '/leaderboard', translationKey: 'nav.leaderboard', icon: 'trophy' }
  ];

  footerItems = [
    { path: '/settings', translationKey: 'nav.settings', icon: 'settings' }
  ];
}
