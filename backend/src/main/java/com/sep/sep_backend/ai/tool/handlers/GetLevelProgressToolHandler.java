package com.sep.sep_backend.ai.tool.handlers;

import com.google.genai.types.FunctionDeclaration;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import com.sep.sep_backend.ai.tool.AbstractAiToolHandler;
import com.sep.sep_backend.user.entity.UserLearning;
import com.sep.sep_backend.user.service.UserLearningService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Tool handler for retrieving user's level and XP progress information.
 */
@Component
public class GetLevelProgressToolHandler extends AbstractAiToolHandler {

    private final UserLearningService userLearningService;

    // XP thresholds for each CEFR level
    private static final Map<String, Integer> LEVEL_XP_THRESHOLDS = Map.of(
        "A1", 0,
        "A2", 500,
        "B1", 1500,
        "B2", 3000,
        "C1", 5000,
        "C2", 8000
    );

    public GetLevelProgressToolHandler(UserLearningService userLearningService) {
        this.userLearningService = userLearningService;
    }

    @Override
    public FunctionDeclaration getFunctionDeclaration() {
        return FunctionDeclaration.builder()
            .name("getLevelProgress")
            .description("Get the user's current XP, level, and progress towards next level")
            .parameters(Schema.builder()
                .type("object")
                .build())
            .build();
    }

    @Override
    protected Object executeInternal(UUID userId, Map<String, Object> parameters) {
        Optional<UserLearning> learning = userLearningService.findLearningByUserId(userId);

        if (learning.isPresent()) {
            UserLearning ul = learning.get();
            String currentLevel = ul.getCurrentLevel() != null
                ? ul.getCurrentLevel().name()
                : "A1";
            int currentXp = ul.getXp() != null ? ul.getXp() : 0;

            // Calculate progress to next level
            String nextLevel = getNextLevel(currentLevel);
            int nextLevelXp = LEVEL_XP_THRESHOLDS.getOrDefault(nextLevel, Integer.MAX_VALUE);
            int currentLevelXp = LEVEL_XP_THRESHOLDS.getOrDefault(currentLevel, 0);
            int xpForNextLevel = nextLevelXp - currentXp;
            int progressPercent = (int) (((double) (currentXp - currentLevelXp) /
                                         (nextLevelXp - currentLevelXp)) * 100);

            return Map.of(
                "success", true,
                "currentXp", currentXp,
                "currentLevel", currentLevel,
                "targetLevel", ul.getTargetLevel() != null
                    ? ul.getTargetLevel().name()
                    : "B2",
                "nextLevel", nextLevel,
                "xpForNextLevel", Math.max(0, xpForNextLevel),
                "progressPercent", Math.max(0, Math.min(100, progressPercent))
            );
        } else {
            return Map.of(
                "success", false,
                "error", "User learning data not found"
            );
        }
    }

    private String getNextLevel(String currentLevel) {
        return switch (currentLevel) {
            case "A1" -> "A2";
            case "A2" -> "B1";
            case "B1" -> "B2";
            case "B2" -> "C1";
            case "C1" -> "C2";
            default -> "C2";
        };
    }

    @Override
    public String getToolName() {
        return "getLevelProgress";
    }
}
