package com.sep.sep_backend.user.config;

import com.sep.sep_backend.user.entity.Achievement;
import com.sep.sep_backend.user.entity.AchievementType; // IMPORTANT
import com.sep.sep_backend.user.repository.AchievementRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds the database with a predefined set of achievements.
 * This runs once at application startup, and only inserts missing achievements.
 */
@Component
public class AchievementDataInitializer implements CommandLineRunner {

    private final AchievementRepository achievementRepository;

    public AchievementDataInitializer(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // XP milestones
        createIfMissing(
                "XP_100",
                "Getting Started",
                "Earn a total of 100 XP.",
                "icon-xp-100.png",
                AchievementType.XP_MILESTONE
        );
        
        createIfMissing(
                "XP_600",
                "XP Master",
                "Earn a total of 600 XP.",
                "icon-xp-600.png",
                AchievementType.XP_MILESTONE
        );

        // Streak achievements
        createIfMissing(
                "STREAK_3",
                "Three Day Streak",
                "Practice for 3 consecutive days.",
                "icon-streak-3.png",
                AchievementType.STREAK
        );
        
        createIfMissing(
                "STREAK_7",
                "One Week Streak",
                "Practice for 7 consecutive days.",
                "icon-streak-7.png",
                AchievementType.STREAK
        );
    }

    /**
     * Inserts an achievement only if its code is not already present.
     */
    private void createIfMissing(String code,
                                 String title,
                                 String description,
                                 String iconUrl,
                                 AchievementType type) {

        // If achievement already exists, skip it
        if (achievementRepository.findByCode(code).isPresent()) {
            return;
        }

        Achievement achievement = new Achievement(
                code,
                title,
                description,
                iconUrl,
                type
        );

        achievementRepository.save(achievement);
    }
}
