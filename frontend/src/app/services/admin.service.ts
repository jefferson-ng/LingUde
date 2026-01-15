import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AdminUserSummary, AdminUserDetail, PageResponse } from '../models/admin.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/admin`;

  getUsers(page: number = 0, size: number = 20): Observable<PageResponse<AdminUserSummary>> {
    return this.http.get<PageResponse<AdminUserSummary>>(`${this.apiUrl}/users`, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

  getUserDetails(userId: string): Observable<AdminUserDetail> {
    return this.http.get<AdminUserDetail>(`${this.apiUrl}/users/${userId}`);
  }
}