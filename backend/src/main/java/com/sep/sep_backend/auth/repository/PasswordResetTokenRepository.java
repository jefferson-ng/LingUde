package com.sep.sep_backend.auth.repository;

import com.sep.sep_backend.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * PasswordResetTokenRepository
 *
 * This repository provides DB access for password reset tokens.
 *
 * We mainly need:
 * - find a token by its tokenHash (when the user submits the raw token)
 * - find all tokens for a user (to invalidate older ones)
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    /**
     * Find the newest (latest created) token with this token hash.
     * In practice token_hash should be unique per token, but we keep it safe with Optional.
     */
    Optional<PasswordResetToken> findTopByTokenHashOrderByCreatedAtDesc(String tokenHash);

    /**
     * Find all tokens for a user.
     * We will use this to invalidate/mark-used old tokens when creating a new one.
     */
    List<PasswordResetToken> findByUser_Id(UUID userId);

    /**
     * (Optional helper) Delete expired tokens (can be used later with a scheduled job).
     * Not required now, but useful and clean.
     */
    long deleteByExpiresAtBefore(LocalDateTime time);
}

