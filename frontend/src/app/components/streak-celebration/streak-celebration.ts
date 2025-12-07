import { Component, signal, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-streak-celebration',
  imports: [CommonModule],
  templateUrl: './streak-celebration.html',
  styleUrl: './streak-celebration.css',
  standalone: true
})
export class StreakCelebrationComponent {
  // Inputs
  previousStreak = input.required<number>();
  newStreak = input.required<number>();
  
  // Outputs
  animationComplete = output<void>();
  
  // Internal state
  protected isFlying = signal(false);
  
  /**
   * Starts the animation sequence automatically when component is shown
   */
  ngOnInit() {
    this.startAnimation();
  }
  
  private startAnimation() {
    // Reset state
    this.isFlying.set(false);
    
    // Step 1: Show modal for 2.5 seconds
    setTimeout(() => {
      this.isFlying.set(true);
    }, 2500);
    
    // Step 2: Emit completion after slide-out (2500ms + 700ms slide + 100ms buffer)
    setTimeout(() => {
      this.animationComplete.emit();
    }, 3300);
  }
}
