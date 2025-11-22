 LinguDE - Language Learning Platform

## 🎯 Project Overview

**LinguDE** (Linguistics + UDE) is a **full-stack web-based language learning application** developed by team-t06. It's a gamified platform that teaches languages (German, English, French, Spanish, Italian) through interactive exercises with progress tracking, XP rewards, and streak mechanics.

**Repository**: GitLab-hosted SEP Winter 2025 project
**Team**: 6-person development team


---

## 🏗️ Architecture

### System Components

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│   Angular 20    │────▶ │  Spring Boot    │────▶ │  PostgreSQL 16  │
│   Frontend      │      │   Backend       │      │   Database      │
│   Port: 4200    │      │   Port: 8080    │      │   Port: 5432    │
└─────────────────┘      └─────────────────┘      └─────────────────┘
```

All services are containerized via **Docker Compose** (`compose.yaml`)

---

## 💻 Frontend (Angular 20)

### Tech Stack
- **Framework**: Angular 20.3.0 (standalone components)
- **Language**: TypeScript 5.9.2
- **State**: Angular Signals + RxJS Observables
- **i18n**: Transloco 8.1.0 (German/English)
- **Icons**: Lucide Angular
- **Testing**: Jasmine + Karma

### Key Files
- `app.routes.ts` - Routing with lazy loading
- `app.config.ts` - App configuration
- `auth.guard.ts` - Route protection
- `auth.interceptor.ts` - JWT injection

### Pages (All Lazy-Loaded)

| Route | Component | Purpose |
|-------|-----------|---------|
| `/home` | home | Landing page (public) |
| `/login` | login | Authentication (public) |
| `/register` | register | User registration (public) |
| `/dashboard` | dashboard | Main hub with stats (protected) |
| `/learning` | learning | Exercise interface (protected) |
| `/lessons` | lessons | Lesson overview (protected) |
| `/goals` | goals | Learning goals (protected) |
| `/leaderboard` | leaderboard | User rankings (protected) |
| `/statistics` | statistics | Progress analytics (protected) |
| `/settings` | settings | User preferences (protected) |

### Core Services

**auth.service.ts**
- Manages JWT tokens (access + refresh)
- Handles login/signup/logout
- Stores tokens in localStorage
- Provides current user Observable

**exercise.service.ts**
- Fetches MCQ and Fill-Blank exercises
- Filters by language, difficulty, topic
- Submits answers and receives results
- Uses Angular Signals for state

**user-learning.service.ts**
- Tracks XP accumulation
- Maintains streak count
- Calculates level progression (100 XP/level)

### Components

**exercise-viewer**
- Polymorphic component supporting MCQ and Fill-Blank types
- Handles answer validation
- Displays feedback and XP earned

**header** & **footer**
- Navigation and branding

### Models
- `exercise.model.ts` - Exercise interfaces
- `user-settings.model.ts` - Settings types

---

## ⚙️ Backend (Spring Boot 3.5.6)

### Tech Stack
- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security + JWT (JJWT 0.12.6)
- **Database**: PostgreSQL 16
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Build**: Gradle

### Project Structure

```
backend/src/main/java/com/sep/sep_backend/
├── auth/               # JWT authentication
├── user/               # User management
└── exercise/           # Exercise system
```

### Modules

#### 1️⃣ Authentication Module (`auth/`)

**Key Classes:**
- `JwtAuthenticationFilter.java` - Extracts and validates JWT from requests
- `JwtUtil.java` - Generates/validates access & refresh tokens
- `RefreshTokenService.java` - Token rotation logic
- `SecurityConfig.java` - Spring Security configuration

**Features:**
- Access tokens: 30-minute expiration
- Refresh tokens: 7-day expiration, stored in DB
- Token rotation on refresh (old tokens invalidated)
- BCrypt password hashing

#### 2️⃣ User Module (`user/`)

**Entities:**
- `User.java` - Core account (username, email, password_hash)
- `UserProfile.java` - Display info (display_name, avatar, bio, city)
- `UserSettings.java` - Preferences (theme, ui_language, notifications)
- `UserLearning.java` - Progress (learning_language, level, xp, streak)

**Controllers:**
- `UserController.java` - Auth endpoints (signup, signin, logout, refresh)
- `UserLearningController.java` - Learning progress API
- `UserSettingsController.java` - Settings management

**Services:**
- `UserService.java` - User CRUD & authentication logic
- `UserLearningService.java` - XP and streak management
- `UserSettingsService.java` - Settings persistence
- `UserProfileService.java` - Profile management

#### 3️⃣ Exercise Module (`exercise/`)

**Entities:**
- `ExerciseMcq.java` - Multiple Choice Questions (question, correct_answer, 3 wrong options)
- `ExerciseFillBlank.java` - Fill-in-the-Blank (sentence_with_blank using `___` marker)
- `UserProgress.java` - Tracks completion status per user/exercise

**Pattern**: Concrete Table Inheritance (separate tables for MCQ and Fill-Blank)

**Controllers:**
- `ExerciseController.java` - REST API for exercise CRUD and submission

**Services:**
- `ExerciseService.java` - Exercise business logic, answer validation

**Features:**
- Answer shuffling for MCQs (randomized option order)
- Case-insensitive, whitespace-normalized fill-blank validation
- One-time XP reward (prevents farming)
- UNIQUE constraint on (user_id, exercise_id, exercise_type) in UserProgress

---

## 🗄️ Database Schema

### Core Tables

**User Tables:**
```
users
├── id (UUID, PK)
├── username (VARCHAR, UNIQUE)
├── email (VARCHAR, UNIQUE)
├── password_hash (VARCHAR)
├── email_verified (BOOLEAN)
└── created_at, updated_at

user_profile (1:1 with users)
user_settings (1:1 with users)
user_learning (1:1 with users)
refresh_token (N:1 with users)
```

**Exercise Tables:**
```
exercise_mcq
├── id (UUID, PK)
├── target_language (ENUM)
├── difficulty_level (ENUM: A1-C2)
├── topic (VARCHAR)
├── xp_reward (INT, default 10)
├── question_text (TEXT)
├── correct_answer (VARCHAR)
├── wrong_option_1/2/3 (VARCHAR)
└── created_at

exercise_fill_blank
├── id (UUID, PK)
├── target_language, difficulty_level, topic, xp_reward
├── sentence_with_blank (TEXT, uses "___")
├── correct_answer (VARCHAR)
└── created_at

user_progress
├── id (UUID, PK)
├── user_id (UUID, FK)
├── exercise_id (UUID)
├── exercise_type (ENUM: MCQ, FILL_BLANK)
├── is_completed (BOOLEAN)
├── completed_at (TIMESTAMP)
├── xp_earned (INT)
└── UNIQUE(user_id, exercise_id, exercise_type)
```

---

## 🔌 API Endpoints

### Authentication
```
POST   /api/auth/signup        - User registration
POST   /api/auth/signin        - Login (returns JWT)
POST   /api/auth/logout        - Invalidate all refresh tokens
POST   /api/auth/refresh       - Get new access token
GET    /api/auth/me            - Get current user info
```

### Exercises
```
GET    /api/exercises/mcq                  - List MCQs (filterable)
GET    /api/exercises/fillblank            - List Fill-Blank exercises
GET    /api/exercises/mcq/{id}             - MCQ details
GET    /api/exercises/fillblank/{id}       - Fill-Blank details
POST   /api/exercises/mcq/{id}/submit      - Submit MCQ answer
POST   /api/exercises/fillblank/{id}/submit - Submit Fill-Blank answer
```

### User Learning
```
GET    /api/user/learning/myLearning  - Get progress (XP, streak, level)
POST   /api/user/learning/addXp       - Add XP to user
```

### Settings
```
GET    /api/settings           - Get user settings
POST   /api/settings/update    - Update settings
```

---

## 🎮 Key Features

### ✅ Implemented (Sprint 2)
- **Authentication**: Signup, login, logout with JWT
- **Exercise System**: MCQ and Fill-Blank types
- **Progress Tracking**: XP accumulation, streak counting
- **Internationalization**: German/English UI
- **Responsive Design**: Mobile-friendly layout
- **Gamification**: Levels (100 XP/level), XP rewards
- **Security**: JWT with refresh token rotation, BCrypt passwords

### 🚧 Planned (Future Sprints)
- Social features (friends, real leaderboards)
- Badge/achievement system
- Discord OAuth integration
- Admin panel for content creation
- Advanced statistics
- Daily challenges and goals
- Content management system

---

## 📐 Architectural Decisions

### Frontend Patterns
1. **Standalone Components**: No NgModules (Angular 14+ approach)
2. **Signal-based State**: Angular Signals for reactivity
3. **Route Guards**: AuthGuard protects authenticated routes
4. **HTTP Interceptors**: Auto-inject JWT tokens
5. **Lazy Loading**: All page components loaded on-demand

### Backend Patterns
1. **Layered Architecture**: Controller → Service → Repository
2. **DTO Pattern**: Separate request/response objects from entities
3. **Concrete Table Inheritance**: Separate tables for exercise types (simpler queries)
4. **Global Exception Handling**: Custom exceptions with @ControllerAdvice
5. **Transactional Operations**: @Transactional for data consistency

### Security Choices
- **Refresh Token Rotation**: Old tokens invalidated after refresh
- **One-time XP Rewards**: Prevents XP farming via UNIQUE constraint
- **CORS Configuration**: Allows localhost:4200 during development
- **Password Hashing**: BCrypt with strength 12

---

## 📚 Documentation

Comprehensive Wiki at `Wiki/`:
- `Brainstorming.md` - Feature planning
- `db_design.md` - Database schema rationale
- `technical_design-decisions.md` - Architecture decisions
- `ROUTING_SETUP.md` - Frontend routing guide
- `TRANSLOCO_GUIDE.md` - i18n implementation
- `design-guidelines.md` - UI/UX standards

---

## 🐳 Deployment

**Docker Compose Setup** (`compose.yaml`):
- **PostgreSQL**: Port 5432, volume-backed persistence
- **Backend**: Port 8080, waits for DB health check
- **Frontend**: Port 4200 (Nginx in production)

**Start the stack:**
```bash
docker-compose up -d
```

---

## 📊 Project Metrics

- **Frontend Files**: ~65 TypeScript/HTML/CSS files
- **Backend Files**: ~54 Java files
- **Languages**: German (primary), English
- **Exercise Types**: 2 (MCQ, Fill-Blank)
- **Difficulty Levels**: 6 (A1, A2, B1, B2, C1, C2)
- **Supported Languages**: 5 (DE, EN)

---

## 🎓 Summary


- ✅ Clean separation of concerns (frontend/backend)
- ✅ Scalable exercise system (polymorphic design)
- ✅ Secure authentication (JWT with refresh tokens)
- ✅ Gamification mechanics (XP, streaks, levels)
- ✅ Internationalization support
- ✅ Comprehensive documentation
- ✅ Containerized deployment

