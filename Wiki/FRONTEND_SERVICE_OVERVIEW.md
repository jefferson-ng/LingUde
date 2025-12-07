# Frontend Service Files Overview

This document explains the main service files in the Angular frontend and their responsibilities.

---

## 1. `auth.service.ts`
Handles user authentication (login, registration, logout) and manages the current user session.
- **API endpoints:** `/api/auth/signin`, `/api/auth/signup`, `/api/auth/logout`, `/api/auth/me`
- **Features:**
  - Login, register, logout
  - Stores JWT tokens in localStorage
  - Fetches and exposes user info as observable
  - Navigates to home on logout
    - Uses RxJS `BehaviorSubject` for user and loading state

---

## 2. `user-settings.service.ts`
Manages user preferences such as UI language, theme, and notifications. (Not fully functional)
- **API endpoint:** `/api/settings`
- **Features:**
  - Fetch and update user settings
  - Update language, theme, notifications individually
  - Stores current settings in a signal
    - Uses RxJS and Angular signals for reactive state

---

## 3. `user-learning.service.ts`
Tracks user learning progress, XP, and streaks.
- **API endpoint:** `/api/user/learning`
- **Features:**
  - Fetches current learning data (level, XP, streak)
  - Adds XP after exercise completion
  - Exposes learning data as observable
  - Clears cache on logout
    - Uses RxJS `BehaviorSubject` for state

---

## 4. `exercise.service.ts`
Handles fetching and submitting exercises (MCQ and Fill-Blank). (Exercises will be adjusted next sprint)
- **API endpoint:** `/api/exercises`
- **Features:**
  - Fetches all exercises, or by type (MCQ, Fill-Blank)
  - Filters by language, difficulty, topic
  - Fetches exercise details by ID
  - Submits answers and receives result
  - Type guards for exercise types
    - Client-side filtering (backend does not support filter params yet)
    - Uses Angular signals for current exercise state

---

## Language-Based Exercise Filtering

The application now supports filtering exercises by the user's target language preference. This feature ensures users only see exercises in their selected learning language (German or English).

### How It Works

1. **User Preference Storage**
   - User selects target language during registration (`level-selection` page) or in settings
   - Language preference stored in backend (`UserLearning.learningLanguage` field)
   - API: `GET /api/user/learning/myLearning` returns user's language preference

2. **Exercise Filtering Flow**
   - Learning page loads and fetches user's language preference via `UserLearningService`
   - When user starts a lesson, `ExerciseService.getExercises()` is called with the user's language
   - Backend returns all exercises; frontend filters by `targetLanguage` field (client-side)
   - Only exercises matching user's selected language are displayed

3. **Supported Languages**
   - Currently: German (`DE`) and English (`EN`)
   - Language type defined in `exercise.model.ts`: `type Language = 'DE' | 'EN'`
   - Future languages can be added by updating the type and adding exercise seed data

4. **Key Files**
   - `learning.ts` (line 175): Uses `this.userLanguage()` to filter exercises
   - `settings.ts` (lines 32-35): Language selection dropdown
   - `level-selection.ts` (lines 26-29): Language selection during registration
   - `exercise.model.ts` (line 10): Language type definition

---

