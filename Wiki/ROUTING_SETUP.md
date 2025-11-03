# LinguDE - Angular Routing Setup

## 📁 Projektstruktur

```
frontend/src/app/
├── app.ts                    # Haupt-App Component mit Navigation
├── app.html                  # Layout mit Sidebar & Router Outlet
├── app.css                   # App-Layout Styling
├── app.routes.ts             # Routing-Konfiguration
├── app.config.ts             # App-Konfiguration
└── pages/
    ├── dashboard/           # Dashboard-Seite (Startseite)
    │   ├── dashboard.ts
    │   ├── dashboard.html
    │   └── dashboard.css
    ├── lessons/             # Lektionen-Übersicht
    │   ├── lessons.ts
    │   ├── lessons.html
    │   └── lessons.css
    ├── goals/               # Meine Ziele
    │   ├── goals.ts
    │   ├── goals.html
    │   └── goals.css
    ├── leaderboard/         # Bestenliste
    │   ├── leaderboard.ts
    │   ├── leaderboard.html
    │   └── leaderboard.css
    └── statistics/          # Statistiken
        ├── statistics.ts
        ├── statistics.html
        └── statistics.css
```



 **Seiten-Komponenten (Minimal/Raw)**
   - ✅ Dashboard - Grundstruktur
   - ✅ Lektionen - Grundstruktur
   - ✅ Meine Ziele - Grundstruktur
   - ✅ Bestenliste - Grundstruktur
   - ✅ Statistiken - Grundstruktur

> **Hinweis**: Alle Seiten enthalten nur die Grundstruktur ohne Dummy-Daten - bereit für Backend-Integration!

## 🎨 Design-Konzept

noch nicht durchdacht

## 🛠️ Verwendete Technologien

- **Angular 18+** (Standalone Components)
- **TypeScript**
- **Angular Router** (mit Lazy Loading)
- **CSS3** (Flexbox, Grid, Gradients)
- **Signal API** (Reaktive Daten)

## 📝 Routing-Konfiguration

```typescript
/                    → /dashboard (redirect)
/dashboard           → Dashboard Component
/lessons             → Lessons Component
/goals               → Goals Component
/leaderboard         → Leaderboard Component
/statistics          → Statistics Component
/**                  → /dashboard (404 redirect)
```



---

