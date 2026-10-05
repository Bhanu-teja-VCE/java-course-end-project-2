package com.cpl.model;

import java.io.Serializable;

/**
 * Abstract base class representing a cricket player in CPL.
 * Demonstrates:
 * - Unit I: Abstract classes, Encapsulation, static counters, constructor overloading, this keyword
 * - Unit III: Serialization (implements Serializable)
 */
public abstract class Player implements Serializable, Rateable {
    private static final long serialVersionUID = 1L;

    // Unit I: static field for ID generation
    private static int idSequence = 1000;

    private final int id;
    private String name;
    private final Role role;
    private double basePrice;
    private Double soldPrice;
    private Integer teamId;
    private final boolean overseas;
    private int battingRating;
    private int bowlingRating;
    private int fieldingRating;

    // Unit I: Constructor Overloading (Constructor 1)
    public Player(String name, Role role, double basePrice, boolean overseas,
                  int battingRating, int bowlingRating, int fieldingRating) {
        this(++idSequence, name, role, basePrice, overseas, battingRating, bowlingRating, fieldingRating);
    }

    // Unit I: Constructor Overloading (Constructor 2 with explicit ID)
    public Player(int id, String name, Role role, double basePrice, boolean overseas,
                  int battingRating, int bowlingRating, int fieldingRating) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.basePrice = basePrice;
        this.overseas = overseas;
        this.battingRating = battingRating;
        this.bowlingRating = bowlingRating;
        this.fieldingRating = fieldingRating;
        this.soldPrice = null;
        this.teamId = null;
    }

    // Unit I: Abstract methods implemented differently by subclasses (Dynamic Method Dispatch)
    public abstract double calculateImpactScore();
    public abstract String getSpecialtyDescription();

    // Unit I: Method Overriding from Rateable
    @Override
    public double getOverallRating() {
        return (battingRating * 0.45) + (bowlingRating * 0.45) + (fieldingRating * 0.10);
    }

    public static synchronized void resetIdSequence() {
        idSequence = 1000;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public Double getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(Double soldPrice) {
        this.soldPrice = soldPrice;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public boolean isOverseas() {
        return overseas;
    }

    public int getBattingRating() {
        return battingRating;
    }

    public void setBattingRating(int battingRating) {
        this.battingRating = battingRating;
    }

    public int getBowlingRating() {
        return bowlingRating;
    }

    public void setBowlingRating(int bowlingRating) {
        this.bowlingRating = bowlingRating;
    }

    public int getFieldingRating() {
        return fieldingRating;
    }

    public void setFieldingRating(int fieldingRating) {
        this.fieldingRating = fieldingRating;
    }

    public boolean isSold() {
        return teamId != null;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s, Rating: %.1f, Base: ₹%,.0f%s)",
                id, name, role.getDisplayName(), getOverallRating(), basePrice,
                overseas ? ", Overseas" : "");
    }
}
