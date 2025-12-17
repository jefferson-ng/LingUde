
export interface UserSummary {
  id: string;
  username: string;
  email: string;
  avatarUrl: string | null;
}

export interface FriendshipResponse {
  friendshipId: string;
  requester: UserSummary;
  receiver: UserSummary;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  createdAt: string;
  updatedAt: string;
}

export interface PendingRequest {
  friendshipId: string;
  requester: UserSummary;  // For incoming requests
  receiver: UserSummary;   // For outgoing requests
  createdAt: string;
}

export interface Friend {
  friendshipId: string;
  requester: UserSummary;
  receiver: UserSummary;
  status: 'PENDING' | 'ACCEPTED'| 'REJECTED';
  createdAt: string;
}

export interface FriendRequestPayload {
  usernameOrEmail: string;
}
