import { Component, signal, inject, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TranslocoDirective } from '@jsverse/transloco';
import { UserLearningService } from './services/user-learning.service';
import { LucideAngularModule, Home, BookOpen, Target, BarChart3, Settings, GraduationCap } from 'lucide-angular';

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
  readonly BarChart3Icon = BarChart3;
  readonly SettingsIcon = Settings;
  readonly GraduationCapIcon = GraduationCap;
  
  protected readonly title = signal('LingUDE');
  protected readonly userLevel = signal(5);
  protected readonly userXP = signal(0); // Will be loaded from backend
  protected readonly userName = signal('Alex');
  
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
    { path: '/dashboard', translationKey: 'nav.dashboard', icon: 'home' },
    { path: '/lessons', translationKey: 'nav.lessons', icon: 'book-open' },
    { path: '/learning', translationKey: 'nav.learning', icon: 'graduation-cap' },
    { path: '/goals', translationKey: 'nav.goals', icon: 'target' }
  ];
  
  footerItems = [
    { path: '/statistics', translationKey: 'nav.statistics', icon: 'bar-chart-3' },
    { path: '/settings', translationKey: 'nav.settings', icon: 'settings' }
  ];
}
