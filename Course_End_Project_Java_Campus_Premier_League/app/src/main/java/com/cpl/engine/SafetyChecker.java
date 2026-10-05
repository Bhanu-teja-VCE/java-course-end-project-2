package com.cpl.engine;

import com.cpl.model.Innings;
import com.cpl.model.Match;
import com.cpl.model.Player;
import com.cpl.model.Team;
import com.cpl.tournament.PointsTable;
import com.cpl.tournament.PointsTableEntry;
import com.cpl.tournament.Tournament;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Invariant safety checker that rigorously verifies game state consistency.
 * Demonstrates:
 * - Unit I: Logic verification
 * - Unit III: Collections (HashSet for uniqueness assertions)
 */
public class SafetyChecker {

    public static void verifyTournamentInvariants(Tournament tournament) throws IllegalStateException {
        verifyAuctionInvariants(tournament.getTeams(), tournament.getAllPlayers());

        for (Match m : tournament.getCompletedMatches()) {
            verifyMatchInvariants(m);
        }

        verifyPointsTableInvariants(tournament);
    }

    public static void verifyAuctionInvariants(List<Team> teams, List<Player> players) throws IllegalStateException {
        Set<Integer> soldPlayerIds = new HashSet<Integer>();

        for (Team team : teams) {
            // 1. Purse bounds
            if (team.getCurrentPurse() < 0.0) {
                throw new IllegalStateException(String.format("Invariant violation: Team %s exceeded purse: %.2f",
                        team.getName(), team.getCurrentPurse()));
            }

            // 2. Squad size bounds
            if (team.getSquadSize() > team.getMaxSquad()) {
                throw new IllegalStateException(String.format("Invariant violation: Team %s exceeded max squad: %d > %d",
                        team.getName(), team.getSquadSize(), team.getMaxSquad()));
            }

            // 3. Overseas limits
            if (team.getOverseasCount() > team.getMaxOverseas()) {
                throw new IllegalStateException(String.format("Invariant violation: Team %s exceeded overseas limit: %d > %d",
                        team.getName(), team.getOverseasCount(), team.getMaxOverseas()));
            }

            // 4. No player sold twice
            for (Player p : team.getSquad()) {
                if (soldPlayerIds.contains(p.getId())) {
                    throw new IllegalStateException(String.format("Invariant violation: Player %s (ID %d) was sold more than once!",
                            p.getName(), p.getId()));
                }
                soldPlayerIds.add(p.getId());
            }
        }
    }

    public static void verifyMatchInvariants(Match match) throws IllegalStateException {
        if (match.getInnings1() != null) {
            verifyInningsInvariants(match.getInnings1(), match.getTeam1Name() + " (Inn 1)");
        }
        if (match.getInnings2() != null) {
            verifyInningsInvariants(match.getInnings2(), match.getTeam2Name() + " (Inn 2)");
        }
    }

    public static void verifyInningsInvariants(Innings innings, String context) throws IllegalStateException {
        // 1. Legal balls <= 120
        if (innings.getLegalBalls() > 120) {
            throw new IllegalStateException(String.format("[%s] Invariant failed: legal balls bowled %d > 120",
                    context, innings.getLegalBalls()));
        }

        // 2. Wickets <= 10
        if (innings.getWickets() > 10) {
            throw new IllegalStateException(String.format("[%s] Invariant failed: wickets %d > 10",
                    context, innings.getWickets()));
        }

        // 3. Batter runs + extras == totalRuns
        int totalBatterRuns = 0;
        for (Innings.BatterStats b : innings.getBatterStatsMap().values()) {
            totalBatterRuns += b.runs;
        }

        if (totalBatterRuns + innings.getExtras() != innings.getTotalRuns()) {
            throw new IllegalStateException(String.format(
                    "[%s] Invariant failed: Batter runs (%d) + extras (%d) != total runs (%d)",
                    context, totalBatterRuns, innings.getExtras(), innings.getTotalRuns()));
        }
    }

    public static void verifyPointsTableInvariants(Tournament tournament) throws IllegalStateException {
        PointsTable table = tournament.getPointsTable();
        int totalPoints = 0;
        int totalMatches = 0;

        for (PointsTableEntry entry : table.getRankings()) {
            totalPoints += entry.getPoints();
            totalMatches += entry.getMatchesPlayed();
        }

        // In cricket, total points distributed in regular matches = 2 * completed league matches
        int completedLeagueMatches = 0;
        for (Match m : tournament.getCompletedMatches()) {
            if (m.getStage() == com.cpl.model.MatchStage.LEAGUE) {
                completedLeagueMatches++;
            }
        }

        int expectedPoints = completedLeagueMatches * 2;
        if (totalPoints != expectedPoints) {
            throw new IllegalStateException(String.format(
                    "Points table invariant failed: Total points (%d) != Expected (%d)",
                    totalPoints, expectedPoints));
        }
    }
}
