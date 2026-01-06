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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

/**
 * PasswordResetService
 *
 * Email-based reset token design (NO FK to users table).
 *
 * Why?
 * - Flyway runs before Hibernate creates tables.
 * - On a fresh DB, "users" table might not exist yet.
 * - So we avoid foreign key dependency in early migrations.
 *
 * Security:
 * - Store ONLY token hash (SHA-256) in DB (never raw token).
 * - Expiring tokens
 * - One-time use tokens
 * - No user enumeration: request always behaves the same externally
 */
@Service
@Transactional
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.reset.base-url:http://localhost:4200/reset-password}")
    private String resetBaseUrl;

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
     * Start password reset flow ("Forgot password").
     *
     * IMPORTANT:
     * We do NOT reveal whether the email exists.
     * Controller should always respond with the same message.
     */
    public void requestPasswordReset(String email) {
        String normalizedEmail = email.trim().toLowerCase();

        Optional<User> userOptional = userRepository.findByEmail(normalizedEmail);

        // If user does not exist -> do nothing (avoid user enumeration).
        if (userOptional.isEmpty()) {
            return;
        }

        // 1) Invalidate old tokens for this email (mark as used).
        invalidateAllTokensForEmail(normalizedEmail);

        // 2) Generate strong random raw token
        String rawToken = generateSecureToken();

        // 3) Store only hash
        String tokenHash = sha256Hex(rawToken);

        // 4) Calculate expiry
        Instant now = Instant.now();
        Instant expiresAt = now.plus(tokenExpirationMinutes, ChronoUnit.MINUTES);

        // 5) Create & save token entity (email-based)
        PasswordResetToken tokenEntity = new PasswordResetToken();
        tokenEntity.setId(java.util.UUID.randomUUID());
        tokenEntity.setEmail(normalizedEmail);
        tokenEntity.setTokenHash(tokenHash);
        tokenEntity.setCreatedAt(now);
        tokenEntity.setExpiresAt(expiresAt);
        tokenEntity.setUsedAt(null);

        tokenRepository.save(tokenEntity);

        // 6) Build reset link
        String resetLink = buildResetLink(rawToken);

        // 7) Send email via SMTP (MailHog in dev)
        emailService.sendPasswordResetEmail(normalizedEmail, resetLink);
    }

    /**
     * Finish password reset flow ("Reset password").
     */
    public void resetPassword(String rawToken, String newPassword) {

        // 1) Hash raw token and find token in DB
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

        // 3) Find user by email stored in the token
        String email = token.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidResetTokenException::new); // user deleted? treat like invalid token

        // 4) Encode new password
        String newPasswordHash = passwordEncoder.encode(newPassword);

        // 5) Update user
        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);

        // 6) Mark token as used
        token.setUsedAt(Instant.now());
        tokenRepository.save(token);

        // 7) Invalidate other tokens for same email (extra safety)
        invalidateAllTokensForEmail(email);
    }

    /**
     * Build link: {resetBaseUrl}?token={rawToken}
     */
    private String buildResetLink(String rawToken) {
        String separator = resetBaseUrl.contains("?") ? "&" : "?";
        return resetBaseUrl + separator + "token=" + rawToken;
    }

    /**
     * Marks all existing tokens for this email as used.
     * Ensures only newest token works.
     */
    private void invalidateAllTokensForEmail(String email) {
        List<PasswordResetToken> tokens = tokenRepository.findByEmail(email);
        Instant now = Instant.now();

        for (PasswordResetToken t : tokens) {
            if (t.getUsedAt() == null) {
                t.setUsedAt(now);
            }
        }

        tokenRepository.saveAll(tokens);
    }

    /**
     * Secure random token:
     * - 32 bytes random
     * - hex encoded (URL-safe, easy)
     */
    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    /**
     * SHA-256 -> hex string
     */
    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
