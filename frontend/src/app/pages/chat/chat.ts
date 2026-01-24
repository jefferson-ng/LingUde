import { Component, inject, signal, OnInit, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslocoDirective } from '@jsverse/transloco';
import { LucideAngularModule, Send, Bot, User, Trash2, MessageSquare, ChevronDown, ChevronUp, History, Clock, Sparkles, Star, TrendingUp, Flame, Trophy, Wrench } from 'lucide-angular';
import { MarkdownComponent } from 'ngx-markdown';
import { ChatService } from '../../services/chat.service';
import { ChatMessage, ChatSession } from '../../models/chat.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslocoDirective, LucideAngularModule, MarkdownComponent],
  templateUrl: './chat.html',
  styleUrl: './chat.css'
})
export class Chat implements OnInit, AfterViewChecked {
  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;
  @ViewChild('messageInput') private messageInput!: ElementRef;

  private chatService = inject(ChatService);
  private authService = inject(AuthService);

  // Icons
  readonly SendIcon = Send;
  readonly BotIcon = Bot;
  readonly UserIcon = User;
  readonly TrashIcon = Trash2;
  readonly MessageIcon = MessageSquare;
  readonly ChevronDownIcon = ChevronDown;
  readonly ChevronUpIcon = ChevronUp;
  readonly HistoryIcon = History;
  readonly ClockIcon = Clock;
  readonly SparklesIcon = Sparkles;
  readonly WrenchIcon = Wrench;
  readonly StarIcon = Star;
  readonly TrendingUpIcon = TrendingUp;
  readonly FlameIcon = Flame;
  readonly TrophyIcon = Trophy;

  // State
  protected messages = signal<ChatMessage[]>([]);
  protected isLoading = signal(false);
  protected messageText = signal('');
  protected toolActivities = signal<any[]>([]);
  protected showActivityPanel = signal(true);
  protected currentSessionId = signal<string | null>(null);

  // Session history state
  protected sessions = signal<ChatSession[]>([]);
  protected showHistoryPanel = signal(false);
  protected isLoadingSessions = signal(false);

  // User info
  protected userName = signal<string>('');

  // For auto-scroll
  private shouldScrollToBottom = false;

  // For streaming effect
  protected streamingMessage = signal<string>('');
  protected isStreaming = signal(false);
  private streamingIntervalId: any = null;

  ngOnInit(): void {
    // Get current user info
    this.authService.user$.subscribe(user => {
      if (user) {
        this.userName.set(user.username);
      }
    });

    // Subscribe to messages
    this.chatService.messages$.subscribe(messages => {
      this.messages.set(messages);
      // Always scroll to bottom when messages update
      setTimeout(() => this.scrollToBottom(), 50);
    });

    // Subscribe to loading state
    this.chatService.loading$.subscribe(loading => {
      this.isLoading.set(loading);
    });

    // Subscribe to tool activities
    this.chatService.debugInfo$.subscribe(info => {
      // Filter to only show tool activities (not errors)
      const activities = info.filter(entry => entry.type === 'tool');
      this.toolActivities.set(activities);
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
      next: (response) => {
        // Start streaming animation for AI response
        this.startStreamingEffect(response.response);
      },
      error: (err) => {
        console.error('Chat error:', err);
        this.isStreaming.set(false);
        this.chatService.addDebugEntry({
          type: 'error',
          timestamp: new Date().toISOString(),
          error: err.message || 'Failed to send message'
        });
      }
    });
  }

  private startStreamingEffect(fullText: string): void {
    // Clear any existing streaming
    if (this.streamingIntervalId) {
      clearInterval(this.streamingIntervalId);
    }

    // Split text into words
    const words = fullText.split(' ');
    let currentIndex = 0;

    this.streamingMessage.set('');
    this.isStreaming.set(true);

    // Stream words one by one
    this.streamingIntervalId = setInterval(() => {
      if (currentIndex < words.length) {
        const currentText = this.streamingMessage();
        const newText = currentText + (currentIndex > 0 ? ' ' : '') + words[currentIndex];
        this.streamingMessage.set(newText);
        currentIndex++;

        // Auto-scroll during streaming
        setTimeout(() => this.scrollToBottom(), 10);
      } else {
        // Streaming complete
        clearInterval(this.streamingIntervalId);
        this.streamingIntervalId = null;
        this.isStreaming.set(false);
        this.streamingMessage.set('');
      }
    }, 50); // 50ms between words for smooth effect
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

  protected toggleActivityPanel(): void {
    this.showActivityPanel.set(!this.showActivityPanel());
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

  protected formatActivityTime(timestamp: string): string {
    try {
      const date = new Date(timestamp);
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } catch {
      return '';
    }
  }

  protected getUserInitial(): string {
    const name = this.userName();
    return name ? name.charAt(0).toUpperCase() : 'U';
  }
}
