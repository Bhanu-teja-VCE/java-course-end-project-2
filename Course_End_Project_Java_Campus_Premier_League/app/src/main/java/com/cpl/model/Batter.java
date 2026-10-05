package com.cpl.model;

/**
 * Specialist batsman entity.
 * Demonstrates:
 * - Unit I: Inheritance, super keyword, method overriding, dynamic method dispatch
 */
public class Batter extends Player {
    private static final long serialVersionUID = 1L;

    public Batter(String name, double basePrice, boolean overseas,
                  int battingRating, int bowlingRating, int fieldingRating) {
        // Unit I: super keyword calling parent constructor
        super(name, Role.BATTER, basePrice, overseas, battingRating, bowlingRating, fieldingRating);
    }

    public Batter(int id, String name, double basePrice, boolean overseas,
                  int battingRating, int bowlingRating, int fieldingRating) {
        super(id, name, Role.BATTER, basePrice, overseas, battingRating, bowlingRating, fieldingRating);
    }

    // Unit I: Method Overriding & Dynamic Dispatch
    @Override
    public double calculateImpactScore() {
        return (getBattingRating() * 0.85) + (getFieldingRating() * 0.15);
    }

    @Override
    public String getSpecialtyDescription() {
        return String.format("Top-order specialist with batting rating %d", getBattingRating());
    }

    @Override
    public double getOverallRating() {
        // Specialist batters weighted heavily on batting
        return (getBattingRating() * 0.80) + (getFieldingRating() * 0.15) + (getBowlingRating() * 0.05);
    }
}
