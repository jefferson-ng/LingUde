package com.sep.sep_backend.user.repository;

import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, UUID> {

    /**
     * Find user settings by the associated user
     * @param user the user to search for
     * @return Optional containing the user settings if found
     */
    Optional<UserSettings> findByUser(User user);

    /**
     * Find user settings by user ID
     * @param userId the user ID to search for
     * @return Optional containing the user settings if found
     */
    Optional<UserSettings> findByUserId(UUID userId);

    /**
     * Delete user settings by the associated user
     * @param user the user whose settings should be deleted
     */
    void deleteByUser(User user);
}