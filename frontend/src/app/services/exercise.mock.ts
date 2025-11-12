import { Exercise, ExerciseMCQ, ExerciseFillBlank } from '../models/exercise.model';

export const mockMCQExercises: ExerciseMCQ[] = [
  {
    id: 'mcq-example-001',
    type: 'MCQ',
    targetLanguage: 'DE',
    difficultyLevel: 'A1',
    topic: 'example',
    xpReward: 10,
    questionText: 'Beispiel Frage?',
    correctAnswer: 'Richtige Antwort',
    wrongOption1: 'Falsche Option 1',
    wrongOption2: 'Falsche Option 2',
    wrongOption3: 'Falsche Option 3'
  },
];

export const mockFillBlankExercises: ExerciseFillBlank[] = [
  {
    id: 'fill-example-001',
    type: 'FILL_BLANK',
    targetLanguage: 'DE',
    difficultyLevel: 'A1',
    topic: 'example',
    xpReward: 10,
    sentenceWithBlank: 'Der Hund ___ groß.',
    correctAnswer: 'ist'
  },
];

export const mockMixedExercises: Exercise[] = [
  ...mockMCQExercises,
  ...mockFillBlankExercises
];

export const mockExercisesByTopic: Record<string, Exercise[]> = {};

[...mockMCQExercises, ...mockFillBlankExercises].forEach(exercise => {
  if (!mockExercisesByTopic[exercise.topic]) {
    mockExercisesByTopic[exercise.topic] = [];
  }
  mockExercisesByTopic[exercise.topic].push(exercise);
});

export function getMockExercisesByTopic(topic: string): Exercise[] {
  return mockExercisesByTopic[topic] || [];
}

export function getMockExercisesByDifficulty(level: string): Exercise[] {
  const allExercises = [...mockMCQExercises, ...mockFillBlankExercises];
  return allExercises.filter(ex => ex.difficultyLevel === level);
}

export function getRandomMockExercise(): Exercise {
  const allExercises = [...mockMCQExercises, ...mockFillBlankExercises];
  return allExercises[Math.floor(Math.random() * allExercises.length)];
}

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
