-- V0_1__initial_schema.sql (Version 0.1 - runs after baseline V0, before V1)
-- Creates all base tables required by the application.
-- Uses CREATE TABLE IF NOT EXISTS for idempotency (safe for existing databases).
--
-- IMPORTANT: This migration must run BEFORE V1 (which alters the users table).
-- For existing databases where V1 is already applied, enable out-of-order migrations.

-- =====================================================
-- 1. USERS TABLE (base table - no FK dependencies)
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(60) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 2. ACHIEVEMENTS TABLE (no FK dependencies)
-- =====================================================
CREATE TABLE IF NOT EXISTS achievements (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    icon_url VARCHAR(255),
    type VARCHAR(30) NOT NULL
);

-- =====================================================
-- 3. EXERCISE_MCQ TABLE (no FK dependencies)
-- =====================================================
CREATE TABLE IF NOT EXISTS exercise_mcq (
    id UUID PRIMARY KEY,
    target_language VARCHAR(10) NOT NULL,
    difficulty_level VARCHAR(10) NOT NULL,
    topic VARCHAR(50),
    xp_reward INTEGER NOT NULL DEFAULT 10,
    question_text VARCHAR(500) NOT NULL,
    correct_answer VARCHAR(100) NOT NULL,
    wrong_option_1 VARCHAR(100) NOT NULL,
    wrong_option_2 VARCHAR(100) NOT NULL,
    wrong_option_3 VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 4. EXERCISE_FILL_BLANK TABLE (no FK dependencies)
-- =====================================================
CREATE TABLE IF NOT EXISTS exercise_fill_blank (
    id UUID PRIMARY KEY,
    target_language VARCHAR(10) NOT NULL,
    difficulty_level VARCHAR(10) NOT NULL,
    topic VARCHAR(50),
    xp_reward INTEGER NOT NULL DEFAULT 10,
    sentence_with_blank VARCHAR(500) NOT NULL,
    correct_answer VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 5. USER_LEARNING TABLE (FK to users)
-- =====================================================
CREATE TABLE IF NOT EXISTS user_learning (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    learning_language VARCHAR(10),
    current_level VARCHAR(10),
    target_level VARCHAR(10),
    xp INTEGER NOT NULL DEFAULT 0,
    streak_count INTEGER NOT NULL DEFAULT 0,
    last_activity_date DATE,
    completed_levels VARCHAR(500),
    CONSTRAINT fk_user_learning_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- 6. USER_PROFILE TABLE (FK to users)
-- =====================================================
CREATE TABLE IF NOT EXISTS user_profile (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    display_name VARCHAR(100),
    avatar_url VARCHAR(255),
    bio TEXT,
    city VARCHAR(100),
    country VARCHAR(100),
    CONSTRAINT fk_user_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- 7. USER_SETTINGS TABLE (FK to users)
-- =====================================================
CREATE TABLE IF NOT EXISTS user_settings (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    ui_language VARCHAR(10) DEFAULT 'EN',
    notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    theme VARCHAR(10) DEFAULT 'AUTO',
    CONSTRAINT fk_user_settings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- 8. USER_PROGRESS TABLE (FK to users)
-- =====================================================
CREATE TABLE IF NOT EXISTS user_progress (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    exercise_id UUID NOT NULL,
    exercise_type VARCHAR(20) NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMP,
    xp_earned INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_progress_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_progress UNIQUE (user_id, exercise_id, exercise_type)
);

-- =====================================================
-- 9. FRIENDSHIP TABLE (FK to users twice)
-- =====================================================
CREATE TABLE IF NOT EXISTS friendship (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    friend_id UUID NOT NULL,
    status VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_friendship_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friendship_friend FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_friendship UNIQUE (user_id, friend_id)
);

-- =====================================================
-- 10. REFRESH_TOKENS TABLE (FK to users)
-- =====================================================
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    device_info VARCHAR(500),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- 11. USER_ACHIEVEMENTS TABLE (FK to users and achievements)
-- =====================================================
CREATE TABLE IF NOT EXISTS user_achievements (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    achievement_id UUID NOT NULL,
    earned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_achievements_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_achievements_achievement FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_achievement UNIQUE (user_id, achievement_id)
);

-- =====================================================
-- INDEXES for better query performance
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_user_learning_user_id ON user_learning(user_id);
CREATE INDEX IF NOT EXISTS idx_user_profile_user_id ON user_profile(user_id);
CREATE INDEX IF NOT EXISTS idx_user_settings_user_id ON user_settings(user_id);
CREATE INDEX IF NOT EXISTS idx_user_progress_user_id ON user_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_user_progress_exercise ON user_progress(exercise_id, exercise_type);
CREATE INDEX IF NOT EXISTS idx_friendship_user_id ON friendship(user_id);
CREATE INDEX IF NOT EXISTS idx_friendship_friend_id ON friendship(friend_id);
CREATE INDEX IF NOT EXISTS idx_friendship_status ON friendship(status);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX IF NOT EXISTS idx_user_achievements_user_id ON user_achievements(user_id);
CREATE INDEX IF NOT EXISTS idx_exercise_mcq_language_level ON exercise_mcq(target_language, difficulty_level);
CREATE INDEX IF NOT EXISTS idx_exercise_fill_blank_language_level ON exercise_fill_blank(target_language, difficulty_level);