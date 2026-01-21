package com.sep.sep_backend.ai.repository;

import com.sep.sep_backend.ai.entity.ToolExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for managing ToolExecutionLog entities.
 * Provides methods for debugging and monitoring tool usage.
 */
@Repository
public interface ToolExecutionLogRepository extends JpaRepository<ToolExecutionLog, UUID> {

    /**
     * Find all tool executions for a session, ordered by most recent first.
     * Used for debugging conversation flows.
     */
    List<ToolExecutionLog> findBySession_IdOrderByExecutedAtDesc(UUID sessionId);

    /**
     * Find all executions of a specific tool, ordered by most recent first.
     * Used for monitoring and analyzing tool usage patterns.
     */
    List<ToolExecutionLog> findByToolNameOrderByExecutedAtDesc(String toolName);
}
