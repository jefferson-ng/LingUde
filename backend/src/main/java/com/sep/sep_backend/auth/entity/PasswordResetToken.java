package com.sep.sep_backend.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * PasswordResetToken
 *
 * Stores password reset tokens for users (by email).
 *
 * Why email (not user_id)?
 * - Flyway runs before Hibernate creates tables.
 * - Your DB might not have "users" table on a fresh setup.
 * - Avoids foreign key dependency during early migrations.
 *
 * Token security:
 * - We store ONLY a SHA-256 hash of the token in DB (tokenHash).
 * - The raw token is sent via email (MailHog in dev).
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    private UUID id;

    /**
     * Email that requested the reset.
     * This is enough to find the user later (UserRepository.findByEmail).
     */
    @Column(nullable = false)
    private String email;

    /**
     * SHA-256 hash of the raw token.
     * Length 64 because SHA-256 hex string = 64 chars.
     */
    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    /**
     * When the token expires.
     */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /**
     * When the token was used. Null means not used yet.
     */
    @Column(name = "used_at")
    private Instant usedAt;

    /**
     * Created time (useful for auditing/debugging).
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // --- Constructors ---

    public PasswordResetToken() {
        // Required by JPA
    }

    // --- Helper methods ---

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    // --- Getters / Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(Instant usedAt) {
        this.usedAt = usedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
