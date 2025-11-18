import { Component } from '@angular/core';
import { TranslocoDirective } from '@jsverse/transloco';
import { LucideAngularModule, Crown, Flame, Zap, Target } from 'lucide-angular';

@Component({
  selector: 'app-leaderboard',
  imports: [TranslocoDirective, LucideAngularModule],
  templateUrl: './leaderboard.html',
  styleUrl: './leaderboard.css'
})
export class Leaderboard {
  // Lucide Icons
  readonly CrownIcon = Crown;
  readonly FlameIcon = Flame;
  readonly ZapIcon = Zap;
  readonly TargetIcon = Target;
}
