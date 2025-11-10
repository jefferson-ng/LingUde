package com.sep.sep_backend.user.entity;

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