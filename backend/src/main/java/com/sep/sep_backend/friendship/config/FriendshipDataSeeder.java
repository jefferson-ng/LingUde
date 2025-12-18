package com.sep.sep_backend.friendship.config;

import com.sep.sep_backend.friendship.entity.Friendship;
import com.sep.sep_backend.friendship.entity.FriendshipStatus;
import com.sep.sep_backend.friendship.repository.FriendshipRepository;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * CommandLineRunner that automatically seeds the database with test users and friendship data
 * for testing and display purposes.
 *
 * This seeder creates 10 test users with realistic names and various friendship relationships:
 * - Accepted friendships (existing friends)
 * - Pending incoming requests (requests received)
 * - Pending outgoing requests (requests sent)
 * - Users with no relationships (for testing search functionality)
 */
@Component
public class FriendshipDataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(FriendshipDataSeeder.class);

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final PasswordEncoder passwordEncoder;

    // Test users list
    private final List<User> testUsers = new ArrayList<>();

    public FriendshipDataSeeder(UserRepository userRepository,
                                FriendshipRepository friendshipRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Check if test users already exist
        if (userRepository.existsByUsername("alice")) {
            logger.info("Friendship test data already exists, skipping seeding");
            return;
        }

        logger.info("Starting friendship data seeding...");

        createTestUsers();
        createFriendships();

        long userCount = userRepository.count();
        long friendshipCount = friendshipRepository.count();

        logger.info("Friendship data seeding completed!");
        logger.info("Total users in database: {}", userCount);
        logger.info("Total friendships created: {}", friendshipCount);
        logger.info("Test users can log in with password: 'password123'");
    }

    private void createTestUsers() {
        logger.info("Creating 10 test users...");

        String[] usernames = {
            "alice", "bob", "charlie", "diana", "emma",
            "frank", "grace", "henry", "isabel", "jack"
        };

        // All test users use the same password for convenience
        String hashedPassword = passwordEncoder.encode("password123");

        for (String username : usernames) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(username + "@test.com");
            user.setPasswordHash(hashedPassword);
            user.setEmailVerified(true); // Set to verified for easier testing

            User savedUser = userRepository.save(user);
            testUsers.add(savedUser);
            logger.debug("Created test user: {} ({})", username, savedUser.getEmail());
        }

        logger.info("Successfully created {} test users", testUsers.size());
    }

    private void createFriendships() {
        logger.info("Creating friendship relationships...");

        // Main reference user is alice (index 0)
        User alice = testUsers.get(0);

        // Create ACCEPTED friendships (alice is friends with bob, charlie, and diana)
        createAcceptedFriendship(alice, testUsers.get(1)); // alice <-> bob
        createAcceptedFriendship(alice, testUsers.get(2)); // alice <-> charlie
        createAcceptedFriendship(alice, testUsers.get(3)); // alice <-> diana

        // Create PENDING incoming requests (emma and frank sent requests to alice)
        createPendingRequest(testUsers.get(4), alice); // emma -> alice (incoming for alice)
        createPendingRequest(testUsers.get(5), alice); // frank -> alice (incoming for alice)

        // Create PENDING outgoing requests (alice sent requests to grace and henry)
        createPendingRequest(alice, testUsers.get(6)); // alice -> grace (outgoing for alice)
        createPendingRequest(alice, testUsers.get(7)); // alice -> henry (outgoing for alice)

        // isabel (index 8) and jack (index 9) have no relationship with alice
        // These are available for testing search and friend request functionality

        logger.info("Friendship relationships created:");
        logger.info("  - 3 accepted friendships (alice with bob, charlie, diana)");
        logger.info("  - 2 incoming pending requests (from emma, frank to alice)");
        logger.info("  - 2 outgoing pending requests (from alice to grace, henry)");
        logger.info("  - 2 users with no relationship (isabel, jack - for testing search)");
    }

    private void createAcceptedFriendship(User user1, User user2) {
        Friendship friendship = new Friendship();
        friendship.setUser(user1);
        friendship.setFriend(user2);
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);
        logger.debug("Created ACCEPTED friendship: {} <-> {}", user1.getUsername(), user2.getUsername());
    }

    private void createPendingRequest(User requester, User receiver) {
        Friendship friendship = new Friendship();
        friendship.setUser(requester);
        friendship.setFriend(receiver);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);
        logger.debug("Created PENDING request: {} -> {}", requester.getUsername(), receiver.getUsername());
    }
}
