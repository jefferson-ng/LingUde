package com.sep.sep_backend.user.service;

import com.sep.sep_backend.user.entity.Achievement;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.entity.UserAchievement;
import com.sep.sep_backend.user.repository.AchievementRepository;
import com.sep.sep_backend.user.repository.UserAchievementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service responsible for unlocking and storing achievements for users.
 * <p>
 * This class provides helper methods that:
 * <ul>
 *     <li>Look up achievements by their technical code (e.g. "XP_100").</li>
 *     <li>Check if a user already owns an achievement.</li>
 *     <li>Create a {@link UserAchievement} entry when a new achievement is unlocked.</li>
 * </ul>
 * <p>
 * Other parts of the application (e.g. XP, streak or lesson services) should call
 * these methods whenever a potential achievement condition is met.
 * </p>
 */
@Service
public class AchievementService {

    private static final Logger log = LoggerFactory.getLogger(AchievementService.class);

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;

    /**
     * Creates a new AchievementService with the required repositories.
     *
     * @param achievementRepository     repository for loading achievement definitions
     * @param userAchievementRepository repository for storing user-achievement links
     */
    public AchievementService(AchievementRepository achievementRepository,
                              UserAchievementRepository userAchievementRepository) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
    }

    /**
     * Grants the achievement with the given code to the user,
     * if and only if:
     * <ul>
     *     <li>The achievement with this code exists in the database.</li>
     *     <li>The user does not already own this achievement.</li>
     * </ul>
     * <p>
     * If the achievement code is unknown (not configured in the DB),
     * the method logs a warning and does nothing.
     * </p>
     *
     * @param user            the user who should receive the achievement
     * @param achievementCode the technical code of the achievement, e.g. "XP_100"
     */
    @Transactional
    public void grantAchievementIfNotOwned(User user, String achievementCode) {
        // 1. Look up the achievement definition by code
        Optional<Achievement> achievementOpt = achievementRepository.findByCode(achievementCode);

        if (achievementOpt.isEmpty()) {
            // Achievement not configured in DB -> nothing to do
            log.warn("Achievement with code '{}' not found. Skipping grant for user {}.",
                    achievementCode, user.getId());
            return;
        }

        Achievement achievement = achievementOpt.get();

        // 2. Check if the user already has this achievement
        boolean alreadyHasIt = userAchievementRepository.existsByUserAndAchievement(user, achievement);
        if (alreadyHasIt) {
            // User already owns this achievement -> do not create a duplicate
            log.debug("User {} already has achievement '{}'. Skipping.", user.getId(), achievementCode);
            return;
        }

        // 3. Create a new UserAchievement entry
        UserAchievement userAchievement = new UserAchievement(user, achievement);
        userAchievementRepository.save(userAchievement);

        log.info("User {} unlocked achievement '{}'.", user.getId(), achievementCode);
    }
}
