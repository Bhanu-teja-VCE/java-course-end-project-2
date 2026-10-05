package com.cpl.model;

import java.io.Serializable;

/**
 * Represents a complete T20 cricket fixture between two franchise teams.
 * Demonstrates:
 * - Unit I: Encapsulation, object association
 * - Unit III: Serialization
 */
public class Match implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status { SCHEDULED, IN_PROGRESS, COMPLETED, TIED }

    private final int id;
    private final int matchNumber;
    private final MatchStage stage;
    private final int team1Id;
    private final int team2Id;
    private final String team1Name;
    private final String team2Name;

    private Innings innings1;
    private Innings innings2;
    private Integer winnerId;
    private String resultSummary;
    private Integer momPlayerId;
    private String momPlayerName;
    private Status status;

    public Match(int id, int matchNumber, MatchStage stage,
                 int team1Id, String team1Name, int team2Id, String team2Name) {
        this.id = id;
        this.matchNumber = matchNumber;
        this.stage = stage;
        this.team1Id = team1Id;
        this.team1Name = team1Name;
        this.team2Id = team2Id;
        this.team2Name = team2Name;
        this.status = Status.SCHEDULED;
        this.resultSummary = "Scheduled";
    }

    public void completeMatch(Innings inn1, Innings inn2, Integer momId, String momName) {
        this.innings1 = inn1;
        this.innings2 = inn2;
        this.momPlayerId = momId;
        this.momPlayerName = momName;

        if (inn1.getTotalRuns() > inn2.getTotalRuns()) {
            this.winnerId = team1Id;
            int margin = inn1.getTotalRuns() - inn2.getTotalRuns();
            this.resultSummary = String.format("%s won by %d runs", team1Name, margin);
            this.status = Status.COMPLETED;
        } else if (inn2.getTotalRuns() > inn1.getTotalRuns()) {
            this.winnerId = team2Id;
            int wicketsLeft = 10 - inn2.getWickets();
            this.resultSummary = String.format("%s won by %d wickets", team2Name, wicketsLeft);
            this.status = Status.COMPLETED;
        } else {
            // Match Tied
            this.winnerId = null;
            this.resultSummary = "Match Tied (Scores Level - Super Over Required)";
            this.status = Status.TIED;
        }
    }

    public void resolveSuperOver(int superOverWinnerId, String summary) {
        this.winnerId = superOverWinnerId;
        this.resultSummary = summary;
        this.status = Status.COMPLETED;
    }

    public int getId() {
        return id;
    }

    public int getMatchNumber() {
        return matchNumber;
    }

    public MatchStage getStage() {
        return stage;
    }

    public int getTeam1Id() {
        return team1Id;
    }

    public int getTeam2Id() {
        return team2Id;
    }

    public String getTeam1Name() {
        return team1Name;
    }

    public String getTeam2Name() {
        return team2Name;
    }

    public Innings getInnings1() {
        return innings1;
    }

    public Innings getInnings2() {
        return innings2;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public Integer getMomPlayerId() {
        return momPlayerId;
    }

    public String getMomPlayerName() {
        return momPlayerName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Match #%d (%s): %s vs %s [%s]",
                matchNumber, stage.name(), team1Name, team2Name, resultSummary);
    }
}
