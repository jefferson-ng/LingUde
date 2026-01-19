import { Component, signal, inject, OnInit, HostListener } from '@angular/core';
import { AuthService, UserInfo } from './services/auth.service';
import { Router, RouterLink, RouterLinkActive, RouterOutlet, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TranslocoDirective } from '@jsverse/transloco';
import { UserLearningService } from './services/user-learning.service';
import { LucideAngularModule, Home, BookOpen, Target, Trophy, Users, Settings, GraduationCap, LogOut, Menu, X, Shield } from 'lucide-angular';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule, TranslocoDirective, LucideAngularModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private userLearningService = inject(UserLearningService);
  private router = inject(Router);

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
  readonly  UsersIcon = Users;
  readonly ShieldIcon = Shield;

  protected readonly title = signal('LingUDE');
  protected readonly isAdmin = signal(false);
  protected readonly userLevel = signal(1);
  protected readonly userXP = signal(0);
  protected readonly userStreak = signal(0);
  protected readonly isStreakActiveToday = signal(false);
  protected readonly xpInCurrentLevel = signal(0);
  protected readonly xpForNextLevel = signal(100);
  protected readonly xpProgressPercent = signal(0);
  protected readonly userName = signal('');
  protected readonly userEmail = signal('');
  protected readonly isLoggedIn = signal(false);
  private auth = inject(AuthService);
  protected readonly isUserDropdownOpen = signal(false);
  protected readonly isSidebarOpen = signal(false);
  protected readonly isOnLevelSelection = signal(false);

    // TODO: Replace with actual logged-in user ID from authentication service
  // This is a temporary test user ID
  // Removed TEST_USER_ID; use authenticated user only

  /**
   * Initialize component and load user XP data from backend
   */
  ngOnInit(): void {
      // Ensure user info is loaded from storage on app startup
      this.auth.loadUserFromStorage();

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
  }
  async onLogout(): Promise<void> {
    this.userLearningService.clearCache();
    this.userXP.set(0);
    this.userStreak.set(0);
    await this.auth.logout();
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
      },
      error: (error) => {
        console.error('Error loading user XP:', error);
        // Keep default value on error
      }
    });
  }

  /**
   * Update XP and calculate level progress
   */
  private updateXPAndLevel(xp: number): void {
    this.userXP.set(xp);
    
    // Calculate level (100 XP per level)
    const level = Math.floor(xp / 100) + 1;
    this.userLevel.set(level);
    
    // Calculate XP progress within current level
    const xpInLevel = xp % 100;
    this.xpInCurrentLevel.set(xpInLevel);
    this.xpForNextLevel.set(100 - xpInLevel);
    
    // Calculate progress percentage for the bar
    const percent = (xpInLevel / 100) * 100;
    this.xpProgressPercent.set(percent);
  }

  /**
   * Toggle user dropdown visibility
   */
  toggleUserDropdown(): void {
    this.isUserDropdownOpen.set(!this.isUserDropdownOpen());
  }

  /**
   * Close user dropdown
   */
  closeUserDropdown(): void {
    this.isUserDropdownOpen.set(false);
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
    const clickedInside = target.closest('.user-profile');

    if (!clickedInside && this.isUserDropdownOpen()) {
      this.closeUserDropdown();
    }
  }

  navItems = [
    { path: '/dashboard', translationKey: 'nav.dashboard', icon: 'home' },
    { path: '/learning', translationKey: 'nav.learning', icon: 'graduation-cap' },
    { path: '/friends', translationKey: 'nav.friends', icon: 'users' },
    { path: '/leaderboard', translationKey: 'nav.leaderboard', icon: 'trophy' }
  ];

  footerItems = [
    { path: '/settings', translationKey: 'nav.settings', icon: 'settings' }
  ];
}
