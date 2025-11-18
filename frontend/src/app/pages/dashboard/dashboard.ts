import { Component, signal, inject, OnInit } from '@angular/core';
import { TranslocoDirective } from '@jsverse/transloco';
import { CommonModule } from '@angular/common';
import { UserLearningService } from '../../services/user-learning.service';
import { LucideAngularModule, Flame } from 'lucide-angular';

@Component({
  selector: 'app-dashboard',
  imports: [TranslocoDirective, CommonModule, LucideAngularModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  private userLearningService = inject(UserLearningService);
  
  // Icons
  readonly FlameIcon = Flame;
  
  // User progress data
  protected userXP = signal(0);
  protected userStreak = signal(0);
  protected userLevel = signal(1);
  protected xpForCurrentLevel = signal(0);
  protected xpForNextLevel = signal(100);
  protected progressPercent = signal(0);
  
  ngOnInit(): void {
    this.loadUserData();
    
    // Subscribe to XP updates
    this.userLearningService.userLearning$.subscribe(data => {
      if (data) {
        this.updateUserProgress(data.xp, data.streakCount);
      }
    });
  }
  
  private loadUserData(): void {
    this.userLearningService.getUserLearning().subscribe({
      next: (data) => {
        this.updateUserProgress(data.xp, data.streakCount);
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
}
