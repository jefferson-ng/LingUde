package com.sep.sep_backend.user.service;

import com.sep.sep_backend.user.dto.UserLearningDTO;
import com.sep.sep_backend.user.entity.Language;
import com.sep.sep_backend.user.entity.LanguageLevel;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.entity.UserLearning;
import com.sep.sep_backend.user.repository.UserLearningRepository;
import com.sep.sep_backend.user.repository.UserRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserLearningService {

    private final UserLearningRepository userLearningRepository;
    private final UserRepository userRepository;

    public UserLearningService(UserLearningRepository userLearningRepository,
                               UserRepository userRepository) {
        this.userLearningRepository = userLearningRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create new user learning data
     * @param userLearning the user learning data to create
     * @return the created user learning data
     */
    public UserLearning createUserLearning(UserLearning userLearning) {
        return userLearningRepository.save(userLearning);
    }

    /**
     * Find user learning data by ID
     * @param id the learning data ID
     * @return Optional containing the user learning data if found
     */
    public Optional<UserLearning> findLearningById(UUID id) {
        return userLearningRepository.findById(id);
    }

    /**
     * Find user learning data by user
     * @param user the user
     * @return Optional containing the user learning data if found
     */
    public Optional<UserLearning> findLearningByUser(User user) {
        return userLearningRepository.findByUser(user);
    }

    /**
     * Find user learning data by user ID
     * @param userId the user ID
     * @return Optional containing the user learning data if found
     */
    public Optional<UserLearning> findLearningByUserId(UUID userId) {
        Optional<UserLearning> learningOptional = userLearningRepository.findByUser_Id(userId);
        if (learningOptional.isPresent()) {
            return learningOptional;
        } else {
            // Auto-create UserLearning for new users
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return Optional.empty();
            }
            UserLearning learning = new UserLearning(userOptional.get());
            return Optional.of(userLearningRepository.save(learning));
        }
    }

    /**
     * Update existing user learning data
     * @param userLearning the user learning data with updated information
     * @return the updated user learning data
     */
    public UserLearning updateUserLearning(UserLearning userLearning) {
        return userLearningRepository.save(userLearning);
    }

    /**
     * Add XP to user's learning progress
     * @param userId the user ID
     * @param xpToAdd the amount of XP to add
     * @return Optional containing the updated learning data if found
     */
    public Optional<UserLearning> addXp(UUID userId, Integer xpToAdd) {
        Optional<UserLearning> learningOptional = userLearningRepository.findByUser_Id(userId);
        UserLearning learning;
        if (learningOptional.isPresent()) {
            learning = learningOptional.get();
        } else {
            // Auto-create UserLearning for new users
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return Optional.empty();
            }
            learning = new UserLearning(userOptional.get());
        }
        learning.setXp(learning.getXp() + xpToAdd);
        return Optional.of(userLearningRepository.save(learning));
    }

    /**
     * Update streak count based on last activity date
     * @param userId the user ID
     * @return Optional containing the updated learning data if found
     */
    public Optional<UserLearning> updateStreak(UUID userId) {
        Optional<UserLearning> learningOptional = userLearningRepository.findByUser_Id(userId);
        if (learningOptional.isPresent()) {
            UserLearning learning = learningOptional.get();
            LocalDate today = LocalDate.now();
            LocalDate lastActivity = learning.getLastActivityDate();

            if (lastActivity == null) {
                // First activity ever
                learning.setStreakCount(1);
            } else {
                long daysBetween = ChronoUnit.DAYS.between(lastActivity, today);
                if (daysBetween == 1) {
                    // Consecutive day - increment streak
                    learning.setStreakCount(learning.getStreakCount() + 1);
                } else if (daysBetween > 1) {
                    // Streak broken - reset to 1
                    learning.setStreakCount(1);
                }
                // If daysBetween == 0, same day - no change to streak
            }

            learning.setLastActivityDate(today);
            return Optional.of(userLearningRepository.save(learning));
        }
        return Optional.empty();
    }

    /**
     * Update learning language
     * @param userId the user ID
     * @param language the new learning language
     * @return Optional containing the updated learning data if found
     */
    public Optional<UserLearning> updateLearningLanguage(UUID userId, Language language) {
        Optional<UserLearning> learningOptional = userLearningRepository.findByUser_Id(userId);
        if (learningOptional.isPresent()) {
            UserLearning learning = learningOptional.get();
            learning.setLearningLanguage(language);
            return Optional.of(userLearningRepository.save(learning));
        }
        return Optional.empty();
    }

    /**
     * Update current level
     * @param userId the user ID
     * @param level the new current level
     * @return Optional containing the updated learning data if found
     */
    public Optional<UserLearning> updateCurrentLevel(UUID userId, LanguageLevel level) {
        Optional<UserLearning> learningOptional = userLearningRepository.findByUser_Id(userId);
        if (learningOptional.isPresent()) {
            UserLearning learning = learningOptional.get();
            learning.setCurrentLevel(level);
            return Optional.of(userLearningRepository.save(learning));
        }
        return Optional.empty();
    }

    /**
     * Update target level
     * @param userId the user ID
     * @param level the new target level
     * @return Optional containing the updated learning data if found
     */
    public Optional<UserLearning> updateTargetLevel(UUID userId, LanguageLevel level) {
        Optional<UserLearning> learningOptional = userLearningRepository.findByUser_Id(userId);
        if (learningOptional.isPresent()) {
            UserLearning learning = learningOptional.get();
            learning.setTargetLevel(level);
            return Optional.of(userLearningRepository.save(learning));
        }
        return Optional.empty();
    }

    /**
     * Get leaderboard for a specific language
     * @param language the language
     * @return List of user learning data ordered by XP descending
     */
    public List<UserLearning> getLeaderboard(Language language) {
        return userLearningRepository.findByLearningLanguageOrderByXpDesc(language);
    }

    /**
     * Find all users learning a specific language
     * @param language the language
     * @return List of user learning data for that language
     */
    public List<UserLearning> findByLearningLanguage(Language language) {
        return userLearningRepository.findByLearningLanguage(language);
    }


    /**
     * Update the learning language and CEFR levels (current and target)
     * for a given user in a single operation.
     * <p>
     * If the user has no learning record yet, a new one is created.
     *
     * @param userId the user ID
     * @param dto    DTO containing learningLanguage, currentLevel, targetLevel
     * @return Optional containing the updated DTO if user and learning data exist
     */
    public Optional<UserLearningDTO> updateLearningConfig(UUID userId, UserLearningDTO dto) {
        // Try to load existing learning data
        Optional<UserLearning> learningOptional = userLearningRepository.findByUser_Id(userId);

        UserLearning learning;
        if (learningOptional.isPresent()) {
            learning = learningOptional.get();
        } else {
            // No learning row yet -> create one for this user
            return userRepository.findById(userId)
                    .map(user -> {
                        UserLearning newLearning = new UserLearning(user);
                        // Optionally set defaults
                        newLearning.setLearningLanguage(dto.getLearningLanguage());
                        newLearning.setCurrentLevel(dto.getCurrentLevel());
                        newLearning.setTargetLevel(dto.getTargetLevel());
                        newLearning.setLastActivityDate(LocalDate.now());
                        return userLearningRepository.save(newLearning);
                    })
                    .map(saved -> new UserLearningDTO(
                            saved.getUser().getId().toString(),
                            saved.getLearningLanguage(),
                            saved.getCurrentLevel(),
                            saved.getTargetLevel(),
                            saved.getXp(),
                            saved.getStreakCount(),
                            LocalDate.now()
                    ));
        }

        // Update existing learning data
        if (dto.getLearningLanguage() != null) {
            learning.setLearningLanguage(dto.getLearningLanguage());
        }
        if (dto.getCurrentLevel() != null) {
            learning.setCurrentLevel(dto.getCurrentLevel());
        }
        if (dto.getTargetLevel() != null) {
            learning.setTargetLevel(dto.getTargetLevel());
        }
        learning.setLastActivityDate(LocalDate.now());

        UserLearning saved = userLearningRepository.save(learning);

        return Optional.of(new UserLearningDTO(
                saved.getUser().getId().toString(),
                saved.getLearningLanguage(),
                saved.getCurrentLevel(),
                saved.getTargetLevel(),
                saved.getXp(),
                saved.getStreakCount(),
                saved.getLastActivityDate()
        ));
    }





    /**
     * Delete user learning data by ID
     * @param id the learning data ID
     */
    public void deleteUserLearning(UUID id) {
        userLearningRepository.deleteById(id);
    }

    /**
     * Delete user learning data by user
     * @param user the user whose learning data should be deleted
     */
    public void deleteUserLearningByUser(User user) {
        userLearningRepository.deleteByUser(user);
    }
}