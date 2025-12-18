package com.sep.sep_backend.friendship.repository;

import com.sep.sep_backend.friendship.entity.Friendship;
import com.sep.sep_backend.friendship.entity.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {

    //Check if friendship exits in either direction
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friendship f " +
            "WHERE (f.user.id = :userId AND f.friend.id = :friendId) " +
        "OR (f.user.id = :friendId AND f.friend.id = :userId)")
    boolean existsByBothDirections(@Param("userId") UUID userId, @Param("friendId") UUID friendId);

    //Get incoming requests of a specific status (where current user is receiver)
    @Query("SELECT f FROM Friendship f " +
        "JOIN FETCH f.user " +
        "WHERE f.friend.id = :userId AND f.status = :status")
    List<Friendship> findIncomingByUserIdAndStatus(@Param("userId") UUID userId, @Param("status")FriendshipStatus status);

    //Get outgoing requests of a specific status (where current user is requester)
    @Query("SELECT f FROM Friendship f " +
        " JOIN FETCH f.friend " +
        " WHERE f.user.id = :userId AND f.status = :status")
    List<Friendship> findOutgoingByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") FriendshipStatus status);

    // Get all accepted friendships ( bidirectional)
    @Query("SELECT f FROM Friendship f " +
        "LEFT JOIN FETCH f.user " +
        "LEFT JOIN FETCH f.friend " +
        "WHERE (f.user.id = :userId OR f.friend.id = :userId) " +
        "AND f.status = :status")
    List<Friendship> findAcceptedFriendships(@Param("userId") UUID userId, @Param("status") FriendshipStatus status);

    // Find friendship by iD with user validation
    Optional<Friendship> findByIdAndUserId(@Param("friendshipId") UUID friendshipId, @Param("userId") UUID userId);
}
