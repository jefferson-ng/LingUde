import { Component, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TranslocoDirective } from '@jsverse/transloco';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule, TranslocoDirective],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('LinguDE');
  protected readonly userLevel = signal(5);
  protected readonly userXP = signal(1850);
  protected readonly userName = signal('Alex');

  navItems = [
    { path: '/dashboard', translationKey: 'nav.dashboard', icon: '🏠' },
    { path: '/lessons', translationKey: 'nav.lessons', icon: '📚' },
    { path: '/goals', translationKey: 'nav.goals', icon: '🎯' },
    { path: '/leaderboard', translationKey: 'nav.leaderboard', icon: '🏆' },
    { path: '/statistics', translationKey: 'nav.statistics', icon: '📈' }
  ];
}
