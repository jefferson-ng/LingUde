import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserLearningService } from '../../services/user-learning.service';
import { TranslocoDirective } from '@jsverse/transloco';

interface LanguageOption {
  code: 'DE' | 'EN' | 'FR' | 'ES' | 'IT';
  name: string;
  flagCode: string;
  disabled?: boolean;
}

interface LevelOption {
  code: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';
}

@Component({
  selector: 'app-level-selection',
  standalone: true,
  imports: [CommonModule, TranslocoDirective],
  templateUrl: './level-selection.html',
  styleUrls: ['./level-selection.css']
})
export class LevelSelectionComponent implements OnInit {
  languages: LanguageOption[] = [
    { code: 'DE', name: 'German', flagCode: 'de', disabled: false },
    { code: 'EN', name: 'English', flagCode: 'gb', disabled: false }
  ];

  levels: LevelOption[] = [
    { code: 'A1' },
    { code: 'A2' },
    { code: 'B1' },
    { code: 'B2' },
    { code: 'C1' },
    { code: 'C2' }
  ];

  selectedLanguage: 'DE' | 'EN' | 'FR' | 'ES' | 'IT' | null = null;
  selectedCurrentLevel: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2' | null = null;
  selectedTargetLevel: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2' | null = null;

  error = '';
  loading = false;
  step: 'language' | 'current-level' | 'target-level' = 'language';

  constructor(
    private userLearningService: UserLearningService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userLearningService.getUserLearning().subscribe({
      next: (data) => {
        if (data.learningLanguage) {
          this.router.navigate(['/dashboard']);
        }
      },
      error: () => {
        // Error fetching data, stay on this page
      }
    });
  }

  selectLanguage(language: 'DE' | 'EN' | 'FR' | 'ES' | 'IT'): void {
    const selectedLang = this.languages.find(l => l.code === language);
    if (selectedLang?.disabled) {
      return;
    }
    this.selectedLanguage = language;
    this.step = 'current-level';
  }

  selectCurrentLevel(level: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2'): void {
    this.selectedCurrentLevel = level;
    this.step = 'target-level';
  }

  selectTargetLevel(level: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2'): void {
    this.selectedTargetLevel = level;
  }

  goBack(): void {
    if (this.step === 'current-level') {
      this.step = 'language';
      this.selectedLanguage = null;
    } else if (this.step === 'target-level') {
      this.step = 'current-level';
      this.selectedTargetLevel = null;
    }
  }

  canSubmit(): boolean {
    return this.selectedLanguage !== null &&
           this.selectedCurrentLevel !== null &&
           this.selectedTargetLevel !== null;
  }

  submit(): void {
    if (!this.canSubmit()) {
      this.error = 'Please complete all selections';
      return;
    }

    this.loading = true;
    this.error = '';

    const config = {
      learningLanguage: this.selectedLanguage!,
      currentLevel: this.selectedCurrentLevel!,
      targetLevel: this.selectedTargetLevel!
    };

    console.log('📤 Submitting learning configuration:', config);

    this.userLearningService.updateLearningConfig(config).subscribe({
      next: (response) => {
        console.log('✅ Learning configuration saved successfully:', response);
        this.loading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('❌ Failed to save learning configuration:', err);
        this.loading = false;
        this.error = err?.error?.message || 'Failed to save your preferences';
      }
    });
  }
}
