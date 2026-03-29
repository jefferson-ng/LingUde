# Lingude — Language Learning Web Application

A gamified, full-stack language learning platform (similar to Duolingo) built as part of the **Software Engineering Project (SEP)** at the University of Duisburg-Essen.

Users can learn languages through structured lessons, vocabulary/grammar exercises, multiple-choice quizzes, and an AI-powered chat tutor — all with XP, streaks, and social features to keep them motivated.

---

## Tech Stack

| Layer     | Technology                          |
|-----------|-------------------------------------|
| Frontend  | Angular 20, TypeScript, Transloco (i18n) |
| Backend   | Spring Boot 3.5, Java 21, Spring Security (JWT) |
| Database  | PostgreSQL 16, Flyway (migrations)  |
| AI Tutor  | Google Gemini 2.0                   |
| Email     | MailHog (dev) / SMTP (prod)         |
| DevOps    | Docker, Docker Compose              |

---

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/) (recommended — runs everything in one command)
- **Or**, for local development without Docker:
  - Node.js ≥ 20 and npm
  - Java 21 (JDK)
  - PostgreSQL 16 running locally

---

## Running the Project

### Option 1 — Docker Compose (recommended)

This spins up all four services: database, backend, frontend, and the local mail server.

```bash
# From the project root
docker compose up --build
```

| Service   | URL                          | Description                     |
|-----------|------------------------------|---------------------------------|
| Frontend  | http://localhost:4200        | Angular app                     |
| Backend   | http://localhost:8080        | Spring Boot REST API            |
| MailHog   | http://localhost:8025        | Local email inbox (web UI)      |
| Database  | localhost:5432               | PostgreSQL (`sep`/`sep`/`sep`)  |

To stop and remove containers:

```bash
docker compose down
```

To also delete the database volume (full reset):

```bash
docker compose down -v
```

---

### Option 2 — Local Development (without Docker)

**1. Start PostgreSQL** and create the database:

```sql
CREATE USER sep WITH PASSWORD 'sep';
CREATE DATABASE sep OWNER sep;
```

**2. Run the backend:**

```bash
cd backend
./gradlew bootRun
```

The backend starts on `http://localhost:8080`. Flyway will automatically apply all database migrations on startup.

**3. Run the frontend:**

```bash
cd frontend
npm install
npm start
```

The Angular dev server starts on `http://localhost:4200` with hot-reload enabled.

**4. Start MailHog** (optional — required for password reset emails):

```bash
docker run -p 1025:1025 -p 8025:8025 mailhog/mailhog
```

---

## Environment Variables

The backend works out-of-the-box with sensible defaults. To enable optional features, create a `backend/.env` file:

```env
# Required for AI Chat Tutor
GOOGLE_GEMINI_API_KEY=your-gemini-api-key

# Required for Pronunciation Assessment
AZURE_SPEECH_KEY=your-azure-speech-key
AZURE_SPEECH_REGION=your-azure-region

# JWT secret (change in production!)
JWT_SECRET=your-secret-key

# Override frontend URL for password reset emails
APP_RESET_BASE_URL=http://localhost:4200/reset-password
```

See the [AI Setup Guide](./Wiki/AI%20SETUP/AI_SETUP.md) for detailed instructions on configuring the Gemini API.

---

## Project Structure

```
Sep_Project/
├── backend/          # Spring Boot application (Java 21)
│   ├── src/
│   └── Dockerfile
├── frontend/         # Angular application
│   ├── src/
│   └── Dockerfile
├── Wiki/             # Project documentation
│   ├── AI SETUP/     # AI & Gemini integration guide
│   ├── design-guidelines.md
│   ├── db_design.md
│   └── technical_design-decisions.md
└── compose.yaml      # Docker Compose configuration
```

---

## Running Tests

**Backend:**

```bash
cd backend
./gradlew test
```

Tests use an in-memory H2 database — no PostgreSQL instance required.

**Frontend:**

```bash
cd frontend
npm test
```

Runs unit tests via [Karma](https://karma-runner.github.io).

---

## Documentation

| Document | Description |
|----------|-------------|
| [Design Guidelines](./Wiki/design-guidelines.md) | UI/UX conventions and component standards |
| [Database Design](./Wiki/db_design.md) | Entity-relationship overview |
| [Technical Decisions](./Wiki/technical_design-decisions.md) | Architecture decision records (ADRs) |
| [Routing Setup](./Wiki/ROUTING_SETUP.md) | Angular routing structure |
| [Transloco Guide](./Wiki/TRANSLOCO_GUIDE.md) | Internationalization (i18n) usage |
| [AI Setup Guide](./Wiki/AI%20SETUP/AI_SETUP.md) | Google Gemini AI chat integration |
| [AI Chat API](./Wiki/AI%20SETUP/API_CHAT.md) | REST API reference for AI endpoints |
| [Frontend Architecture](./Wiki/FRONTEND_ARCHITECTURE_OVERVIEW.md) | Component and module overview |
| [Frontend Services](./Wiki/FRONTEND_SERVICE_OVERVIEW.md) | Service layer documentation |
