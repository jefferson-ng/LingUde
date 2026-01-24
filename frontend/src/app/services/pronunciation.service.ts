import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { firstValueFrom, Observable } from 'rxjs';
import { PronunciationAnalyzeResponse, PracticeSentence } from '../models/pronunciation.model';

@Injectable({ providedIn: 'root' })
export class PronunciationService {
  private apiUrl = '/api/speech';

  constructor(private http: HttpClient) {}

  /**
   * Fetch practice sentences from exercises based on user's learning language and level
   */
  getPracticeSentences(count: number = 5): Observable<PracticeSentence[]> {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<PracticeSentence[]>(
      `${this.apiUrl}/practice-sentences?count=${count}`,
      { headers }
    );
  }

  async analyzePronunciation(
    audioBlob: Blob,
    referenceText: string,
    language: string,
    exerciseId?: string
  ): Promise<PronunciationAnalyzeResponse> {
    const formData = new FormData();
    formData.append('audio', audioBlob, 'audio.wav');
    formData.append('referenceText', referenceText);
    formData.append('language', language);

    // Add exerciseId if this is exercise-based practice (for XP rewards)
    if (exerciseId) {
      formData.append('exerciseId', exerciseId);
    }

    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return firstValueFrom(
      this.http.post<PronunciationAnalyzeResponse>(`${this.apiUrl}/analyze`, formData, { headers })
    );
  }
}
