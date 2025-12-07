import { Component, signal, inject, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslocoPipe, TranslocoService, TranslocoDirective } from '@jsverse/transloco';
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
  descriptionKey?: string;
  difficultyLevel: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';
  topic: string;
  exerciseType: 'MCQ' | 'FILL_BLANK';  // New: separates by exercise type
  exercisePreviews?: ExerciseSummaryResponse[];  // Dynamically loaded exercise summaries
}

type DifficultyLevel = 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';

@Component({
  selector: 'app-learning',
  imports: [CommonModule, TranslocoPipe, TranslocoDirective, ExerciseViewerComponent],
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

  // Computed lessons based on selected difficulty
  protected lessons = computed<Lesson[]>(() => {
    const difficulty = this.selectedDifficulty();

    return [
      {
        id: 1,
        titleKey: `learning.lessons.${difficulty.toLowerCase()}.lesson1.title`,
        sectionKey: `learning.lessons.${difficulty.toLowerCase()}.lesson1.section`,
        status: 'available',
        progress: 0,
        stars: 0,
        descriptionKey: `learning.lessons.${difficulty.toLowerCase()}.lesson1.description`,
        difficultyLevel: difficulty,
        topic: '', // Topic filtering removed - exercises filtered by difficulty only
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
        difficultyLevel: difficulty,
        topic: '', // Topic filtering removed - exercises filtered by difficulty only
        exerciseType: 'FILL_BLANK'
      }
    ];
  });

  protected readonly dailyGoal = signal(10);
  protected readonly dailyProgress = signal(0);
  protected readonly streak = signal(0);
  protected readonly hearts = signal(4);
  protected readonly gems = signal(500);
  
  // Streak celebration modal
  protected showStreakCelebration = signal(false);
  protected newStreakValue = signal(0);
  protected previousStreakForDisplay = signal(0);
  protected isStreakShrinking = signal(false);
  protected isStreakFlying = signal(false);
  private previousStreakValue = 0;
  
  // Track correct answers in current lesson
  private correctAnswersCount = 0;
  
  protected selectedLesson = signal<Lesson | null>(null);

  protected exerciseMode = signal<boolean>(false);
  protected currentExerciseSummaries = signal<ExerciseSummaryResponse[]>([]);
  protected currentExerciseIndex = signal<number>(0);
  protected currentExercise = signal<ExerciseDetailResponse | null>(null);

  // Store user's selected learning language (default to 'DE' if not set)
  protected userLanguage = signal<'DE' | 'EN'>('DE');

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
        // Update user's selected language (only DE and EN supported for exercises)
        if (data.learningLanguage === 'DE' || data.learningLanguage === 'EN') {
          this.userLanguage.set(data.learningLanguage);
        } else {
          // Default to DE if user selected unsupported language
          this.userLanguage.set('DE');
        }
        this.previousStreakValue = data.streakCount; // Remember for comparison
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
    // Load exercise previews dynamically when lesson is selected
    this.loadExercisePreviews(lesson);
  }

  /**
   * Load exercise summaries for the selected lesson to display in the side panel.
   * This replaces hardcoded exercise names with actual exercises from the backend.
   */
  private loadExercisePreviews(lesson: Lesson): void {
    this.exerciseService.getExercises(this.userLanguage(), lesson.difficultyLevel, undefined).subscribe({
      next: (exerciseSummaries) => {
        // Filter by exercise type (MCQ or FILL_BLANK)
        const filteredExercises = exerciseSummaries.filter(ex => ex.type === lesson.exerciseType);

        console.log(`Loaded ${filteredExercises.length} exercise previews for ${lesson.exerciseType}`, filteredExercises);

        // Update the selected lesson with the exercise previews
        const updatedLesson = { ...lesson, exercisePreviews: filteredExercises };
        this.selectedLesson.set(updatedLesson);
      },
      error: (error) => {
        console.error('Error loading exercise previews:', error);
      }
    });
  }

  closePanel() {
    this.selectedLesson.set(null);
  }

  startLesson(lesson: Lesson) {
    this.closePanel();
    this.correctAnswersCount = 0; // Reset correct answers counter

    const difficulty = lesson.difficultyLevel;
    const exerciseType = lesson.exerciseType;

    console.log(`Starting lesson - Difficulty: ${difficulty}, Type: ${exerciseType}`);

    // Fetch exercises filtered by difficulty only (not topic)
    this.exerciseService.getExercises(this.userLanguage(), difficulty, undefined).subscribe({
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
            targetLanguage: this.userLanguage(),
            difficultyLevel: difficulty,
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
   * The backend now automatically awards XP when exercises are completed.
   * We track correct answers to determine if streak should be updated.
   * 
   * @param result - The result of the exercise submission
   */
  onExerciseSubmit(result: ExerciseResult) {
    console.log('Exercise submitted:', result);
    
    // Track correct answers
    if (result.isCorrect) {
      this.correctAnswersCount++;
      this.loadUserLearningData();
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
    console.log(`Correct answers: ${this.correctAnswersCount}`);
    
    // Only update streak if at least one question was answered correctly
    if (this.correctAnswersCount > 0) {
      this.userLearningService.updateStreak().subscribe({
        next: (data) => {
          console.log('Streak updated after lesson completion:', data.streakCount);
          
          // Check if streak increased (first lesson of the day)
          if (data.streakCount > this.previousStreakValue) {
            this.previousStreakForDisplay.set(this.previousStreakValue);
            this.newStreakValue.set(data.streakCount);
            
            // Small delay to ensure translations are loaded
            setTimeout(() => {
              this.showStreakCelebration.set(true);
              this.startStreakAnimation();
            }, 100);
          }
          
          this.streak.set(data.streakCount);
          this.previousStreakValue = data.streakCount;
        },
        error: (error) => {
          console.error('Error updating streak:', error);
        }
      });
    } else {
      console.log('No correct answers - streak not updated');
    }
    
    // Reset counter for next lesson
    this.correctAnswersCount = 0;
  }

  /**
   * Starts the streak celebration animation sequence:
   * 1. Show modal with pop-up animation (2.5s display)
   * 2. Slide out elegantly to the right (0.7s)
   * 3. Hide modal
   */
  private startStreakAnimation() {
    // Reset states
    this.isStreakShrinking.set(false);
    this.isStreakFlying.set(false);
    
    // Step 1: Show modal for 2.5 seconds
    setTimeout(() => {
      this.isStreakFlying.set(true);
    }, 2500);
    
    // Step 2: Hide modal after slide-out completes (2500ms + 700ms slide + 100ms buffer)
    setTimeout(() => {
      this.closeStreakCelebration();
    }, 3300);
  }
  
  closeStreakCelebration() {
    this.showStreakCelebration.set(false);
    this.isStreakShrinking.set(false);
    this.isStreakFlying.set(false);
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
