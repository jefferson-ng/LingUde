import { Component, OnInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
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
export class Pronunciation implements OnInit, OnDestroy {
  @ViewChild('waveformCanvas') waveformCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('playbackTimeline') playbackTimeline!: ElementRef<HTMLDivElement>;
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

  // Waveform visualization
  private animationFrameId: number | null = null;

  // Recording timer
  recordingTime = 0;
  private recordingInterval: any = null;

  // Audio playback
  private audioElement: HTMLAudioElement | null = null;
  isPlaying = false;
  currentTime = 0;
  audioDuration = 0;
  playbackProgress = 0;

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

  ngOnDestroy() {
    this.stopWaveformVisualization();
    this.stopRecordingTimer();
    this.cleanupAudioPlayback();
  }

  loadPracticeSentences() {
    this.loadingSentences = true;
    this.pronunciationService.getPracticeSentences(10).subscribe({
      next: (sentences) => {
        this.practiceSentences = sentences;
        this.loadingSentences = false;
        // Automatically select the first sentence
        if (sentences.length > 0 && !this.selectedSentence) {
          this.selectedSentence = sentences[0];
          this.referenceText = sentences[0].sentence;
        }
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

  getNextSentence() {
    if (this.practiceSentences.length === 0) {
      this.loadPracticeSentences();
      return;
    }

    // Find current index
    const currentIndex = this.selectedSentence
      ? this.practiceSentences.findIndex(s => s.exerciseId === this.selectedSentence!.exerciseId)
      : -1;

    // Get next sentence (loop back to start if at the end)
    const nextIndex = (currentIndex + 1) % this.practiceSentences.length;
    this.selectSentence(this.practiceSentences[nextIndex]);

    // If we've cycled through all sentences, load new ones
    if (nextIndex === 0 && currentIndex !== -1) {
      this.loadPracticeSentences();
    }
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
      this.cleanupAudioPlayback();
      this.recordingTime = 0;
      await this.audioRecorder.startRecording();
      this.isRecording = true;
      this.startRecordingTimer();
      this.startWaveformVisualization();
    } catch (err: any) {
      this.error = err.message || 'Failed to start recording';
    }
  }

  async stopRecording() {
    try {
      this.stopRecordingTimer();
      this.stopWaveformVisualization();
      this.audioBlob = await this.audioRecorder.stopRecording();
      this.isRecording = false;
      this.setupAudioPlayback();
    } catch (err: any) {
      this.error = err.message || 'Failed to stop recording';
      this.isRecording = false;
      this.stopRecordingTimer();
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
    this.cleanupAudioPlayback();
  }

  retryRecording() {
    // Only clear recording-related data, keep the sentence
    this.result = null;
    this.error = null;
    this.audioBlob = null;
    this.recordingTime = 0;
    this.cleanupAudioPlayback();
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

  // New helper functions for coach-like UI
  getPerformanceInsight(pronunciationScore: number, accuracyScore: number): { label: string; sublabel: string } {
    const avgScore = (pronunciationScore + accuracyScore) / 2;

    if (avgScore >= 95) {
      return {
        label: 'Excellent',
        sublabel: 'Native-like clarity and precision'
      };
    } else if (avgScore >= 85) {
      return {
        label: 'Great Work',
        sublabel: 'Clear and understandable with minor refinements'
      };
    } else if (avgScore >= 70) {
      return {
        label: 'Good Progress',
        sublabel: 'Understandable with room for improvement'
      };
    } else if (avgScore >= 50) {
      return {
        label: 'Keep Practicing',
        sublabel: 'Focus on clarity and pronunciation patterns'
      };
    } else {
      return {
        label: 'Needs Work',
        sublabel: 'Review the basics and practice regularly'
      };
    }
  }

  getConfidenceTint(score: number): string {
    if (score >= 95) return 'confidence-excellent';
    if (score >= 85) return 'confidence-great';
    if (score >= 70) return 'confidence-good';
    if (score >= 50) return 'confidence-fair';
    return 'confidence-needs-work';
  }

  needsAttention(score: number, threshold: number = 92): boolean {
    return score < threshold;
  }

  getWordFeedback(word: any): string[] {
    // Generate contextual feedback based on error type and score
    const feedback: string[] = [];

    if (word.errorType === 'Mispronunciation') {
      feedback.push('Check pronunciation guide');
      if (word.accuracyScore < 70) {
        feedback.push('Try breaking into syllables');
      }
    } else if (word.errorType === 'Omission') {
      feedback.push('Word was skipped');
      feedback.push('Speak more slowly');
    } else if (word.errorType === 'Insertion') {
      feedback.push('Extra word detected');
      feedback.push('Review sentence structure');
    } else if (word.accuracyScore < 92 && word.accuracyScore >= 70) {
      feedback.push('Good attempt, minor adjustments needed');
    } else if (word.accuracyScore >= 92) {
      feedback.push('Excellent pronunciation');
    }

    return feedback.length > 0 ? feedback : ['No specific feedback'];
  }

  // Tooltip state management
  hoveredWordIndex: number | null = null;
  tooltipPosition = { top: 0, left: 0 };

  showWordTooltip(index: number, event: MouseEvent): void {
    this.hoveredWordIndex = index;
    this.updateTooltipPosition(event);
  }

  hideWordTooltip(): void {
    this.hoveredWordIndex = null;
  }

  toggleWordTooltip(index: number, event: Event): void {
    // For touch devices - toggle tooltip on tap
    event.preventDefault();
    if (this.hoveredWordIndex === index) {
      this.hideWordTooltip();
    } else {
      this.hoveredWordIndex = index;
      if (event instanceof MouseEvent) {
        this.updateTooltipPosition(event);
      } else if (event instanceof TouchEvent && event.touches.length > 0) {
        const touch = event.touches[0];
        this.updateTooltipPosition({ clientX: touch.clientX, clientY: touch.clientY } as MouseEvent);
      }
    }
  }

  private updateTooltipPosition(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    const rect = target.getBoundingClientRect();

    // Position tooltip above the word
    this.tooltipPosition = {
      top: rect.top + window.scrollY - 10, // 10px above the word
      left: rect.left + window.scrollX + (rect.width / 2) // Center horizontally
    };
  }

  // Recording timer methods
  private startRecordingTimer() {
    this.recordingTime = 0;
    this.recordingInterval = setInterval(() => {
      this.recordingTime++;
    }, 1000);
  }

  private stopRecordingTimer() {
    if (this.recordingInterval) {
      clearInterval(this.recordingInterval);
      this.recordingInterval = null;
    }
  }

  // Waveform visualization methods
  private startWaveformVisualization() {
    if (!this.waveformCanvas) {
      setTimeout(() => this.startWaveformVisualization(), 100);
      return;
    }

    const canvas = this.waveformCanvas.nativeElement;
    const canvasContext = canvas.getContext('2d');
    const analyser = this.audioRecorder.getAnalyser();

    if (!canvasContext || !analyser) return;

    canvas.width = canvas.offsetWidth;
    canvas.height = canvas.offsetHeight;

    const bufferLength = analyser.frequencyBinCount;
    const dataArray = new Uint8Array(bufferLength);

    const draw = () => {
      if (!this.isRecording) return;

      this.animationFrameId = requestAnimationFrame(draw);
      analyser.getByteTimeDomainData(dataArray);

      canvasContext.fillStyle = '#1e293b';
      canvasContext.fillRect(0, 0, canvas.width, canvas.height);

      canvasContext.lineWidth = 2;
      canvasContext.strokeStyle = '#3b82f6';
      canvasContext.beginPath();

      const sliceWidth = canvas.width / bufferLength;
      let x = 0;

      for (let i = 0; i < bufferLength; i++) {
        const v = dataArray[i] / 128.0;
        const y = (v * canvas.height) / 2;

        if (i === 0) {
          canvasContext.moveTo(x, y);
        } else {
          canvasContext.lineTo(x, y);
        }

        x += sliceWidth;
      }

      canvasContext.lineTo(canvas.width, canvas.height / 2);
      canvasContext.stroke();
    };

    draw();
  }

  private stopWaveformVisualization() {
    if (this.animationFrameId !== null) {
      cancelAnimationFrame(this.animationFrameId);
      this.animationFrameId = null;
    }
  }

  // Audio playback methods
  private setupAudioPlayback() {
    if (!this.audioBlob) return;

    this.cleanupAudioPlayback();

    const url = URL.createObjectURL(this.audioBlob);
    this.audioElement = new Audio(url);

    this.audioElement.addEventListener('loadedmetadata', () => {
      this.audioDuration = this.audioElement?.duration || 0;
    });

    this.audioElement.addEventListener('timeupdate', () => {
      if (this.audioElement) {
        this.currentTime = this.audioElement.currentTime;
        this.playbackProgress = (this.currentTime / this.audioDuration) * 100;
      }
    });

    this.audioElement.addEventListener('ended', () => {
      this.isPlaying = false;
      this.currentTime = 0;
      this.playbackProgress = 0;
    });
  }

  private cleanupAudioPlayback() {
    if (this.audioElement) {
      this.audioElement.pause();
      this.audioElement.src = '';
      this.audioElement = null;
    }
    this.isPlaying = false;
    this.currentTime = 0;
    this.audioDuration = 0;
    this.playbackProgress = 0;
  }

  togglePlayback() {
    if (!this.audioElement) return;

    if (this.isPlaying) {
      this.audioElement.pause();
      this.isPlaying = false;
    } else {
      this.audioElement.play();
      this.isPlaying = true;
    }
  }

  seekAudio(event: MouseEvent) {
    if (!this.audioElement || !this.playbackTimeline) return;

    const timeline = this.playbackTimeline.nativeElement;
    const rect = timeline.getBoundingClientRect();
    const clickX = event.clientX - rect.left;
    const percentage = clickX / rect.width;
    const newTime = percentage * this.audioDuration;

    this.audioElement.currentTime = newTime;
    this.currentTime = newTime;
    this.playbackProgress = percentage * 100;
  }

  formatDuration(seconds: number): string {
    if (!seconds || isNaN(seconds)) return '0:00';
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }
}
