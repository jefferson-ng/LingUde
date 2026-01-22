import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ReferralLink {
  referralCode: string;
  referralUrl: string;
}

/**
 * Service for fetching user's referral link
 * The referral code is generated lazily on first request
 */
@Injectable({
  providedIn: 'root'
})
export class ReferralService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  /**
   * Get the current user's referral link.
   * If the user doesn't have a referral code yet, one will be generated.
   *
   * @returns Observable with referralCode and referralUrl
   */
  getReferralLink(): Observable<ReferralLink> {
    return this.http.get<ReferralLink>(`${this.apiUrl}/api/users/me/referral-link`);
  }
}
