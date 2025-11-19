import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { TranslocoDirective } from '@jsverse/transloco';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslocoDirective],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent {
  email = '';
  username = '';
  password = '';
  error = '';

  constructor(private auth: AuthService, private router: Router) {}

  async onRegister() {
    this.error = '';
    try {
      await this.auth.register(this.email, this.username, this.password);
      this.router.navigate(['/dashboard']);
    } catch (e: any) {
      this.error = e?.error?.message || 'Registrierung fehlgeschlagen';
    }
  }
}
