export interface WordScore {
  word: string;
  accuracyScore: number;
  errorType: string;
  offset: number;
  duration: number;
}

export interface PronunciationAnalyzeResponse {
  success: boolean;
  transcript: string;
  accuracyScore: number;
  fluencyScore: number;
  completenessScore: number;
  pronunciationScore: number;
  words: WordScore[];
  error?: string;
  xpAwarded?: number;
}

export interface PracticeSentence {
  exerciseId: string;
  sentence: string;
  level: string;
  topic: string;
  contentType: string;
}
