/**
 * Mock Exercise Data für Testing
 * 
 * WICHTIG: Diese Datei wird nur verwendet, wenn in exercise.service.ts
 * der Mock-Modus aktiviert ist (useMockData = true)
 * 
 * EIGENE AUFGABEN HINZUFÜGEN:
 * 1. Kopiere eine der Beispiel-Exercises unten
 * 2. Ändere die Werte (id, questionText, answers, etc.)
 * 3. Speichern - fertig!
 */

import { Exercise, ExerciseMCQ, ExerciseFillBlank } from '../models/exercise.model';

// ==================== MCQ Exercises (Multiple Choice) ====================
// Format: 1 richtige Antwort + 3 falsche Optionen

export const mockMCQExercises: ExerciseMCQ[] = [
  // ✨ BEISPIEL - Kopiere dieses Format für deine eigenen Aufgaben:
  {
    id: 'mcq-example-001',           // Eindeutige ID (z.B. mcq-grammar-001)
    type: 'MCQ',                      // Immer 'MCQ'
    targetLanguage: 'DE',             // DE, EN, FR, ES
    difficultyLevel: 'A1',            // A1, A2, B1, B2, C1, C2
    topic: 'example',                 // z.B. 'grammar', 'vocabulary', 'verbs'
    xpReward: 10,                     // XP-Punkte (meist 10-15)
    questionText: 'Deine Frage hier?',
    correctAnswer: 'Richtige Antwort',
    wrongOption1: 'Falsche Option 1',
    wrongOption2: 'Falsche Option 2',
    wrongOption3: 'Falsche Option 3'
  },

  // 👇 HIER EIGENE MCQ AUFGABEN EINFÜGEN:
  // { id: 'mcq-001', type: 'MCQ', ... },
  // { id: 'mcq-002', type: 'MCQ', ... },
];

// ==================== Fill Blank Exercises (Lückentext) ====================
// Format: Satz mit ___ (drei Unterstriche) als Platzhalter

export const mockFillBlankExercises: ExerciseFillBlank[] = [
  // ✨ BEISPIEL - Kopiere dieses Format für deine eigenen Aufgaben:
  {
    id: 'fill-example-001',           // Eindeutige ID
    type: 'FILL_BLANK',               // Immer 'FILL_BLANK'
    targetLanguage: 'DE',             // DE, EN, FR, ES
    difficultyLevel: 'A1',            // A1, A2, B1, B2, C1, C2
    topic: 'example',                 // z.B. 'grammar', 'verbs', 'articles'
    xpReward: 10,                     // XP-Punkte (meist 10-15)
    sentenceWithBlank: 'Der Hund ___ groß.',  // Verwende ___ für die Lücke!
    correctAnswer: 'ist'              // Die richtige Antwort für die Lücke
  },

  // 👇 HIER EIGENE FILL BLANK AUFGABEN EINFÜGEN:
  // { id: 'fill-001', type: 'FILL_BLANK', ... },
  // { id: 'fill-002', type: 'FILL_BLANK', ... },
];

// ==================== Kombinierte Exercise Liste ====================

export const mockMixedExercises: Exercise[] = [
  ...mockMCQExercises,
  ...mockFillBlankExercises
];

// ==================== Nach Topic gruppiert ====================
// Wird automatisch aus den obigen Arrays generiert

export const mockExercisesByTopic: Record<string, Exercise[]> = {};

// Gruppiere alle Exercises nach Topic
[...mockMCQExercises, ...mockFillBlankExercises].forEach(exercise => {
  if (!mockExercisesByTopic[exercise.topic]) {
    mockExercisesByTopic[exercise.topic] = [];
  }
  mockExercisesByTopic[exercise.topic].push(exercise);
});

// ==================== Helper Functions ====================

/**
 * Hole Exercises nach Topic
 */
export function getMockExercisesByTopic(topic: string): Exercise[] {
  return mockExercisesByTopic[topic] || [];
}

/**
 * Hole Exercises nach Difficulty
 */
export function getMockExercisesByDifficulty(level: string): Exercise[] {
  const allExercises = [...mockMCQExercises, ...mockFillBlankExercises];
  return allExercises.filter(ex => ex.difficultyLevel === level);
}

/**
 * Hole zufällige Exercise
 */
export function getRandomMockExercise(): Exercise {
  const allExercises = [...mockMCQExercises, ...mockFillBlankExercises];
  return allExercises[Math.floor(Math.random() * allExercises.length)];
}

/**
 * Simuliere Backend-Response für submitAnswer
 */
export function mockSubmitAnswer(exerciseId: string, userAnswer: string): {
  isCorrect: boolean;
  correctAnswer: string;
  xpEarned: number;
  progress: any;
} {
  const allExercises = [...mockMCQExercises, ...mockFillBlankExercises];
  const exercise = allExercises.find(ex => ex.id === exerciseId);
  
  if (!exercise) {
    throw new Error('Exercise not found');
  }
  
  const correctAnswer = exercise.type === 'MCQ' 
    ? (exercise as ExerciseMCQ).correctAnswer
    : (exercise as ExerciseFillBlank).correctAnswer;
  
  const isCorrect = userAnswer.trim().toLowerCase() === correctAnswer.trim().toLowerCase();
  
  return {
    isCorrect,
    correctAnswer,
    xpEarned: isCorrect ? exercise.xpReward : 0,
    progress: {
      id: `progress-${exerciseId}`,
      userId: 'mock-user-id',
      exerciseId: exercise.id,
      exerciseType: exercise.type,
      isCompleted: isCorrect,
      completedAt: isCorrect ? new Date() : undefined,
      xpEarned: isCorrect ? exercise.xpReward : 0,
      createdAt: new Date()
    }
  };
}
