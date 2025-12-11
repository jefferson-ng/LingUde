import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { LeaderboardEntry } from '../models/leaderboard.model';

/**
 * Service for fetching leaderboard data
 * Provides methods to get friends leaderboard rankings
 */
@Injectable({
  providedIn: 'root'
})
export class LeaderboardService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  /**
   * Get friends leaderboard (current user + all friends ranked by XP)
   * Returns list of users sorted by XP descending
   *
   * @returns Observable of leaderboard entries with rank, user info, XP, level, and streak
   */
  getFriendsLeaderboard(): Observable<LeaderboardEntry[]> {
    return this.http.get<LeaderboardEntry[]>(
      `${this.apiUrl}/api/user/learning/leaderboard/friends`
    );
  }
}
