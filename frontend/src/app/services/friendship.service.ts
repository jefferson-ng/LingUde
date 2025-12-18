import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  UserSummary,
  FriendshipResponse,
  PendingRequest,
  Friend,
  FriendRequestPayload
} from '../models/friendship.model';

@Injectable({
  providedIn: 'root'
})
export class FriendshipService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/friendships`;



  /**
   * Get friends list
   */
  getFriendsList(): Observable<Friend[]> {
    return this.http.get<Friend[]>(`${this.apiUrl}`);
  }

  /**
   * Send friend request
   */
  sendFriendRequest(usernameOrEmail: string): Observable<FriendshipResponse> {
    const payload: FriendRequestPayload = { usernameOrEmail };
    return this.http.post<FriendshipResponse>(`${this.apiUrl}/request`, payload);
  }

  /**
   * Get incoming pending requests
   */
  getIncomingRequests(): Observable<PendingRequest[]> {
    return this.http.get<PendingRequest[]>(`${this.apiUrl}/pending/incoming`);
  }

  /**
   * Get outgoing pending requests
   */
  getOutgoingRequests(): Observable<PendingRequest[]> {
    return this.http.get<PendingRequest[]>(`${this.apiUrl}/pending/outgoing`);
  }

  /**
   * Accept friend request
   */
  acceptRequest(friendshipId: string): Observable<FriendshipResponse> {
    return this.http.put<FriendshipResponse>(`${this.apiUrl}/${friendshipId}/accept`, {});
  }

  /**
   * Reject friend request
   */
  rejectRequest(friendshipId: string): Observable<FriendshipResponse> {
    return this.http.put<FriendshipResponse>(`${this.apiUrl}/${friendshipId}/reject`, {});
  }
  /**
   * Remove friendship
   */
  removeFriendship(friendshipId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${friendshipId}`);
  }

  /**
   * Search users by username or email
   */
  searchUsers(query: string): Observable<UserSummary[]> {
    return this.http.get<UserSummary[]>(`${this.apiUrl}/users/search`, {
      params: { q: query }
    });
  }
}
