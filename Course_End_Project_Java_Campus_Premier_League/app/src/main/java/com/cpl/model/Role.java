package com.cpl.model;

/**
 * Player cricket role in the Campus Premier League.
 * // Unit I: Enums
 */
public enum Role {
    BATTER("Batter", "Batsman specializing in scoring runs"),
    BOWLER("Bowler", "Specialist bowler taking wickets and controlling runs"),
    ALL_ROUNDER("All-Rounder", "Dual-skill player capable of batting and bowling"),
    WICKET_KEEPER("Wicket-Keeper", "Specialist wicket-keeper and middle-order batter");

    private final String displayName;
    private final String description;

    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
