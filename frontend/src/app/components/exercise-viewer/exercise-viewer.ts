import { Component, Input, Output, EventEmitter, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslocoPipe } from '@jsverse/transloco';
import { 
  Exercise, 
  ExerciseMCQ, 
  ExerciseFillBlank,
  getMCQOptions,
  parseFillBlankSentence
} from '../../models/exercise.model';
import { ExerciseService } from '../../services/exercise.service';

export interface ExerciseResult {
  isCorrect: boolean;
  userAnswer: string;
  correctAnswer: string;
  xpEarned: number;
}

@Component({
  selector: 'app-exercise-viewer',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslocoPipe],
  templateUrl: './exercise-viewer.html',
  styleUrl: './exercise-viewer.css'
})
export class ExerciseViewerComponent {
  @Input({ required: true }) exercise!: Exercise;
  @Input() showFeedback = true;
  @Input() autoSubmit = false;
  
  @Output() onSubmit = new EventEmitter<ExerciseResult>();
  @Output() onNext = new EventEmitter<void>();
  
  userAnswer = signal<string>('');
  selectedOption = signal<string>('');
  submitted = signal<boolean>(false);
  isCorrect = signal<boolean | null>(null);
  
  canSubmit = computed(() => {
    if (this.exercise.type === 'MCQ') {
      return this.selectedOption().length > 0;
    }
    return this.userAnswer().trim().length > 0;
  });

  mcqOptions = computed(() => {
    if (this.exercise?.type === 'MCQ') {
      return getMCQOptions(this.exercise as ExerciseMCQ);
    }
    return [];
  });

  fillBlankParts = computed(() => {
    if (this.exercise?.type === 'FILL_BLANK') {
      return parseFillBlankSentence((this.exercise as ExerciseFillBlank).sentenceWithBlank);
    }
    return { before: '', after: '' };
  });

  constructor(private exerciseService: ExerciseService) {}

  selectOption(option: string): void {
    if (this.submitted()) return;
    
    this.selectedOption.set(option);
    
    if (this.autoSubmit) {
      this.submit();
    }
  }

  submit(): void {
    if (!this.canSubmit() || this.submitted()) return;

    const answer = this.exercise.type === 'MCQ' 
      ? this.selectedOption() 
      : this.userAnswer();

    this.exerciseService.submitAnswer({
      exerciseId: this.exercise.id,
      exerciseType: this.exercise.type,
      userAnswer: answer
    }).subscribe({
      next: (result) => {
        this.submitted.set(true);
        this.isCorrect.set(result.isCorrect);
        
        const exerciseResult: ExerciseResult = {
          isCorrect: result.isCorrect,
          userAnswer: answer,
          correctAnswer: result.correctAnswer,
          xpEarned: result.xpEarned
        };
        
        this.onSubmit.emit(exerciseResult);
      },
      error: (error) => {
        console.error('Error submitting answer:', error);
      }
    });
  }

  next(): void {
    this.onNext.emit();
    this.reset();
  }

  reset(): void {
    this.userAnswer.set('');
    this.selectedOption.set('');
    this.submitted.set(false);
    this.isCorrect.set(null);
  }

  getCorrectAnswer(): string {
    if (this.exercise.type === 'MCQ') {
      return (this.exercise as ExerciseMCQ).correctAnswer;
    }
    return (this.exercise as ExerciseFillBlank).correctAnswer;
  }

  isOptionCorrect(option: string): boolean {
    if (!this.submitted() || this.exercise.type !== 'MCQ') return false;
    return option === (this.exercise as ExerciseMCQ).correctAnswer;
  }

  isOptionWrong(option: string): boolean {
    if (!this.submitted() || this.exercise.type !== 'MCQ') return false;
    return option === this.selectedOption() && !this.isCorrect();
  }
}
