package com.sep.sep_backend.user.entity;


/**
 * CEFR language proficiency levels used to describe
 * how well a user can understand and use the learning language.
 */
public enum LanguageLevel {
    A1("Beginner"),
    A2("Elementary"),
    B1("Intermediate"),
    B2("Upper Intermediate"),
    C1("Advanced"),
    C2("Proficient");

    private final String description;

    LanguageLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}