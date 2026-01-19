
import { Routes } from '@angular/router';
import { AuthGuard } from './services/auth.guard';
import { AdminGuard } from './services/admin.guard';
import {FriendsList} from './components/friends-list/friends-list';
import {FriendsLayout} from './layouts/friends-layout/friends-layout';
import {FriendRequests} from './components/friend-requests/friend-requests';
import {FriendSearch} from './components/friend-search/friend-search';

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
      },
      {
        path: 'forgot-password',
        loadComponent: () =>
          import('./pages/forgot-password/forgot-password').then(m => m.ForgotPassword),
      },
      {
        path: 'reset-password',
        loadComponent: () =>
          import('./pages/reset-password/reset-password').then(m => m.ResetPassword),
      },
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
        component: FriendsLayout,
        canActivate: [AuthGuard],
        children: [
          {
            path: '',
            redirectTo: 'list',
            pathMatch: 'full'
          },
          {
            path: 'list',
            component: FriendsList
          },
          {
            path: 'requests',
            component: FriendRequests
          },
          {
            path: 'search',
            component: FriendSearch
          }
        ]
      },
      {
        path: 'admin',
        canActivate: [AdminGuard],
        children: [
          {
            path: '',
            redirectTo: 'users',
            pathMatch: 'full'
          },
          {
            path: 'users',
            loadComponent: () => import('./pages/admin/admin-users/admin-users').then(m => m.AdminUsers)
          }
        ]
      },
      {
        path: '**',
        redirectTo: 'dashboard'
      }
    ]
  }
];
