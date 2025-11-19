import { Component, inject, HostListener, OnInit } from '@angular/core';
import { TranslocoDirective, TranslocoService } from '@jsverse/transloco';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserSettingsService } from '../../services/user-settings.service';
import { Language, Theme } from '../../models/user-settings.model';

@Component({
  selector: 'app-settings',
  imports: [TranslocoDirective, CommonModule, FormsModule],
  templateUrl: './settings.html',
  styleUrl: './settings.css'
})
export class Settings implements OnInit {
  private translocoService = inject(TranslocoService);
  private userSettingsService = inject(UserSettingsService);

  availableLanguages = [
    { code: 'de', name: 'Deutsch', flag: 'de', backendCode: Language.DE },
    { code: 'en', name: 'English', flag: 'gb', backendCode: Language.EN }
  ];

  availableThemes = [
    { value: Theme.LIGHT, key: 'themeLight' },
    { value: Theme.DARK, key: 'themeDark' },
    { value: Theme.AUTO, key: 'themeAuto' }
  ];

  isLanguageDropdownOpen = false;
  isThemeDropdownOpen = false;
  selectedTheme: Theme = Theme.AUTO;
  notificationsEnabled = true;
  isLoading = false;

  ngOnInit() {
    this.loadSettings();
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

  mapBackendToFrontendLang(backendLang: Language): string {
    const mapping: { [key: string]: string } = {
      'EN': 'en',
      'DE': 'de'
    };
    return mapping[backendLang] || 'de';
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
  }

  toggleThemeDropdown() {
    this.isThemeDropdownOpen = !this.isThemeDropdownOpen;
    this.isLanguageDropdownOpen = false;
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

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.custom-select')) {
      this.isLanguageDropdownOpen = false;
      this.isThemeDropdownOpen = false;
    }
  }
}
