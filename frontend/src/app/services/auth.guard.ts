import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { filter } from 'rxjs/operators';
import { combineLatest } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): Observable<boolean | UrlTree> {
    return combineLatest([this.auth.loading$, this.auth.user$]).pipe(
      // Only continue when loading is false
      filter(([loading, user]: [boolean, any]) => !loading),
      map(([_, user]: [boolean, any]) => {
        if (user) {
          return true;
        } else {
          return this.router.createUrlTree(['/home']);
        }
      })
    );
  }
}
