import { Component, inject } from '@angular/core';
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
    { code: 'de', name: 'Deutsch', flag: '🇩🇪' },
    { code: 'en', name: 'English', flag: '🇬🇧' }
  ];

  get currentLang() {
    return this.translocoService.getActiveLang();
  }

  changeLanguage(langCode: string) {
    this.translocoService.setActiveLang(langCode);
  }
}
