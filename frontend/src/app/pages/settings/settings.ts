import { Component, inject, HostListener } from '@angular/core';
import { TranslocoDirective, TranslocoService } from '@jsverse/transloco';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-settings',
  imports: [TranslocoDirective, CommonModule],
  templateUrl: './settings.html',
  styleUrl: './settings.css'
})
export class Settings {
  private translocoService = inject(TranslocoService);

  availableLanguages = [
    { code: 'de', name: 'Deutsch', flag: 'de' },
    { code: 'en', name: 'English', flag: 'gb' }
  ];

  isDropdownOpen = false;

  get currentLang() {
    return this.translocoService.getActiveLang();
  }

  changeLanguage(langCode: string) {
    this.translocoService.setActiveLang(langCode);
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  selectLanguage(langCode: string) {
    this.changeLanguage(langCode);
    this.isDropdownOpen = false;
  }

  getSelectedLanguage() {
    return this.availableLanguages.find(lang => lang.code === this.currentLang) || this.availableLanguages[0];
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.custom-select')) {
      this.isDropdownOpen = false;
    }
  }
}
