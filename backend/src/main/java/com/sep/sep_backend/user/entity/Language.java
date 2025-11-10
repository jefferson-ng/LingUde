package com.sep.sep_backend.user.entity;

public enum Language {
    EN("English"),
    DE("German"),
    FR("French"),
    ES("Spanish"),
    IT("Italian");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}