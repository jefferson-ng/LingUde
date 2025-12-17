package com.sep.sep_backend.user.service;

import com.sep.sep_backend.auth.JwtUtil;
import com.sep.sep_backend.auth.service.RefreshTokenService;
import com.sep.sep_backend.user.exception.AuthFailedException;
import com.sep.sep_backend.user.exception.EmailAlreadyUsedException;
import com.sep.sep_backend.user.dto.AuthResponse;
import com.sep.sep_backend.user.dto.SigninRequest;
import com.sep.sep_backend.user.dto.SignupRequest;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.repository.UserRepository;
import com.sep.sep_backend.user.entity.UserLearning;
import com.sep.sep_backend.user.repository.UserLearningRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserLearningRepository userLearningRepo;

    public UserService(PasswordEncoder encoder, UserRepository userRepo, JwtUtil jwtUtil, RefreshTokenService refreshTokenService, UserLearningRepository userLearningRepo) {
        this.encoder = encoder;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.userLearningRepo = userLearningRepo;
    }


    /**
     * Registers a new user in the system by creating a user record,
     * saving their hashed password, and generating authentication tokens.
     *
     * @param req the signup request containing user details such as name, email, and password
     * @return an AuthResponse containing the user's ID, email, username, access token, and refresh token
     * @throws EmailAlreadyUsedException if the email is already in use by another user
     */
    public AuthResponse signup(SignupRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        if (userRepo.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyUsedException(email);
        }

        String hash = encoder.encode(req.getPassword());

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(email);
        user.setPasswordHash(hash);
        userRepo.save(user);

        // Create UserLearning for this user
        UserLearning userLearning = new UserLearning(user);
        userLearningRepo.save(userLearning);

        // Generate tokens
        // We now include the user's role in the access token (default: USER)
        String accessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getRole().name()    // "USER" or "ADMIN"
        );
        String refreshToken = refreshTokenService.createRefreshToken(user, "Unknown Device");


        return new AuthResponse(user.getId(), user.getEmail(), user.getUsername(), accessToken, refreshToken);
    }

    /**
     * Authenticates the user based on the provided sign-in details and generates authentication tokens.
     *
     * @param req the sign-in request containing user credentials such as email and password
     * @return an AuthResponse containing the user's ID, email, username, access token, and refresh token
     * @throws AuthFailedException if the authentication fails due to an incorrect email or password
     */
    public AuthResponse signin(SigninRequest req) {
        String email = req.getEmail().toLowerCase().trim();

        User user = userRepo.findByEmail(email)
                .orElseThrow(AuthFailedException::new);

        boolean ok = encoder.matches(req.getPassword(), user.getPasswordHash());
        if (!ok) throw new AuthFailedException();

        // Generate tokens
        // Include the role from the database so the token knows if this user is ADMIN or USER
        String accessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getRole().name()
        );
        String refreshToken = refreshTokenService.createRefreshToken(user, "Unknown Device");


        return new AuthResponse(user.getId(), user.getEmail(), user.getUsername(), accessToken, refreshToken);
    }


    /**
     * Create a new user
     * @param user the user to create
     * @return the created user
     */
    public User createUser(User user) {
        return userRepo.save(user);
    }

    /**
     * Find a user by ID
     * @param id the user ID
     * @return Optional containing the user if found
     */
    public Optional<User> findUserById(UUID id) {
        return userRepo.findById(id);
    }

    /**
     * Find a user by email
     * @param email the email address
     * @return Optional containing the user if found
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    /**
     * Find a user by username
     * @param username the username
     * @return Optional containing the user if found
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    /**
     * Get all users
     * @return List of all users
     */

    /**
     * Returns the currently logged-in user by extracting the user ID
     * from the JWT access token stored in the SecurityContext.
     */
    public User getCurrentUser() {
        UUID userId = jwtUtil.getCurrentUserId();  // works now!

        return userRepo.findById(userId)
                .orElseThrow(AuthFailedException::new);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * Update an existing user
     * @param user the user with updated information
     * @return the updated user
     */
    public User updateUser(User user) {
        return userRepo.save(user);
    }

    /**
     * Delete a user by ID
     * @param id the user ID
     */
    public void deleteUser(UUID id) {
        userRepo.deleteById(id);
    }

    /**
     * Check if a user exists with the given email
     * @param email the email to check
     * @return true if the user exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    /**
     * Check if a user exists with the given username
     * @param username the username to check
     * @return true if the user exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    /**
     * Verify user's email
     * @param userId the user ID
     * @return the updated user
     */
    public Optional<User> verifyUserEmail(UUID userId) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEmailVerified(true);
            return Optional.of(userRepo.save(user));
        }
        return Optional.empty();
    }

    public User findById(UUID userId) {
        return userRepo.findById(userId).orElse(null);
    }
}