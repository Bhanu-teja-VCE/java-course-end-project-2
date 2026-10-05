package com.cpl.tournament;

import java.io.Serializable;

/**
 * Points table row entry encapsulating team statistics and Net Run Rate (NRR).
 * Demonstrates:
 * - Unit I: Encapsulation
 * - Unit III: Comparable interface and Collections sorting
 */
public class PointsTableEntry implements Comparable<PointsTableEntry>, Serializable {
    private static final long serialVersionUID = 1L;

    private final int teamId;
    private final String teamName;
    private final String shortName;
    private final String colorHex;
    private int matchesPlayed;
    private int wins;
    private int losses;
    private int ties;
    private int points;

    // Granular NRR components
    private int totalRunsScored;
    private double totalOversFaced; // in decimal overs (e.g. 20.0, 19.3 -> 19.5)
    private int totalRunsConceded;
    private double totalOversBowled;

    public PointsTableEntry(int teamId, String teamName, String shortName, String colorHex) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.shortName = shortName;
        this.colorHex = colorHex;
        this.matchesPlayed = 0;
        this.wins = 0;
        this.losses = 0;
        this.ties = 0;
        this.points = 0;
        this.totalRunsScored = 0;
        this.totalOversFaced = 0.0;
        this.totalRunsConceded = 0;
        this.totalOversBowled = 0.0;
    }

    public synchronized void recordMatchResult(int runsScored, double oversFaced,
                                              int runsConceded, double oversBowled,
                                              boolean won, boolean tied) {
        matchesPlayed++;
        totalRunsScored += runsScored;
        totalOversFaced += oversFaced;
        totalRunsConceded += runsConceded;
        totalOversBowled += oversBowled;

        if (won) {
            wins++;
            points += 2;
        } else if (tied) {
            ties++;
            points += 1;
        } else {
            losses++;
        }
    }

    public double getNetRunRate() {
        if (totalOversFaced <= 0.0 || totalOversBowled <= 0.0) {
            return 0.0;
        }
        double forRate = totalRunsScored / totalOversFaced;
        double againstRate = totalRunsConceded / totalOversBowled;
        return forRate - againstRate;
    }

    // Unit III: Comparable implementation for natural ranking order
    @Override
    public int compareTo(PointsTableEntry other) {
        if (this.points != other.points) {
            return Integer.compare(other.points, this.points); // Descending points
        }
        return Double.compare(other.getNetRunRate(), this.getNetRunRate()); // Descending NRR
    }

    public int getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getColorHex() {
        return colorHex;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getTies() {
        return ties;
    }

    public int getPoints() {
        return points;
    }

    public int getTotalRunsScored() {
        return totalRunsScored;
    }

    public int getTotalRunsConceded() {
        return totalRunsConceded;
    }

    @Override
    public String toString() {
        return String.format("%-18s | P: %2d | W: %2d | L: %2d | Pts: %2d | NRR: %+6.3f",
                teamName, matchesPlayed, wins, losses, points, getNetRunRate());
    }
}
