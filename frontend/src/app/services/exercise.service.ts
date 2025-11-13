import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, of, delay } from 'rxjs';
import { 
  Exercise, 
  ExerciseMCQ, 
  ExerciseFillBlank, 
  UserProgress, 
  ExerciseSubmission,
  DifficultyLevel,
  Language
} from '../models/exercise.model';
import { 
  getMockExercisesByTopic,
  getMockExercisesByDifficulty,
  mockSubmitAnswer,
  mockMixedExercises
} from './exercise.mock';

@Injectable({
  providedIn: 'root'
})
export class ExerciseService {
  private readonly apiUrl = '/api/exercises';  // Adjust based on your backend
  
  // Toggle für Mock-Modus (auf false setzen wenn Backend bereit ist)
  private readonly useMockData = true;
  
  // Current exercise state
  currentExercise = signal<Exercise | null>(null);
  currentProgress = signal<UserProgress | null>(null);
  
  constructor(private http: HttpClient) {}

  /**
   * Fetch exercises by criteria
   */
  getExercises(
    targetLanguage: Language,
    difficultyLevel: DifficultyLevel,
    topic?: string
  ): Observable<Exercise[]> {
    if (this.useMockData) {
      // Mock-Daten zurückgeben
      let exercises = topic 
        ? getMockExercisesByTopic(topic)
        : getMockExercisesByDifficulty(difficultyLevel);
      
      // Falls keine Exercises für das Topic, gib gemischte zurück
      if (exercises.length === 0) {
        exercises = mockMixedExercises;
      }
      
      return of(exercises).pipe(delay(500)); // Simuliere Netzwerk-Delay
    }
    
    const params: any = {
      targetLanguage,
      difficultyLevel
    };
    
    if (topic) {
      params.topic = topic;
    }
    
    return this.http.get<Exercise[]>(this.apiUrl, { params });
  }

  /**
   * Fetch a single exercise by ID and type
   */
  getExerciseById(id: string, type: 'MCQ' | 'FILL_BLANK'): Observable<Exercise> {
    return this.http.get<Exercise>(`${this.apiUrl}/${type.toLowerCase()}/${id}`)
      .pipe(
        tap(exercise => this.currentExercise.set(exercise))
      );
  }

  /**
   * Submit user's answer for validation
   */
  submitAnswer(submission: ExerciseSubmission): Observable<{
    isCorrect: boolean;
    correctAnswer: string;
    xpEarned: number;
    progress: UserProgress;
  }> {
    if (this.useMockData) {
      // Mock-Response zurückgeben
      const result = mockSubmitAnswer(submission.exerciseId, submission.userAnswer);
      return of(result).pipe(
        delay(300),
        tap(result => {
          if (result.progress) {
            this.currentProgress.set(result.progress);
          }
        })
      );
    }
    
    return this.http.post<any>(`${this.apiUrl}/submit`, submission)
      .pipe(
        tap(result => {
          if (result.progress) {
            this.currentProgress.set(result.progress);
          }
        })
      );
  }

  /**
   * Get user's progress for specific exercise
   */
  getUserProgress(exerciseId: string, exerciseType: 'MCQ' | 'FILL_BLANK'): Observable<UserProgress> {
    return this.http.get<UserProgress>(`${this.apiUrl}/progress/${exerciseType.toLowerCase()}/${exerciseId}`)
      .pipe(
        tap(progress => this.currentProgress.set(progress))
      );
  }

  /**
   * Get all user progress (for a specific language/level)
   */
  getAllUserProgress(
    targetLanguage?: Language,
    difficultyLevel?: DifficultyLevel
  ): Observable<UserProgress[]> {
    const params: any = {};
    
    if (targetLanguage) params.targetLanguage = targetLanguage;
    if (difficultyLevel) params.difficultyLevel = difficultyLevel;
    
    return this.http.get<UserProgress[]>(`${this.apiUrl}/progress`, { params });
  }

  /**
   * Reset current exercise state
   */
  resetCurrentExercise(): void {
    this.currentExercise.set(null);
    this.currentProgress.set(null);
  }

  /**
   * Type guard for MCQ exercises
   */
  isMCQ(exercise: Exercise): exercise is ExerciseMCQ {
    return exercise.type === 'MCQ';
  }

  /**
   * Type guard for Fill Blank exercises
   */
  isFillBlank(exercise: Exercise): exercise is ExerciseFillBlank {
    return exercise.type === 'FILL_BLANK';
  }
}
