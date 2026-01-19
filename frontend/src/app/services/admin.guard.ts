import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';
import { Observable, combineLatest } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): Observable<boolean | UrlTree> {
    return combineLatest([this.auth.loading$, this.auth.user$]).pipe(
      filter(([loading, _]: [boolean, any]) => !loading),
      map(([_, user]: [boolean, any]) => {
        if (user && user.role === 'ADMIN') {
          return true;
        } else if (user) {
          return this.router.createUrlTree(['/dashboard']);
        } else {
          return this.router.createUrlTree(['/home']);
        }
      })
    );
  }
}