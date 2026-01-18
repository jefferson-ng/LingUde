import { Component, signal, inject, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslocoPipe, TranslocoService, TranslocoDirective } from '@jsverse/transloco';
import { ExerciseViewerComponent, ExerciseResult } from '../../components/exercise-viewer/exercise-viewer';
import { ExerciseSummaryResponse, ExerciseDetailResponse } from '../../models/exercise.model';
import { ExerciseService } from '../../services/exercise.service';
import { UserLearningService } from '../../services/user-learning.service';

interface Level {
  id: number;
  levelNumber: number;
  difficultyLevel: DifficultyLevel;
  status: 'locked' | 'unlocked' | 'completed';
  stars: number;
  totalExercises: number;
  completedExercises: number;
}

type DifficultyLevel = 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';
type LanguageCode = 'DE' | 'EN';

@Component({
  selector: 'app-learning',
  imports: [CommonModule, TranslocoPipe, TranslocoDirective, ExerciseViewerComponent],
  templateUrl: './learning.html',
  styleUrl: './learning.css'
})
export class Learning implements OnInit {
  private translocoService = inject(TranslocoService);
  private userLearningService = inject(UserLearningService);
  
  protected userLanguage = signal<LanguageCode>('DE');
  protected userTargetLevel = signal<DifficultyLevel>('A1');
  protected userCurrentLevel = signal<DifficultyLevel>('A1');
  
  protected levels = signal<Level[]>([]);
  protected currentUnlockedLevel = signal<number>(1);
  protected highestUnlockedLevel = signal<number>(1);
  
  protected readonly dailyGoal = signal(10);
  protected readonly dailyProgress = signal(0);
  protected readonly streak = signal(0);
  
  protected showStreakCelebration = signal(false);
  protected newStreakValue = signal(0);
  protected previousStreakForDisplay = signal(0);
  protected isStreakShrinking = signal(false);
  protected isStreakFlying = signal(false);
  private previousStreakValue = 0;
  private streakAnimationShownToday = false;
  private lastActivityDate: string | null = null;
  
  private correctAnswersCount = 0;
  private totalExercisesInLevel = 0;
  private skipLoadLevelsCount = 0; // Counter to skip N loadLevels() calls (for multiple concurrent updates)
  
  protected selectedLevel = signal<Level | null>(null);
  protected showLevelPopup = signal<boolean>(false);
  protected popupPosition = signal<{ top: number; left: number } | null>(null);
  protected levelXP = signal<number>(10);
  
  protected showLockedNotification = signal<boolean>(false);
  private notificationTimeout: any = null;
  
  protected showPracticeWidgetNotification = signal<boolean>(false);
  private practiceNotificationTimeout: any = null;

  // Helper to check if a level is the first in its difficulty group (for showing headers)
  protected isFirstInDifficultyGroup(index: number): boolean {
    if (index === 0) return true;
    const levels = this.levels();
    return levels[index].difficultyLevel !== levels[index - 1].difficultyLevel;
  }

  protected isLanguageDropdownOpen = signal<boolean>(false);
  private languageDropdownTimeout: any = null;
  protected availableLanguages = [
    { code: 'DE' as const, name: 'German', flagCode: 'de' },
    { code: 'EN' as const, name: 'English', flagCode: 'gb' }
  ];

  protected exerciseMode = signal<boolean>(false);
  protected currentExerciseSummaries = signal<ExerciseSummaryResponse[]>([]);
  protected currentExerciseIndex = signal<number>(0);
  protected currentExercise = signal<ExerciseDetailResponse | null>(null);
  
  // Practice Widget: Shows persistently saved incorrect exercises from backend
  protected practiceWidgetCount = signal<number>(0);
  private persistedIncorrectExercises: ExerciseSummaryResponse[] = [];
  protected isPracticeMode = false; // Track if we're in practice mode from widget

  constructor(private exerciseService: ExerciseService) {}

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (this.showLevelPopup()) {
      const target = event.target as HTMLElement;
      const popup = target.closest('.level-popup');
      const levelNode = target.closest('.level-node');
      
      // Close popup if clicking outside of both the popup and level nodes
      if (!popup && !levelNode) {
        this.closePanel();
      }
    }
  }

  ngOnInit(): void {
    this.loadUserLearningData();
    
    this.userLearningService.userLearning$.subscribe(data => {
      if (data) {
        console.log('📡 Learning page received userLearning$ update - Streak:', data.streakCount, 'XP:', data.xp);
        this.dailyProgress.set(data.xp);
        this.streak.set(data.streakCount);
        console.log('✅ Updated local streak signal to:', data.streakCount);

        if (data.learningLanguage === 'DE' || data.learningLanguage === 'EN') {
          this.userLanguage.set(data.learningLanguage);
        }
        if (data.targetLevel) {
          this.userTargetLevel.set(data.targetLevel);
        }
        if (data.currentLevel) {
          this.userCurrentLevel.set(data.currentLevel);
        }

        // Skip loadLevels if we just completed a level and are updating streak/progress
        // This prevents overwriting our local level status changes
        // BUT we still update streak/XP values above, just don't reload levels
        if (this.skipLoadLevelsCount > 0) {
          console.log(`⏭️ Skipping loadLevels() - ${this.skipLoadLevelsCount} skip(s) remaining`);
          console.log('✅ Streak and XP values were still updated above');
          this.skipLoadLevelsCount--;
        } else {
          this.loadLevels();
        }
      }
    });
  }

  private loadUserLearningData(): void {
    this.userLearningService.getUserLearning().subscribe({
      next: (data) => {
        console.log('Loaded user learning data:', data);
        this.dailyProgress.set(data.xp);
        this.streak.set(data.streakCount);
        
        if (data.learningLanguage === 'DE' || data.learningLanguage === 'EN') {
          this.userLanguage.set(data.learningLanguage);
        }
        
        if (data.targetLevel) {
          this.userTargetLevel.set(data.targetLevel);
        }
        if (data.currentLevel) {
          this.userCurrentLevel.set(data.currentLevel);
        }
        
        // Don't set highestUnlockedLevel from currentLevel anymore
        // It will be calculated from completedLevels for the specific targetLevel in loadLevels()
        this.highestUnlockedLevel.set(1); // Default: Level 1 is always unlocked
        
        this.previousStreakValue = data.streakCount;
        this.lastActivityDate = data.lastActivityDate || null;
        
        // Reset animation flag if it's a new day
        const today = new Date().toISOString().split('T')[0];
        if (this.lastActivityDate !== today) {
          this.streakAnimationShownToday = false;
        }
        
        this.loadLevels();
        this.loadPracticeWidgetCount();
      },
      error: (error) => {
        console.error('Error loading user learning data:', error);
      }
    });
  }

  /**
   * Load count of persisted incorrect exercises from backend for Practice Widget
   */
  private loadPracticeWidgetCount(): void {
    this.exerciseService.getIncorrectExercises().subscribe({
      next: (incorrectExercises) => {
        this.practiceWidgetCount.set(incorrectExercises.length);
        console.log('📊 Practice Widget: Loaded', incorrectExercises.length, 'persisted incorrect exercises');
      },
      error: (error) => {
        console.error('❌ Failed to load practice widget count:', error);
        this.practiceWidgetCount.set(0);
      }
    });
  }

  /**
   * Start practice mode with all persisted incorrect exercises from backend
   */
  protected startPracticeMode(): void {
    console.log('🔄 Starting practice mode from widget');
    
    // If no exercises available, show notification instead
    if (this.practiceWidgetCount() === 0) {
      this.showPracticeNotification();
      return;
    }
    
    this.exerciseService.getIncorrectExercises().subscribe({
      next: (incorrectExercises) => {
        if (incorrectExercises.length === 0) {
          console.log('No incorrect exercises to practice');
          return;
        }

        // Fetch all exercise summaries and filter to only include incorrect ones
        this.exerciseService.getExercises(this.userLanguage()).subscribe({
          next: (allExercises) => {
            const incorrectExerciseIds = new Set(incorrectExercises.map(e => e.exerciseId));
            const exercisesToPractice = allExercises.filter(ex => incorrectExerciseIds.has(ex.id));
            
            console.log('📝 Starting practice with', exercisesToPractice.length, 'exercises');
            this.currentExerciseSummaries.set(exercisesToPractice);
            this.currentExerciseIndex.set(0);
            this.exerciseMode.set(true);
            this.isPracticeMode = true; // Enable practice mode
            
            // Load first exercise
            if (exercisesToPractice.length > 0) {
              const first = exercisesToPractice[0];
              this.exerciseService.getExerciseById(first.id, first.type).subscribe({
                next: (exercise) => {
                  this.currentExercise.set(exercise);
                },
                error: (error) => {
                  console.error('❌ Failed to load exercise:', error);
                }
              });
            }
          },
          error: (error) => {
            console.error('❌ Failed to load exercises:', error);
          }
        });
      },
      error: (error) => {
        console.error('❌ Failed to load incorrect exercises:', error);
      }
    });
  }

  /**
   * Show notification when trying to practice with no exercises
   */
  private showPracticeNotification(): void {
    // Clear existing timeout
    if (this.practiceNotificationTimeout) {
      clearTimeout(this.practiceNotificationTimeout);
    }

    // Show notification
    this.showPracticeWidgetNotification.set(true);

    // Auto-hide after 3 seconds
    this.practiceNotificationTimeout = setTimeout(() => {
      this.showPracticeWidgetNotification.set(false);
    }, 3000);
  }

  // Helper to get all difficulty levels in range (e.g., A2 to B2 = ['A2', 'B1', 'B2'])
  private getDifficultyRange(from: DifficultyLevel, to: DifficultyLevel): DifficultyLevel[] {
    const allLevels: DifficultyLevel[] = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];
    const fromIndex = allLevels.indexOf(from);
    const toIndex = allLevels.indexOf(to);

    if (fromIndex === -1 || toIndex === -1 || fromIndex > toIndex) {
      return [from]; // Fallback to just the current level
    }

    return allLevels.slice(fromIndex, toIndex + 1);
  }

  private loadLevels(): void {
    const currentLevel = this.userCurrentLevel();
    const targetLevel = this.userTargetLevel();
    const targetLanguage = this.userLanguage();

    const levelsPerDifficulty = 1;
    const existingLevels = this.levels();
    const newLevels: Level[] = [];

    // Get all difficulty levels in range (e.g., A2 to B2 = ['A2', 'B1', 'B2'])
    const difficultyRange = this.getDifficultyRange(currentLevel, targetLevel);
    console.log(`📊 Loading levels for difficulty range: ${difficultyRange.join(' → ')}`);

    // Parse completed levels from backend for THIS language (all difficulties)
    // Format: "DE-A1:1,2;EN-A1:1,3;DE-B1:1"
    const completedLevelsMap = new Map<string, Set<number>>(); // e.g., "A2" -> Set(1, 2)
    const currentData = this.userLearningService.getCurrentData();
    if (currentData?.completedLevels) {
      console.log('📥 Loading completed levels from backend:', currentData.completedLevels);

      const difficultyGroups = currentData.completedLevels.split(';');
      for (const group of difficultyGroups) {
        if (group.trim()) {
          const [langDifficulty, levelsStr] = group.split(':');
          if (langDifficulty && levelsStr) {
            const [lang, difficulty] = langDifficulty.split('-');
            if (lang === targetLanguage && difficulty) {
              const ids = levelsStr.split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id));
              completedLevelsMap.set(difficulty, new Set(ids));
              console.log(`🎯 Found completed levels for ${langDifficulty}: [${ids.join(', ')}]`);
            }
          }
        }
      }
    }

    // Build levels for each difficulty in range
    let globalLevelId = 1;
    let foundFirstIncomplete = false;

    for (const difficulty of difficultyRange) {
      const completedInDifficulty = completedLevelsMap.get(difficulty) || new Set<number>();

      for (let levelNum = 1; levelNum <= levelsPerDifficulty; levelNum++) {
        const existingLevel = existingLevels.find(l => l.id === globalLevelId);

        // Determine status
        let status: 'locked' | 'unlocked' | 'completed';

        if (completedInDifficulty.has(levelNum)) {
          status = 'completed';
          console.log(`✅ ${difficulty}-Level ${levelNum} (ID: ${globalLevelId}) is COMPLETED`);
        } else if (!foundFirstIncomplete) {
          // First incomplete level is unlocked (active)
          status = 'unlocked';
          foundFirstIncomplete = true;
          console.log(`🔓 ${difficulty}-Level ${levelNum} (ID: ${globalLevelId}) is UNLOCKED (current)`);
        } else {
          status = 'locked';
          console.log(`🔒 ${difficulty}-Level ${levelNum} (ID: ${globalLevelId}) is LOCKED`);
        }

        newLevels.push({
          id: globalLevelId,
          levelNumber: levelNum,
          difficultyLevel: difficulty,
          status: status,
          stars: existingLevel?.stars || 0,
          totalExercises: 2,
          completedExercises: existingLevel?.completedExercises || 0
        });

        globalLevelId++;
      }
    }

    this.levels.set(newLevels);
    console.log(`Created ${newLevels.length} levels for ${targetLanguage} (${currentLevel} → ${targetLevel})`);
  }

  selectLevel(level: Level, event?: MouseEvent): void {
    if (level.status === 'locked') {
      this.showLockedLevelNotification();
      return;
    }
    
    // Toggle: If clicking on the same level that's already selected, close the popup
    const currentSelectedLevel = this.selectedLevel();
    if (currentSelectedLevel && currentSelectedLevel.id === level.id && this.showLevelPopup()) {
      this.closePanel();
      return;
    }
    
    // Show popup next to the clicked level
    this.selectedLevel.set(level);
    
    // Calculate position if event is available
    if (event) {
      const target = event.currentTarget as HTMLElement;
      const rect = target.getBoundingClientRect();
      this.popupPosition.set({
        top: rect.top + window.scrollY + (rect.height / 2),
        left: rect.right + 25 // 25px spacing from the level button
      });
    }
    
    // Load exercises to calculate XP
    this.loadLevelXP(level);
  }
  
  private showLockedLevelNotification(): void {
    if (this.notificationTimeout) {
      clearTimeout(this.notificationTimeout);
    }
    
    this.showLockedNotification.set(true);
    
    this.notificationTimeout = setTimeout(() => {
      this.showLockedNotification.set(false);
    }, 3000);
  }

  closePanel(): void {
    this.selectedLevel.set(null);
    this.showLevelPopup.set(false);
    this.popupPosition.set(null);
    this.levelXP.set(10); // Reset to default
  }
  
  private loadLevelXP(level: Level): void {
    const targetLanguage = this.userLanguage();
    const difficultyLevel = level.difficultyLevel;

    this.exerciseService.getExercises(targetLanguage, difficultyLevel, undefined).subscribe({
      next: (allExercises) => {
        const mcqExercises = allExercises.filter(ex => ex.type === 'MCQ');
        const fillBlankExercises = allExercises.filter(ex => ex.type === 'FILL_BLANK');
        
        const mixedExercises: ExerciseSummaryResponse[] = [];
        const levelIndex = level.levelNumber - 1;
        
        if (levelIndex < mcqExercises.length) {
          mixedExercises.push(mcqExercises[levelIndex]);
        }
        if (levelIndex < fillBlankExercises.length) {
          mixedExercises.push(fillBlankExercises[levelIndex]);
        }
        
        // Calculate total XP from exercises
        const totalXP = mixedExercises.reduce((sum, ex) => sum + ex.xpReward, 0);
        this.levelXP.set(totalXP);
        this.showLevelPopup.set(true);
      },
      error: (error) => {
        console.error('Error loading exercises for XP calculation:', error);
        this.levelXP.set(10); // Fallback value
        this.showLevelPopup.set(true);
      }
    });
  }
  
  startLevelFromPopup(): void {
    const level = this.selectedLevel();
    if (level) {
      this.showLevelPopup.set(false);
      this.startLevel(level);
    }
  }

  startLevel(level: Level): void {
    // Keep the selected level so we can mark it as completed later
    this.selectedLevel.set(level);
    this.correctAnswersCount = 0;
    this.isPracticeMode = false; // Disable practice mode for normal levels

    const targetLanguage = this.userLanguage();
    const difficultyLevel = level.difficultyLevel;

    console.log(`🎮 Starting level ${level.levelNumber} (ID: ${level.id}) - Language: ${targetLanguage}, Difficulty: ${difficultyLevel}`);

    this.exerciseService.getExercises(targetLanguage, difficultyLevel, undefined).subscribe({
      next: (allExercises) => {
        const mcqExercises = allExercises.filter(ex => ex.type === 'MCQ');
        const fillBlankExercises = allExercises.filter(ex => ex.type === 'FILL_BLANK');
        
        console.log(`Found ${mcqExercises.length} MCQ and ${fillBlankExercises.length} Fill-Blank exercises`);
        
        const mixedExercises: ExerciseSummaryResponse[] = [];
        
        // Take only 1 MCQ and 1 Fill-Blank per level
        const levelIndex = level.levelNumber - 1;
        if (levelIndex < mcqExercises.length) {
          mixedExercises.push(mcqExercises[levelIndex]);
        }
        if (levelIndex < fillBlankExercises.length) {
          mixedExercises.push(fillBlankExercises[levelIndex]);
        }
        
        console.log(`Mixed ${mixedExercises.length} exercises for level`, mixedExercises);
        
        if (mixedExercises.length > 0) {
          this.currentExerciseSummaries.set(mixedExercises);
          this.currentExerciseIndex.set(0);
          this.totalExercisesInLevel = mixedExercises.length;
          this.loadExerciseDetail(mixedExercises[0]);
        } else {
          console.warn('No exercises available for this level');
        }
      },
      error: (error) => {
        console.error('Error loading exercises:', error);
      }
    });
  }

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

  onExerciseSubmit(result: ExerciseResult): void {
    console.log('Exercise submitted:', result);
    
    if (result.isCorrect) {
      this.correctAnswersCount++;
      console.log(`✅ Correct answer! Total: ${this.correctAnswersCount}/${this.totalExercisesInLevel}`);
    } else {
      console.log(`❌ Incorrect answer. Total: ${this.correctAnswersCount}/${this.totalExercisesInLevel}`);
      
      // Update practice widget count immediately when answer is incorrect
      // This ensures repeated exercises also update the widget
      this.loadPracticeWidgetCount();
    }
    
    // Don't reload here - the exercise submission already updates XP via backend response
    // Don't save progress here - only save when level is completed with 100% correct answers
  }
  
  private saveProgressToBackendSync(_unlockedLevelNumber?: number): void {
    // Build completedLevels string with language-difficulty prefix
    // Format: "DE-A1:1,2;EN-A1:1,3;DE-B1:1"
    const currentTargetLanguage = this.userLanguage();

    // Group completed levels by their difficulty level
    const completedByDifficulty = new Map<string, number[]>();
    for (const level of this.levels()) {
      if (level.status === 'completed') {
        const key = `${currentTargetLanguage}-${level.difficultyLevel}`;
        if (!completedByDifficulty.has(key)) {
          completedByDifficulty.set(key, []);
        }
        completedByDifficulty.get(key)!.push(level.levelNumber);
      }
    }

    // Get existing completedLevels from backend and merge
    const currentData = this.userLearningService.getCurrentData();
    let completedLevelsMap = new Map<string, string>();

    // Parse existing completed levels (preserve other languages)
    if (currentData?.completedLevels) {
      const difficultyGroups = currentData.completedLevels.split(';');
      for (const group of difficultyGroups) {
        if (group.trim()) {
          const [langDifficulty, levelsStr] = group.split(':');
          if (langDifficulty && levelsStr) {
            // Only keep entries for OTHER languages
            const [lang] = langDifficulty.split('-');
            if (lang !== currentTargetLanguage) {
              completedLevelsMap.set(langDifficulty.trim(), levelsStr.trim());
            }
          }
        }
      }
    }

    // Add current language's completed levels
    for (const [key, levelNumbers] of completedByDifficulty) {
      completedLevelsMap.set(key, levelNumbers.join(','));
    }

    // Build final string: "DE-A1:1,2;DE-B1:1,3;EN-A1:1"
    const completedLevelsStr = Array.from(completedLevelsMap.entries())
      .map(([langDifficulty, levels]) => `${langDifficulty}:${levels}`)
      .join(';');

    console.log('💾 Saving to backend - completedLevels:', completedLevelsStr);

    this.userLearningService.updateLearningConfig({
      completedLevels: completedLevelsStr
    }).subscribe({
      next: (data: any) => {
        console.log('✅ Progress saved to backend:', data);
        console.log('✅ Confirmed completedLevels in response:', data.completedLevels);
      },
      error: (error: any) => {
        console.error('❌ Error saving progress:', error);
      }
    });
  }

  onNextExercise(): void {
    const nextIndex = this.currentExerciseIndex() + 1;
    const summaries = this.currentExerciseSummaries();

    if (nextIndex < summaries.length) {
      // More exercises in current list
      this.currentExerciseIndex.set(nextIndex);
      this.loadExerciseDetail(summaries[nextIndex]);
    } else {
      // All done - complete level (incorrect answers are tracked in Practice Widget)
      this.completeLevel();
    }
  }

  completeLevel(): void {
    console.log('Level completed!');
    console.log(`Correct answers: ${this.correctAnswersCount} / ${this.totalExercisesInLevel}`);

    const percentage = (this.correctAnswersCount / this.totalExercisesInLevel) * 100;
    let stars = 0;
    if (percentage >= 90) stars = 3;
    else if (percentage >= 70) stars = 2;
    else if (percentage >= 50) stars = 1;

    const selectedLevelValue = this.selectedLevel();
    const levelWasCompleted = percentage === 100;

    if (selectedLevelValue) {
      // Track if we need to unlock the next level
      let shouldUnlockNext = false;

      // Create new array with updated level objects (immutable update)
      const updatedLevels = this.levels().map((level, index) => {
        if (level.id === selectedLevelValue.id) {
          // Only mark as completed (green) if ALL questions were correct (100%)
          if (levelWasCompleted) {
            console.log(`✅ Level ${index + 1} marked as COMPLETED (green) with ${stars} stars (100%)`);
            shouldUnlockNext = true; // Flag to unlock next level
            return {
              ...level,
              status: 'completed' as const,
              stars: stars,
              completedExercises: this.correctAnswersCount
            };
          } else {
            // Not 100% - level stays unlocked but NOT completed (not green)
            console.log(`❌ Level ${index + 1} NOT completed - only ${percentage.toFixed(0)}% correct (need 100%)`);
            return {
              ...level,
              stars: stars,
              completedExercises: this.correctAnswersCount
            };
          }
        } else if (shouldUnlockNext && level.id === selectedLevelValue.id + 1 && level.status === 'locked') {
          // Unlock next level if current level was completed at 100%
          console.log(`✅ Level ${level.levelNumber} (ID: ${level.id}) unlocked!`);
          const newUnlockedLevel = level.levelNumber;
          this.currentUnlockedLevel.set(newUnlockedLevel);
          this.highestUnlockedLevel.set(newUnlockedLevel);

          return {
            ...level,
            status: 'unlocked' as const
          };
        }
        return level;
      });

      // Update the levels signal to trigger UI update
      this.levels.set(updatedLevels);
      console.log('🔄 Levels signal updated with new array');
    }

    // Close exercise mode and return to level path
    this.exerciseMode.set(false);
    this.currentExercise.set(null);
    this.currentExerciseSummaries.set([]);
    this.currentExerciseIndex.set(0);
    this.selectedLevel.set(null);
    this.isPracticeMode = false; // Reset practice mode flag
    
    // Reload practice widget count after completing exercises
    this.loadPracticeWidgetCount();

    // CRITICAL: Set skip counter BEFORE any HTTP calls to prevent race conditions
    const willSaveProgress = levelWasCompleted && selectedLevelValue;
    const willUpdateStreak = this.correctAnswersCount > 0;

    if (willSaveProgress && willUpdateStreak) {
      this.skipLoadLevelsCount = 2;
    } else if (willSaveProgress || willUpdateStreak) {
      this.skipLoadLevelsCount = 1;
    }
    console.log(`🚩 Set skipLoadLevelsCount = ${this.skipLoadLevelsCount}`);

    // Update streak FIRST if at least one answer was correct
    // This ensures the header gets the new streak value immediately
    if (willUpdateStreak) {
      const streakBeforeUpdate = this.previousStreakValue;
      console.log('🔥 Calling updateStreak() FIRST with previousStreakValue:', streakBeforeUpdate);

      this.userLearningService.updateStreak().subscribe({
        next: (streakData) => {
          console.log('✅ Streak updated:', streakData.streakCount);
          this.streak.set(streakData.streakCount);

          const streakIncreased = streakData.streakCount > streakBeforeUpdate;
          if (streakIncreased && !this.streakAnimationShownToday) {
            this.previousStreakForDisplay.set(streakBeforeUpdate);
            this.newStreakValue.set(streakData.streakCount);
            this.streakAnimationShownToday = true;
            console.log('🎉 Showing streak animation:', streakBeforeUpdate, '->', streakData.streakCount);
            setTimeout(() => {
              this.showStreakCelebration.set(true);
              this.startStreakAnimation();
            }, 100);
          }

          this.previousStreakValue = streakData.streakCount;
          this.lastActivityDate = streakData.lastActivityDate || null;

          // Save progress AFTER streak update completes (if needed)
          if (willSaveProgress && selectedLevelValue) {
            console.log('💾 Now saving completed level progress');
            this.saveProgressToBackendSync(selectedLevelValue.levelNumber + 1);
          }
        },
        error: (error) => {
          console.error('❌ Error updating streak:', error);
          // Still save progress even if streak fails
          if (willSaveProgress && selectedLevelValue) {
            this.saveProgressToBackendSync(selectedLevelValue.levelNumber + 1);
          }
        }
      });
    } else if (willSaveProgress && selectedLevelValue) {
      // Only save progress (no streak update needed)
      console.log('💾 Saving completed level progress (no streak update)');
      this.saveProgressToBackendSync(selectedLevelValue.levelNumber + 1);
    }

    this.correctAnswersCount = 0;
    this.totalExercisesInLevel = 0;
  }

  private startStreakAnimation(): void {
    this.isStreakShrinking.set(false);
    this.isStreakFlying.set(false);
    
    setTimeout(() => {
      this.isStreakFlying.set(true);
    }, 2500);
    
    setTimeout(() => {
      this.closeStreakCelebration();
    }, 3300);
  }
  
  closeStreakCelebration(): void {
    this.showStreakCelebration.set(false);
    this.isStreakShrinking.set(false);
    this.isStreakFlying.set(false);
  }

  exitExerciseMode(): void {
    this.exerciseMode.set(false);
    this.currentExercise.set(null);
  }

  onLanguageHover(): void {
    // Clear any pending close timeout
    if (this.languageDropdownTimeout) {
      clearTimeout(this.languageDropdownTimeout);
      this.languageDropdownTimeout = null;
    }
    this.isLanguageDropdownOpen.set(true);
  }

  onLanguageLeave(): void {
    // Add a small delay before closing to allow moving to the dropdown options
    this.languageDropdownTimeout = setTimeout(() => {
      this.isLanguageDropdownOpen.set(false);
    }, 200);
  }

  selectLanguage(languageCode: 'DE' | 'EN'): void {
    console.log('🌍 Switching language to:', languageCode);
    this.userLanguage.set(languageCode);
    this.isLanguageDropdownOpen.set(false);
    
    // Save to backend
    this.userLearningService.updateLearningConfig({
      learningLanguage: languageCode
    }).subscribe({
      next: (data) => {
        console.log('✅ Language updated in backend:', data);
        // Reload levels for new language
        this.loadLevels();
        // Reload practice widget for new language
        this.loadPracticeWidgetCount();
      },
      error: (err) => {
        console.error('❌ Failed to update language:', err);
      }
    });
  }

  getSelectedLanguage() {
    const langCode = this.userLanguage();
    return this.availableLanguages.find(l => l.code === langCode) || this.availableLanguages[0];
  }
}
