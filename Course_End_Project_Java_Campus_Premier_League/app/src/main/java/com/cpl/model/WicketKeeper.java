package com.cpl.model;

/**
 * Wicket-Keeper batsman entity.
 * Demonstrates:
 * - Unit I: Inheritance, super keyword, method overriding, dynamic method dispatch
 */
public class WicketKeeper extends Player {
    private static final long serialVersionUID = 1L;

    public WicketKeeper(String name, double basePrice, boolean overseas,
                        int battingRating, int bowlingRating, int fieldingRating) {
        super(name, Role.WICKET_KEEPER, basePrice, overseas, battingRating, bowlingRating, fieldingRating);
    }

    public WicketKeeper(int id, String name, double basePrice, boolean overseas,
                        int battingRating, int bowlingRating, int fieldingRating) {
        super(id, name, Role.WICKET_KEEPER, basePrice, overseas, battingRating, bowlingRating, fieldingRating);
    }

    @Override
    public double calculateImpactScore() {
        return (getBattingRating() * 0.70) + (getFieldingRating() * 0.30);
    }

    @Override
    public String getSpecialtyDescription() {
        return String.format("Gloveman & Batter (Bat: %d, Glove/Field: %d)", getBattingRating(), getFieldingRating());
    }

    @Override
    public double getOverallRating() {
        return (getBattingRating() * 0.65) + (getFieldingRating() * 0.30) + (getBowlingRating() * 0.05);
    }
}
