package com.sep.sep_backend.ai.repository;

import com.sep.sep_backend.ai.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing ChatSession entities.
 * Provides methods to find active sessions and session history for users.
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    /**
     * Find the most recent active session for a user.
     * Used to continue existing conversations.
     */
    Optional<ChatSession> findFirstByUser_IdAndIsActiveTrueOrderByUpdatedAtDesc(UUID userId);

    /**
     * Find all sessions for a user, ordered by most recent first.
     * Used for displaying conversation history.
     */
    List<ChatSession> findByUser_IdOrderByUpdatedAtDesc(UUID userId);

    /**
     * Find all active sessions for a user.
     */
    List<ChatSession> findByUser_IdAndIsActiveTrueOrderByUpdatedAtDesc(UUID userId);
}
