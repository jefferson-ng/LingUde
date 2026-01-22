package com.sep.sep_backend.referral.service;

import com.sep.sep_backend.auth.JwtUtil;
import com.sep.sep_backend.referral.dto.ReferralLinkResponse;
import com.sep.sep_backend.referral.entity.Referral;
import com.sep.sep_backend.referral.repository.ReferralRepository;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@Transactional
public class ReferralService {

    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private static final int MAX_GENERATION_ATTEMPTS = 10;

    private final ReferralRepository referralRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final SecureRandom secureRandom;

    @Value("${app.base-url}")
    private String appBaseUrl;

    public ReferralService(ReferralRepository referralRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.referralRepository = referralRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.secureRandom = new SecureRandom();
    }

    /**
     * Get or create the referral link for the current authenticated user.
     * If a referral already exists, return it. Otherwise, generate a new one.
     */
    public ReferralLinkResponse getOrCreateReferralLink() {
        UUID userId = jwtUtil.getCurrentUserId();

        Referral referral = referralRepository.findByUserId(userId)
                .orElseGet(() -> createReferralForUser(userId));

        return buildResponse(referral);
    }

    /**
     * Create a new referral for the given user.
     */
    private Referral createReferralForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String referralCode = generateUniqueCode();

        Referral referral = new Referral(user, referralCode);
        return referralRepository.save(referral);
    }

    /**
     * Generate a unique 6-character alphanumeric code.
     * Retries if a collision occurs (extremely rare with 56.8 billion combinations).
     */
    private String generateUniqueCode() {
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            String code = generateRandomCode();
            if (!referralRepository.existsByReferralCode(code)) {
                return code;
            }
        }
        throw new RuntimeException("Failed to generate unique referral code after " + MAX_GENERATION_ATTEMPTS + " attempts");
    }

    /**
     * Generate a random 6-character alphanumeric string.
     */
    private String generateRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = secureRandom.nextInt(ALPHANUMERIC_CHARS.length());
            code.append(ALPHANUMERIC_CHARS.charAt(index));
        }
        return code.toString();
    }

    /**
     * Build the response DTO with referral code and full URL.
     */
    private ReferralLinkResponse buildResponse(Referral referral) {
        String referralUrl = appBaseUrl + "/invite/" + referral.getReferralCode();
        return new ReferralLinkResponse(referral.getReferralCode(), referralUrl);
    }
}
