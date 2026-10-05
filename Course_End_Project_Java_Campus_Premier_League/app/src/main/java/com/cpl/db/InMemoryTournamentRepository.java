package com.cpl.db;

import com.cpl.model.BallEvent;
import com.cpl.model.Innings;
import com.cpl.model.Match;
import com.cpl.model.Player;
import com.cpl.model.Team;
import com.cpl.tournament.PointsTable;
import com.cpl.tournament.PointsTableEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Offline in-memory fallback implementation of TournamentRepository.
 * Ensures the application runs seamlessly even when MySQL is offline.
 * Demonstrates:
 * - Unit I: Polymorphism & Interface Implementation
 * - Unit III: Collections (HashMap, ArrayList)
 */
public class InMemoryTournamentRepository implements TournamentRepository {
    private final Map<Integer, Team> teams = new HashMap<Integer, Team>();
    private final Map<Integer, Player> players = new HashMap<Integer, Player>();
    private final Map<Integer, Match> matches = new HashMap<Integer, Match>();
    private PointsTable pointsTable;

    public InMemoryTournamentRepository(List<Team> initialTeams, List<Player> initialPlayers) {
        for (Team t : initialTeams) {
            teams.put(t.getId(), t);
        }
        for (Player p : initialPlayers) {
            players.put(p.getId(), p);
        }
        this.pointsTable = new PointsTable(initialTeams);
    }

    @Override
    public List<Team> loadTeams() {
        return new ArrayList<Team>(teams.values());
    }

    @Override
    public void saveTeam(Team team) {
        teams.put(team.getId(), team);
    }

    @Override
    public List<Player> loadPlayers() {
        return new ArrayList<Player>(players.values());
    }

    @Override
    public void savePlayer(Player player) {
        players.put(player.getId(), player);
    }

    @Override
    public void recordAuctionSale(int playerId, int teamId, double soldPrice) {
        Player p = players.get(playerId);
        Team t = teams.get(teamId);
        if (p != null && t != null) {
            try {
                t.addPlayer(p, soldPrice);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void saveMatch(Match match) {
        matches.put(match.getId(), match);
        if (pointsTable != null) {
            pointsTable.updateFromMatch(match);
        }
    }

    @Override
    public void saveBallEvents(int matchId, List<BallEvent> events) {
        // stored in Match object directly in memory
    }

    @Override
    public List<PointsTableEntry> fetchPointsTable() {
        if (pointsTable != null) {
            return pointsTable.getRankings();
        }
        return new ArrayList<PointsTableEntry>();
    }

    @Override
    public List<Map<String, Object>> fetchOrangeCap(int limit) {
        Map<Integer, Integer> runsMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> ballsMap = new HashMap<Integer, Integer>();

        for (Match m : matches.values()) {
            aggregateInningsBatting(m.getInnings1(), runsMap, ballsMap);
            aggregateInningsBatting(m.getInnings2(), runsMap, ballsMap);
        }

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Map.Entry<Integer, Integer> e : runsMap.entrySet()) {
            Player p = players.get(e.getKey());
            if (p != null) {
                Map<String, Object> row = new HashMap<String, Object>();
                row.put("name", p.getName());
                Team t = p.getTeamId() != null ? teams.get(p.getTeamId()) : null;
                row.put("team", t != null ? t.getShortName() : "FA");
                row.put("runs", e.getValue());
                int b = ballsMap.getOrDefault(e.getKey(), 0);
                row.put("balls", b);
                row.put("strike_rate", b > 0 ? (e.getValue() * 100.0 / b) : 0.0);
                list.add(row);
            }
        }
        // sort descending by runs
        list.sort((a, b) -> Integer.compare((Integer) b.get("runs"), (Integer) a.get("runs")));
        return list.subList(0, Math.min(limit, list.size()));
    }

    private void aggregateInningsBatting(Innings inn, Map<Integer, Integer> runs, Map<Integer, Integer> balls) {
        if (inn == null) return;
        for (Innings.BatterStats bs : inn.getBatterStatsMap().values()) {
            runs.put(bs.id, runs.getOrDefault(bs.id, 0) + bs.runs);
            balls.put(bs.id, balls.getOrDefault(bs.id, 0) + bs.ballsFaced);
        }
    }

    @Override
    public List<Map<String, Object>> fetchPurpleCap(int limit) {
        Map<Integer, Integer> wktsMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> ballsMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> runsMap = new HashMap<Integer, Integer>();

        for (Match m : matches.values()) {
            aggregateInningsBowling(m.getInnings1(), wktsMap, ballsMap, runsMap);
            aggregateInningsBowling(m.getInnings2(), wktsMap, ballsMap, runsMap);
        }

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Map.Entry<Integer, Integer> e : wktsMap.entrySet()) {
            Player p = players.get(e.getKey());
            if (p != null) {
                Map<String, Object> row = new HashMap<String, Object>();
                row.put("name", p.getName());
                Team t = p.getTeamId() != null ? teams.get(p.getTeamId()) : null;
                row.put("team", t != null ? t.getShortName() : "FA");
                row.put("wickets", e.getValue());
                int b = ballsMap.getOrDefault(e.getKey(), 0);
                row.put("balls", b);
                int r = runsMap.getOrDefault(e.getKey(), 0);
                row.put("economy", b > 0 ? (r * 6.0 / b) : 0.0);
                list.add(row);
            }
        }
        list.sort((a, b) -> Integer.compare((Integer) b.get("wickets"), (Integer) a.get("wickets")));
        return list.subList(0, Math.min(limit, list.size()));
    }

    private void aggregateInningsBowling(Innings inn, Map<Integer, Integer> wkts, Map<Integer, Integer> balls, Map<Integer, Integer> runs) {
        if (inn == null) return;
        for (Innings.BowlerStats bs : inn.getBowlerStatsMap().values()) {
            wkts.put(bs.id, wkts.getOrDefault(bs.id, 0) + bs.wicketsTaken);
            balls.put(bs.id, balls.getOrDefault(bs.id, 0) + bs.legalBallsBowled);
            runs.put(bs.id, runs.getOrDefault(bs.id, 0) + bs.runsConceded);
        }
    }

    @Override
    public boolean isAvailable() {
        return true; // Always available in memory
    }
}
