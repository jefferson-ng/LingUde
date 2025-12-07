package com.sep.sep_backend.friendship.service;

import com.sep.sep_backend.friendship.dto.FriendRequestDTO;
import com.sep.sep_backend.friendship.dto.FriendshipResponseDTO;
import com.sep.sep_backend.friendship.dto.UserSummaryDTO;
import com.sep.sep_backend.friendship.entity.Friendship;
import com.sep.sep_backend.friendship.entity.FriendshipStatus;
import com.sep.sep_backend.friendship.exception.UserNotFoundException;
import com.sep.sep_backend.friendship.repository.FriendshipRepository;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for managing friendship operations.
 * Handles business logic for friend requests, accepting/rejecting requests,
 * and managing friendships between users.
 */
@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepo;
    private final UserRepository userRepo;

    public FriendshipService(FriendshipRepository friendshipRepo, UserRepository userRepo) {
        this.friendshipRepo = friendshipRepo;
        this.userRepo = userRepo;
    }

    /**
     * Finds a user by username or email.
     * Automatically detects if the input is an email (contains '@') or username.
     *
     * @param usernameOrEmail the username or email to search for
     * @return Optional containing the User if found, empty otherwise
     */
    private Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        if (usernameOrEmail.contains("@")) {
            return userRepo.findByEmail(usernameOrEmail);
        } else {
            return userRepo.findByUsername(usernameOrEmail);
        }
    }

    /**
     * Converts a User entity to a UserSummaryDTO.
     * Extracts only essential user information for API responses.
     *
     * @param user the User entity to convert
     * @return UserSummaryDTO containing user's id, username, and email
     */
    private UserSummaryDTO toUserSummary(User user) {
        return new UserSummaryDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail()
        );
    }

    /**
     * Converts a Friendship entity to a FriendshipResponseDTO.
     * Includes full details of both users involved and friendship metadata.
     *
     * @param friendship the Friendship entity to convert
     * @return FriendshipResponseDTO containing complete friendship information
     */
    private FriendshipResponseDTO toFriendshipResponse(Friendship friendship) {
        return new FriendshipResponseDTO(
            friendship.getId(),
            toUserSummary(friendship.getUser()),
            toUserSummary(friendship.getFriend()),
            friendship.getStatus(),
            friendship.getCreatedAt()
        );
    }

    /**
     * Sends a friend request from one user to another.
     * Creates a new friendship record with PENDING status.
     * Validates that users cannot send requests to themselves and
     * prevents duplicate friendship requests.
     *
     * @param userId the UUID of the user sending the friend request
     * @param usernameOrEmail the username or email of the user to befriend
     * @return FriendshipResponseDTO containing the created friendship details
     * @throws UserNotFoundException if either user is not found
     * @throws IllegalArgumentException if user tries to befriend themselves
     * @throws IllegalStateException if a friendship already exists between the users
     */
    @Transactional
    public FriendshipResponseDTO sendFriendRequest(UUID userId, String usernameOrEmail) {
        User requester = userRepo.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("Requester not found"));

        User receiver = findByUsernameOrEmail(usernameOrEmail)
            .orElseThrow(() -> new UserNotFoundException("User not found with identifier: " + usernameOrEmail));

        if (requester.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        if (friendshipRepo.existsByBothDirections(requester.getId(), receiver.getId())) {
            throw new IllegalStateException("Friendship request already exists");
        }

        Friendship friendship = new Friendship(requester, receiver, FriendshipStatus.PENDING);
        friendship = friendshipRepo.save(friendship);

        return toFriendshipResponse(friendship);
    }

    /**
     * Retrieves all pending friend requests where the given user is the receiver.
     * These are requests that others have sent to the user.
     *
     * @param userId the UUID of the user to get incoming requests for
     * @return List of FriendshipResponseDTO containing all incoming pending requests
     */
    @Transactional(readOnly = true)
    public List<FriendshipResponseDTO> getIncomingPendingRequests(UUID userId) {
        return friendshipRepo.findIncomingByUserIdAndStatus(userId, FriendshipStatus.PENDING)
            .stream()
            .map(this::toFriendshipResponse)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all pending friend requests where the given user is the requester.
     * These are requests that the user has sent to others.
     *
     * @param userId the UUID of the user to get outgoing requests for
     * @return List of FriendshipResponseDTO containing all outgoing pending requests
     */
    @Transactional(readOnly = true)
    public List<FriendshipResponseDTO> getOutgoingPendingRequests(UUID userId) {
        return friendshipRepo.findOutgoingByUserIdAndStatus(userId, FriendshipStatus.PENDING)
            .stream()
            .map(this::toFriendshipResponse)
            .collect(Collectors.toList());
    }

    /**
     * Accepts a pending friend request.
     * Only the receiver of the request can accept it.
     * Updates the friendship status from PENDING to ACCEPTED.
     *
     * @param friendshipId the UUID of the friendship request to accept
     * @param userId the UUID of the user accepting the request
     * @return FriendshipResponseDTO containing the updated friendship details
     * @throws UserNotFoundException if the friendship request is not found
     * @throws IllegalArgumentException if the user is not the receiver of the request
     * @throws IllegalStateException if the request is not in PENDING status
     */
    @Transactional
    public FriendshipResponseDTO acceptFriendRequest(UUID friendshipId, UUID userId) {
        Friendship friendship = friendshipRepo.findById(friendshipId)
            .orElseThrow(() -> new UserNotFoundException("Friendship request not found"));

        if (!friendship.getFriend().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only accept requests sent to you");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be accepted");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship = friendshipRepo.save(friendship);

        return toFriendshipResponse(friendship);
    }

    /**
     * Rejects a pending friend request.
     * Only the receiver of the request can reject it.
     * Updates the friendship status from PENDING to REJECTED.
     *
     * @param friendshipId the UUID of the friendship request to reject
     * @param userId the UUID of the user rejecting the request
     * @return FriendshipResponseDTO containing the updated friendship details
     * @throws UserNotFoundException if the friendship request is not found
     * @throws IllegalArgumentException if the user is not the receiver of the request
     * @throws IllegalStateException if the request is not in PENDING status
     */
    @Transactional
    public FriendshipResponseDTO rejectFriendRequest(UUID friendshipId, UUID userId) {
        Friendship friendship = friendshipRepo.findById(friendshipId)
            .orElseThrow(() -> new UserNotFoundException("Friendship request not found"));

        if (!friendship.getFriend().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only reject requests sent to you");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be rejected");
        }

        friendship.setStatus(FriendshipStatus.REJECTED);
        friendship = friendshipRepo.save(friendship);

        return toFriendshipResponse(friendship);
    }

    /**
     * Retrieves all accepted friendships for a given user.
     * Returns friendships bidirectionally - where the user is either
     * the requester or the receiver of the accepted friendship.
     *
     * @param userId the UUID of the user to get friends for
     * @return List of FriendshipResponseDTO containing all accepted friendships
     */
    @Transactional(readOnly = true)
    public List<FriendshipResponseDTO> getAcceptedFriends(UUID userId) {
        return friendshipRepo.findAcceptedFriendships(userId, FriendshipStatus.ACCEPTED)
            .stream()
            .map(this::toFriendshipResponse)
            .collect(Collectors.toList());
    }

    /**
     * Removes an existing friendship.
     * Permanently deletes the friendship record from the database.
     * Only users who are part of the friendship can remove it.
     *
     * @param friendshipId the UUID of the friendship to remove
     * @param userId the UUID of the user removing the friendship
     * @throws UserNotFoundException if the friendship is not found
     * @throws IllegalArgumentException if the user is not part of the friendship
     */
    @Transactional
    public void removeFriend(UUID friendshipId, UUID userId) {
        Friendship friendship = friendshipRepo.findById(friendshipId)
            .orElseThrow(() -> new UserNotFoundException("Friendship not found"));

        if (!friendship.getUser().getId().equals(userId) && !friendship.getFriend().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only remove your own friendships");
        }

        friendshipRepo.delete(friendship);
    }

    /**
     * Searches for users by username or email.
     * Automatically determines the search type based on the presence of '@' character.
     *
     * @param usernameOrEmail the username or email to search for
     * @return List of UserSummaryDTO containing matching users (typically 0 or 1)
     */
    @Transactional(readOnly = true)
    public List<UserSummaryDTO> searchUsers(String usernameOrEmail) {
        return findByUsernameOrEmail(usernameOrEmail)
            .map(user -> List.of(toUserSummary(user)))
            .orElse(List.of());
    }
}
