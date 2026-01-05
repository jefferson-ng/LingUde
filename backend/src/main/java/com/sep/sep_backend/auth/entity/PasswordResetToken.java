package com.sep.sep_backend.auth.entity;

import com.sep.sep_backend.user.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * PasswordResetToken
 *
 * This entity stores password reset tokens for users.
 *
 * SECURITY NOTE:
 * We NEVER store the raw token that is sent by email.
 * Instead, we store ONLY a hash of the token (tokenHash).
 *
 * Why?
 * - If the database is leaked, attackers cannot use the reset tokens.
 * - Same idea as storing password hashes, not passwords.
 *
 * Lifecycle:
 * 1) User requests reset -> create new token with expiry (expiresAt)
 * 2) Email sent to user with raw token in link
 * 3) User submits raw token -> backend hashes it and compares with tokenHash
 * 4) If successful -> mark usedAt, update passwordHash in users table
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Which user requested this password reset.
     *
     * Many tokens can exist for one user (e.g., user clicked "forgot password" multiple times),
     * but in our service logic we will invalidate old tokens when creating a new one
     * (so only the latest token remains usable).
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Hash of the reset token (NOT the raw token).
     *
     * We will store SHA-256 as hex -> 64 characters.
     * Example: "a3f1... (64 chars total)"
     */
    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    /**
     * Expiration time of this reset token.
     * After this time, the token is invalid and reset should fail.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * If not null => token already used.
     * This prevents token reuse.
     */
    @Column(name = "used_at")
    private LocalDateTime usedAt;

    /**
     * When the token was created.
     * Automatically filled by Hibernate.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ----- Constructors -----

    public PasswordResetToken() {}

    public PasswordResetToken(User user, String tokenHash, LocalDateTime expiresAt) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    // ----- Helper methods (business meaning) -----

    /**
     * @return true if token was already used (cannot be reused).
     */
    public boolean isUsed() {
        return usedAt != null;
    }

    /**
     * @return true if token is expired.
     */
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    // ----- Getters / Setters -----

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
