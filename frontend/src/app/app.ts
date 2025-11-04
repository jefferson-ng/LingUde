import { Component, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('LinguDE');
  protected readonly userLevel = signal(5);
  protected readonly userXP = signal(1850);
  protected readonly userName = signal('Alex');

  navItems = [
    { path: '/dashboard', label: 'Startseite', icon: '🏠' },
    { path: '/lessons', label: 'Lektionen', icon: '📚' },
    { path: '/goals', label: 'Meine Ziele', icon: '🎯' },
    { path: '/leaderboard', label: 'Bestenliste', icon: '🏆' },
    { path: '/statistics', label: 'Statistiken', icon: '📈' }
  ];
}
