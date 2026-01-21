/**
 * Chat models for AI tutor communication
 */

export interface ChatMessage {
  role: 'USER' | 'MODEL' | 'TOOL';
  content: string;
  timestamp: string;
}

export interface ChatMessageRequest {
  message: string;
  sessionId?: string;
}

export interface ChatMessageResponse {
  sessionId: string;
  response: string;
  timestamp: string;
  currentXp: number;
  currentStreak: number;
  toolCalls?: ToolCallInfo[];
}

export interface ToolCallInfo {
  name: string;
  arguments: Record<string, any>;
  result: any;
  durationMs: number;
}

export interface ChatHistoryResponse {
  sessionId: string;
  learningLanguage: string;
  createdAt: string;
  messages: ChatMessage[];
}

export interface ChatSession {
  sessionId: string;
  learningLanguage: string;
  createdAt: string;
  updatedAt: string;
  isActive: boolean;
  messageCount: number;
}
