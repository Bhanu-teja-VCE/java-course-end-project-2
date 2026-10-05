package com.cpl.match;

import com.cpl.model.BallEvent;
import com.cpl.model.Batter;
import com.cpl.model.Bowler;
import com.cpl.model.Innings;
import com.cpl.model.Match;
import com.cpl.model.Player;
import com.cpl.model.Role;
import com.cpl.model.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Core ball-by-ball match simulation engine.
 * Demonstrates:
 * - Unit II: Creating threads (implements Runnable), inter-thread communication (wait / notifyAll), pause/resume
 */
public class MatchEngine implements Runnable {

    public interface MatchListener {
        void onBallBowled(BallEvent event, Innings currentInnings, int inningsNum);
        void onInningsCompleted(Innings innings, int inningsNum);
        void onMatchCompleted(Match match);
    }

    private final Match match;
    private final Team team1;
    private final Team team2;
    private final PitchCondition pitch;
    private final Random random;
    private final CommentaryEngine commentaryEngine;
    private final List<MatchListener> listeners;

    private volatile boolean running;
    private volatile boolean paused;
    private int ballDelayMs;

    public MatchEngine(Match match, Team team1, Team team2, PitchCondition pitch, long seed) {
        this.match = match;
        this.team1 = team1;
        this.team2 = team2;
        this.pitch = pitch;
        this.random = new Random(seed);
        this.commentaryEngine = new CommentaryEngine();
        this.listeners = new ArrayList<MatchListener>();
        this.running = true;
        this.paused = false;
        this.ballDelayMs = 0; // default 0 ms for tests/fast, UI sets higher
    }

    public void addListener(MatchListener l) {
        listeners.add(l);
    }

    public void setBallDelayMs(int delayMs) {
        this.ballDelayMs = Math.max(0, delayMs);
    }

    public void pauseSimulation() {
        this.paused = true;
    }

    public synchronized void resumeSimulation() {
        this.paused = false;
        notifyAll();
    }

    public void stopSimulation() {
        this.running = false;
    }

    @Override
    public void run() {
        match.setStatus(Match.Status.IN_PROGRESS);

        // Innings 1: Team 1 bats, Team 2 bowls
        Innings inn1 = simulateInnings(1, team1, team2, -1);
        for (MatchListener l : listeners) {
            l.onInningsCompleted(inn1, 1);
        }

        if (!running) return;

        // Innings 2: Team 2 bats, Team 1 bowls (Target: inn1.getTotalRuns() + 1)
        int target = inn1.getTotalRuns() + 1;
        Innings inn2 = simulateInnings(2, team2, team1, target);
        for (MatchListener l : listeners) {
            l.onInningsCompleted(inn2, 2);
        }

        // Determine player of the match
        Player mom = selectPlayerOfTheMatch(inn1, inn2);
        match.completeMatch(inn1, inn2, mom != null ? mom.getId() : null, mom != null ? mom.getName() : "None");

        if (match.getStatus() == Match.Status.TIED) {
            // Simulate 1-over Super Over tie breaker
            int soRunsT1 = 8 + random.nextInt(14);
            int soRunsT2 = 8 + random.nextInt(14);
            if (soRunsT1 > soRunsT2) {
                match.resolveSuperOver(team1.getId(), String.format("%s won via Super Over (%d-%d)", team1.getName(), soRunsT1, soRunsT2));
            } else if (soRunsT2 > soRunsT1) {
                match.resolveSuperOver(team2.getId(), String.format("%s won via Super Over (%d-%d)", team2.getName(), soRunsT2, soRunsT1));
            }
        }

        for (MatchListener l : listeners) {
            l.onMatchCompleted(match);
        }
    }

    private Innings simulateInnings(int inningsNum, Team battingTeam, Team bowlingTeam, int target) {
        Innings innings = new Innings(battingTeam.getId(), bowlingTeam.getId());

        List<Player> battingLineup = getPlayingXI(battingTeam);
        List<Player> bowlingAttack = getBowlers(bowlingTeam);

        int strikerIdx = 0;
        int nonStrikerIdx = 1;
        int nextBatterIdx = 2;

        int totalDeliveries = 0;
        int over = 0;

        while (over < 20 && innings.getWickets() < 10 && running) {
            OverPhase phase = OverPhase.fromOver(over);
            Player bowler = bowlingAttack.get(over % bowlingAttack.size());
            int legalBallsInOver = 0;

            while (legalBallsInOver < 6 && innings.getWickets() < 10 && running) {
                // Check pause gate
                synchronized (this) {
                    while (paused && running) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            return innings;
                        }
                    }
                }

                // Check 2nd innings target chase completion
                if (target > 0 && innings.getTotalRuns() >= target) {
                    return innings;
                }

                Player striker = battingLineup.get(strikerIdx);
                totalDeliveries++;

                // Delivery simulation logic
                BallOutcome outcome = computeOutcome(striker, bowler, phase, target, innings);

                BallEvent ball = new BallEvent(
                    totalDeliveries,
                    over,
                    legalBallsInOver + 1,
                    striker.getId(),
                    striker.getName(),
                    bowler.getId(),
                    bowler.getName(),
                    outcome.runs,
                    outcome.extraRuns,
                    outcome.extraType,
                    outcome.wicket,
                    outcome.dismissalType,
                    outcome.shotAngle,
                    outcome.shotDistance,
                    commentaryEngine.generateCommentary(bowler.getName(), striker.getName(),
                            outcome.runs, outcome.wicket, outcome.extraType)
                );

                innings.recordBall(ball);

                if (!"WIDE".equalsIgnoreCase(outcome.extraType) && !"NO_BALL".equalsIgnoreCase(outcome.extraType)) {
                    legalBallsInOver++;
                }

                // Handle strike rotation or dismissal
                if (outcome.wicket) {
                    if (nextBatterIdx < battingLineup.size()) {
                        strikerIdx = nextBatterIdx++;
                    }
                } else {
                    if (outcome.runs % 2 == 1) {
                        // Odd runs rotate strike
                        int temp = strikerIdx;
                        strikerIdx = nonStrikerIdx;
                        nonStrikerIdx = temp;
                    }
                }

                for (MatchListener l : listeners) {
                    l.onBallBowled(ball, innings, inningsNum);
                }

                if (ballDelayMs > 0) {
                    try {
                        Thread.sleep(ballDelayMs);
                    } catch (InterruptedException e) {
                        return innings;
                    }
                }
            }

            // End of over: switch strike
            int temp = strikerIdx;
            strikerIdx = nonStrikerIdx;
            nonStrikerIdx = temp;
            over++;
        }

        return innings;
    }

    private BallOutcome computeOutcome(Player batter, Player bowler, OverPhase phase, int target, Innings inn) {
        BallOutcome outcome = new BallOutcome();

        // 3% probability of extra
        double extraRoll = random.nextDouble();
        if (extraRoll < 0.02) {
            outcome.extraRuns = 1;
            outcome.extraType = "WIDE";
            return outcome;
        } else if (extraRoll < 0.028) {
            outcome.extraRuns = 1;
            outcome.extraType = "NO_BALL";
            outcome.runs = random.nextDouble() < 0.35 ? 4 : (random.nextDouble() < 0.15 ? 6 : 1);
            return outcome;
        }

        // Skill contest
        double batSkill = batter.getBattingRating();
        double bowlSkill = bowler.getBowlingRating();
        double differential = (batSkill - bowlSkill) / 100.0; // [-0.3, +0.3]

        double aggression = phase.getAggressionFactor();
        double pitchRun = pitch.getRunMultiplier();
        double pitchWkt = pitch.getWicketMultiplier();

        // Target pressure in 2nd innings
        if (target > 0) {
            int runsNeeded = target - inn.getTotalRuns();
            int ballsLeft = Math.max(1, 120 - inn.getLegalBalls());
            double reqRate = (runsNeeded * 6.0) / ballsLeft;
            if (reqRate > 10.0) {
                aggression *= 1.35; // Batters hit out
            }
        }

        // Wicket probability: baseline ~ 4.5% per ball, modulated by phase and skill
        double wicketChance = 0.045 * pitchWkt * phase.getRiskFactor() - (differential * 0.02);
        wicketChance = Math.max(0.015, Math.min(0.12, wicketChance));

        if (random.nextDouble() < wicketChance) {
            outcome.wicket = true;
            double dismissRoll = random.nextDouble();
            if (dismissRoll < 0.35) outcome.dismissalType = "CAUGHT";
            else if (dismissRoll < 0.65) outcome.dismissalType = "BOWLED";
            else if (dismissRoll < 0.85) outcome.dismissalType = "LBW";
            else if (dismissRoll < 0.95) outcome.dismissalType = "RUN_OUT";
            else outcome.dismissalType = "STUMPED";
            return outcome;
        }

        // Run distribution
        double runRoll = random.nextDouble();
        double sixProb = 0.040 * aggression * pitchRun + (differential * 0.02);
        double fourProb = 0.120 * aggression * pitchRun + (differential * 0.03);
        double twoProb = 0.090;
        double oneProb = 0.380;

        if (runRoll < sixProb) {
            outcome.runs = 6;
            outcome.shotDistance = 75 + random.nextDouble() * 35;
            outcome.shotAngle = random.nextDouble() * 360;
        } else if (runRoll < sixProb + fourProb) {
            outcome.runs = 4;
            outcome.shotDistance = 65 + random.nextDouble() * 15;
            outcome.shotAngle = random.nextDouble() * 360;
        } else if (runRoll < sixProb + fourProb + twoProb) {
            outcome.runs = 2;
            outcome.shotDistance = 40 + random.nextDouble() * 25;
            outcome.shotAngle = random.nextDouble() * 360;
        } else if (runRoll < sixProb + fourProb + twoProb + oneProb) {
            outcome.runs = 1;
            outcome.shotDistance = 25 + random.nextDouble() * 30;
            outcome.shotAngle = random.nextDouble() * 360;
        } else {
            outcome.runs = 0;
            outcome.shotDistance = 5 + random.nextDouble() * 20;
            outcome.shotAngle = random.nextDouble() * 360;
        }

        return outcome;
    }

    private List<Player> getPlayingXI(Team team) {
        List<Player> squad = new ArrayList<Player>(team.getSquad());
        while (squad.size() < 2) {
            squad.add(new Batter(9900 + squad.size(), team.getShortName() + "-Sub" + (squad.size() + 1),
                    200000.0, false, 75, 40, 75));
        }
        if (squad.size() <= 11) {
            return squad;
        }
        return new ArrayList<Player>(squad.subList(0, 11));
    }

    private List<Player> getBowlers(Team team) {
        List<Player> squad = new ArrayList<Player>(team.getSquad());
        while (squad.isEmpty()) {
            squad.add(new Bowler(9950, team.getShortName() + "-BowlerSub", 200000.0, false, 30, 80, 75));
        }
        List<Player> bowlers = new ArrayList<Player>();
        for (Player p : squad) {
            if (p.getRole() == Role.BOWLER || p.getRole() == Role.ALL_ROUNDER) {
                bowlers.add(p);
            }
        }
        if (bowlers.isEmpty()) {
            bowlers.addAll(squad);
        }
        return bowlers;
    }

    private Player selectPlayerOfTheMatch(Innings inn1, Innings inn2) {
        Player bestPlayer = null;
        double highestPoints = -1;

        List<Player> allPlayers = new ArrayList<Player>();
        allPlayers.addAll(team1.getSquad());
        allPlayers.addAll(team2.getSquad());

        for (Player p : allPlayers) {
            double pts = 0;
            Innings.BatterStats b1 = inn1.getBatterStatsMap().get(p.getId());
            Innings.BatterStats b2 = inn2.getBatterStatsMap().get(p.getId());
            if (b1 != null) pts += (b1.runs * 1.0) + (b1.sixes * 2.0);
            if (b2 != null) pts += (b2.runs * 1.0) + (b2.sixes * 2.0);

            Innings.BowlerStats bowl1 = inn1.getBowlerStatsMap().get(p.getId());
            Innings.BowlerStats bowl2 = inn2.getBowlerStatsMap().get(p.getId());
            if (bowl1 != null) pts += (bowl1.wicketsTaken * 25.0);
            if (bowl2 != null) pts += (bowl2.wicketsTaken * 25.0);

            if (pts > highestPoints) {
                highestPoints = pts;
                bestPlayer = p;
            }
        }
        return bestPlayer;
    }

    private static class BallOutcome {
        int runs = 0;
        int extraRuns = 0;
        String extraType = "NONE";
        boolean wicket = false;
        String dismissalType = "NONE";
        double shotAngle = 0;
        double shotDistance = 0;
    }
}
