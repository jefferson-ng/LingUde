package com.sep.sep_backend.friendship.dto;

import com.sep.sep_backend.friendship.entity.FriendshipStatus;

import java.sql.Timestamp;
import java.util.UUID;

public class FriendshipResponseDTO {

    private UUID friendshipId ;
    private UserSummaryDTO requester;
    private UserSummaryDTO receiver;
    private FriendshipStatus status;
    private Timestamp createdAt;

    public FriendshipResponseDTO(){}

    public FriendshipResponseDTO(UUID friendshipId, UserSummaryDTO requester, UserSummaryDTO receiver, FriendshipStatus status, Timestamp createdAt) {
        this.friendshipId = friendshipId;
        this.requester = requester;
        this.receiver = receiver;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void setFriendshipId(UUID friendshipId) {
        this.friendshipId = friendshipId;
    }

    public void setRequester(UserSummaryDTO requester) {
        this.requester = requester;
    }

    public void setReceiver(UserSummaryDTO receiver) {
        this.receiver = receiver;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getFriendshipId() {
        return friendshipId;
    }

    public UserSummaryDTO getRequester() {
        return requester;
    }

    public UserSummaryDTO getReceiver() {
        return receiver;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
