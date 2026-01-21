package com.sep.sep_backend.ai.repository;

import com.sep.sep_backend.ai.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for managing ChatMessage entities.
 * Provides methods to retrieve conversation history for a session.
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    /**
     * Find all messages for a session, ordered chronologically.
     * Used to build conversation history for AI context.
     */
    List<ChatMessage> findBySession_IdOrderByCreatedAtAsc(UUID sessionId);

    /**
     * Count the number of messages in a session.
     * Used for session summary statistics.
     */
    long countBySession_Id(UUID sessionId);
}
