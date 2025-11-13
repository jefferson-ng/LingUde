import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';

/**
 * User Learning Data interface matching backend UserLearningDTO
 */
export interface UserLearningData {
  userId: string;
  learningLanguage: 'DE' | 'EN' | 'FR' | 'ES' | 'IT';
  currentLevel: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';
  targetLevel: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';
  xp: number;
  streakCount: number;
  lastActivityDate?: string;
}

/**
 * Service for managing user learning data and XP tracking.
 * Connects to the backend API to fetch and update user progress, XP, and streaks.
 */
@Injectable({
  providedIn: 'root'
})
export class UserLearningService {
  private readonly apiUrl = 'http://localhost:8080/api/user/learning';
  
  // Observable state for current user's learning data
  private userLearningSubject = new BehaviorSubject<UserLearningData | null>(null);
  public userLearning$ = this.userLearningSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Fetches user learning data from the backend for a specific user.
   * Updates the internal state with the fetched data.
   *
   * @param userId - The UUID of the user
   * @returns Observable of UserLearningData
   */
  getUserLearning(userId: string): Observable<UserLearningData> {
    return this.http.get<UserLearningData>(`${this.apiUrl}/${userId}`)
      .pipe(
        tap(data => this.userLearningSubject.next(data))
      );
  }

  /**
   * Adds XP to a user's learning progress.
   * This is useful for testing or manual XP adjustments.
   *
   * @param userId - The UUID of the user
   * @param xpAmount - The amount of XP to add
   * @returns Observable of updated UserLearningData
   */
  addXp(userId: string, xpAmount: number): Observable<UserLearningData> {
    return this.http.post<UserLearningData>(
      `${this.apiUrl}/${userId}/xp?xpAmount=${xpAmount}`,
      {}
    ).pipe(
      tap(data => this.userLearningSubject.next(data))
    );
  }

  /**
   * Gets the current XP value from the cached state.
   * Returns 0 if no data is loaded yet.
   *
   * @returns Current XP value
   */
  getCurrentXp(): number {
    return this.userLearningSubject.value?.xp ?? 0;
  }

  /**
   * Gets the current streak count from the cached state.
   * Returns 0 if no data is loaded yet.
   *
   * @returns Current streak count
   */
  getCurrentStreak(): number {
    return this.userLearningSubject.value?.streakCount ?? 0;
  }

  /**
   * Clears the cached user learning data.
   * Useful when logging out or switching users.
   */
  clearCache(): void {
    this.userLearningSubject.next(null);
  }
}
