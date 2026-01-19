import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './reset-password.html',
  styleUrls: ['./reset-password.css']
})
export class ResetPassword implements OnInit {
  token = '';
  newPassword = '';
  confirmPassword = '';
  message = '';
  error = '';
  loading = false;
  success = false;

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || '';
    });
  }

  get passwordsMatch(): boolean {
    return this.newPassword === this.confirmPassword;
  }

  get passwordValid(): boolean {
    return this.newPassword.length >= 8;
  }

  get canSubmit(): boolean {
    return this.token !== '' &&
           this.newPassword !== '' &&
           this.confirmPassword !== '' &&
           this.passwordsMatch &&
           this.passwordValid &&
           !this.loading;
  }

  async onSubmit() {
    if (!this.canSubmit) return;

    this.error = '';
    this.message = '';
    this.loading = true;

    try {
      const res: any = await firstValueFrom(
        this.http.post('/api/auth/reset-password', {
          token: this.token,
          newPassword: this.newPassword
        })
      );
      this.message = res.message;
      this.success = true;
    } catch (e: any) {
      this.error = e?.error?.message || 'Failed to reset password. The link may be invalid or expired.';
    } finally {
      this.loading = false;
    }
  }
}