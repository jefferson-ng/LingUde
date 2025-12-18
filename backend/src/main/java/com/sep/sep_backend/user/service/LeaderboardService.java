package com.sep.sep_backend.user.service;

import com.sep.sep_backend.friendship.service.FriendshipService;
import com.sep.sep_backend.user.dto.LeaderboardEntryDTO;
import com.sep.sep_backend.user.entity.UserLearning;
import com.sep.sep_backend.user.entity.UserProfile;
import com.sep.sep_backend.user.repository.UserLearningRepository;
import com.sep.sep_backend.user.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for generating leaderboard rankings
 * Combines user learning data (XP, streaks) with profile data (names, avatars)
 */
@Service
@Transactional(readOnly = true)
public class LeaderboardService {

    private final UserLearningRepository userLearningRepository;
    private final UserProfileRepository userProfileRepository;
    private final FriendshipService friendshipService;

    public LeaderboardService(UserLearningRepository userLearningRepository,
                            UserProfileRepository userProfileRepository,
                            FriendshipService friendshipService) {
        this.userLearningRepository = userLearningRepository;
        this.userProfileRepository = userProfileRepository;
        this.friendshipService = friendshipService;
    }

    /**
     * Get friends leaderboard (current user + all accepted friends ranked by XP)
     *
     * @param userId Current user's ID
     * @return List of leaderboard entries sorted by XP descending with ranks
     */
    public List<LeaderboardEntryDTO> getFriendsLeaderboard(UUID userId) {
        // Get accepted friendships and extract friend user IDs
        List<UUID> friendIds = friendshipService.getAcceptedFriends(userId).stream()
            .map(friendship -> {
                // Get the other user's ID (not the current user)
                if (friendship.getRequester().getId().equals(userId)) {
                    return friendship.getReceiver().getId();
                } else {
                    return friendship.getRequester().getId();
                }
            })
            .collect(Collectors.toList());

        // Add current user to the list for leaderboard
        List<UUID> allUserIds = new ArrayList<>(friendIds);
        allUserIds.add(userId);

        return buildLeaderboard(allUserIds);
    }

    /**
     * Get global leaderboard (all users in the system ranked by XP)
     *
     * @return List of leaderboard entries sorted by XP descending with ranks
     */
    public List<LeaderboardEntryDTO> getGlobalLeaderboard() {
        // Get all users who have UserLearning records (i.e., have started learning)
        List<UserLearning> allLearnings = userLearningRepository.findAll();

        // Extract user IDs
        List<UUID> allUserIds = allLearnings.stream()
            .map(ul -> ul.getUser().getId())
            .collect(Collectors.toList());

        // Reuse existing buildLeaderboard method
        return buildLeaderboard(allUserIds);
    }

    /**
     * Builds leaderboard from list of user IDs
     * Fetches learning data and profiles in batch, then ranks by XP
     *
     * @param userIds List of user IDs to include in leaderboard
     * @return Ranked leaderboard entries
     */
    private List<LeaderboardEntryDTO> buildLeaderboard(List<UUID> userIds) {
        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Fetch all learning data in one query (avoids N+1 problem)
        List<UserLearning> learnings = userLearningRepository.findByUser_IdIn(userIds);

        // Sort by XP descending
        learnings.sort((a, b) -> b.getXp().compareTo(a.getXp()));

        // Fetch all profiles in one query
        List<UUID> learningUserIds = learnings.stream()
            .map(ul -> ul.getUser().getId())
            .collect(Collectors.toList());

        Map<UUID, UserProfile> profileMap = userProfileRepository
            .findByUser_IdIn(learningUserIds)
            .stream()
            .collect(Collectors.toMap(p -> p.getUser().getId(), p -> p));

        // Build leaderboard entries with ranks
        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        int rank = 1;
        for (UserLearning learning : learnings) {
            UUID userIdForEntry = learning.getUser().getId();
            UserProfile profile = profileMap.get(userIdForEntry);
            leaderboard.add(new LeaderboardEntryDTO(rank++, learning, profile));
        }

        return leaderboard;
    }
}
