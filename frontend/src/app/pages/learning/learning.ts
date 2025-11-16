import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslocoPipe, TranslocoService } from '@jsverse/transloco';
import { ExerciseViewerComponent, ExerciseResult } from '../../components/exercise-viewer/exercise-viewer';
import { ExerciseSummaryResponse, ExerciseDetailResponse } from '../../models/exercise.model';
import { ExerciseService } from '../../services/exercise.service';
import { UserLearningService } from '../../services/user-learning.service';

interface Lesson {
  id: number;
  titleKey: string;  // Translation key instead of direct title
  sectionKey: string;  // Translation key for section
  status: 'locked' | 'available' | 'completed' | 'current';
  progress?: number;
  stars?: number;
  exerciseKeys?: string[];  // Translation keys for exercises
  descriptionKey?: string;  // Translation key for description
  // Link to actual exercises
  difficultyLevel?: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';
  topic?: string;
}

@Component({
  selector: 'app-learning',
  imports: [CommonModule, TranslocoPipe, ExerciseViewerComponent],
  templateUrl: './learning.html',
  styleUrl: './learning.css'
})
export class Learning implements OnInit {
  private translocoService = inject(TranslocoService);
  private userLearningService = inject(UserLearningService);
  
  protected readonly unitTitle = signal('Bestelle im Cafe');
  
  protected readonly lessons = signal<Lesson[]>([
    { 
      id: 1, 
      titleKey: 'learning.lessons.lesson1.title',
      sectionKey: 'learning.lessons.lesson1.section',
      status: 'available',
      progress: 0, 
      stars: 0,
      descriptionKey: 'learning.lessons.lesson1.description',
      exerciseKeys: [
        'learning.lessons.lesson1.exercises.ex1',
        'learning.lessons.lesson1.exercises.ex2',
        'learning.lessons.lesson1.exercises.ex3'
      ],
      difficultyLevel: 'A1',
      topic: 'basics'
    },
    { 
      id: 2, 
      titleKey: 'learning.lessons.lesson2.title',
      sectionKey: 'learning.lessons.lesson2.section',
      status: 'locked',
      progress: 0, 
      stars: 0,
      descriptionKey: 'learning.lessons.lesson2.description',
      exerciseKeys: [
        'learning.lessons.lesson2.exercises.ex1',
        'learning.lessons.lesson2.exercises.ex2',
        'learning.lessons.lesson2.exercises.ex3'
      ],
      difficultyLevel: 'B1',
      topic: 'intermediate'
    },
    { 
      id: 3, 
      titleKey: 'learning.lessons.lesson3.title',
      sectionKey: 'learning.lessons.lesson3.section',
      status: 'locked', 
      progress: 0, 
      stars: 0,
      descriptionKey: 'learning.lessons.lesson3.description',
      exerciseKeys: [
        'learning.lessons.lesson3.exercises.ex1',
        'learning.lessons.lesson3.exercises.ex2',
        'learning.lessons.lesson3.exercises.ex3'
      ],
      difficultyLevel: 'C1',
      topic: 'advanced'
    },
    
  ]);

  protected readonly dailyGoal = signal(10);
  protected readonly dailyProgress = signal(0);
  protected readonly streak = signal(0);
  protected readonly hearts = signal(4);
  protected readonly gems = signal(500);
  
  protected selectedLesson = signal<Lesson | null>(null);
  
  protected exerciseMode = signal<boolean>(false);
  protected currentExerciseSummaries = signal<ExerciseSummaryResponse[]>([]);
  protected currentExerciseIndex = signal<number>(0);
  protected currentExercise = signal<ExerciseDetailResponse | null>(null);

  // TODO: Replace with actual logged-in user ID from authentication service
  // Current test user ID from database: testuser@test.com
  // IMPORTANT: This ID must match the one printed in backend console on startup
  // Look for: "📊 Test User ID: [uuid]" in the backend logs
  private readonly TEST_USER_ID = 'f1586c14-a0b9-4dd6-917e-0ca7938595fa';

  constructor(private exerciseService: ExerciseService) {}

  /**
   * Initialize component and fetch user learning data from backend.
   * Loads XP and streak information to display in the UI.
   */
  ngOnInit(): void {
    this.loadUserLearningData();
    
    // Subscribe to user learning updates
    this.userLearningService.userLearning$.subscribe(data => {
      if (data) {
        this.dailyProgress.set(data.xp);
        this.streak.set(data.streakCount);
      }
    });
  }

  /**
   * Loads user learning data from the backend.
   * NOTE: Currently uses a hardcoded TEST_USER_ID. 
   * When authentication is implemented, replace with the actual logged-in user's ID.
   * 
   * To get the test user ID:
   * 1. Start the backend
   * 2. Check console logs for "Test User ID: [uuid]"
   * 3. Copy that UUID and replace TEST_USER_ID constant above
   */
  private loadUserLearningData(): void {
    // TODO: Get user ID from authentication service when available
    // For now, the backend creates a test user on startup
    // Check backend console logs for: "Test User ID: [uuid]"
    
    this.userLearningService.getUserLearning(this.TEST_USER_ID).subscribe({
      next: (data) => {
        console.log('✅ Loaded user learning data:', data);
        this.dailyProgress.set(data.xp);
        this.streak.set(data.streakCount);
      },
      error: (error) => {
        console.error('❌ Error loading user learning data:', error);
        console.log('💡 Make sure to:');
        console.log('   1. Start the backend');
        console.log('   2. Check that backend is running on http://localhost:8080');
        // Keep using mock data on error
      }
    });
  }

  selectLesson(lesson: Lesson) {
    this.selectedLesson.set(lesson);
  }

  closePanel() {
    this.selectedLesson.set(null);
  }

  startLesson(lesson: Lesson) {
    if (lesson.status === 'locked') {
      console.log('Diese Lektion ist noch gesperrt');
      return;
    }

    this.closePanel();

    const difficulty = lesson.difficultyLevel || 'A1';
    const topic = lesson.topic || 'basics';

    console.log(`🎯 Starting lesson - Difficulty: ${difficulty}, Topic: ${topic}`);

    this.exerciseService.getExercises('DE', difficulty, topic).subscribe({
      next: (exerciseSummaries) => {
        console.log(`✅ Found ${exerciseSummaries.length} exercises`, exerciseSummaries);
        if (exerciseSummaries.length > 0) {
          this.currentExerciseSummaries.set(exerciseSummaries);
          this.currentExerciseIndex.set(0);
          // Fetch full details for the first exercise
          this.loadExerciseDetail(exerciseSummaries[0]);
        } else {
          console.warn('⚠️ Keine Übungen verfügbar für diese Lektion. Check if exercises exist in backend with:', {
            targetLanguage: 'DE',
            difficultyLevel: difficulty,
            topic: topic
          });
        }
      },
      error: (error) => {
        console.error('❌ Error loading exercises:', error);
      }
    });
  }

  /**
   * Load full exercise details by ID and type
   */
  private loadExerciseDetail(summary: ExerciseSummaryResponse): void {
    console.log(`📖 Loading exercise detail:`, summary);
    this.exerciseService.getExerciseById(summary.id, summary.type).subscribe({
      next: (detail) => {
        console.log(`✅ Exercise detail loaded:`, detail);
        this.currentExercise.set(detail);
        this.exerciseMode.set(true);
      },
      error: (error) => {
        console.error('❌ Error loading exercise detail:', error);
      }
    });
  }

  /**
   * Handles exercise submission results.
   * If the exercise is completed successfully, awards XP to the user.
   * 
   * @param result - The result of the exercise submission
   */
  onExerciseSubmit(result: ExerciseResult) {
    console.log('Exercise submitted:', result);
    
    // Award XP if exercise was completed correctly
    if (result.isCorrect) {
      const xpEarned = result.xpEarned || 10; // Default 10 XP
      this.userLearningService.addXp(this.TEST_USER_ID, xpEarned).subscribe({
        next: (data) => {
          console.log(`✅ Awarded ${xpEarned} XP! Total XP: ${data.xp}`);
          this.dailyProgress.set(data.xp);
        },
        error: (error) => {
          console.error('❌ Error awarding XP:', error);
        }
      });
    }
  }

  onNextExercise() {
    const nextIndex = this.currentExerciseIndex() + 1;
    const summaries = this.currentExerciseSummaries();

    if (nextIndex < summaries.length) {
      this.currentExerciseIndex.set(nextIndex);
      this.loadExerciseDetail(summaries[nextIndex]);
    } else {
      this.completeLesson();
    }
  }

  completeLesson() {
    this.exerciseMode.set(false);
    this.currentExercise.set(null);
    this.currentExerciseSummaries.set([]);
    this.currentExerciseIndex.set(0);
    
    console.log('Lektion abgeschlossen!');
  }

  exitExerciseMode() {
    this.exerciseMode.set(false);
    this.currentExercise.set(null);
  }

  getLessonIcon(lesson: Lesson): string {
    return '';
  }

  getLessonStatusText(lesson: Lesson): string {
    if (lesson.status === 'completed') return 'Abgeschlossen';
    if (lesson.status === 'current') return 'In Bearbeitung';
    if (lesson.status === 'available') return 'Verfügbar';
    return 'Gesperrt';
  }
}
