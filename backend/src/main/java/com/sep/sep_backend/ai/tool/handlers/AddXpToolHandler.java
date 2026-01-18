package com.sep.sep_backend.ai.tool.handlers;

import com.google.genai.types.FunctionDeclaration;
import com.google.genai.types.Schema;
import com.sep.sep_backend.ai.tool.AbstractAiToolHandler;
import com.sep.sep_backend.user.entity.UserLearning;
import com.sep.sep_backend.user.service.UserLearningService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Tool handler for awarding XP to users during chat conversations.
 * The AI can call this when users successfully complete language exercises.
 */
@Component
public class AddXpToolHandler extends AbstractAiToolHandler {

    private final UserLearningService userLearningService;

    public AddXpToolHandler(UserLearningService userLearningService) {
        this.userLearningService = userLearningService;
    }

    @Override
    public FunctionDeclaration getFunctionDeclaration() {
        return FunctionDeclaration.builder()
            .name("addXp")
            .description("Award XP to the user for completing chat exercises or good practice. " +
                        "Only use when user successfully completes a language exercise you created.")
            .parameters(Schema.builder()
                .type("object")
                .properties(Map.of(
                    "xpAmount", Schema.builder()
                        .type("integer")
                        .description("Amount of XP to award (5-25 based on difficulty)")
                        .build(),
                    "reason", Schema.builder()
                        .type("string")
                        .description("Brief reason for the XP award")
                        .build()
                ))
                .required(List.of("xpAmount", "reason"))
                .build())
            .build();
    }

    @Override
    protected Object executeInternal(UUID userId, Map<String, Object> parameters) {
        Integer xpAmount = getInteger(parameters, "xpAmount", 0);
        String reason = getString(parameters, "reason", "");

        // Validate XP range
        if (xpAmount < 5 || xpAmount > 25) {
            return Map.of(
                "success", false,
                "error", "XP amount must be between 5 and 25"
            );
        }

        Optional<UserLearning> result = userLearningService.addXp(userId, xpAmount);

        if (result.isPresent()) {
            UserLearning learning = result.get();
            return Map.of(
                "success", true,
                "newXp", learning.getXp() != null ? learning.getXp() : 0,
                "xpAdded", xpAmount,
                "reason", reason
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
        return "addXp";
    }
}
