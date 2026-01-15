import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import {
  ChatMessageRequest,
  ChatMessageResponse,
  ChatHistoryResponse,
  ChatSession,
  ChatMessage
} from '../models/chat.model';

const STORAGE_KEY_SESSION = 'chat_session_id';

@Injectable({ providedIn: 'root' })
export class ChatService {
  private http = inject(HttpClient);
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
      return this.getChatHistory(sessionId).pipe(
        tap(() => {
          this.initialized = true;
          this.loadingSubject.next(false);
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
          
          // Add user message and AI response to local messages
          const currentMessages = this.messagesSubject.value;
          const newMessages: ChatMessage[] = [
            ...currentMessages,
            {
              role: 'USER',
              content: message,
              timestamp: new Date().toISOString()
            },
            {
              role: 'MODEL',
              content: response.response,
              timestamp: response.timestamp
            }
          ];
          this.messagesSubject.next(newMessages);

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
   * Start a new chat session (clears current session and storage)
   */
  startNewSession(): void {
    localStorage.removeItem(STORAGE_KEY_SESSION);
    this.currentSessionSubject.next(null);
    this.messagesSubject.next([]);
    this.debugInfoSubject.next([]);
    this.initialized = false;
  }

  /**
   * Load a specific session by ID
   */
  loadSession(sessionId: string): Observable<ChatHistoryResponse> {
    this.loadingSubject.next(true);
    return this.getChatHistory(sessionId).pipe(
      tap(() => {
        this.loadingSubject.next(false);
      }),
      catchError((err) => {
        this.loadingSubject.next(false);
        throw err;
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
