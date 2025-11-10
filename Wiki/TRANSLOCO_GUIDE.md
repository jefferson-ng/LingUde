# Transloco - Internationalisierung (i18n) Guide

## 📚 Was ist Transloco?

Transloco ist eine moderne Internationalisierungs-Bibliothek (i18n) für Angular. Sie ermöglicht es uns, die App in mehreren Sprachen anzubieten.

## 🎯 Aktuelle Konfiguration

- **Verfügbare Sprachen**: Deutsch (de), English (en)
- **Standard-Sprache**: Deutsch (de)
- **Übersetzungsdateien**: `/frontend/src/assets/i18n/`

## 📁 Dateistruktur

```
frontend/
  src/
    assets/
      i18n/
        de.json          # Deutsche Übersetzungen
        en.json          # Englische Übersetzungen
    app/
      transloco-loader.ts   # HTTP Loader für Translation Files
      app.config.ts         # Transloco Konfiguration
```

## 🚀 Verwendung in Komponenten

### In HTML Templates (Empfohlen)

```html
<ng-container *transloco="let t">
  <h1>{{ t('dashboard.title') }}</h1>
  <p>{{ t('dashboard.subtitle') }}</p>
  
  <!-- Mit Parametern -->
  <span>{{ t('user.greeting', { name: userName }) }}</span>
</ng-container>
```

### In TypeScript

```typescript
import { Component, inject } from '@angular/core';
import { TranslocoService } from '@jsverse/transloco';

export class MyComponent {
  private translocoService = inject(TranslocoService);

  ngOnInit() {
    // Übersetzung abrufen
    const title = this.translocoService.translate('dashboard.title');
    
    // Aktive Sprache abrufen
    const currentLang = this.translocoService.getActiveLang();
    
    // Sprache wechseln
    this.translocoService.setActiveLang('en');
    
    // Auf Sprachwechsel reagieren
    this.translocoService.langChanges$.subscribe(lang => {
      console.log('Sprache gewechselt zu:', lang);
    });
  }
}
```

### Imports für Komponenten

```typescript
import { TranslocoDirective } from '@jsverse/transloco';

@Component({
  selector: 'app-my-component',
  imports: [TranslocoDirective],  // Directive importieren!
  templateUrl: './my-component.html'
})
```

## 📝 Übersetzungen hinzufügen

### 1. Übersetzungskeys in JSON-Dateien hinzufügen

**de.json:**
```json
{
  "myFeature": {
    "title": "Mein Feature",
    "description": "Beschreibung des Features",
    "button": "Klick mich"
  }
}
```

**en.json:**
```json
{
  "myFeature": {
    "title": "My Feature",
    "description": "Description of the feature",
    "button": "Click me"
  }
}
```

### 2. Im Template verwenden

```html
<ng-container *transloco="let t">
  <h2>{{ t('myFeature.title') }}</h2>
  <p>{{ t('myFeature.description') }}</p>
  <button>{{ t('myFeature.button') }}</button>
</ng-container>
```

## 🔧 Sprache wechseln

Die Sprache kann in den Einstellungen gewechselt werden. Programmatisch:

```typescript
// Sprache auf Englisch setzen
this.translocoService.setActiveLang('en');

// Aktuelle Sprache abrufen
const lang = this.translocoService.getActiveLang(); // 'de' oder 'en'
```

## 📋 Best Practices

### ✅ DO

- **Strukturierte Keys verwenden**: `feature.section.element` statt flacher Keys
- **Konsistente Namensgebung**: Gleiche Struktur in de.json und en.json
- **Beschreibende Namen**: `button.save` statt `btn1`
- **TranslocoDirective importieren**: In jeder Komponente die Übersetzungen nutzt

### ❌ DON'T

- **Keine hartcodierten Texte**: Alle sichtbaren Texte sollten übersetzt werden
- **Keine doppelten Keys**: Jeden Text nur einmal definieren
- **Keine fehlenden Übersetzungen**: Immer beide Sprachen pflegen

## 🎮 Für Sprachlernspiele (siehe LANGUAGE_LEARNING_GAMES.md)

Transloco ist ideal für:
- UI-Texte (Menüs, Buttons, Anweisungen)
- Spielanweisungen
- Feedback-Nachrichten

Für Lerninhalte siehe separate Dokumentation.

## 🐛 Troubleshooting

### Problem: "Cannot find module '@jsverse/transloco'"
```bash
npm install @jsverse/transloco
```

### Problem: Übersetzungen werden nicht geladen
- Prüfe, ob `/assets/i18n/de.json` und `en.json` existieren
- Prüfe Browser DevTools → Network für 404-Fehler
- Stelle sicher, dass `angular.json` assets korrekt konfiguriert hat

### Problem: Template zeigt Keys statt Übersetzungen
- `TranslocoDirective` in Component imports hinzufügen
- Prüfe, ob der Key in der JSON-Datei existiert

## 📚 Weitere Ressourcen

- [Transloco Offizielle Dokumentation](https://jsverse.github.io/transloco/)
- [Angular i18n Best Practices](https://angular.dev/guide/i18n)

## 👥 Fragen?

Bei Fragen zum Setup oder zur Verwendung von Transloco, kontaktiere das Team!
