package com.cpl.model;

/**
 * All-Rounder player entity excelling in both batting and bowling.
 * Demonstrates:
 * - Unit I: Inheritance, super keyword, method overriding, dynamic method dispatch
 */
public class AllRounder extends Player {
    private static final long serialVersionUID = 1L;

    public AllRounder(String name, double basePrice, boolean overseas,
                      int battingRating, int bowlingRating, int fieldingRating) {
        super(name, Role.ALL_ROUNDER, basePrice, overseas, battingRating, bowlingRating, fieldingRating);
    }

    public AllRounder(int id, String name, double basePrice, boolean overseas,
                      int battingRating, int bowlingRating, int fieldingRating) {
        super(id, name, Role.ALL_ROUNDER, basePrice, overseas, battingRating, bowlingRating, fieldingRating);
    }

    @Override
    public double calculateImpactScore() {
        return (getBattingRating() * 0.45) + (getBowlingRating() * 0.45) + (getFieldingRating() * 0.10);
    }

    @Override
    public String getSpecialtyDescription() {
        return String.format("Dual-threat asset (Bat: %d, Bowl: %d)", getBattingRating(), getBowlingRating());
    }

    @Override
    public double getOverallRating() {
        return (getBattingRating() * 0.45) + (getBowlingRating() * 0.45) + (getFieldingRating() * 0.10);
    }
}
