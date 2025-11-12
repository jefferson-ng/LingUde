import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslocoDirective, TranslocoPipe, TranslocoService } from '@jsverse/transloco';
import { ExerciseViewerComponent, ExerciseResult } from '../../components/exercise-viewer/exercise-viewer';
import { Exercise } from '../../models/exercise.model';
import { ExerciseService } from '../../services/exercise.service';

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
  imports: [CommonModule, TranslocoDirective, TranslocoPipe, ExerciseViewerComponent],
  templateUrl: './learning.html',
  styleUrl: './learning.css'
})
export class Learning {
  private translocoService = inject(TranslocoService);
  
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
      topic: 'example'
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
      difficultyLevel: 'A1',
      topic: 'example'
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
      difficultyLevel: 'A2',
      topic: 'example'
    },
    
  ]);

  protected readonly dailyGoal = signal(10);
  protected readonly dailyProgress = signal(0);
  protected readonly streak = signal(0);
  protected readonly hearts = signal(4);
  protected readonly gems = signal(500);
  
  protected selectedLesson = signal<Lesson | null>(null);
  
  protected exerciseMode = signal<boolean>(false);
  protected currentExercises = signal<Exercise[]>([]);
  protected currentExerciseIndex = signal<number>(0);
  protected currentExercise = signal<Exercise | null>(null);

  constructor(private exerciseService: ExerciseService) {}

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
    const topic = lesson.topic || 'example';

    this.exerciseService.getExercises('DE', difficulty, topic).subscribe({
      next: (exercises) => {
        if (exercises.length > 0) {
          this.currentExercises.set(exercises);
          this.currentExerciseIndex.set(0);
          this.currentExercise.set(exercises[0]);
          this.exerciseMode.set(true);
        } else {
          console.log('Keine Übungen verfügbar für diese Lektion');
        }
      },
      error: (error) => {
        console.error('Error loading exercises:', error);
      }
    });
  }

  onExerciseSubmit(result: ExerciseResult) {
    console.log('Exercise submitted:', result);
  }

  onNextExercise() {
    const nextIndex = this.currentExerciseIndex() + 1;
    const exercises = this.currentExercises();

    if (nextIndex < exercises.length) {
      this.currentExerciseIndex.set(nextIndex);
      this.currentExercise.set(exercises[nextIndex]);
    } else {
      this.completeLesson();
    }
  }

  completeLesson() {
    this.exerciseMode.set(false);
    this.currentExercise.set(null);
    this.currentExercises.set([]);
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
