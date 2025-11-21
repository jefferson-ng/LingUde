package com.sep.sep_backend.user.repository;

import com.sep.sep_backend.user.entity.Language;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.entity.UserLearning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserLearningRepository extends JpaRepository<UserLearning, UUID> {

    /**
     * Find user learning data by the associated user
     * @param user the user to search for
     * @return Optional containing the user learning data if found
     */
    Optional<UserLearning> findByUser(User user);

    /**
     * Find user learning data by user ID
     * @param userId the user ID to search for
     * @return Optional containing the user learning data if found
     */
    Optional<UserLearning> findByUser_Id(UUID userId);

    /**
     * Find all users learning a specific language
     * @param language the language being learned
     * @return List of user learning records for that language
     */
    List<UserLearning> findByLearningLanguage(Language language);

    /**
     * Find users by learning language and minimum XP (for leaderboards)
     * @param language the language being learned
     * @return List of user learning records sorted by XP descending
     */
    List<UserLearning> findByLearningLanguageOrderByXpDesc(Language language);

    /**
     * Delete user learning data by the associated user
     * @param user the user whose learning data should be deleted
     */
    void deleteByUser(User user);
}
