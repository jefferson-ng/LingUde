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
 * Tool handler for retrieving user's streak information.
 */
@Component
public class GetStreakToolHandler extends AbstractAiToolHandler {

    private final UserLearningService userLearningService;

    public GetStreakToolHandler(UserLearningService userLearningService) {
        this.userLearningService = userLearningService;
    }

    @Override
    public FunctionDeclaration getFunctionDeclaration() {
        return FunctionDeclaration.builder()
            .name("getStreak")
            .description("Get the user's current learning streak count and last activity date")
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
            return Map.of(
                "success", true,
                "streakCount", ul.getStreakCount() != null ? ul.getStreakCount() : 0,
                "lastActivityDate", ul.getLastActivityDate() != null
                    ? ul.getLastActivityDate().toString()
                    : "never"
            );
        } else {
            return Map.of(
                "success", false,
                "error", "User learning data not found"
            );
        }
    }

    @Override
    public String getToolName() {
        return "getStreak";
    }
}
