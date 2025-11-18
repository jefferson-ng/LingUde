import { Component, signal, inject, OnInit, HostListener } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TranslocoDirective } from '@jsverse/transloco';
import { UserLearningService } from './services/user-learning.service';
import { LucideAngularModule, Home, BookOpen, Target, Trophy, Settings, GraduationCap, LogOut } from 'lucide-angular';

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
  
  protected readonly title = signal('LingUDE');
  protected readonly userLevel = signal(5);
  protected readonly userXP = signal(0); // Will be loaded from backend
  protected readonly userStreak = signal(0); // Will be loaded from backend
  protected readonly userName = signal('Alex');
  protected readonly isUserDropdownOpen = signal(false);
  
  // TODO: Replace with actual logged-in user ID from authentication service
  // Fixed UUID that matches backend - will always be the same
  private readonly TEST_USER_ID = '7b88460b-04f2-4138-bb75-7658e27d1ba7';

  /**
   * Initialize component and load user XP data from backend
   */
  ngOnInit(): void {
    this.loadUserXP();
    
    // Subscribe to XP updates from anywhere in the app
    this.userLearningService.userLearning$.subscribe(data => {
      if (data) {
        this.userXP.set(data.xp);
        this.userStreak.set(data.streakCount);
        console.log('XP and Streak updated in header:', data.xp, data.streakCount);
      }
    });
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
