// friendship.model.ts
export interface UserSummary {
  id: string;
  username: string;
  email: string;
  avatarUrl: string | null;
}

export interface FriendshipResponse {
  id: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  createdAt: string;
  updatedAt: string;
}

export interface PendingRequest {
  id: string;
  requester?: UserSummary;  // For incoming requests
  receiver?: UserSummary;   // For outgoing requests
  createdAt: string;
}

export interface Friend {
  id: string;
  friend: UserSummary;
  acceptedAt: string;
}

export interface FriendRequestPayload {
  usernameOrEmail: string;
}
