import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, firstValueFrom } from 'rxjs';

export interface UserInfo {
  id: string;
  email: string;
  username: string;
  role?: string;
  level?: number;
  xp?: number;
  streak?: number;
}

function decodeJwtPayload(token: string): any {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch {
    return null;
  }
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = '/api/auth';
  public userSubject = new BehaviorSubject<UserInfo | null>(null);
  user$ = this.userSubject.asObservable();
  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.loadUserFromStorage();
  }

  async login(email: string, password: string) {
    const res: any = await firstValueFrom(this.http.post(`${this.apiUrl}/signin`, { email, password }));
    this.setSession(res);
  }

  async register(email: string, username: string, password: string) {
    const res: any = await firstValueFrom(this.http.post(`${this.apiUrl}/signup`, { email, username, password }));
    this.setSession(res);
  }

  async logout() {
    try {
      await firstValueFrom(this.http.post(`${this.apiUrl}/logout`, {}));
    } catch (e) {
      // Ignore errors, proceed with logout
    }
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    this.userSubject.next(null);
    this.router.navigate(['/home']);
  }

  async fetchUserInfo() {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      this.loadingSubject.next(false);
      return;
    }
    this.loadingSubject.next(true);
    try {
      const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
      const user: any = await firstValueFrom(this.http.get(`${this.apiUrl}/me`, { headers }));
      const payload = decodeJwtPayload(token);
      this.userSubject.next({
        ...user,
        role: payload?.role || 'USER'
      });
    } catch (error) {
      this.userSubject.next(null);
    } finally {
      this.loadingSubject.next(false);
    }
  }

  private setSession(res: any) {
    localStorage.setItem('accessToken', res.accessToken);
    localStorage.setItem('refreshToken', res.refreshToken);
    const payload = decodeJwtPayload(res.accessToken);
    this.userSubject.next({
      id: res.id,
      email: res.email,
      username: res.username,
      role: payload?.role || 'USER'
    });
  }

  public loadUserFromStorage() {
    const token = localStorage.getItem('accessToken');
    if (token) {
      this.fetchUserInfo();
    } else {
      this.loadingSubject.next(false);
    }
  }
}
