-- Migration: Add incorrect_attempts column to user_progress table
-- This enables tracking of incorrect exercise attempts for the retry functionality

ALTER TABLE user_progress
ADD COLUMN incorrect_attempts INTEGER NOT NULL DEFAULT 0;

-- Add comment to the column for documentation
COMMENT ON COLUMN user_progress.incorrect_attempts IS 'Number of incorrect attempts for this exercise';
