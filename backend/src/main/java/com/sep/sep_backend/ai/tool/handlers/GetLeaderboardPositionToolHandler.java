package com.sep.sep_backend.ai.tool.handlers;

import com.google.genai.types.FunctionDeclaration;
import com.google.genai.types.Schema;
import com.sep.sep_backend.ai.tool.AbstractAiToolHandler;
import com.sep.sep_backend.user.dto.LeaderboardEntryDTO;
import com.sep.sep_backend.user.service.LeaderboardService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Tool handler for retrieving the user's leaderboard position.
 * Returns the user's rank among friends and globally, along with XP and nearby competitors.
 */
@Component
public class GetLeaderboardPositionToolHandler extends AbstractAiToolHandler {

    private final LeaderboardService leaderboardService;

    public GetLeaderboardPositionToolHandler(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @Override
    public FunctionDeclaration getFunctionDeclaration() {
        return FunctionDeclaration.builder()
            .name("getLeaderboardPosition")
            .description("Get the user's current position on the leaderboard. " +
                        "Returns their rank among friends and globally, their XP, and nearby competitors. " +
                        "Use this when the user asks about their ranking, how they compare to others, or their competitive standing.")
            .parameters(Schema.builder()
                .type("object")
                .build())
            .build();
    }

    @Override
    protected Object executeInternal(UUID userId, Map<String, Object> parameters) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Get friends leaderboard
            List<LeaderboardEntryDTO> friendsLeaderboard = leaderboardService.getFriendsLeaderboard(userId);

            // Get global leaderboard
            List<LeaderboardEntryDTO> globalLeaderboard = leaderboardService.getGlobalLeaderboard();

            // Find user's position in friends leaderboard
            LeaderboardEntryDTO userFriendsEntry = findUserEntry(friendsLeaderboard, userId);

            // Find user's position in global leaderboard
            LeaderboardEntryDTO userGlobalEntry = findUserEntry(globalLeaderboard, userId);

            result.put("success", true);

            // Friends ranking info
            if (userFriendsEntry != null) {
                result.put("friendsRank", userFriendsEntry.getRank());
                result.put("totalFriends", friendsLeaderboard.size());
                result.put("xp", userFriendsEntry.getXp());
                result.put("streakCount", userFriendsEntry.getStreakCount());

                // Get nearby friends (1 above and 1 below if they exist)
                int userIndex = userFriendsEntry.getRank() - 1;
                if (userIndex > 0) {
                    LeaderboardEntryDTO above = friendsLeaderboard.get(userIndex - 1);
                    result.put("friendAbove", Map.of(
                        "username", above.getDisplayName() != null ? above.getDisplayName() : above.getUsername(),
                        "xp", above.getXp(),
                        "xpDifference", above.getXp() - userFriendsEntry.getXp()
                    ));
                }
                if (userIndex < friendsLeaderboard.size() - 1) {
                    LeaderboardEntryDTO below = friendsLeaderboard.get(userIndex + 1);
                    result.put("friendBelow", Map.of(
                        "username", below.getDisplayName() != null ? below.getDisplayName() : below.getUsername(),
                        "xp", below.getXp(),
                        "xpDifference", userFriendsEntry.getXp() - below.getXp()
                    ));
                }
            } else {
                result.put("friendsRank", null);
                result.put("totalFriends", 0);
            }

            // Global ranking info
            if (userGlobalEntry != null) {
                result.put("globalRank", userGlobalEntry.getRank());
                result.put("totalUsers", globalLeaderboard.size());

                // Calculate percentile (top X%)
                double percentile = ((double) userGlobalEntry.getRank() / globalLeaderboard.size()) * 100;
                result.put("topPercentile", Math.round(percentile));
            } else {
                result.put("globalRank", null);
                result.put("totalUsers", globalLeaderboard.size());
            }

            return result;

        } catch (Exception e) {
            log.error("Error fetching leaderboard position: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("error", "Failed to retrieve leaderboard position");
            return result;
        }
    }

    private LeaderboardEntryDTO findUserEntry(List<LeaderboardEntryDTO> leaderboard, UUID userId) {
        String userIdStr = userId.toString();
        return leaderboard.stream()
            .filter(entry -> entry.getUserId().equals(userIdStr))
            .findFirst()
            .orElse(null);
    }

    @Override
    public String getToolName() {
        return "getLeaderboardPosition";
    }
}
