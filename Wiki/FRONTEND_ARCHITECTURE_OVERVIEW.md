# Frontend Architecture Overview

This document provides a high-level overview of the frontend app structure and data flow.

---

## Main Concepts
- **Standalone Components**: Angular 18+ uses standalone components for modularity.
- **Routing**: Defined in `app.routes.ts`, with public and protected routes.
- **Services**: Handle API communication, state management, and business logic.
- **Signals & Observables**: Used for reactive state (e.g., user, settings, learning progress).
- **Environment Config**: API URLs and feature flags set via environment files.

---

## Data Flow
- User actions trigger service calls (e.g., login, fetch exercises)
- Services update signals/observables
- Components subscribe to signals/observables and update the UI

---

## State Management
- **Signals**: For local, reactive state (Angular 18+)
- **BehaviorSubject/Observable**: For shared state and async data

---

## Folder Structure
- `src/app/pages/` - Main pages (dashboard, learning, lessons, etc.)
- `src/app/components/` - Reusable UI components
- `src/app/services/` - API and state management services
- `src/app/models/` - TypeScript interfaces and types
- `src/assets/i18n/` - Translation files
- `src/environments/` - Environment configs

---

For more details, see the other Wiki files and comments in the codebase.
