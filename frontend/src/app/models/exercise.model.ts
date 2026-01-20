/**
 * Exercise Models - Aligned with Backend DTOs
 * Backend has ExerciseSummaryResponse and ExerciseDetailResponse
 */

export type ExerciseType = 'MCQ' | 'FILL_BLANK';

export type DifficultyLevel = 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';

export type Language = 'DE' | 'EN';

/**
 * Exercise Summary Response from Backend
 * Used for listing exercises (GET /api/exercises/mcq or /api/exercises/fillblank)
 */
export interface ExerciseSummaryResponse {
  id: string;
  type: ExerciseType;
  targetLanguage: Language;
  difficultyLevel: DifficultyLevel;
  topic: string;
  xpReward: number;
  previewText: string;  // Backend sends questionText or sentenceWithBlank as preview
  contentType?: 'VOCABULARY' | 'GRAMMAR' | 'SYNONYM';
}

/**
 * Exercise Detail Response from Backend
 * Used for individual exercise details
 */
export interface ExerciseDetailResponse {
  id: string;
  type: ExerciseType;
  targetLanguage: Language;
  difficultyLevel: DifficultyLevel;
  topic: string;
  xpReward: number;
  contentType?: 'VOCABULARY' | 'GRAMMAR' | 'SYNONYM';

  // MCQ fields (populated when type === 'MCQ')
  questionText?: string;
  options?: string[];  // Already shuffled by backend

  // Fill-Blank fields (populated when type === 'FILL_BLANK')
  sentenceWithBlank?: string;
}

/**
 * Submission Result Response from Backend
 */
export interface SubmissionResultResponse {
  correct: boolean;
  xpEarned: number;
  correctAnswer: string;
  feedback: string;
}

/**
 * MCQ Submission Request to Backend
 */
export interface McqSubmissionRequest {
  selectedAnswer: string;
}

/**
 * Fill-Blank Submission Request to Backend
 */
export interface FillBlankSubmissionRequest {
  answerText: string;
}

// Legacy interfaces for backward compatibility with existing components
// These map to backend responses but with more specific types

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
 * Maps to backend ExerciseDetailResponse when type === MCQ
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
 * Maps to backend ExerciseDetailResponse when type === FILL_BLANK
 */
export interface ExerciseFillBlank extends BaseExercise {
  type: 'FILL_BLANK';
  sentenceWithBlank: string;
  correctAnswer: string;
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
 * Helper to get all options from backend detail response
 * Backend already shuffles options, so we just return them
 */
export function getMCQOptions(exercise: ExerciseDetailResponse): string[] {
  return exercise.options || [];
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
