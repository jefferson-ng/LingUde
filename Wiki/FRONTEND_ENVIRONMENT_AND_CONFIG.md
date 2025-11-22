# Frontend Environment & Configuration

This document explains the environment files and configuration options for the Angular frontend.

---

## Environment Files
- **`src/environments/environment.ts`**: Default (production) config
- **`src/environments/environment.development.ts`**: Development config

### Typical Properties
- `apiUrl`: Base URL for backend API (e.g., `http://localhost:8080`)
- Other feature flags as needed

---

## Switching Environments
Angular uses the correct environment file based on the build command:
- `ng build` → uses `environment.ts`
- `ng serve --configuration=development` → uses `environment.development.ts`

---

## Adding New Configurations
To add a new environment (e.g., staging):
1. Create `environment.staging.ts` in `src/environments/`
2. Add it to `angular.json` under `fileReplacements`

---

## Usage Example
```typescript
import { environment } from '../environments/environment';
const apiUrl = environment.apiUrl;
```
