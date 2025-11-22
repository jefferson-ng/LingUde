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
// Public routes (no AuthGuard):
/                    → /home (redirect)
/home                → Home Component
/login               → Login Component
/register            → Register Component

// Protected routes (require AuthGuard):
/dashboard           → Dashboard Component
/learning            → Learning Component
/lessons             → Lessons Component
/goals               → Goals Component
/leaderboard         → Leaderboard Component
/statistics          → Statistics Component
/settings            → Settings Component
/**                  → /dashboard (404 redirect, protected)
```


Hinweis: Die meisten Seiten sind durch AuthGuard geschützt und nur nach Login erreichbar.
Die Routing-Konfiguration nutzt `loadComponent` für Lazy Loading und Layout-Komponenten (`LandingLayout`, `App`) für die Strukturierung der öffentlichen und geschützten Bereiche.
Siehe app.routes.ts für die vollständige Konfiguration und Lazy Loading.



---

