package com.sep.sep_backend.user.controller;

import com.sep.sep_backend.auth.JwtUtil;
import com.sep.sep_backend.auth.dto.RefreshTokenRequest;
import com.sep.sep_backend.auth.dto.RefreshTokenResponse;
import com.sep.sep_backend.auth.entity.RefreshToken;
import com.sep.sep_backend.auth.service.RefreshTokenService;
import com.sep.sep_backend.user.dto.SigninRequest;
import com.sep.sep_backend.user.dto.SignupRequest;
import com.sep.sep_backend.user.dto.AuthResponse;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.service.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, RefreshTokenService refreshTokenService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest req) {
        AuthResponse created = userService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody SigninRequest req) {
        AuthResponse res = userService.signin(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        String refreshToken = req.getRefreshToken();

        // Validate the refresh token
        if (!refreshTokenService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Get the refresh token entity
        Optional<RefreshToken> tokenOptional = refreshTokenService.findValidRefreshToken(refreshToken);
        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RefreshToken token = tokenOptional.get();
        User user = token.getUser();

        // Generate new access token
        String newAccessToken = jwtUtil.generateAccessToken(user.getId());

        // Optionally, generate a new refresh token (rotate refresh tokens for better security)
        String newRefreshToken = refreshTokenService.createRefreshToken(user, token.getDeviceInfo());

        // Delete the old refresh token
        refreshTokenService.deleteByTokenHash(token.getTokenHash());

        RefreshTokenResponse response = new RefreshTokenResponse(newAccessToken, newRefreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Logs out the currently authenticated user by deleting all associated refresh tokens.
     * The user is logged out from all sessions in consequence.
     * This operation clears the user's session and invalidates their tokens to prevent unauthorized access.
     *
     * @return a {@code ResponseEntity} containing a confirmation message of successful logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ( auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int session_count = refreshTokenService.countUserSession(userId);
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully from all devices",
                                        "sessionTerminated", String.valueOf(session_count)));
    }

    /**
     * Gets the currently authenticated user's information.
     * This is a protected endpoint that requires a valid JWT token.
     *
     * @return a {@code ResponseEntity} containing the authenticated user's details
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }
}
