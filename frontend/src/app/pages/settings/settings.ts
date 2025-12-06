import { Component, inject, HostListener, OnInit } from '@angular/core';
import { TranslocoDirective, TranslocoService } from '@jsverse/transloco';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserSettingsService } from '../../services/user-settings.service';
import { Language, Theme } from '../../models/user-settings.model';
import { UserLearningService, UserLearningData } from '../../services/user-learning.service';

@Component({
  selector: 'app-settings',
  imports: [TranslocoDirective, CommonModule, FormsModule],
  templateUrl: './settings.html',
  styleUrl: './settings.css'
})
export class Settings implements OnInit {
  private translocoService = inject(TranslocoService);
  private userSettingsService = inject(UserSettingsService);
  private userLearningService = inject(UserLearningService);

  availableLanguages = [
    { code: 'de', name: 'Deutsch', flag: 'de', backendCode: Language.DE },
    { code: 'en', name: 'English', flag: 'gb', backendCode: Language.EN }
  ];

  availableThemes = [
    { value: Theme.LIGHT, key: 'themeLight' },
    { value: Theme.DARK, key: 'themeDark' },
    { value: Theme.AUTO, key: 'themeAuto' }
  ];

  // Learning language options
  learningLanguages = [
    { code: 'DE' as const, name: 'German', flagCode: 'de' },
    { code: 'EN' as const, name: 'English', flagCode: 'gb' }
  ];

  // CEFR levels with transloco keys for names and descriptions
  levels = [
    { code: 'A1' as const, name: 'learning.difficulty.a1.label', description: 'learning.difficulty.a1.description' },
    { code: 'A2' as const, name: 'learning.difficulty.a2.label', description: 'learning.difficulty.a2.description' },
    { code: 'B1' as const, name: 'learning.difficulty.b1.label', description: 'learning.difficulty.b1.description' },
    { code: 'B2' as const, name: 'learning.difficulty.b2.label', description: 'learning.difficulty.b2.description' },
    { code: 'C1' as const, name: 'learning.difficulty.c1.label', description: 'learning.difficulty.c1.description' },
    { code: 'C2' as const, name: 'learning.difficulty.c2.label', description: 'learning.difficulty.c2.description' }
  ];

  // Level order for comparison
  private levelOrder: { [key: string]: number } = {
    'A1': 1, 'A2': 2, 'B1': 3, 'B2': 4, 'C1': 5, 'C2': 6
  };

  isLanguageDropdownOpen = false;
  isThemeDropdownOpen = false;
  isLearningLanguageDropdownOpen = false;
  isCurrentLevelDropdownOpen = false;
  isTargetLevelDropdownOpen = false;

  selectedTheme: Theme = Theme.AUTO;
  notificationsEnabled = true;
  isLoading = false;

  // Learning data
  learningData: UserLearningData | null = null;
  selectedLearningLanguage: 'DE' | 'EN' | 'FR' | 'ES' | 'IT' | null = null;
  selectedCurrentLevel: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2' | null = null;
  selectedTargetLevel: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2' | null = null;

  ngOnInit() {
    this.loadSettings();
    this.loadLearningData();
  }

  loadSettings() {
    this.isLoading = true;
    this.userSettingsService.getUserSettings().subscribe({
      next: (settings) => {
        console.log('Loaded settings from backend:', settings);

        // Set UI language in frontend
        const langCode = this.mapBackendToFrontendLang(settings.uiLanguage);
        console.log('Setting frontend language to:', langCode);
        this.translocoService.setActiveLang(langCode);

        // Set theme
        this.selectedTheme = settings.theme;
        console.log('Setting theme to:', this.selectedTheme);

        // Set notifications
        this.notificationsEnabled = settings.notificationsEnabled;
        console.log('Setting notifications to:', this.notificationsEnabled);

        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load settings:', err);
        // Set defaults if loading fails
        this.notificationsEnabled = true;
        this.selectedTheme = Theme.AUTO;
        this.isLoading = false;
      }
    });
  }

  loadLearningData() {
    this.userLearningService.getUserLearning().subscribe({
      next: (data) => {
        console.log('Loaded learning data:', data);
        this.learningData = data;
        this.selectedLearningLanguage = data.learningLanguage;
        this.selectedCurrentLevel = data.currentLevel;
        this.selectedTargetLevel = data.targetLevel;
      },
      error: (err) => {
        console.error('Failed to load learning data:', err);
        // User might not have learning data set yet
      }
    });
  }

  mapBackendToFrontendLang(backendLang: Language): string {
    const mapping: { [key: string]: string } = {
      'EN': 'en',
      'DE': 'de'
    };
    return mapping[backendLang] || 'en';
  }

  get currentLang() {
    return this.translocoService.getActiveLang();
  }

  changeLanguage(langCode: string) {
    this.translocoService.setActiveLang(langCode);
  }

  toggleLanguageDropdown() {
    this.isLanguageDropdownOpen = !this.isLanguageDropdownOpen;
    this.isThemeDropdownOpen = false;
    this.isLearningLanguageDropdownOpen = false;
    this.isCurrentLevelDropdownOpen = false;
    this.isTargetLevelDropdownOpen = false;
  }

  toggleThemeDropdown() {
    this.isThemeDropdownOpen = !this.isThemeDropdownOpen;
    this.isLanguageDropdownOpen = false;
    this.isLearningLanguageDropdownOpen = false;
    this.isCurrentLevelDropdownOpen = false;
    this.isTargetLevelDropdownOpen = false;
  }

  toggleLearningLanguageDropdown() {
    this.isLearningLanguageDropdownOpen = !this.isLearningLanguageDropdownOpen;
    this.isLanguageDropdownOpen = false;
    this.isThemeDropdownOpen = false;
    this.isCurrentLevelDropdownOpen = false;
    this.isTargetLevelDropdownOpen = false;
  }

  toggleCurrentLevelDropdown() {
    this.isCurrentLevelDropdownOpen = !this.isCurrentLevelDropdownOpen;
    this.isLanguageDropdownOpen = false;
    this.isThemeDropdownOpen = false;
    this.isLearningLanguageDropdownOpen = false;
    this.isTargetLevelDropdownOpen = false;
  }

  toggleTargetLevelDropdown() {
    this.isTargetLevelDropdownOpen = !this.isTargetLevelDropdownOpen;
    this.isLanguageDropdownOpen = false;
    this.isThemeDropdownOpen = false;
    this.isLearningLanguageDropdownOpen = false;
    this.isCurrentLevelDropdownOpen = false;
  }

  selectLanguage(langCode: string, backendCode: Language) {
    console.log('Changing language to:', langCode, 'Backend code:', backendCode);
    this.changeLanguage(langCode);
    this.isLanguageDropdownOpen = false;
    
    // Update on backend
    this.userSettingsService.updateLanguage(backendCode).subscribe({
      next: (updated) => {
        console.log('Language updated on backend:', updated);
      },
      error: (err) => {
        console.error('Failed to update language:', err);
        // Revert on error
        const currentSettings = this.userSettingsService.currentSettings();
        if (currentSettings) {
          const oldLang = this.mapBackendToFrontendLang(currentSettings.uiLanguage);
          this.changeLanguage(oldLang);
        }
      }
    });
  }

  selectTheme(theme: Theme) {
    console.log('Changing theme to:', theme);
    this.selectedTheme = theme;
    this.isThemeDropdownOpen = false;
    
    // Update on backend
    this.userSettingsService.updateTheme(theme).subscribe({
      next: (updated) => {
        console.log('Theme updated on backend:', updated);
      },
      error: (err) => {
        console.error('Failed to update theme:', err);
        // Revert on error
        const currentSettings = this.userSettingsService.currentSettings();
        if (currentSettings) {
          this.selectedTheme = currentSettings.theme;
        }
      }
    });
  }

  toggleNotifications() {
    const newValue = !this.notificationsEnabled;
    console.log('Toggling notifications from', this.notificationsEnabled, 'to', newValue);
    this.notificationsEnabled = newValue;
    
    // Update on backend
    this.userSettingsService.updateNotifications(newValue).subscribe({
      next: (updated) => {
        console.log('Notifications updated on backend:', updated);
      },
      error: (err) => {
        console.error('Failed to update notifications:', err);
        // Revert on error
        this.notificationsEnabled = !newValue;
      }
    });
  }

  onNotificationChange(newValue: boolean) {
    console.log('Notification changed to:', newValue);
    
    // Update on backend
    this.userSettingsService.updateNotifications(newValue).subscribe({
      next: (updated) => {
        console.log('Notifications updated on backend:', updated);
      },
      error: (err) => {
        console.error('Failed to update notifications:', err);
        // Revert on error
        this.notificationsEnabled = !newValue;
      }
    });
  }

  getSelectedLanguage() {
    return this.availableLanguages.find(lang => lang.code === this.currentLang) || this.availableLanguages[0];
  }

  getSelectedTheme() {
    return this.availableThemes.find(theme => theme.value === this.selectedTheme) || this.availableThemes[0];
  }

  selectLearningLanguage(langCode: 'DE' | 'EN' | 'FR' | 'ES' | 'IT') {
    console.log('Changing learning language to:', langCode);
    this.selectedLearningLanguage = langCode;
    this.isLearningLanguageDropdownOpen = false;

    // Update on backend
    this.userLearningService.updateLearningConfig({
      learningLanguage: langCode
    }).subscribe({
      next: (updated) => {
        console.log('Learning language updated on backend:', updated);
        this.learningData = updated;
      },
      error: (err) => {
        console.error('Failed to update learning language:', err);
        // Revert on error
        if (this.learningData) {
          this.selectedLearningLanguage = this.learningData.learningLanguage;
        }
      }
    });
  }

  selectCurrentLevel(level: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2') {
    console.log('Changing current level to:', level);
    this.selectedCurrentLevel = level;
    this.isCurrentLevelDropdownOpen = false;

    // Update on backend
    this.userLearningService.updateLearningConfig({
      currentLevel: level
    }).subscribe({
      next: (updated) => {
        console.log('Current level updated on backend:', updated);
        this.learningData = updated;
      },
      error: (err) => {
        console.error('Failed to update current level:', err);
        // Revert on error
        if (this.learningData) {
          this.selectedCurrentLevel = this.learningData.currentLevel;
        }
      }
    });
  }

  selectTargetLevel(level: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2') {
    // Prevent selecting a target level lower than current level
    if (this.selectedCurrentLevel && this.levelOrder[level] < this.levelOrder[this.selectedCurrentLevel]) {
      console.warn('Cannot select target level lower than current level');
      return;
    }

    console.log('Changing target level to:', level);
    this.selectedTargetLevel = level;
    this.isTargetLevelDropdownOpen = false;

    // Update on backend
    this.userLearningService.updateLearningConfig({
      targetLevel: level
    }).subscribe({
      next: (updated) => {
        console.log('Target level updated on backend:', updated);
        this.learningData = updated;
      },
      error: (err) => {
        console.error('Failed to update target level:', err);
        // Revert on error
        if (this.learningData) {
          this.selectedTargetLevel = this.learningData.targetLevel;
        }
      }
    });
  }

  // Check if a target level is valid (not lower than current level)
  isTargetLevelDisabled(targetLevel: string): boolean {
    if (!this.selectedCurrentLevel) return false;
    return this.levelOrder[targetLevel] < this.levelOrder[this.selectedCurrentLevel];
  }

  getSelectedLearningLanguage() {
    return this.learningLanguages.find(lang => lang.code === this.selectedLearningLanguage) || this.learningLanguages[0];
  }

  getSelectedCurrentLevel() {
    return this.levels.find(level => level.code === this.selectedCurrentLevel) || this.levels[0];
  }

  getSelectedTargetLevel() {
    return this.levels.find(level => level.code === this.selectedTargetLevel) || this.levels[1];
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.custom-select')) {
      this.isLanguageDropdownOpen = false;
      this.isThemeDropdownOpen = false;
      this.isLearningLanguageDropdownOpen = false;
      this.isCurrentLevelDropdownOpen = false;
      this.isTargetLevelDropdownOpen = false;
    }
  }
}
