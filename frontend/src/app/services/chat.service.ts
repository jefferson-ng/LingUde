import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, forkJoin } from 'rxjs';
import { tap, catchError, map } from 'rxjs/operators';
import {
  ChatMessageRequest,
  ChatMessageResponse,
  ChatHistoryResponse,
  ChatSession,
  ChatMessage
} from '../models/chat.model';
import { UserLearningService } from './user-learning.service';

const STORAGE_KEY_SESSION = 'chat_session_id';

@Injectable({ providedIn: 'root' })
export class ChatService {
  private http = inject(HttpClient);
  private userLearningService = inject(UserLearningService);
  private apiUrl = '/api/chat';

  // Current session state
  private currentSessionSubject = new BehaviorSubject<string | null>(null);
  currentSession$ = this.currentSessionSubject.asObservable();

  // Messages for current session
  private messagesSubject = new BehaviorSubject<ChatMessage[]>([]);
  messages$ = this.messagesSubject.asObservable();

  // Loading state
  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  // Debug info for tool calls
  private debugInfoSubject = new BehaviorSubject<any[]>([]);
  debugInfo$ = this.debugInfoSubject.asObservable();

  // Initialization flag
  private initialized = false;

  constructor() {
    // Load session from storage on service creation
    this.loadSessionFromStorage();
  }

  /**
   * Load session ID from localStorage and fetch history if exists
   */
  private loadSessionFromStorage(): void {
    const savedSessionId = localStorage.getItem(STORAGE_KEY_SESSION);
    if (savedSessionId) {
      this.currentSessionSubject.next(savedSessionId);
    }
  }

  /**
   * Initialize the chat - load existing session history if available
   * Call this from the chat component's ngOnInit
   */
  initializeChat(): Observable<ChatHistoryResponse | null> {
    if (this.initialized && this.messagesSubject.value.length > 0) {
      // Already initialized with messages
      return of(null);
    }

    const sessionId = this.currentSessionSubject.value;
    if (sessionId) {
      this.loadingSubject.next(true);

      // Load both chat history and tool activities in parallel
      return forkJoin({
        history: this.getChatHistory(sessionId),
        tools: this.getToolActivities(sessionId)
      }).pipe(
        map(({ history, tools }) => {
          // Update tool activities
          this.debugInfoSubject.next(tools);
          this.initialized = true;
          this.loadingSubject.next(false);
          return history;
        }),
        catchError((err) => {
          console.error('Failed to load chat history:', err);
          // Session might be invalid, clear it
          this.clearStoredSession();
          this.loadingSubject.next(false);
          return of(null);
        })
      );
    }

    this.initialized = true;
    return of(null);
  }

  /**
   * Save session ID to localStorage
   */
  private saveSessionToStorage(sessionId: string): void {
    localStorage.setItem(STORAGE_KEY_SESSION, sessionId);
  }

  /**
   * Clear stored session from localStorage
   */
  private clearStoredSession(): void {
    localStorage.removeItem(STORAGE_KEY_SESSION);
    this.currentSessionSubject.next(null);
    this.messagesSubject.next([]);
  }

  /**
   * Send a message to the AI tutor
   */
  sendMessage(message: string, sessionId?: string): Observable<ChatMessageResponse> {
    // Add user message immediately to UI
    const currentMessages = this.messagesSubject.value;
    const userMessage: ChatMessage = {
      role: 'USER',
      content: message,
      timestamp: new Date().toISOString()
    };
    this.messagesSubject.next([...currentMessages, userMessage]);

    // Start loading animation
    this.loadingSubject.next(true);

    const request: ChatMessageRequest = {
      message,
      sessionId: sessionId || this.currentSessionSubject.value || undefined
    };

    return this.http.post<ChatMessageResponse>(`${this.apiUrl}/message`, request).pipe(
      tap({
        next: (response) => {
          // Update and persist current session
          this.currentSessionSubject.next(response.sessionId);
          this.saveSessionToStorage(response.sessionId);

          // Add AI response to messages (user message already added)
          const currentMessagesAfterSend = this.messagesSubject.value;
          const aiMessage: ChatMessage = {
            role: 'MODEL',
            content: response.response,
            timestamp: response.timestamp
          };
          this.messagesSubject.next([...currentMessagesAfterSend, aiMessage]);

          // Add tool calls to debug info if available
          if (response.toolCalls && response.toolCalls.length > 0) {
            const currentDebug = this.debugInfoSubject.value;
            const toolCallEntries = response.toolCalls.map(tc => ({
              type: 'tool',
              name: tc.name,
              arguments: tc.arguments,
              result: tc.result,
              durationMs: tc.durationMs,
              timestamp: new Date().toISOString()
            }));
            this.debugInfoSubject.next([...currentDebug, ...toolCallEntries]);

            // Check if addXp was called - refresh user learning data to update sidebar
            const hasXpChange = response.toolCalls.some(tc => tc.name === 'addXp');
            if (hasXpChange) {
              console.log('📢 Chat: XP was added, refreshing user learning data...');
              this.userLearningService.getUserLearning().subscribe();
            }
          }

          this.loadingSubject.next(false);
        },
        error: () => {
          this.loadingSubject.next(false);
        }
      })
    );
  }

  /**
   * Get chat history for a session
   */
  getChatHistory(sessionId: string): Observable<ChatHistoryResponse> {
    return this.http.get<ChatHistoryResponse>(`${this.apiUrl}/history/${sessionId}`).pipe(
      tap((response) => {
        this.currentSessionSubject.next(response.sessionId);
        this.saveSessionToStorage(response.sessionId);
        this.messagesSubject.next(response.messages);
      })
    );
  }

  /**
   * Get all sessions for current user
   */
  getUserSessions(): Observable<ChatSession[]> {
    return this.http.get<ChatSession[]>(`${this.apiUrl}/sessions`);
  }

  /**
   * Start a new chat session (closes current session and clears storage)
   */
  startNewSession(): void {
    const currentSessionId = this.currentSessionSubject.value;

    // Close the current session on the backend if one exists
    if (currentSessionId) {
      this.http.post(`${this.apiUrl}/sessions/${currentSessionId}/close`, {}).subscribe({
        next: () => console.log('Previous session closed'),
        error: (err) => console.warn('Failed to close previous session:', err)
      });
    }

    // Clear local state
    localStorage.removeItem(STORAGE_KEY_SESSION);
    this.currentSessionSubject.next(null);
    this.messagesSubject.next([]);
    this.debugInfoSubject.next([]);
    this.initialized = false;
  }

  /**
   * Load a specific session by ID (including tool activities)
   */
  loadSession(sessionId: string): Observable<ChatHistoryResponse> {
    this.loadingSubject.next(true);

    // Load both chat history and tool activities in parallel
    return forkJoin({
      history: this.getChatHistory(sessionId),
      tools: this.getToolActivities(sessionId)
    }).pipe(
      map(({ history, tools }) => {
        // Update tool activities (debugInfo)
        this.debugInfoSubject.next(tools);
        this.loadingSubject.next(false);
        return history;
      }),
      catchError((err) => {
        this.loadingSubject.next(false);
        throw err;
      })
    );
  }

  /**
   * Get tool activities for a session
   * Maps backend ToolActivityDto to frontend format with 'type' field
   * Backend returns descending order, we reverse to show oldest first
   */
  getToolActivities(sessionId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/sessions/${sessionId}/tools`).pipe(
      map((activities) => activities
        .map(activity => ({
          type: 'tool',
          name: activity.name,
          arguments: activity.arguments,
          result: activity.result,
          durationMs: activity.durationMs,
          timestamp: activity.timestamp
        }))
        .reverse() // Backend returns newest first, we want oldest first for chronological display
      ),
      catchError((err) => {
        console.warn('Failed to load tool activities:', err);
        return of([]);
      })
    );
  }

  /**
   * Clear debug info
   */
  clearDebugInfo(): void {
    this.debugInfoSubject.next([]);
  }

  /**
   * Add manual debug entry (for testing)
   */
  addDebugEntry(entry: any): void {
    const current = this.debugInfoSubject.value;
    this.debugInfoSubject.next([...current, entry]);
  }
}
