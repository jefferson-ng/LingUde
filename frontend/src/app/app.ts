import { Component, signal, inject, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TranslocoDirective } from '@jsverse/transloco';
import { UserLearningService } from './services/user-learning.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule, TranslocoDirective],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private userLearningService = inject(UserLearningService);
  
  protected readonly title = signal('LingUDE');
  protected readonly userLevel = signal(5);
  protected readonly userXP = signal(0); // Will be loaded from backend
  protected readonly userName = signal('Alex');
  
  // TODO: Replace with actual logged-in user ID from authentication service
  // IMPORTANT: This ID must match the one printed in backend console on startup
  // Look for: "📊 Test User ID: [uuid]" in the backend logs
  private readonly TEST_USER_ID = 'f1586c14-a0b9-4dd6-917e-0ca7938595fa';

  /**
   * Initialize component and load user XP data from backend
   */
  ngOnInit(): void {
    this.loadUserXP();
    
    // Subscribe to XP updates from anywhere in the app
    this.userLearningService.userLearning$.subscribe(data => {
      if (data) {
        this.userXP.set(data.xp);
        console.log('🎯 XP updated in header:', data.xp);
      }
    });
  }

  /**
   * Load user XP from backend
   */
  private loadUserXP(): void {
    this.userLearningService.getUserLearning(this.TEST_USER_ID).subscribe({
      next: (data) => {
        this.userXP.set(data.xp);
        console.log('✅ Loaded user XP in app header:', data.xp);
      },
      error: (error) => {
        console.error('❌ Error loading user XP:', error);
        // Keep default value on error
      }
    });
  }

  navItems = [
    { path: '/dashboard', translationKey: 'nav.dashboard', icon: '🏠' },
    { path: '/learning', translationKey: 'nav.learning', icon: '⭐' },
    { path: '/lessons', translationKey: 'nav.lessons', icon: '📚' },
    { path: '/goals', translationKey: 'nav.goals', icon: '🎯' },
    { path: '/leaderboard', translationKey: 'nav.leaderboard', icon: '🏆' },
    { path: '/statistics', translationKey: 'nav.statistics', icon: '📈' }
  ];
}
