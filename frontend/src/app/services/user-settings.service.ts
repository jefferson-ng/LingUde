import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { UserSettings } from '../models/user-settings.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserSettingsService {
  private readonly apiUrl = `${environment.apiUrl}/api/settings`;
  
  // Current user settings state
  currentSettings = signal<UserSettings | null>(null);

  constructor(private http: HttpClient) {}

  /**
   * Get authentication headers with JWT token
   */
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  /**
   * Fetch user settings from backend
   */
  getUserSettings(): Observable<UserSettings> {
    return this.http.get<UserSettings>(this.apiUrl, { 
      headers: this.getAuthHeaders() 
    }).pipe(
      tap(settings => {
        this.currentSettings.set(settings);
      })
    );
  }

  /**
   * Update user settings on backend
   */
  updateUserSettings(settings: Partial<UserSettings>): Observable<UserSettings> {
    return this.http.put<UserSettings>(this.apiUrl, settings, {
      headers: this.getAuthHeaders()
    }).pipe(
      tap(updatedSettings => {
        this.currentSettings.set(updatedSettings);
      })
    );
  }

  /**
   * Update only the UI language
   */
  updateLanguage(language: string): Observable<UserSettings> {
    return this.updateUserSettings({ uiLanguage: language as any });
  }

  /**
   * Update only the theme
   */
  updateTheme(theme: string): Observable<UserSettings> {
    return this.updateUserSettings({ theme: theme as any });
  }

  /**
   * Update only the notifications setting
   */
  updateNotifications(enabled: boolean): Observable<UserSettings> {
    return this.updateUserSettings({ notificationsEnabled: enabled });
  }
}
