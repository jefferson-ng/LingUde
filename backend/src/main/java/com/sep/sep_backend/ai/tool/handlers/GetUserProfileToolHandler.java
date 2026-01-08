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
 * Tool handler for retrieving user's learning profile information.
 * Returns the user's learning language, current level, and target level.
 */
@Component
public class GetUserProfileToolHandler extends AbstractAiToolHandler {

    private final UserLearningService userLearningService;

    public GetUserProfileToolHandler(UserLearningService userLearningService) {
        this.userLearningService = userLearningService;
    }

    @Override
    public FunctionDeclaration getFunctionDeclaration() {
        return FunctionDeclaration.builder()
            .name("getUserProfile")
            .description("Get the user's learning profile including learning language, current level, and target level. " +
                        "Call this ONCE at the start of a conversation to personalize the tutoring.")
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
                "learningLanguage", ul.getLearningLanguage() != null
                    ? ul.getLearningLanguage().name()
                    : "SPANISH",  // Default to Spanish if not set
                "currentLevel", ul.getCurrentLevel() != null
                    ? ul.getCurrentLevel().name()
                    : "A1",
                "targetLevel", ul.getTargetLevel() != null
                    ? ul.getTargetLevel().name()
                    : "B2"
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
        return "getUserProfile";
    }
}
