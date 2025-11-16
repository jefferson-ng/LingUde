package com.sep.sep_backend.auth.service;

import com.sep.sep_backend.auth.JwtUtil;
import com.sep.sep_backend.auth.entity.RefreshToken;
import com.sep.sep_backend.auth.repository.RefreshTokenRepository;
import com.sep.sep_backend.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.refresh-token-expiration}")
    private long REFRESH_TOKEN_EXPIRATION;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtUtil jwtUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Hashes the given token using SHA-256 algorithm.
     * The resulting hash is returned as a hexadecimal string.
     *
     * @param token the token string to hash
     * @return the MD5 hash of the token as a hexadecimal string
     * @throws RuntimeException if MD5-algorithm is not available
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

    /**
     * Creates a new refresh token for the specified user and device information.
     * The refresh token is generated, hashed for secure storage, and saved to the repository
     * along with its expiration time and device details.
     *
     * @param user the user for whom the refresh token is being created
     * @param deviceInfo information about the device from which the request is made
     * @return the plain (non-hashed) refresh token string to be sent back to the client
     */
    @Transactional
    public String createRefreshToken(User user, String deviceInfo) {
        // Generate the actual JWT refresh token
        String token = jwtUtil.generateRefreshToken(user.getId());

        // Hash the token for secure storage
        String tokenHash = hashToken(token);

        // Calculate expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION / 1000);

        // Create and save the refresh token entity
        RefreshToken refreshToken = new RefreshToken(user, tokenHash, expiresAt, deviceInfo);
        refreshTokenRepository.save(refreshToken);

        // Return the plain token to be sent to the client
        return token;
    }

    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return refreshTokenRepository.findByTokenHash(tokenHash);
    }

    /**
     * Validates the provided refresh token by checking its validity, associating it with a user,
     * matching it with stored token hashes, and ensuring it has not expired.
     *
     * @param token the refresh token string to validate
     * @return true if the token is valid and not expired; false otherwise
     */
    public boolean validateRefreshToken(String token) {
        try {
            // First, validate the JWT itself
            if (!jwtUtil.validateToken(token)) {
                return false;
            }

            // Get userId from token
            UUID userId = jwtUtil.getUserIdFromToken(token);

            // Hash the incoming token to compare with stored hash
            String tokenHash = hashToken(token);

            // Get all refresh tokens for this user
            List<RefreshToken> userTokens = refreshTokenRepository.findByUserId(userId);

            // Check if any stored token matches
            for (RefreshToken storedToken : userTokens) {
                if (tokenHash.equals(storedToken.getTokenHash())) {
                    // Check if token is not expired
                    return storedToken.getExpiresAt().isAfter(LocalDateTime.now());
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Finds a valid refresh token for the given token string.
     * It verifies the token by matching its hash and checks if it is not expired.
     * If a valid refresh token is found, it is returned wrapped in an Optional.
     *
     * @param token the refresh token string to validate and find
     * @return an Optional containing the valid RefreshToken if found, otherwise an empty Optional
     */
    public Optional<RefreshToken> findValidRefreshToken(String token) {
        try {
            UUID userId = jwtUtil.getUserIdFromToken(token);

            // Hash the incoming token to compare with stored hash
            String tokenHash = hashToken(token);

            List<RefreshToken> userTokens = refreshTokenRepository.findByUserId(userId);

            for (RefreshToken storedToken : userTokens) {
                if (tokenHash.equals(storedToken.getTokenHash())
                    && storedToken.getExpiresAt().isAfter(LocalDateTime.now())) {
                    return Optional.of(storedToken);
                }
            }

            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Deletes all refresh tokens associated with the specified user ID.
     * This operation will remove all tokens linked to the given user from the repository.
     * The method is executed within a transactional context to maintain consistency.
     *
     * @param userId the unique identifier of the user whose refresh tokens are to be deleted
     */
    @Transactional
    public void deleteByUserId(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * Deletes a refresh token from the repository by its token hash.
     * The provided token hash is used to identify and remove the corresponding record.
     * This method operates within a transactional context to ensure atomicity.
     *
     * @param tokenHash the hash of the refresh token to delete
     */
    @Transactional
    public void deleteByTokenHash(String tokenHash) {
        refreshTokenRepository.deleteByTokenHash(tokenHash);
    }

    /**
     * Deletes all expired refresh tokens from the repository.
     * A token is considered expired if its expiration timestamp is before the current time.
     * This method is transactional to ensure atomicity during the deletion process.
     */
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    /**
     * Retrieves a list of refresh tokens associated with the given user ID.
     *
     * @param userId the unique identifier of the user whose refresh tokens are to be fetched
     * @return a list of RefreshToken objects linked to the specified user ID
     */
    public List<RefreshToken> findByUserId(UUID userId) {
        return refreshTokenRepository.findByUserId(userId);
    }

    public int countUserSession(UUID userId){
        return refreshTokenRepository.countRefreshTokenByUser_Id(userId);
    }
}
