package com.sep.sep_backend.user.service;

import com.sep.sep_backend.user.exception.AuthFailedException;
import com.sep.sep_backend.user.exception.EmailAlreadyUsedException;
import com.sep.sep_backend.user.dto.AuthResponse;
import com.sep.sep_backend.user.dto.SigninRequest;
import com.sep.sep_backend.user.dto.SignupRequest;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {


    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }



    //signup feature
    public AuthResponse signup(SignupRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        if (userRepo.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyUsedException(email);
        }

        String hash = encoder.encode(req.getPassword());

        User user = new User();
        user.setUsername(req.getName());
        user.setEmail(email);
        user.setPasswordHash(hash);
        userRepo.save(user);

        return new AuthResponse(user.getId(), user.getEmail(), user.getUsername());
    }

    //signin feature
    public AuthResponse signin(SigninRequest req) {
        String email = req.getEmail().toLowerCase().trim();

        User user = userRepo.findByEmail(email)
                .orElseThrow(AuthFailedException::new);

        boolean ok = encoder.matches(req.getPassword(), user.getPasswordHash());
        if (!ok) throw new AuthFailedException();

        return new AuthResponse(user.getId(), user.getEmail(), user.getUsername());
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
     * @return true if user exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    /**
     * Check if a user exists with the given username
     * @param username the username to check
     * @return true if user exists, false otherwise
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
}