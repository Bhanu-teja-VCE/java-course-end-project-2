package com.cpl.model;

import java.io.Serializable;

/**
 * Immutable record representing a single delivery in a T20 innings.
 * Demonstrates:
 * - Unit I: Encapsulation & access protection
 * - Unit III: Serialization
 */
public class BallEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int ballNumber;
    private final int overNumber;
    private final int ballInOver;
    private final int batterId;
    private final String batterName;
    private final int bowlerId;
    private final String bowlerName;
    private final int runsScored;
    private final int extraRuns;
    private final String extraType;
    private final boolean wicket;
    private final String dismissalType;
    private final double shotAngleDegrees;
    private final double shotDistanceMeters;
    private final String commentary;

    public BallEvent(int ballNumber, int overNumber, int ballInOver,
                     int batterId, String batterName,
                     int bowlerId, String bowlerName,
                     int runsScored, int extraRuns, String extraType,
                     boolean wicket, String dismissalType,
                     double shotAngleDegrees, double shotDistanceMeters,
                     String commentary) {
        this.ballNumber = ballNumber;
        this.overNumber = overNumber;
        this.ballInOver = ballInOver;
        this.batterId = batterId;
        this.batterName = batterName;
        this.bowlerId = bowlerId;
        this.bowlerName = bowlerName;
        this.runsScored = runsScored;
        this.extraRuns = extraRuns;
        this.extraType = extraType != null ? extraType : "NONE";
        this.wicket = wicket;
        this.dismissalType = dismissalType != null ? dismissalType : "NONE";
        this.shotAngleDegrees = shotAngleDegrees;
        this.shotDistanceMeters = shotDistanceMeters;
        this.commentary = commentary != null ? commentary : "";
    }

    public int getBallNumber() {
        return ballNumber;
    }

    public int getOverNumber() {
        return overNumber;
    }

    public int getBallInOver() {
        return ballInOver;
    }

    public int getBatterId() {
        return batterId;
    }

    public String getBatterName() {
        return batterName;
    }

    public int getBowlerId() {
        return bowlerId;
    }

    public String getBowlerName() {
        return bowlerName;
    }

    public int getRunsScored() {
        return runsScored;
    }

    public int getExtraRuns() {
        return extraRuns;
    }

    public String getExtraType() {
        return extraType;
    }

    public boolean isWicket() {
        return wicket;
    }

    public String getDismissalType() {
        return dismissalType;
    }

    public double getShotAngleDegrees() {
        return shotAngleDegrees;
    }

    public double getShotDistanceMeters() {
        return shotDistanceMeters;
    }

    public String getCommentary() {
        return commentary;
    }

    public int getTotalBallRuns() {
        return runsScored + extraRuns;
    }

    @Override
    public String toString() {
        return String.format("[%d.%d] %s to %s: %d run(s)%s - %s",
                overNumber, ballInOver, bowlerName, batterName,
                runsScored + extraRuns, wicket ? " (W)" : "", commentary);
    }
}
