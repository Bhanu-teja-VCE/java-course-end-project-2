package com.cpl.tournament;

import com.cpl.match.MatchEngine;
import com.cpl.match.PitchCondition;
import com.cpl.model.Match;
import com.cpl.model.MatchStage;
import com.cpl.model.Player;
import com.cpl.model.Team;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Top-level orchestrator managing teams, rosters, fixtures, points table, and playoffs.
 * Demonstrates:
 * - Unit I: OOP architecture & Object association
 * - Unit II: Multithreaded parallel match simulation
 * - Unit III: Collections (ArrayList, HashMap) & Serialization
 */
public class Tournament implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Phase { AUCTION, LEAGUE, PLAYOFFS, COMPLETED }

    private final String leagueName;
    private final int seasonYear;
    private final List<Team> teams;
    private final Map<Integer, Team> teamMap;
    private final List<Player> allPlayers;
    private final PointsTable pointsTable;
    private final List<Match> fixtures;
    private final List<Match> completedMatches;
    private Phase phase;

    private Match qualifier1;
    private Match eliminator;
    private Match qualifier2;
    private Match grandFinal;
    private Team champion;

    public Tournament(String leagueName, int seasonYear, List<Team> teams, List<Player> players) {
        this.leagueName = leagueName;
        this.seasonYear = seasonYear;
        this.teams = new ArrayList<Team>(teams);
        this.teamMap = new HashMap<Integer, Team>();
        for (Team t : teams) {
            teamMap.put(t.getId(), t);
        }
        this.allPlayers = new ArrayList<Player>(players);
        this.pointsTable = new PointsTable(teams);
        this.fixtures = TournamentScheduler.generateRoundRobinFixtures(teams);
        this.completedMatches = new ArrayList<Match>();
        this.phase = Phase.AUCTION;
    }

    public void startLeaguePhase() {
        this.phase = Phase.LEAGUE;
    }

    public synchronized void recordCompletedMatch(Match match) {
        completedMatches.add(match);
        if (match.getStage() == MatchStage.LEAGUE) {
            pointsTable.updateFromMatch(match);
        }

        // Check if league phase finished (56 matches)
        if (phase == Phase.LEAGUE && completedMatches.size() >= fixtures.size()) {
            setupPlayoffs();
        }
    }

    public void setupPlayoffs() {
        this.phase = Phase.PLAYOFFS;
        List<PointsTableEntry> ranks = pointsTable.getRankings();
        if (ranks.size() < 4) return;

        Team t1 = teamMap.get(ranks.get(0).getTeamId());
        Team t2 = teamMap.get(ranks.get(1).getTeamId());
        Team t3 = teamMap.get(ranks.get(2).getTeamId());
        Team t4 = teamMap.get(ranks.get(3).getTeamId());

        int nextId = fixtures.size() + 1;
        qualifier1 = new Match(nextId++, 57, MatchStage.QUALIFIER_1,
                t1.getId(), t1.getName(), t2.getId(), t2.getName());
        eliminator = new Match(nextId++, 58, MatchStage.ELIMINATOR,
                t3.getId(), t3.getName(), t4.getId(), t4.getName());
    }

    public void setupQualifier2() {
        if (qualifier1 == null || eliminator == null) return;
        if (qualifier1.getStatus() != Match.Status.COMPLETED || eliminator.getStatus() != Match.Status.COMPLETED) return;

        int loserQ1Id = qualifier1.getWinnerId() == qualifier1.getTeam1Id() ?
                qualifier1.getTeam2Id() : qualifier1.getTeam1Id();
        int winnerElimId = eliminator.getWinnerId();

        Team tA = teamMap.get(loserQ1Id);
        Team tB = teamMap.get(winnerElimId);

        qualifier2 = new Match(fixtures.size() + 3, 59, MatchStage.QUALIFIER_2,
                tA.getId(), tA.getName(), tB.getId(), tB.getName());
    }

    public void setupGrandFinal() {
        if (qualifier1 == null || qualifier2 == null) return;
        if (qualifier2.getStatus() != Match.Status.COMPLETED) return;

        int winnerQ1Id = qualifier1.getWinnerId();
        int winnerQ2Id = qualifier2.getWinnerId();

        Team tA = teamMap.get(winnerQ1Id);
        Team tB = teamMap.get(winnerQ2Id);

        grandFinal = new Match(fixtures.size() + 4, 60, MatchStage.FINAL,
                tA.getId(), tA.getName(), tB.getId(), tB.getName());
    }

    public void completeFinal(Match finalMatch) {
        if (finalMatch.getWinnerId() != null) {
            this.champion = teamMap.get(finalMatch.getWinnerId());
        }
        this.phase = Phase.COMPLETED;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public int getSeasonYear() {
        return seasonYear;
    }

    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    public Team getTeam(int id) {
        return teamMap.get(id);
    }

    public List<Player> getAllPlayers() {
        return Collections.unmodifiableList(allPlayers);
    }

    public PointsTable getPointsTable() {
        return pointsTable;
    }

    public List<Match> getFixtures() {
        return Collections.unmodifiableList(fixtures);
    }

    public List<Match> getCompletedMatches() {
        return Collections.unmodifiableList(completedMatches);
    }

    public Phase getPhase() {
        return phase;
    }

    public Match getQualifier1() {
        return qualifier1;
    }

    public Match getEliminator() {
        return eliminator;
    }

    public Match getQualifier2() {
        return qualifier2;
    }

    public Match getGrandFinal() {
        return grandFinal;
    }

    public Team getChampion() {
        return champion;
    }
}
