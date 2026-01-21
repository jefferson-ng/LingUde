package com.sep.sep_backend.ai.tool.handlers;

import com.google.genai.types.FunctionDeclaration;
import com.google.genai.types.Schema;
import com.sep.sep_backend.ai.tool.AbstractAiToolHandler;
import com.sep.sep_backend.user.entity.UserLearning;
import com.sep.sep_backend.user.entity.UserSettings;
import com.sep.sep_backend.user.service.UserLearningService;
import com.sep.sep_backend.user.service.UserService;
import com.sep.sep_backend.user.service.UserSettingsService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Tool handler for retrieving user's learning profile information.
 * Returns the user's username, learning language, current level, target level, and UI language preference.
 */
@Component
public class GetUserProfileToolHandler extends AbstractAiToolHandler {

    private final UserLearningService userLearningService;
    private final UserSettingsService userSettingsService;
    private final UserService userService;

    public GetUserProfileToolHandler(UserLearningService userLearningService, UserSettingsService userSettingsService, UserService userService) {
        this.userLearningService = userLearningService;
        this.userSettingsService = userSettingsService;
        this.userService = userService;
    }

    @Override
    public FunctionDeclaration getFunctionDeclaration() {
        return FunctionDeclaration.builder()
            .name("getUserProfile")
            .description("Get the user's learning profile including their name, learning language, current level, and target level. " +
                        "Call this ONCE at the start of a conversation to personalize the tutoring and address the user by name.")
            .parameters(Schema.builder()
                .type("object")
                .build())
            .build();
    }

    @Override
    protected Object executeInternal(UUID userId, Map<String, Object> parameters) {
        Optional<UserLearning> learning = userLearningService.findLearningByUserId(userId);
        Optional<UserSettings> settings = userSettingsService.findSettingsByUserId(userId);

        // Get the user's username
        String username = userService.findUserById(userId)
            .map(user -> user.getUsername())
            .orElse("Learner");

        if (learning.isPresent()) {
            UserLearning ul = learning.get();

            // Get UI language from settings (this is the language the user prefers for communication)
            String uiLanguage = settings
                .map(s -> s.getUiLanguage() != null ? s.getUiLanguage().name() : "EN")
                .orElse("EN");

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("username", username);
            result.put("learningLanguage", ul.getLearningLanguage() != null
                ? ul.getLearningLanguage().name()
                : "SPANISH");
            result.put("currentLevel", ul.getCurrentLevel() != null
                ? ul.getCurrentLevel().name()
                : "A1");
            result.put("targetLevel", ul.getTargetLevel() != null
                ? ul.getTargetLevel().name()
                : "B2");
            result.put("uiLanguage", uiLanguage);
            return result;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("username", username);
            result.put("error", "User learning data not found");
            return result;
        }
    }

    @Override
    public String getToolName() {
        return "getUserProfile";
    }
}
