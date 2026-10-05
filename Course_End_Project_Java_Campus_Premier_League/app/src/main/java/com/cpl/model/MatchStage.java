package com.cpl.model;

/**
 * Tournament stage for a match.
 * // Unit I: Enums
 */
public enum MatchStage {
    LEAGUE("League Match"),
    QUALIFIER_1("Qualifier 1 (Top 1 vs 2)"),
    ELIMINATOR("Eliminator (Top 3 vs 4)"),
    QUALIFIER_2("Qualifier 2 (Q1 Loser vs Eliminator Winner)"),
    FINAL("Championship Grand Final");

    private final String description;

    MatchStage(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
