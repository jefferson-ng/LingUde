import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserLearningService } from '../../services/user-learning.service';
import { TranslocoDirective } from '@jsverse/transloco';
import { LucideAngularModule, Eye, EyeOff } from 'lucide-angular';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslocoDirective, RouterLink, LucideAngularModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';
  showPassword = false;

  // Icons
  readonly EyeIcon = Eye;
  readonly EyeOffIcon = EyeOff;

  constructor(
    private auth: AuthService,
    private userLearningService: UserLearningService,
    private router: Router
  ) {}

  async onLogin() {
    this.error = '';
    try {
      await this.auth.login(this.email, this.password);

      // Check if user has already selected their learning language
      this.userLearningService.getUserLearning().subscribe({
        next: (data) => {
          if (data.learningLanguage) {
            // User has already configured their preferences
            this.router.navigate(['/dashboard']);
          } else {
            // First time login, redirect to level selection
            this.router.navigate(['/level-selection']);
          }
        },
        error: () => {
          // On error, redirect to level selection to be safe
          this.router.navigate(['/level-selection']);
        }
      });
    } catch (e: any) {
      this.error = e?.error?.message || 'Login fehlgeschlagen';
    }
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
}
