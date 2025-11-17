/**
 * Exercise Models - Based on DB Schema (db_design.md)
 * Supports: EXERCISE_MCQ and EXERCISE_FILL_BLANK
 */

export type ExerciseType = 'MCQ' | 'FILL_BLANK';

export type DifficultyLevel = 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';

export type Language = 'DE' | 'EN' | 'FR' | 'ES';

/**
 * Base interface shared by all exercise types
 */
export interface BaseExercise {
  id: string;
  targetLanguage: Language;
  difficultyLevel: DifficultyLevel;
  topic: string;
  xpReward: number;
  createdAt?: Date;
}

/**
 * Multiple Choice Question Exercise
 * Maps to EXERCISE_MCQ table
 */
export interface ExerciseMCQ extends BaseExercise {
  type: 'MCQ';
  questionText: string;
  correctAnswer: string;
  wrongOption1: string;
  wrongOption2: string;
  wrongOption3: string;
}

/**
 * Fill in the Blank Exercise
 * Maps to EXERCISE_FILL_BLANK table
 * Blank marker convention: ___ (three underscores)
 */
export interface ExerciseFillBlank extends BaseExercise {
  type: 'FILL_BLANK';
  sentenceWithBlank: string;  // e.g., "Der Hund ___ groß"
  correctAnswer: string;       // e.g., "ist"
}

/**
 * Union type for all exercise types
 */
export type Exercise = ExerciseMCQ | ExerciseFillBlank;

/**
 * User's progress on a specific exercise
 * Maps to USER_PROGRESS table
 */
export interface UserProgress {
  id: string;
  userId: string;
  exerciseId: string;
  exerciseType: ExerciseType;
  isCompleted: boolean;
  completedAt?: Date;
  xpEarned: number;
  createdAt: Date;
}

/**
 * User's answer submission
 */
export interface ExerciseSubmission {
  exerciseId: string;
  exerciseType: ExerciseType;
  userAnswer: string;
  isCorrect?: boolean;  // Set by backend
}

/**
 * Helper to get all options shuffled for MCQ
 */
export function getMCQOptions(exercise: ExerciseMCQ): string[] {
  const options = [
    exercise.correctAnswer,
    exercise.wrongOption1,
    exercise.wrongOption2,
    exercise.wrongOption3
  ];
  
  // Shuffle options (Fisher-Yates algorithm)
  for (let i = options.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [options[i], options[j]] = [options[j], options[i]];
  }
  
  return options;
}

/**
 * Helper to parse fill-blank sentence into parts
 * Returns: { before: string, after: string }
 */
export function parseFillBlankSentence(sentence: string): { before: string; after: string } {
  const parts = sentence.split('___');
  return {
    before: parts[0] || '',
    after: parts[1] || ''
  };
}
