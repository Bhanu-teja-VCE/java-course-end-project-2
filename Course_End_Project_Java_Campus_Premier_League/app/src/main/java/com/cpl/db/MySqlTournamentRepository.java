package com.cpl.db;

import com.cpl.exception.CplException;
import com.cpl.exception.DatabaseUnavailableException;
import com.cpl.model.AllRounder;
import com.cpl.model.BallEvent;
import com.cpl.model.Batter;
import com.cpl.model.Bowler;
import com.cpl.model.Match;
import com.cpl.model.Player;
import com.cpl.model.Role;
import com.cpl.model.Team;
import com.cpl.model.WicketKeeper;
import com.cpl.tournament.PointsTableEntry;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Production MySQL implementation of TournamentRepository.
 * Demonstrates:
 * - Unit V: Statement, PreparedStatement, CallableStatement (Stored Procedures),
 *           ResultSet, ResultSetMetaData, Transactions (commit / rollback), Batch updates
 */
public class MySqlTournamentRepository implements TournamentRepository {

    @Override
    public List<Team> loadTeams() throws CplException {
        List<Team> teams = new ArrayList<Team>();
        // Unit V: Statement usage
        String sql = "SELECT id, name, short_name, color_hex, initial_purse, current_purse, min_squad, max_squad, max_overseas FROM cpl_team ORDER BY id";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Team t = new Team(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("short_name"),
                    rs.getString("color_hex"),
                    rs.getDouble("initial_purse"),
                    rs.getInt("min_squad"),
                    rs.getInt("max_squad"),
                    rs.getInt("max_overseas")
                );
                t.setCurrentPurse(rs.getDouble("current_purse"));
                teams.add(t);
            }
            return teams;
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Failed to load teams from MySQL: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveTeam(Team team) throws CplException {
        // Unit V: PreparedStatement usage
        String sql = "INSERT INTO cpl_team (name, short_name, color_hex, initial_purse, current_purse, min_squad, max_squad, max_overseas) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE current_purse = VALUES(current_purse)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, team.getName());
            ps.setString(2, team.getShortName());
            ps.setString(3, team.getColorHex());
            ps.setDouble(4, team.getInitialPurse());
            ps.setDouble(5, team.getCurrentPurse());
            ps.setInt(6, team.getMinSquad());
            ps.setInt(7, team.getMaxSquad());
            ps.setInt(8, team.getMaxOverseas());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Failed to save team: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Player> loadPlayers() throws CplException {
        List<Player> players = new ArrayList<Player>();
        String sql = "SELECT id, name, role, base_price, sold_price, team_id, is_overseas, batting_rating, bowling_rating, fielding_rating FROM cpl_player ORDER BY id";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                Role role = Role.valueOf(rs.getString("role").toUpperCase());
                double base = rs.getDouble("base_price");
                boolean overseas = rs.getBoolean("is_overseas");
                int bat = rs.getInt("batting_rating");
                int bowl = rs.getInt("bowling_rating");
                int field = rs.getInt("fielding_rating");

                Player p;
                switch (role) {
                    case BATTER: p = new Batter(id, name, base, overseas, bat, bowl, field); break;
                    case BOWLER: p = new Bowler(id, name, base, overseas, bat, bowl, field); break;
                    case ALL_ROUNDER: p = new AllRounder(id, name, base, overseas, bat, bowl, field); break;
                    case WICKET_KEEPER: p = new WicketKeeper(id, name, base, overseas, bat, bowl, field); break;
                    default: p = new Batter(id, name, base, overseas, bat, bowl, field);
                }

                double sold = rs.getDouble("sold_price");
                if (!rs.wasNull()) p.setSoldPrice(sold);
                int tId = rs.getInt("team_id");
                if (!rs.wasNull()) p.setTeamId(tId);

                players.add(p);
            }
            return players;
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Failed to load players: " + e.getMessage(), e);
        }
    }

    @Override
    public void savePlayer(Player player) throws CplException {
        String sql = "INSERT INTO cpl_player (id, name, role, base_price, sold_price, team_id, is_overseas, batting_rating, bowling_rating, fielding_rating) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE sold_price = VALUES(sold_price), team_id = VALUES(team_id)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, player.getId());
            ps.setString(2, player.getName());
            ps.setString(3, player.getRole().name());
            ps.setDouble(4, player.getBasePrice());
            if (player.getSoldPrice() != null) ps.setDouble(5, player.getSoldPrice()); else ps.setNull(5, java.sql.Types.DECIMAL);
            if (player.getTeamId() != null) ps.setInt(6, player.getTeamId()); else ps.setNull(6, java.sql.Types.INTEGER);
            ps.setBoolean(7, player.isOverseas());
            ps.setInt(8, player.getBattingRating());
            ps.setInt(9, player.getBowlingRating());
            ps.setInt(10, player.getFieldingRating());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Failed to save player: " + e.getMessage(), e);
        }
    }

    @Override
    public void recordAuctionSale(int playerId, int teamId, double soldPrice) throws CplException {
        // Unit V: CallableStatement calling MySQL Stored Procedure sp_close_auction_lot
        String callSql = "{CALL sp_close_auction_lot(?, ?, ?)}";
        try (Connection conn = Database.getConnection();
             CallableStatement cs = conn.prepareCall(callSql)) {
            cs.setInt(1, playerId);
            cs.setInt(2, teamId);
            cs.setDouble(3, soldPrice);
            cs.execute();
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Failed to execute auction lot stored procedure: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveMatch(Match match) throws CplException {
        String sql = "INSERT INTO cpl_match (id, match_number, stage, team1_id, team2_id, winner_id, " +
                     "team1_runs, team1_wickets, team1_overs, team2_runs, team2_wickets, team2_overs, mom_player_id, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE winner_id = VALUES(winner_id), team1_runs = VALUES(team1_runs), " +
                     "team1_wickets = VALUES(team1_wickets), team2_runs = VALUES(team2_runs), team2_wickets = VALUES(team2_wickets), status = VALUES(status)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, match.getId());
            ps.setInt(2, match.getMatchNumber());
            ps.setString(3, match.getStage().name());
            ps.setInt(4, match.getTeam1Id());
            ps.setInt(5, match.getTeam2Id());
            if (match.getWinnerId() != null) ps.setInt(6, match.getWinnerId()); else ps.setNull(6, java.sql.Types.INTEGER);

            if (match.getInnings1() != null) {
                ps.setInt(7, match.getInnings1().getTotalRuns());
                ps.setInt(8, match.getInnings1().getWickets());
                ps.setDouble(9, match.getInnings1().getOversCompleted());
            } else {
                ps.setInt(7, 0); ps.setInt(8, 0); ps.setDouble(9, 0.0);
            }

            if (match.getInnings2() != null) {
                ps.setInt(10, match.getInnings2().getTotalRuns());
                ps.setInt(11, match.getInnings2().getWickets());
                ps.setDouble(12, match.getInnings2().getOversCompleted());
            } else {
                ps.setInt(10, 0); ps.setInt(11, 0); ps.setDouble(12, 0.0);
            }

            if (match.getMomPlayerId() != null) ps.setInt(13, match.getMomPlayerId()); else ps.setNull(13, java.sql.Types.INTEGER);
            ps.setString(14, match.getStatus().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Failed to save match: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveBallEvents(int matchId, List<BallEvent> events) throws CplException {
        String sql = "INSERT INTO cpl_ball_event (match_id, innings_num, over_num, ball_num, batter_id, bowler_id, runs_scored, extra_runs, extra_type, is_wicket, dismissal_type) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection()) {
            // Unit V: Transaction Management (setAutoCommit(false), commit, rollback)
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (BallEvent b : events) {
                    ps.setInt(1, matchId);
                    ps.setInt(2, b.getOverNumber() < 20 ? 1 : 2);
                    ps.setInt(3, b.getOverNumber());
                    ps.setInt(4, b.getBallInOver());
                    ps.setInt(5, b.getBatterId());
                    ps.setInt(6, b.getBowlerId());
                    ps.setInt(7, b.getRunsScored());
                    ps.setInt(8, b.getExtraRuns());
                    ps.setString(9, b.getExtraType());
                    ps.setBoolean(10, b.isWicket());
                    ps.setString(11, b.getDismissalType());
                    // Unit V: Batch execution
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Failed to save ball events batch: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PointsTableEntry> fetchPointsTable() throws CplException {
        List<PointsTableEntry> list = new ArrayList<PointsTableEntry>();
        // Unit V: CallableStatement calling sp_points_table
        String callSql = "{CALL sp_points_table()}";
        try (Connection conn = Database.getConnection();
             CallableStatement cs = conn.prepareCall(callSql);
             ResultSet rs = cs.executeQuery()) {

            // Unit V: ResultSetMetaData inspection
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            while (rs.next()) {
                PointsTableEntry entry = new PointsTableEntry(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("short_name"),
                    rs.getString("color_hex")
                );
                // Populate summary
                int p = rs.getInt("matches_played");
                int w = rs.getInt("wins");
                int l = rs.getInt("losses");
                int t = rs.getInt("ties");
                double nrr = rs.getDouble("net_run_rate");
                // record simulated bulk stats
                for (int i = 0; i < w; i++) entry.recordMatchResult(160, 20.0, 140, 20.0, true, false);
                for (int i = 0; i < l; i++) entry.recordMatchResult(140, 20.0, 160, 20.0, false, false);
                for (int i = 0; i < t; i++) entry.recordMatchResult(150, 20.0, 150, 20.0, false, true);

                list.add(entry);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Failed to query stored procedure sp_points_table: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> fetchOrangeCap(int limit) throws CplException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String callSql = "{CALL sp_top_scorers(?)}";
        try (Connection conn = Database.getConnection();
             CallableStatement cs = conn.prepareCall(callSql)) {
            cs.setInt(1, limit);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("name", rs.getString("name"));
                    map.put("team", rs.getString("team_name"));
                    map.put("runs", rs.getInt("total_runs"));
                    map.put("balls", rs.getInt("balls_faced"));
                    map.put("strike_rate", rs.getDouble("strike_rate"));
                    list.add(map);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Failed to fetch orange cap: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> fetchPurpleCap(int limit) throws CplException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String callSql = "{CALL sp_top_wicket_takers(?)}";
        try (Connection conn = Database.getConnection();
             CallableStatement cs = conn.prepareCall(callSql)) {
            cs.setInt(1, limit);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("name", rs.getString("name"));
                    map.put("team", rs.getString("team_name"));
                    map.put("wickets", rs.getInt("total_wickets"));
                    map.put("balls", rs.getInt("balls_bowled"));
                    map.put("economy", rs.getDouble("economy_rate"));
                    list.add(map);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Failed to fetch purple cap: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isAvailable() {
        return Database.testConnection(Database.getConfig());
    }
}
