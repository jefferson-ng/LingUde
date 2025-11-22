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

