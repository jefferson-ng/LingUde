package com.sep.sep_backend.auth.service;

import com.sep.sep_backend.auth.entity.PasswordResetToken;
import com.sep.sep_backend.auth.exception.ExpiredResetTokenException;
import com.sep.sep_backend.auth.exception.InvalidResetTokenException;
import com.sep.sep_backend.auth.exception.UsedResetTokenException;
import com.sep.sep_backend.auth.repository.PasswordResetTokenRepository;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * PasswordResetService
 *
 * Contains the business logic for:
 * 1) generating password reset tokens (forgot password)
 * 2) validating tokens + updating the user's password (reset password)
 *
 * Design goals:
 * - Secure: token is random, expires, cannot be reused
 * - No user enumeration: forgot-password always responds success even if email not found
 * - Store only token HASH in DB, never raw token
 * - Works with MailHog SMTP in dev, real SMTP in production (only config changes)
 */
@Service
@Transactional
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * Base URL used to build the reset link that goes into the email.
     * Example (frontend later):
     *   http://localhost:4200/reset-password
     * Email link becomes:
     *   http://localhost:4200/reset-password?token=RAW_TOKEN
     */
    @Value("${app.reset.base-url:http://localhost:4200/reset-password}")
    private String resetBaseUrl;

    /**
     * Token validity duration in minutes.
     * Can be configured later; default is 30 minutes.
     */
    @Value("${app.reset.token-expiration-minutes:30}")
    private long tokenExpirationMinutes;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Start password reset flow.
     *
     * IMPORTANT SECURITY:
     * We do NOT reveal whether the email exists.
     * The controller should always return the same response.
     *
     * @param email the email entered by the user
     */
    public void requestPasswordReset(String email) {
        String normalizedEmail = email.trim().toLowerCase();

        Optional<User> userOptional = userRepository.findByEmail(normalizedEmail);

        // If user does not exist -> do nothing.
        // Caller will still respond "If email exists, we sent a link."
        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        // 1) Invalidate old tokens for this user (mark them used).
        invalidateAllTokensForUser(user.getId());

        // 2) Generate a strong random raw token
        String rawToken = generateSecureToken();

        // 3) Store only the hash of the token
        String tokenHash = sha256Hex(rawToken);

        // 4) Calculate expiry
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(tokenExpirationMinutes);

        // 5) Save token entity
        PasswordResetToken tokenEntity = new PasswordResetToken(user, tokenHash, expiresAt);
        tokenRepository.save(tokenEntity);

        // 6) Build reset link for email
        String resetLink = buildResetLink(rawToken);

        // 7) Send email via SMTP
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    /**
     * Finish password reset flow.
     *
     * @param rawToken    raw token received from client (from email link)
     * @param newPassword new password (plain text) -> will be hashed before saving
     */
    public void resetPassword(String rawToken, String newPassword) {

        // 1) Hash the raw token and find it in DB
        String tokenHash = sha256Hex(rawToken);

        PasswordResetToken token = tokenRepository
                .findTopByTokenHashOrderByCreatedAtDesc(tokenHash)
                .orElseThrow(InvalidResetTokenException::new);

        // 2) Check used / expired
        if (token.isUsed()) {
            throw new UsedResetTokenException();
        }
        if (token.isExpired()) {
            throw new ExpiredResetTokenException();
        }

        // 3) Hash the new password using the same encoder as signup/signin (BCrypt)
        String newPasswordHash = passwordEncoder.encode(newPassword);

        // 4) Update user password hash
        User user = token.getUser();
        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);

        // 5) Mark token as used (prevents reuse)
        token.setUsedAt(LocalDateTime.now());
        tokenRepository.save(token);

        // 6) Optionally invalidate other tokens for this user too (extra safety)
        invalidateAllTokensForUser(user.getId());
    }

    /**
     * Build link: {resetBaseUrl}?token={rawToken}
     */
    private String buildResetLink(String rawToken) {
        // if resetBaseUrl already contains "?" then append with "&"
        String separator = resetBaseUrl.contains("?") ? "&" : "?";
        return resetBaseUrl + separator + "token=" + rawToken;
    }

    /**
     * Marks all existing tokens for this user as used.
     * This ensures only the newest token works.
     */
    private void invalidateAllTokensForUser(UUID userId) {
        List<PasswordResetToken> tokens = tokenRepository.findByUser_Id(userId);
        LocalDateTime now = LocalDateTime.now();

        for (PasswordResetToken t : tokens) {
            if (t.getUsedAt() == null) {
                t.setUsedAt(now);
            }
        }

        // Save all updates in one call.
        // If list is empty, it does nothing.
        tokenRepository.saveAll(tokens);
    }

    /**
     * Generates a secure random token:
     * - 32 random bytes
     * - then encoded as URL-safe string (hex here for simplicity)
     *
     * We use hex because:
     * - easy to copy/paste
     * - safe in URLs
     * - 32 bytes => 64 hex characters (strong enough)
     */
    private String generateSecureToken() {
        byte[] bytes = new byte[32]; // 256-bit token
        new SecureRandom().nextBytes(bytes);

        // Convert to hex string
        return HexFormat.of().formatHex(bytes);
    }

    /**
     * Hashes input with SHA-256 and returns hex string (64 chars).
     */
    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            // SHA-256 always exists in Java; this should never happen.
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}

