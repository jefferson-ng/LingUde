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
                "XP_500",
                "Rising Star",
                "Earn a total of 500 XP.",
                "icon-xp-500.png",
                AchievementType.XP_MILESTONE
        );

        // Streak achievements
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
