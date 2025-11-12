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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


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
}
