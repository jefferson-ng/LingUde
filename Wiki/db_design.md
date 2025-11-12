 
# DATABASE SCHEMA - Sprint 2
Db_diagram available at [db_diagram](https://dbdiagram.io/d/sepT06_db_diagram-690b4bf16735e111705f139b).

## Core Authentication & User Data

### USER (Core authentication)
- `id` UUID PRIMARY KEY
- `username` VARCHAR(50) NOT NULL
- `email` VARCHAR(255) UNIQUE NOT NULL
- `password_hash` VARCHAR(60) NOT NULL
- `email_verified` BOOLEAN DEFAULT FALSE
- `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP

### USER_PROFILE (Display information)
- `id` UUID PRIMARY KEY
- `user_id` UUID FOREIGN KEY → USER(id)
- `display_name` VARCHAR(100)
- `avatar_url` VARCHAR(255)
- `bio` TEXT
- `city` VARCHAR(100)
- `country` VARCHAR(100)

### USER_SETTINGS (User preferences)
- `id` UUID PRIMARY KEY
- `user_id` UUID FOREIGN KEY → USER(id)
- `ui_language`  ENUM('EN', 'DE', 'FR'...)
- `notifications_enabled` BOOLEAN DEFAULT TRUE
- `theme` VARCHAR(10) -- or ENUM('LIGHT', 'DARK', 'AUTO') DEFAULT 'AUTO'

### USER_LEARNING (Learning progress)
- `id` UUID PRIMARY KEY
- `user_id` UUID FOREIGN KEY → USER(id)
- `learning_language`  ENUM('DE', 'EN', 'FR'...)
- `current_level`  ENUM('A1', 'A2', 'B1', 'B2', 'C1', 'C2')
- `target_level` ENUM('A1', 'A2', 'B1', 'B2', 'C1', 'C2')
- `xp` INT DEFAULT 0
- `streak_count` INT DEFAULT 0
- `last_activity_date` DATE


---

## Exercise System

### EXERCISE_MCQ (Multiple Choice Questions)
- `id` UUID PRIMARY KEY
- `target_language` VARCHAR(10) -- ENUM: DE, EN, FR, ES...
- `difficulty_level` VARCHAR(10) -- ENUM: A1, A2, B1, B2, C1, C2
- `topic` VARCHAR(50) -- e.g., 'grammar', 'vocabulary', 'verbs', 'weather', ... 
- `xp_reward` INT DEFAULT 10
- `question_text` VARCHAR(500) NOT NULL
- `correct_answer` VARCHAR(100) NOT NULL
- `wrong_option_1` VARCHAR(100) NOT NULL
- `wrong_option_2` VARCHAR(100) NOT NULL
- `wrong_option_3` VARCHAR(100) NOT NULL
- `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP

### EXERCISE_FILL_BLANK (Fill in the Blank)
- `id` UUID PRIMARY KEY
- `target_language` VARCHAR(10) -- ENUM: DE, EN, FR, ES...
- `difficulty_level` VARCHAR(10) -- ENUM: A1, A2, B1, B2, C1, C2
- `topic` VARCHAR(50) -- e.g., 'grammar', 'vocabulary', 'verbs'
- `xp_reward` INT DEFAULT 10
- `sentence_with_blank` VARCHAR(500) NOT NULL -- Format: "Der Hund ___ groß"
- `correct_answer` VARCHAR(100) NOT NULL -- e.g., "ist"
- `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP

### USER_PROGRESS (Tracks exercise completion)
- `id` UUID PRIMARY KEY
- `user_id` UUID FOREIGN KEY → USER(id)
- `exercise_id` UUID (references either EXERCISE_MCQ or EXERCISE_FILL_BLANK)
- `exercise_type` VARCHAR(20) -- ENUM: 'MCQ', 'FILL_BLANK'
- `is_completed` BOOLEAN DEFAULT FALSE
- `completed_at` TIMESTAMP NULL (set when first completed successfully)
- `xp_earned` INT DEFAULT 0
- `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP (when user first attempted)
- **UNIQUE CONSTRAINT:** (user_id, exercise_id, exercise_type)

## Design Decisions & Rationale

### Exercise Tables

**Concrete Table Inheritance (Strategy 3)**
- Each exercise type has its own self-contained table with duplicated metadata
- **Rationale:** Simplifies queries (no JOINs needed), faster development for 6-person team with 21-day deadline
- **Trade-off:** Metadata duplication across tables vs query simplicity - chose simplicity

**Target Language Only**
- Exercises test knowledge in the target language, not translation between languages
- Single `target_language` field instead of `language_from` + `language_to`
- **Rationale:** Matches learning model where users learn one language at a time

**Blank Marker Convention**
- Using `___` (three underscores) as placeholder in fill-in-the-blank exercises
- **Rationale:** Visually intuitive, easy to parse on frontend, common pattern in language learning apps

**Fixed MCQ Options**
- Exactly 3 wrong options (4 total choices including correct answer)
- **Rationale:** Standard MCQ format, can extend later if needed without breaking existing data

### User Progress Tracking

**Simple Completion Tracking**
- One record per user per exercise (enforced by UNIQUE constraint)
- Only tracks final completion state, not attempt history
- **Rationale:** Meets gamification requirements (XP + streaks) without unnecessary complexity

**Single XP Award**
- XP awarded only on first successful completion
- `xp_earned` set once when `is_completed` flips to true
- **Rationale:** Prevents XP farming, encourages progression to new content

**Retry Support Without History**
- Users can retry exercises but only completion is tracked
- No `attempts_count` field
- **Rationale:** User experience allows practice without cluttering database with attempt history

**Streak Calculation Support**
- `completed_at` timestamp enables querying distinct activity dates
- Query: `SELECT DISTINCT DATE(completed_at) FROM user_progress WHERE user_id = ?`
- **Rationale:** Supports streak tracking without additional tables or complexity

**Exercise Type Discrimination**
- `exercise_type` ENUM field identifies which exercise table to reference
- Enables polymorphic relationship to different exercise types
- **Rationale:** Single progress table works with multiple exercise types without complex JOIN logic
---

## Future Enhancements (Sprint 3+)

### BADGE (Badge definitions)
- `id` UUID PRIMARY KEY
- `name` VARCHAR(100) NOT NULL
- `description` TEXT
- `icon_url` VARCHAR(255)

### USER_BADGES (Join table - tracks earned badges)
- `id` UUID PRIMARY KEY
- `user_id` UUID FOREIGN KEY → USER(id)
- `badge_id` UUID FOREIGN KEY → BADGE(id)
- `earned_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- **UNIQUE CONSTRAINT:** (user_id, badge_id)

### FRIENDSHIP (Friend relationships)
- `id` UUID PRIMARY KEY
- `user_id` UUID FOREIGN KEY → USER(id) (requester)
- `friend_id` UUID FOREIGN KEY → USER(id) (receiver)
- `status` VARCHAR(20) -- ENUM: 'PENDING', 'ACCEPTED', 'REJECTED', 'BLOCKED'
- `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- **UNIQUE CONSTRAINT:** (user_id, friend_id)

---

## Design Decisions & Rationale

### Badge System

**Simple Badge Definition**
- Single `description` field instead of separate `description` + `criteria`
- **Rationale:** Avoids redundancy; badge earning logic lives in backend code, not database

**Earned Timestamp**
- `earned_at` in USER_BADGES tracks when badge was earned
- **Rationale:** Enables UI features like "recently earned," sorting, and celebrating achievements

**Unique Badge Earning**
- UNIQUE constraint prevents earning same badge multiple times
- **Rationale:** Badges represent one-time achievements

### Friendship System

**Request-Based Model**
- Stores requester (`user_id`) and receiver (`friend_id`) with status
- Status ENUM supports workflow: PENDING → ACCEPTED/REJECTED
- **Rationale:** Industry standard pattern, supports full friend request UX

**No Duplicate Storage**
- Single row per friendship (not bidirectional duplication)
- **Rationale:** Avoids data redundancy
- **Trade-off:** Queries require UNION to get all friends, but maintains data integrity

**Duplicate Prevention**
- UNIQUE constraint prevents same direction duplicates
- Opposite direction duplicates prevented by application-level validation
- **Rationale:** Database constraints are limited with UUIDs; application logic provides full protection
---

## Design Decisions
- The exercises are split up into multiple more appropriate tables allowing more efficient & specific tables , less null values . New type of exercise can be easily defined thereby defining a new db structure . 
### Rationale
- **UUID for IDs**: More secure, prevents ID guessing
- **Separated user tables**: Follows separation of concerns, cleaner queries
- **Email-only login**: Simpler auth logic for Sprint 2, industry standard
- **email_verified field**: Future-proofing without implementation overhead
- **YAGNI principle**: Add fields now, implement features later when needed

### Trade-offs
- More joins required but JPA/@OneToOne handles this elegantly
- Slightly more complex schema but better organization and maintainability