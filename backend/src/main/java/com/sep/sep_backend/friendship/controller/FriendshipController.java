package com.sep.sep_backend.friendship.controller;

import com.sep.sep_backend.friendship.dto.*;
import com.sep.sep_backend.friendship.exception.UserNotFoundException;
import com.sep.sep_backend.friendship.service.FriendshipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing friendship operations.
 * Provides endpoints for sending, accepting, rejecting friend requests,
 * and managing existing friendships.
 * All endpoints require authentication via JWT token.
 */
@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    /**
     * Retrieves the currently authenticated user's ID from the security context.
     *
     * @return the UUID of the authenticated user
     */
    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Sends a friend request to another user identified by username or email.
     * The request is created with a PENDING status.
     *
     * @param request DTO containing the username or email of the user to befriend
     * @return ResponseEntity with the created friendship details and HTTP 201 status
     * @throws UserNotFoundException if the target user is not found
     * @throws IllegalArgumentException if user tries to send request to themselves
     * @throws IllegalStateException if a friendship already exists between the users
     */
    @PostMapping("/request")
    public ResponseEntity<FriendshipResponseDTO> sendFriendRequest(@Valid @RequestBody FriendRequestDTO request) {
        UUID userId = getCurrentUserId();
        FriendshipResponseDTO response = friendshipService.sendFriendRequest(userId, request.getUsernameOrEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all pending friend requests received by the authenticated user.
     * These are requests where the current user is the receiver.
     *
     * @return ResponseEntity containing a list of incoming pending friend requests
     */
    @GetMapping("/pending/incoming")
    public ResponseEntity<List<FriendshipResponseDTO>> getIncomingPendingRequests() {
        UUID userId = getCurrentUserId();
        List<FriendshipResponseDTO> requests = friendshipService.getIncomingPendingRequests(userId);
        return ResponseEntity.ok(requests);
    }

    /**
     * Retrieves all pending friend requests sent by the authenticated user.
     * These are requests where the current user is the requester.
     *
     * @return ResponseEntity containing a list of outgoing pending friend requests
     */
    @GetMapping("/pending/outgoing")
    public ResponseEntity<List<FriendshipResponseDTO>> getOutgoingPendingRequests() {
        UUID userId = getCurrentUserId();
        List<FriendshipResponseDTO> requests = friendshipService.getOutgoingPendingRequests(userId);
        return ResponseEntity.ok(requests);
    }

    /**
     * Accepts a pending friend request.
     * Only the receiver of the request can accept it.
     * Changes the friendship status from PENDING to ACCEPTED.
     *
     * @param id the UUID of the friendship request to accept
     * @return ResponseEntity with the updated friendship details
     * @throws UserNotFoundException if the friendship request is not found
     * @throws IllegalArgumentException if the user is not the receiver of the request
     * @throws IllegalStateException if the request is not in PENDING status
     */
    @PutMapping("/{id}/accept")
    public ResponseEntity<FriendshipResponseDTO> acceptFriendRequest(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        FriendshipResponseDTO response = friendshipService.acceptFriendRequest(id, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Rejects a pending friend request.
     * Only the receiver of the request can reject it.
     * Changes the friendship status from PENDING to REJECTED.
     *
     * @param id the UUID of the friendship request to reject
     * @return ResponseEntity with the updated friendship details
     * @throws UserNotFoundException if the friendship request is not found
     * @throws IllegalArgumentException if the user is not the receiver of the request
     * @throws IllegalStateException if the request is not in PENDING status
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<FriendshipResponseDTO> rejectFriendRequest(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        FriendshipResponseDTO response = friendshipService.rejectFriendRequest(id, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all accepted friendships for the authenticated user.
     * Returns friendships in both directions (where user is requester or receiver).
     *
     * @return ResponseEntity containing a list of accepted friendships
     */
    @GetMapping
    public ResponseEntity<List<FriendshipResponseDTO>> getAcceptedFriends() {
        UUID userId = getCurrentUserId();
        List<FriendshipResponseDTO> friends = friendshipService.getAcceptedFriends(userId);
        return ResponseEntity.ok(friends);
    }

    /**
     * Removes an existing friendship.
     * Only users involved in the friendship can remove it.
     * Permanently deletes the friendship record from the database.
     *
     * @param id the UUID of the friendship to remove
     * @return ResponseEntity with HTTP 204 No Content status
     * @throws UserNotFoundException if the friendship is not found
     * @throws IllegalArgumentException if the user is not part of the friendship
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFriend(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        friendshipService.removeFriend(id, userId);
        return ResponseEntity.noContent().build();
    }
}