package com.cpl.match;

/**
 * Cricket pitch condition affecting ball bounce, swing, and run scoring rates.
 * // Unit I: Enums
 */
public enum PitchCondition {
    BATTER_PARADISE("Flat Highway (Batter Friendly)", 1.25, 0.85),
    BOWLING_GREEN("Green Top (Seam & Swing)", 0.80, 1.30),
    SPINNER_DUSTBOWL("Dust Bowl (Turn & Grip)", 0.88, 1.25),
    BALANCED("Standard Sporting Pitch", 1.00, 1.00);

    private final String description;
    private final double runMultiplier;
    private final double wicketMultiplier;

    PitchCondition(String description, double runMultiplier, double wicketMultiplier) {
        this.description = description;
        this.runMultiplier = runMultiplier;
        this.wicketMultiplier = wicketMultiplier;
    }

    public String getDescription() {
        return description;
    }

    public double getRunMultiplier() {
        return runMultiplier;
    }

    public double getWicketMultiplier() {
        return wicketMultiplier;
    }
}
