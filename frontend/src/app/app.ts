import { Component, signal, inject, OnInit, HostListener } from '@angular/core';
import { AuthService, UserInfo } from './services/auth.service';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TranslocoDirective } from '@jsverse/transloco';
import { UserLearningService } from './services/user-learning.service';
import { LucideAngularModule, Home, BookOpen, Target, Trophy, Settings, GraduationCap, LogOut, Menu, X } from 'lucide-angular';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule, TranslocoDirective, LucideAngularModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private userLearningService = inject(UserLearningService);
  
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
  
  protected readonly title = signal('LingUDE');
  protected readonly userLevel = signal(1);
  protected readonly userXP = signal(0);
  protected readonly userStreak = signal(0);
  protected readonly userName = signal('');
  protected readonly userEmail = signal('');
  protected readonly isLoggedIn = signal(false);
  private auth = inject(AuthService);
  protected readonly isUserDropdownOpen = signal(false);
  protected readonly isSidebarOpen = signal(false);
  
    // TODO: Replace with actual logged-in user ID from authentication service
  // This is a temporary test user ID
  // Removed TEST_USER_ID; use authenticated user only

  /**
   * Initialize component and load user XP data from backend
   */
  ngOnInit(): void {
      // Ensure user info is loaded from storage on app startup
      this.auth.loadUserFromStorage();
    this.auth.user$.subscribe((user: UserInfo | null) => {
      if (user) {
        this.userName.set(user.username);
        this.userEmail.set(user.email);
        this.isLoggedIn.set(true);
      } else {
        this.userName.set('');
        this.userEmail.set('');
        this.isLoggedIn.set(false);
      }
    });
    this.loadUserXP();
    this.userLearningService.userLearning$.subscribe(data => {
      if (data) {
        this.userXP.set(data.xp);
        this.userStreak.set(data.streakCount);
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
        this.userXP.set(data.xp);
        this.userStreak.set(data.streakCount);
        console.log('Loaded user XP and Streak in app header:', data.xp, data.streakCount);
      },
      error: (error) => {
        console.error('Error loading user XP:', error);
        // Keep default value on error
      }
    });
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
    { path: '/lessons', translationKey: 'nav.lessons', icon: 'book-open' },
    { path: '/goals', translationKey: 'nav.goals', icon: 'target' },
    { path: '/leaderboard', translationKey: 'nav.leaderboard', icon: 'trophy' }
  ];
  
  footerItems = [
    { path: '/settings', translationKey: 'nav.settings', icon: 'settings' }
  ];
}
