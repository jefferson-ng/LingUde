import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard/dashboard').then(m => m.Dashboard)
  },
  {
    path: 'lessons',
    loadComponent: () => import('./pages/lessons/lessons').then(m => m.Lessons)
  },
  {
    path: 'goals',
    loadComponent: () => import('./pages/goals/goals').then(m => m.Goals)
  },
  {
    path: 'leaderboard',
    loadComponent: () => import('./pages/leaderboard/leaderboard').then(m => m.Leaderboard)
  },
  {
    path: 'statistics',
    loadComponent: () => import('./pages/statistics/statistics').then(m => m.Statistics)
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
