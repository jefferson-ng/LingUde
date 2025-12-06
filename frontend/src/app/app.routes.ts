
import { Routes } from '@angular/router';
import { AuthGuard } from './services/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: '',
    loadComponent: () => import('./layouts/landing-layout/landing-layout').then(m => m.LandingLayout),
    children: [
      {
        path: 'home',
        loadComponent: () => import('./pages/home/home').then(m => m.Home)
      },
      {
        path: 'login',
        loadComponent: () => import('./pages/login/login').then(m => m.LoginComponent)
      },
      {
        path: 'register',
        loadComponent: () => import('./pages/register/register').then(m => m.RegisterComponent)
      }
    ]
  },
  {
    path: '',
    loadComponent: () => import('./app').then(m => m.App),
    canActivate: [AuthGuard],
    children: [
      {
        path: 'level-selection',
        loadComponent: () => import('./pages/level-selection/level-selection').then(m => m.LevelSelectionComponent),
        canActivate: [AuthGuard]
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./pages/dashboard/dashboard').then(m => m.Dashboard),
        canActivate: [AuthGuard]
      },
      {
        path: 'learning',
        loadComponent: () => import('./pages/learning/learning').then(m => m.Learning),
        canActivate: [AuthGuard]
      },
      {
        path: 'lessons',
        loadComponent: () => import('./pages/lessons/lessons').then(m => m.Lessons),
        canActivate: [AuthGuard]
      },
      {
        path: 'goals',
        loadComponent: () => import('./pages/goals/goals').then(m => m.Goals),
        canActivate: [AuthGuard]
      },
      {
        path: 'leaderboard',
        loadComponent: () => import('./pages/leaderboard/leaderboard').then(m => m.Leaderboard),
        canActivate: [AuthGuard]
      },
      {
        path: 'statistics',
        loadComponent: () => import('./pages/statistics/statistics').then(m => m.Statistics),
        canActivate: [AuthGuard]
      },
      {
        path: 'settings',
        loadComponent: () => import('./pages/settings/settings').then(m => m.Settings),
        canActivate: [AuthGuard]
      },
      {
        path: 'friends',
        loadComponent: () => import('./pages/friends/friends-list/friends-list').then(m => m.FriendsList),
        canActivate: [AuthGuard]
      },
      {
        path: 'friends/search',
        loadComponent: () => import('./pages/friends/friends-search/friends-search').then(m => m.FriendsSearch),
        canActivate: [AuthGuard]
      },
      {
        path: 'friends/requests',
        loadComponent: () => import('./pages/friends/friend-requests/friend-requests').then(m => m.FriendRequests),
        canActivate: [AuthGuard]
      },
      {
        path: '**',
        redirectTo: 'dashboard'
      }
    ]
  }
];
