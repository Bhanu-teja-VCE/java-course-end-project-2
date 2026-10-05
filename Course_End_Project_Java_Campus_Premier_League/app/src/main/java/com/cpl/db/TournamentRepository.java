package com.cpl.db;

import com.cpl.exception.CplException;
import com.cpl.model.BallEvent;
import com.cpl.model.Match;
import com.cpl.model.Player;
import com.cpl.model.Team;
import com.cpl.tournament.PointsTableEntry;

import java.util.List;
import java.util.Map;

/**
 * Repository interface for tournament data operations.
 * Demonstrates:
 * - Unit I: Interfaces (Define & Extend)
 * - Unit V: JDBC Abstraction Pattern
 */
public interface TournamentRepository {
    List<Team> loadTeams() throws CplException;
    void saveTeam(Team team) throws CplException;
    List<Player> loadPlayers() throws CplException;
    void savePlayer(Player player) throws CplException;

    void recordAuctionSale(int playerId, int teamId, double soldPrice) throws CplException;

    void saveMatch(Match match) throws CplException;
    void saveBallEvents(int matchId, List<BallEvent> events) throws CplException;

    List<PointsTableEntry> fetchPointsTable() throws CplException;
    List<Map<String, Object>> fetchOrangeCap(int limit) throws CplException;
    List<Map<String, Object>> fetchPurpleCap(int limit) throws CplException;

    boolean isAvailable();
}
