import { Component, signal, inject, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslocoPipe, TranslocoService } from '@jsverse/transloco';
import { ExerciseViewerComponent, ExerciseResult } from '../../components/exercise-viewer/exercise-viewer';
import { ExerciseSummaryResponse, ExerciseDetailResponse } from '../../models/exercise.model';
import { ExerciseService } from '../../services/exercise.service';
import { UserLearningService } from '../../services/user-learning.service';

interface Lesson {
  id: number;
  titleKey: string;
  sectionKey: string;
  status: 'available';  // All available for now, until progress tracking is implemented
  progress?: number;
  stars?: number;
  exerciseKeys?: string[];
  descriptionKey?: string;
  difficultyLevel: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';
  topic: string;
  exerciseType: 'MCQ' | 'FILL_BLANK';  // New: separates by exercise type
}

type DifficultyLevel = 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';

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
  
  // Current selected difficulty
  protected selectedDifficulty = signal<DifficultyLevel>('A1');
  
  // All difficulty levels
  protected readonly difficulties: DifficultyLevel[] = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];
  
  // Mapping of difficulty to topic
  private readonly difficultyToTopic: Record<DifficultyLevel, string> = {
    'A1': 'basics',
    'A2': 'basics',
    'B1': 'intermediate',
    'B2': 'intermediate',
    'C1': 'advanced',
    'C2': 'advanced'
  };
  
  // Computed lessons based on selected difficulty
  protected lessons = computed<Lesson[]>(() => {
    const difficulty = this.selectedDifficulty();
    const topic = this.difficultyToTopic[difficulty];
    
    return [
      {
        id: 1,
        titleKey: `learning.lessons.${difficulty.toLowerCase()}.lesson1.title`,
        sectionKey: `learning.lessons.${difficulty.toLowerCase()}.lesson1.section`,
        status: 'available',
        progress: 0,
        stars: 0,
        descriptionKey: `learning.lessons.${difficulty.toLowerCase()}.lesson1.description`,
        exerciseKeys: [
          `learning.lessons.${difficulty.toLowerCase()}.lesson1.exercises.ex1`,
          `learning.lessons.${difficulty.toLowerCase()}.lesson1.exercises.ex2`,
          `learning.lessons.${difficulty.toLowerCase()}.lesson1.exercises.ex3`
        ],
        difficultyLevel: difficulty,
        topic: topic,
        exerciseType: 'MCQ'
      },
      {
        id: 2,
        titleKey: `learning.lessons.${difficulty.toLowerCase()}.lesson2.title`,
        sectionKey: `learning.lessons.${difficulty.toLowerCase()}.lesson2.section`,
        status: 'available',
        progress: 0,
        stars: 0,
        descriptionKey: `learning.lessons.${difficulty.toLowerCase()}.lesson2.description`,
        exerciseKeys: [
          `learning.lessons.${difficulty.toLowerCase()}.lesson2.exercises.ex1`,
          `learning.lessons.${difficulty.toLowerCase()}.lesson2.exercises.ex2`,
          `learning.lessons.${difficulty.toLowerCase()}.lesson2.exercises.ex3`
        ],
        difficultyLevel: difficulty,
        topic: topic,
        exerciseType: 'FILL_BLANK'
      }
    ];
  });

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

  // Use authenticated user; no hardcoded test user ID
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
   */
  private loadUserLearningData(): void {
    this.userLearningService.getUserLearning().subscribe({
      next: (data) => {
        console.log('Loaded user learning data:', data);
        this.dailyProgress.set(data.xp);
        this.streak.set(data.streakCount);
      },
      error: (error) => {
        console.error('Error loading user learning data:', error);
        console.log('Make sure to:');
        console.log('   1. Start the backend');
        console.log('   2. Check that backend is running on http://localhost:8080');
        // Keep using mock data on error
      }
    });
  }

  selectDifficulty(difficulty: DifficultyLevel) {
    this.selectedDifficulty.set(difficulty);
    this.closePanel();
  }

  getDifficultyLabel(difficulty: DifficultyLevel): string {
    return this.translocoService.translate(`learning.difficulty.${difficulty.toLowerCase()}.label`);
  }

  getDifficultyDescription(difficulty: DifficultyLevel): string {
    return this.translocoService.translate(`learning.difficulty.${difficulty.toLowerCase()}.description`);
  }

  selectLesson(lesson: Lesson) {
    this.selectedLesson.set(lesson);
  }

  closePanel() {
    this.selectedLesson.set(null);
  }

  startLesson(lesson: Lesson) {
    this.closePanel();

    const difficulty = lesson.difficultyLevel;
    const topic = lesson.topic;
    const exerciseType = lesson.exerciseType;

    console.log(`Starting lesson - Difficulty: ${difficulty}, Topic: ${topic}, Type: ${exerciseType}`);

    // Fetch exercises and filter by type
    this.exerciseService.getExercises('DE', difficulty, topic).subscribe({
      next: (exerciseSummaries) => {
        // Filter by exercise type (MCQ or FILL_BLANK)
        const filteredExercises = exerciseSummaries.filter(ex => ex.type === exerciseType);
        
        console.log(`Found ${filteredExercises.length} ${exerciseType} exercises`, filteredExercises);
        if (filteredExercises.length > 0) {
          this.currentExerciseSummaries.set(filteredExercises);
          this.currentExerciseIndex.set(0);
          // Fetch full details for the first exercise
          this.loadExerciseDetail(filteredExercises[0]);
        } else {
          console.warn('⚠️ Keine Übungen verfügbar für diese Lektion. Check if exercises exist in backend with:', {
            targetLanguage: 'DE',
            difficultyLevel: difficulty,
            topic: topic,
            exerciseType: exerciseType
          });
        }
      },
      error: (error) => {
        console.error('Error loading exercises:', error);
      }
    });
  }

  /**
   * Load full exercise details by ID and type
   */
  private loadExerciseDetail(summary: ExerciseSummaryResponse): void {
    console.log(`Loading exercise detail:`, summary);
    this.exerciseService.getExerciseById(summary.id, summary.type).subscribe({
      next: (detail) => {
        console.log(`Exercise detail loaded:`, detail);
        this.currentExercise.set(detail);
        this.exerciseMode.set(true);
      },
      error: (error) => {
        console.error('Error loading exercise detail:', error);
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
      this.userLearningService.addXp(xpEarned).subscribe({
        next: (data) => {
          console.log(`Awarded ${xpEarned} XP! Total XP: ${data.xp}`);
          this.dailyProgress.set(data.xp);
        },
        error: (error) => {
          console.error('Error awarding XP:', error);
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
    return 'Verfügbar';
  }
}
