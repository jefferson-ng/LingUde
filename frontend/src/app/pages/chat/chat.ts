import { Component, inject, signal, OnInit, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslocoDirective } from '@jsverse/transloco';
import { LucideAngularModule, Send, Bot, User, Bug, Trash2, MessageSquare, ChevronDown, ChevronUp, History, Clock } from 'lucide-angular';
import { ChatService } from '../../services/chat.service';
import { ChatMessage, ChatSession } from '../../models/chat.model';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslocoDirective, LucideAngularModule],
  templateUrl: './chat.html',
  styleUrl: './chat.css'
})
export class Chat implements OnInit, AfterViewChecked {
  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;
  @ViewChild('messageInput') private messageInput!: ElementRef;

  private chatService = inject(ChatService);

  // Icons
  readonly SendIcon = Send;
  readonly BotIcon = Bot;
  readonly UserIcon = User;
  readonly BugIcon = Bug;
  readonly TrashIcon = Trash2;
  readonly MessageIcon = MessageSquare;
  readonly ChevronDownIcon = ChevronDown;
  readonly ChevronUpIcon = ChevronUp;
  readonly HistoryIcon = History;
  readonly ClockIcon = Clock;

  // State
  protected messages = signal<ChatMessage[]>([]);
  protected isLoading = signal(false);
  protected messageText = signal('');
  protected debugInfo = signal<any[]>([]);
  protected showDebugPanel = signal(true);
  protected currentSessionId = signal<string | null>(null);

  // Session history state
  protected sessions = signal<ChatSession[]>([]);
  protected showHistoryPanel = signal(false);
  protected isLoadingSessions = signal(false);

  // For auto-scroll
  private shouldScrollToBottom = false;

  ngOnInit(): void {
    // Subscribe to messages
    this.chatService.messages$.subscribe(messages => {
      this.messages.set(messages);
      this.shouldScrollToBottom = true;
    });

    // Subscribe to loading state
    this.chatService.loading$.subscribe(loading => {
      this.isLoading.set(loading);
    });

    // Subscribe to debug info
    this.chatService.debugInfo$.subscribe(info => {
      this.debugInfo.set(info);
    });

    // Subscribe to current session
    this.chatService.currentSession$.subscribe(sessionId => {
      this.currentSessionId.set(sessionId);
    });

    // Initialize chat - load existing session if available
    this.chatService.initializeChat().subscribe();
  }

  ngAfterViewChecked(): void {
    if (this.shouldScrollToBottom) {
      this.scrollToBottom();
      this.shouldScrollToBottom = false;
    }
  }

  private scrollToBottom(): void {
    try {
      if (this.messagesContainer) {
        this.messagesContainer.nativeElement.scrollTop = this.messagesContainer.nativeElement.scrollHeight;
      }
    } catch (err) {
      console.error('Scroll error:', err);
    }
  }

  protected sendMessage(): void {
    const message = this.messageText().trim();
    if (!message || this.isLoading()) return;

    this.messageText.set('');
    
    this.chatService.sendMessage(message).subscribe({
      next: (_response) => {
        // Tool calls and response info are handled by the service
      },
      error: (err) => {
        console.error('Chat error:', err);
        this.chatService.addDebugEntry({
          type: 'error',
          timestamp: new Date().toISOString(),
          error: err.message || 'Failed to send message'
        });
      }
    });
  }

  protected onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  protected startNewChat(): void {
    this.chatService.startNewSession();
  }

  protected clearDebug(): void {
    this.chatService.clearDebugInfo();
  }

  protected toggleDebugPanel(): void {
    this.showDebugPanel.set(!this.showDebugPanel());
  }

  protected toggleHistoryPanel(): void {
    const isOpening = !this.showHistoryPanel();
    this.showHistoryPanel.set(isOpening);

    // Load sessions when opening the panel
    if (isOpening) {
      this.loadSessions();
    }
  }

  protected loadSessions(): void {
    this.isLoadingSessions.set(true);
    this.chatService.getUserSessions().subscribe({
      next: (sessions) => {
        // Sort by updatedAt descending (most recent first)
        const sorted = sessions.sort((a, b) =>
          new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
        );
        this.sessions.set(sorted);
        this.isLoadingSessions.set(false);
      },
      error: (err) => {
        console.error('Failed to load sessions:', err);
        this.isLoadingSessions.set(false);
      }
    });
  }

  protected loadSession(sessionId: string): void {
    if (sessionId === this.currentSessionId()) {
      // Already on this session
      this.showHistoryPanel.set(false);
      return;
    }

    this.chatService.loadSession(sessionId).subscribe({
      next: () => {
        this.showHistoryPanel.set(false);
      },
      error: (err) => {
        console.error('Failed to load session:', err);
      }
    });
  }

  protected formatSessionDate(dateString: string): string {
    try {
      const date = new Date(dateString);
      const now = new Date();
      const diffTime = now.getTime() - date.getTime();
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

      if (diffDays === 0) {
        return 'today';
      } else if (diffDays === 1) {
        return 'yesterday';
      } else {
        return `${diffDays}`;
      }
    } catch {
      return '';
    }
  }

  protected formatTimestamp(timestamp: string): string {
    try {
      const date = new Date(timestamp);
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } catch {
      return '';
    }
  }

  protected formatDebugEntry(entry: any): string {
    return JSON.stringify(entry, null, 2);
  }
}
