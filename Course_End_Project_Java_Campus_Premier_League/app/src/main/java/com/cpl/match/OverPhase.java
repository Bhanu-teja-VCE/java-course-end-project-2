package com.cpl.match;

/**
 * T20 Match Phases with differing field restrictions and run-scoring incentives.
 * // Unit I: Enums
 */
public enum OverPhase {
    POWERPLAY("Powerplay (Overs 1-6)", 1.15, 1.10),
    MIDDLE("Middle Overs (Overs 7-15)", 0.95, 0.90),
    DEATH("Death Overs (Overs 16-20)", 1.35, 1.40);

    private final String description;
    private final double aggressionFactor;
    private final double riskFactor;

    OverPhase(String description, double aggressionFactor, double riskFactor) {
        this.description = description;
        this.aggressionFactor = aggressionFactor;
        this.riskFactor = riskFactor;
    }

    public static OverPhase fromOver(int overIndexZeroBased) {
        if (overIndexZeroBased < 6) return POWERPLAY;
        if (overIndexZeroBased < 15) return MIDDLE;
        return DEATH;
    }

    public String getDescription() {
        return description;
    }

    public double getAggressionFactor() {
        return aggressionFactor;
    }

    public double getRiskFactor() {
        return riskFactor;
    }
}
