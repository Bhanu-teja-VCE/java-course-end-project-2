package com.cpl.model;

/**
 * Specialist bowler entity.
 * Demonstrates:
 * - Unit I: Inheritance, super keyword, method overriding, dynamic method dispatch
 */
public class Bowler extends Player {
    private static final long serialVersionUID = 1L;

    public Bowler(String name, double basePrice, boolean overseas,
                  int battingRating, int bowlingRating, int fieldingRating) {
        super(name, Role.BOWLER, basePrice, overseas, battingRating, bowlingRating, fieldingRating);
    }

    public Bowler(int id, String name, double basePrice, boolean overseas,
                  int battingRating, int bowlingRating, int fieldingRating) {
        super(id, name, Role.BOWLER, basePrice, overseas, battingRating, bowlingRating, fieldingRating);
    }

    @Override
    public double calculateImpactScore() {
        return (getBowlingRating() * 0.85) + (getFieldingRating() * 0.15);
    }

    @Override
    public String getSpecialtyDescription() {
        return String.format("Wicket-taker specialist with bowling rating %d", getBowlingRating());
    }

    @Override
    public double getOverallRating() {
        return (getBowlingRating() * 0.80) + (getFieldingRating() * 0.15) + (getBattingRating() * 0.05);
    }
}
