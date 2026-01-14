import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './forgot-password.html',
  styleUrls: ['./forgot-password.css']
})
export class ForgotPassword {
  email = '';
  message = '';
  error = '';
  loading = false;

  constructor(private http: HttpClient) {}

  async onSubmit() {
    this.error = '';
    this.message = '';
    this.loading = true;

    try {
      const res: any = await firstValueFrom(
        this.http.post('/api/auth/forgot-password', { email: this.email })
      );
      this.message = res.message;
    } catch (e: any) {
      this.error = e?.error?.message || 'Something went wrong. Please try again.';
    } finally {
      this.loading = false;
    }
  }
}