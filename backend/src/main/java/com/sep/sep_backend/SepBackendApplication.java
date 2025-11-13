package com.sep.sep_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sep.sep_backend.user.entity.Language;
import com.sep.sep_backend.user.entity.LanguageLevel;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.entity.UserLearning;
import com.sep.sep_backend.user.repository.UserRepository;
import com.sep.sep_backend.user.repository.UserLearningRepository;

import java.time.LocalDate;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class SepBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SepBackendApplication.class, args);
	
	}

	/**
	 * CommandLineRunner bean that initializes the database with dummy data for testing purposes.
	 * Creates a test user with associated learning data including XP, streak count, and language preferences.
	 * This data can be used to test the XP system and user learning features.
	 *
	 * @param userRepository repository for managing User entities
	 * @param userLearningRepository repository for managing UserLearning entities
	 * @param passwordEncoder encoder for hashing user passwords
	 * @return CommandLineRunner that executes on application startup
	 */
	@Bean
	CommandLineRunner run(UserRepository userRepository, 
	                      UserLearningRepository userLearningRepository,
	                      PasswordEncoder passwordEncoder) {
		return args -> {
			System.out.println("🚀 Initializing database with dummy data...");
			
			// Check if test user already exists
			if (userRepository.findByEmail("testuser@test.com").isEmpty()) {
				// Create a test user
				User testUser = new User();
				testUser.setUsername("testuser");
				testUser.setEmail("testuser@test.com");
				testUser.setPasswordHash(passwordEncoder.encode("password123"));
				testUser.setEmailVerified(true);
				
				testUser = userRepository.save(testUser);
				System.out.println("✅ Created test user: " + testUser.getEmail());
				
				// Create UserLearning data with XP
				UserLearning userLearning = new UserLearning(testUser);
				userLearning.setLearningLanguage(Language.DE);
				userLearning.setCurrentLevel(LanguageLevel.A1);
				userLearning.setTargetLevel(LanguageLevel.B2);
				userLearning.setXp(150); // Starting XP
				userLearning.setStreakCount(5); // 5-day streak
				userLearning.setLastActivityDate(LocalDate.now());
				
				userLearningRepository.save(userLearning);
				System.out.println("✅ Created user learning data with " + userLearning.getXp() + " XP");
				System.out.println("📊 Test User ID: " + testUser.getId());
				System.out.println("💡 Use this ID to test the XP endpoint: GET /api/user/learning/" + testUser.getId());
			} else {
				System.out.println("ℹ️  Test user already exists, skipping initialization");
			}
			
			System.out.println("✨ Application ready!");
		};
	}


}


