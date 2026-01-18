import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, forkJoin, map } from 'rxjs';
import { 
  ExerciseSummaryResponse,
  ExerciseDetailResponse,
  SubmissionResultResponse,
  McqSubmissionRequest,
  FillBlankSubmissionRequest,
  ExerciseType,
  DifficultyLevel,
  Language,
  IncorrectExerciseResponse
} from '../models/exercise.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ExerciseService {
  private readonly apiUrl = `${environment.apiUrl}/api/exercises`;
  
  // Current exercise state
  currentExercise = signal<ExerciseDetailResponse | null>(null);
  
  constructor(private http: HttpClient) {}

  /**
   * Fetch all exercises (both MCQ and Fill-Blank combined)
   * Backend has separate endpoints, so we call both and merge
   */
  getExercises(
    targetLanguage?: Language,
    difficultyLevel?: DifficultyLevel,
    topic?: string
  ): Observable<ExerciseSummaryResponse[]> {
    console.log(`🔍 Fetching exercises with filters:`, { targetLanguage, difficultyLevel, topic });
    
    // Call both endpoints in parallel
    return forkJoin({
      mcq: this.http.get<ExerciseSummaryResponse[]>(`${this.apiUrl}/mcq`),
      fillBlank: this.http.get<ExerciseSummaryResponse[]>(`${this.apiUrl}/fillblank`)
    }).pipe(
      map(result => {
        console.log(`📦 Received from backend - MCQ: ${result.mcq.length}, Fill-Blank: ${result.fillBlank.length}`);
        
        // Combine both arrays
        let exercises = [...result.mcq, ...result.fillBlank];
        
        // Client-side filtering (backend doesn't have filter params yet)
        if (targetLanguage) {
          exercises = exercises.filter(ex => ex.targetLanguage === targetLanguage);
        }
        if (difficultyLevel) {
          exercises = exercises.filter(ex => ex.difficultyLevel === difficultyLevel);
        }
        if (topic) {
          exercises = exercises.filter(ex => ex.topic === topic);
        }
        
        console.log(`After filtering: ${exercises.length} exercises match the criteria`);
        return exercises;
      })
    );
  }

  /**
   * Fetch MCQ exercises only
   */
  getMcqExercises(): Observable<ExerciseSummaryResponse[]> {
    return this.http.get<ExerciseSummaryResponse[]>(`${this.apiUrl}/mcq`);
  }

  /**
   * Fetch Fill-Blank exercises only
   */
  getFillBlankExercises(): Observable<ExerciseSummaryResponse[]> {
    return this.http.get<ExerciseSummaryResponse[]>(`${this.apiUrl}/fillblank`);
  }

  /**
   * Fetch a single exercise by ID and type
   */
  getExerciseById(id: string, type: ExerciseType): Observable<ExerciseDetailResponse> {
    const endpoint = type === 'MCQ' ? 'mcq' : 'fillblank';
    const url = `${this.apiUrl}/${endpoint}/${id}`;
    console.log(`🔍 Fetching exercise detail from: ${url}`);
    return this.http.get<ExerciseDetailResponse>(url)
      .pipe(
        tap(exercise => {
          console.log(`📦 Received exercise detail:`, exercise);
          this.currentExercise.set(exercise);
        })
      );
  }

  /**
   * Submit answer for MCQ exercise
   */
  submitMcqAnswer(exerciseId: string, selectedAnswer: string): Observable<SubmissionResultResponse> {
    const request: McqSubmissionRequest = { selectedAnswer };
    return this.http.post<SubmissionResultResponse>(
      `${this.apiUrl}/mcq/${exerciseId}/submit`,
      request
    );
  }

  /**
   * Submit answer for Fill-Blank exercise
   */
  submitFillBlankAnswer(exerciseId: string, answerText: string): Observable<SubmissionResultResponse> {
    const request: FillBlankSubmissionRequest = { answerText };
    return this.http.post<SubmissionResultResponse>(
      `${this.apiUrl}/fillblank/${exerciseId}/submit`,
      request
    );
  }

  /**
   * Generic submit answer method (determines type automatically)
   */
  submitAnswer(exerciseId: string, exerciseType: ExerciseType, userAnswer: string): Observable<SubmissionResultResponse> {
    if (exerciseType === 'MCQ') {
      return this.submitMcqAnswer(exerciseId, userAnswer);
    } else {
      return this.submitFillBlankAnswer(exerciseId, userAnswer);
    }
  }

  /**
   * Reset current exercise state
   */
  resetCurrentExercise(): void {
    this.currentExercise.set(null);
  }

  /**
   * Type guard for MCQ exercises
   */
  isMCQ(exercise: ExerciseDetailResponse): boolean {
    return exercise.type === 'MCQ';
  }

  /**
   * Type guard for Fill Blank exercises
   */
  isFillBlank(exercise: ExerciseDetailResponse): boolean {
    return exercise.type === 'FILL_BLANK';
  }

  /**
   * Fetch all exercises with incorrect attempts (for retry functionality)
   */
  getIncorrectExercises(): Observable<IncorrectExerciseResponse[]> {
    return this.http.get<IncorrectExerciseResponse[]>(`${this.apiUrl}/incorrect`);
  }
}
