package com.sep.sep_backend.admin.config;

import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.entity.UserRole;
import com.sep.sep_backend.user.entity.UserLearning;
import com.sep.sep_backend.user.repository.UserRepository;
import com.sep.sep_backend.user.repository.UserLearningRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private final UserRepository userRepository;
    private final UserLearningRepository userLearningRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.email:admin@lingude.local}")
    private String adminEmail;

    @Value("${admin.default.username:admin}")
    private String adminUsername;

    @Value("${admin.default.password:Admin123!}")
    private String adminPassword;

    @Value("${admin.default.enabled:true}")
    private boolean createDefaultAdmin;

    public AdminInitializer(UserRepository userRepository,
                           UserLearningRepository userLearningRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userLearningRepository = userLearningRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!createDefaultAdmin) {
            logger.info("Default admin creation is disabled");
            return;
        }

        if (userRepository.existsByEmailIgnoreCase(adminEmail)) {
            logger.info("Admin user already exists: {}", adminEmail);
            return;
        }

        logger.info("Creating default admin user: {}", adminEmail);

        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setEmail(adminEmail);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole(UserRole.ADMIN);
        admin.setEmailVerified(true);
        userRepository.save(admin);

        UserLearning userLearning = new UserLearning(admin);
        userLearningRepository.save(userLearning);

        logger.info("Default admin user created successfully!");
        logger.info("Email: {}", adminEmail);
        logger.info("Password: {}", adminPassword);
    }
}