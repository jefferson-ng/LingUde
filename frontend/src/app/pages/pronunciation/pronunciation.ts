import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslocoDirective } from '@jsverse/transloco';
import { PronunciationService } from '../../services/pronunciation.service';
import { AudioRecorderService } from '../../services/audio-recorder.service';
import { UserLearningService, UserLearningData } from '../../services/user-learning.service';
import { PronunciationAnalyzeResponse, WordScore, PracticeSentence } from '../../models/pronunciation.model';

@Component({
  selector: 'app-pronunciation',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslocoDirective],
  templateUrl: './pronunciation.html',
  styleUrls: ['./pronunciation.css']
})
export class Pronunciation implements OnInit {
  referenceText = '';
  isRecording = false;
  isAnalyzing = false;
  result: PronunciationAnalyzeResponse | null = null;
  error: string | null = null;
  userLearningData: UserLearningData | null = null;
  audioBlob: Blob | null = null;

  // Practice mode: 'exercise' or 'custom'
  practiceMode: 'exercise' | 'custom' = 'exercise';
  practiceSentences: PracticeSentence[] = [];
  selectedSentence: PracticeSentence | null = null;
  loadingSentences = false;

  constructor(
    private pronunciationService: PronunciationService,
    private audioRecorder: AudioRecorderService,
    private userLearningService: UserLearningService
  ) {}

  ngOnInit() {
    this.userLearningService.getUserLearning().subscribe({
      next: (data) => {
        this.userLearningData = data;
        // Load practice sentences by default
        this.loadPracticeSentences();
      },
      error: (err) => {
        console.error('Failed to load user learning data:', err);
      }
    });
  }

  loadPracticeSentences() {
    this.loadingSentences = true;
    this.pronunciationService.getPracticeSentences(5).subscribe({
      next: (sentences) => {
        this.practiceSentences = sentences;
        this.loadingSentences = false;
      },
      error: (err) => {
        console.error('Failed to load practice sentences:', err);
        this.loadingSentences = false;
        this.error = 'Failed to load practice sentences';
      }
    });
  }

  selectSentence(sentence: PracticeSentence) {
    this.selectedSentence = sentence;
    this.referenceText = sentence.sentence;
    this.result = null;
    this.error = null;
    this.audioBlob = null;
  }

  switchMode(mode: 'exercise' | 'custom') {
    this.practiceMode = mode;
    this.referenceText = '';
    this.selectedSentence = null;
    this.result = null;
    this.error = null;
    this.audioBlob = null;

    if (mode === 'exercise' && this.practiceSentences.length === 0) {
      this.loadPracticeSentences();
    }
  }

  get languageCode(): string {
    if (!this.userLearningData) return 'en-GB';

    switch (this.userLearningData.learningLanguage) {
      case 'DE':
        return 'de-DE';
      case 'EN':
        return 'en-GB';
      default:
        return 'en-GB';
    }
  }

  get languageDisplay(): string {
    if (!this.userLearningData) return 'English (UK)';

    switch (this.userLearningData.learningLanguage) {
      case 'DE':
        return 'German';
      case 'EN':
        return 'English (UK)';
      default:
        return 'English (UK)';
    }
  }

  async toggleRecording() {
    if (this.isRecording) {
      await this.stopRecording();
    } else {
      await this.startRecording();
    }
  }

  async startRecording() {
    try {
      this.error = null;
      this.result = null;
      await this.audioRecorder.startRecording();
      this.isRecording = true;
    } catch (err: any) {
      this.error = err.message || 'Failed to start recording';
    }
  }

  async stopRecording() {
    try {
      this.audioBlob = await this.audioRecorder.stopRecording();
      this.isRecording = false;
    } catch (err: any) {
      this.error = err.message || 'Failed to stop recording';
      this.isRecording = false;
    }
  }

  async analyze() {
    if (!this.referenceText.trim()) {
      this.error = 'Please enter reference text';
      return;
    }

    if (!this.audioBlob) {
      this.error = 'Please record audio first';
      return;
    }

    this.isAnalyzing = true;
    this.error = null;
    this.result = null;

    try {
      // Pass exerciseId if in exercise mode for XP rewards
      const exerciseId = this.practiceMode === 'exercise' && this.selectedSentence
        ? this.selectedSentence.exerciseId
        : undefined;

      this.result = await this.pronunciationService.analyzePronunciation(
        this.audioBlob,
        this.referenceText,
        this.languageCode,
        exerciseId
      );

      console.log('=== Pronunciation Analysis Result ===');
      console.log('Success:', this.result.success);
      console.log('Transcript:', this.result.transcript);
      console.log('Overall Scores:', {
        pronunciation: this.result.pronunciationScore,
        accuracy: this.result.accuracyScore,
        fluency: this.result.fluencyScore,
        completeness: this.result.completenessScore
      });
      console.log('Word-by-word scores:', this.result.words);
      console.log('XP Awarded:', this.result.xpAwarded);
      console.log('====================================');

      if (!this.result.success && this.result.error) {
        this.error = this.result.error;
      } else if (this.result.success && this.result.xpAwarded) {
        // Refresh user learning data to show updated XP
        this.userLearningService.getUserLearning().subscribe({
          next: (data) => {
            this.userLearningData = data;
          }
        });
      }
    } catch (err: any) {
      console.error('Pronunciation analysis error:', err);
      this.error = err.error?.error || err.message || 'Failed to analyze pronunciation';
    } finally {
      this.isAnalyzing = false;
    }
  }

  reset() {
    this.referenceText = '';
    this.result = null;
    this.error = null;
    this.audioBlob = null;
    this.selectedSentence = null;
  }

  getScoreColor(score: number): string {
    if (score >= 80) return '#22c55e';
    if (score >= 60) return '#eab308';
    return '#ef4444';
  }

  getErrorTypeBadgeClass(errorType: string): string {
    switch (errorType) {
      case 'None':
        return 'badge-success';
      case 'Omission':
        return 'badge-warning';
      case 'Insertion':
        return 'badge-info';
      case 'Mispronunciation':
        return 'badge-error';
      default:
        return 'badge-neutral';
    }
  }
}
