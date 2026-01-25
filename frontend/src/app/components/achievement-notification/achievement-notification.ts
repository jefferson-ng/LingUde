import { Component, signal, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, Trophy, Flame, Zap, BookOpen, GraduationCap, LucideIconData } from 'lucide-angular';
import { TranslocoDirective } from '@jsverse/transloco';

interface AchievementNotification {
  code: string;
  title: string;
  description: string;
  type: string;
}

@Component({
  selector: 'app-achievement-notification',
  imports: [CommonModule, LucideAngularModule, TranslocoDirective],
  templateUrl: './achievement-notification.html',
  styleUrl: './achievement-notification.css',
  standalone: true
})
export class AchievementNotificationComponent {
  // Inputs
  achievement = input.required<AchievementNotification>();
  
  // Outputs
  animationComplete = output<void>();
  
  // Icons
  readonly TrophyIcon = Trophy;
  readonly FlameIcon = Flame;
  readonly ZapIcon = Zap;
  readonly BookOpenIcon = BookOpen;
  readonly GraduationCapIcon = GraduationCap;
  
  // Internal state
  protected isVisible = signal(false);
  protected isLeaving = signal(false);
  
  /**
   * Starts the notification animation sequence
   */
  ngOnInit() {
    this.startAnimation();
  }
  
  private startAnimation() {
    // Show notification after small delay
    setTimeout(() => {
      this.isVisible.set(true);
    }, 100);
    
    // Start exit animation after 3 seconds
    setTimeout(() => {
      this.isLeaving.set(true);
    }, 3000);
    
    // Emit completion after exit animation
    setTimeout(() => {
      this.animationComplete.emit();
    }, 3500);
  }
  
  /**
   * Returns the appropriate icon for an achievement based on its type
   */
  getAchievementIcon(): LucideIconData {
    const type = this.achievement().type;
    switch (type) {
      case 'XP_MILESTONE':
        return this.ZapIcon;
      case 'STREAK':
        return this.FlameIcon;
      case 'LESSON_COMPLETION':
        return this.BookOpenIcon;
      case 'LEVEL_REACHED':
        return this.GraduationCapIcon;
      default:
        return this.TrophyIcon;
    }
  }
}
