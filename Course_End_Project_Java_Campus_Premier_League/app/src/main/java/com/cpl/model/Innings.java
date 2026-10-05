package com.cpl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an innings of a T20 match.
 * Demonstrates:
 * - Unit I: Arrays (int[] overRuns)
 * - Unit III: Collections (ArrayList, HashMap) & Serialization
 */
public class Innings implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int battingTeamId;
    private final int bowlingTeamId;
    private final List<BallEvent> deliveries;

    // Unit I: Arrays for over-by-over runs tracking (20 overs max)
    private final int[] overRuns;
    private final int[] overWickets;

    private int totalRuns;
    private int wickets;
    private int extras;
    private int legalBalls;

    // Unit III: Collections (HashMap for player statistics tracking)
    private final Map<Integer, BatterStats> batterStatsMap;
    private final Map<Integer, BowlerStats> bowlerStatsMap;
    private final List<String> fallOfWickets;

    public Innings(int battingTeamId, int bowlingTeamId) {
        this.battingTeamId = battingTeamId;
        this.bowlingTeamId = bowlingTeamId;
        this.deliveries = new ArrayList<BallEvent>();
        this.overRuns = new int[20];
        this.overWickets = new int[20];
        this.totalRuns = 0;
        this.wickets = 0;
        this.extras = 0;
        this.legalBalls = 0;
        this.batterStatsMap = new HashMap<Integer, BatterStats>();
        this.bowlerStatsMap = new HashMap<Integer, BowlerStats>();
        this.fallOfWickets = new ArrayList<String>();
    }

    public synchronized void recordBall(BallEvent event) {
        deliveries.add(event);

        int runs = event.getRunsScored();
        int ex = event.getExtraRuns();
        totalRuns += (runs + ex);
        extras += ex;

        int overIdx = Math.min(19, event.getOverNumber());
        overRuns[overIdx] += (runs + ex);

        // Update batter statistics
        BatterStats bStats = batterStatsMap.get(event.getBatterId());
        if (bStats == null) {
            bStats = new BatterStats(event.getBatterId(), event.getBatterName());
            batterStatsMap.put(event.getBatterId(), bStats);
        }
        bStats.runs += runs;
        if (!"WIDE".equalsIgnoreCase(event.getExtraType())) {
            bStats.ballsFaced++;
        }
        if (runs == 4) bStats.fours++;
        if (runs == 6) bStats.sixes++;

        // Update bowler statistics
        BowlerStats bowlStats = bowlerStatsMap.get(event.getBowlerId());
        if (bowlStats == null) {
            bowlStats = new BowlerStats(event.getBowlerId(), event.getBowlerName());
            bowlerStatsMap.put(event.getBowlerId(), bowlStats);
        }
        bowlStats.runsConceded += (runs + ex);

        if (!"WIDE".equalsIgnoreCase(event.getExtraType()) && !"NO_BALL".equalsIgnoreCase(event.getExtraType())) {
            legalBalls++;
            bowlStats.legalBallsBowled++;
        }

        if (event.isWicket()) {
            wickets++;
            overWickets[overIdx]++;
            bStats.dismissed = true;
            bStats.dismissalType = event.getDismissalType();
            if (!"RUN_OUT".equalsIgnoreCase(event.getDismissalType())) {
                bowlStats.wicketsTaken++;
            }
            fallOfWickets.add(String.format("%d/%d (%s, %d.%d ov)",
                    totalRuns, wickets, event.getBatterName(), event.getOverNumber(), event.getBallInOver()));
        }
    }

    public double getOversCompleted() {
        int completedOvers = legalBalls / 6;
        int remainingBalls = legalBalls % 6;
        return completedOvers + (remainingBalls / 10.0);
    }

    public double getDecimalOvers() {
        return legalBalls / 6.0;
    }

    public double getCurrentRunRate() {
        double overs = getDecimalOvers();
        return overs > 0 ? (totalRuns / overs) : 0.0;
    }

    public boolean isInningsComplete() {
        return wickets >= 10 || legalBalls >= 120;
    }

    // Invariant check method for Unit Testing and SafetyChecker
    public boolean verifyInvariants() {
        int calculatedBatterRuns = 0;
        for (BatterStats b : batterStatsMap.values()) {
            calculatedBatterRuns += b.runs;
        }
        boolean runsMatch = (calculatedBatterRuns + extras == totalRuns);
        boolean wicketsMatch = (wickets <= 10);
        boolean ballsMatch = (legalBalls <= 120);
        return runsMatch && wicketsMatch && ballsMatch;
    }

    public int getBattingTeamId() {
        return battingTeamId;
    }

    public int getBowlingTeamId() {
        return bowlingTeamId;
    }

    public List<BallEvent> getDeliveries() {
        return Collections.unmodifiableList(deliveries);
    }

    public int[] getOverRuns() {
        return overRuns.clone();
    }

    public int[] getOverWickets() {
        return overWickets.clone();
    }

    public int getTotalRuns() {
        return totalRuns;
    }

    public int getWickets() {
        return wickets;
    }

    public int getExtras() {
        return extras;
    }

    public int getLegalBalls() {
        return legalBalls;
    }

    public Map<Integer, BatterStats> getBatterStatsMap() {
        return Collections.unmodifiableMap(batterStatsMap);
    }

    public Map<Integer, BowlerStats> getBowlerStatsMap() {
        return Collections.unmodifiableMap(bowlerStatsMap);
    }

    public List<String> getFallOfWickets() {
        return Collections.unmodifiableList(fallOfWickets);
    }

    // Helper statistics classes
    public static class BatterStats implements Serializable {
        private static final long serialVersionUID = 1L;
        public final int id;
        public final String name;
        public int runs = 0;
        public int ballsFaced = 0;
        public int fours = 0;
        public int sixes = 0;
        public boolean dismissed = false;
        public String dismissalType = "not out";

        public BatterStats(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public double getStrikeRate() {
            return ballsFaced > 0 ? (runs * 100.0 / ballsFaced) : 0.0;
        }
    }

    public static class BowlerStats implements Serializable {
        private static final long serialVersionUID = 1L;
        public final int id;
        public final String name;
        public int legalBallsBowled = 0;
        public int runsConceded = 0;
        public int wicketsTaken = 0;

        public BowlerStats(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public double getOvers() {
            int ov = legalBallsBowled / 6;
            int b = legalBallsBowled % 6;
            return ov + (b / 10.0);
        }

        public double getEconomyRate() {
            double ov = legalBallsBowled / 6.0;
            return ov > 0 ? (runsConceded / ov) : 0.0;
        }
    }
}
