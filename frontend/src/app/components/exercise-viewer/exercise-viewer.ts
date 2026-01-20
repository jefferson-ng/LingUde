import { Component, Input, Output, EventEmitter, signal, computed, OnChanges, SimpleChanges, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslocoPipe } from '@jsverse/transloco';
import { 
  ExerciseDetailResponse,
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
export class ExerciseViewerComponent implements OnChanges {
  @Input({ required: true }) 
  set exercise(value: ExerciseDetailResponse) {
    console.log('📥 Exercise input setter called:', value);
    this.exerciseSignal.set(value);
  }
  get exercise(): ExerciseDetailResponse {
    return this.exerciseSignal();
  }
  
  protected exerciseSignal = signal<ExerciseDetailResponse>(null as any);
  
  @Input() showFeedback = true;
  @Input() autoSubmit = false;
  @Input() isPracticeMode = false;
  
  @Output() onSubmit = new EventEmitter<ExerciseResult>();
  @Output() onNext = new EventEmitter<void>();
  
  userAnswer = signal<string>('');
  selectedOption = signal<string>('');
  submitted = signal<boolean>(false);
  isCorrect = signal<boolean | null>(null);
  correctAnswerFromBackend = signal<string>('');
  
  canSubmit = computed(() => {
    const ex = this.exerciseSignal();
    if (ex?.type === 'MCQ') {
      return this.selectedOption().length > 0;
    }
    return this.userAnswer().trim().length > 0;
  });

  mcqOptions = computed(() => {
    const ex = this.exerciseSignal();
    const options = ex?.type === 'MCQ' && ex.options 
      ? ex.options 
      : [];
    console.log('MCQ Options computed:', {
      exerciseId: ex?.id,
      exerciseType: ex?.type,
      options: options
    });
    return options;
  });

  fillBlankParts = computed(() => {
    const ex = this.exerciseSignal();
    if (ex?.type === 'FILL_BLANK' && ex.sentenceWithBlank) {
      return parseFillBlankSentence(ex.sentenceWithBlank);
    }
    return { before: '', after: '' };
  });

  constructor(private exerciseService: ExerciseService) {}

  ngOnChanges(changes: SimpleChanges): void {
    // Reset component state when a new exercise is loaded
    if (changes['exercise']) {
      console.log('🔄 Exercise changed:', {
        previousValue: changes['exercise'].previousValue?.id,
        currentValue: changes['exercise'].currentValue?.id,
        isFirstChange: changes['exercise'].firstChange,
        currentOptions: changes['exercise'].currentValue?.options
      });
      
      if (!changes['exercise'].firstChange) {
        this.reset();
      }
    }
  }

  selectOption(option: string): void {
    if (this.submitted()) return;
    
    this.selectedOption.set(option);
    
    if (this.autoSubmit) {
      this.submit();
    }
  }

  submit(): void {
    if (!this.canSubmit() || this.submitted()) return;

    const ex = this.exerciseSignal();
    const answer = ex.type === 'MCQ' 
      ? this.selectedOption() 
      : this.userAnswer();

    this.exerciseService.submitAnswer(ex.id, ex.type, answer, this.isPracticeMode).subscribe({
      next: (result) => {
        this.submitted.set(true);
        this.isCorrect.set(result.correct);
        this.correctAnswerFromBackend.set(result.correctAnswer);
        
        const exerciseResult: ExerciseResult = {
          isCorrect: result.correct,
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
    this.correctAnswerFromBackend.set('');
    console.log('🧹 Reset viewer state');
  }

  getCorrectAnswer(): string {
    return this.correctAnswerFromBackend();
  }

  isOptionCorrect(option: string): boolean {
    const ex = this.exerciseSignal();
    if (!this.submitted() || ex?.type !== 'MCQ') return false;
    return option === this.correctAnswerFromBackend();
  }

  isOptionWrong(option: string): boolean {
    const ex = this.exerciseSignal();
    if (!this.submitted() || ex?.type !== 'MCQ') return false;
    return option === this.selectedOption() && !this.isCorrect();
  }
}
