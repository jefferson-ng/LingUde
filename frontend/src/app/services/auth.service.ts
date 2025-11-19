import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, firstValueFrom } from 'rxjs';

export interface UserInfo {
  userId: string;
  email: string;
  username: string;
  level?: number;
  xp?: number;
  streak?: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = '/api/auth';
  private userSubject = new BehaviorSubject<UserInfo | null>(null);
  user$ = this.userSubject.asObservable();

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
    if (!token) return;
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    const user: any = await firstValueFrom(this.http.get(`${this.apiUrl}/me`, { headers }));
    this.userSubject.next(user);
  }

  private setSession(res: any) {
    localStorage.setItem('accessToken', res.accessToken);
    localStorage.setItem('refreshToken', res.refreshToken);
    this.userSubject.next({
      userId: res.userId,
      email: res.email,
      username: res.username
    });
  }

  private loadUserFromStorage() {
    const token = localStorage.getItem('accessToken');
    if (token) {
      this.fetchUserInfo();
    }
  }
}
